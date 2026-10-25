package com.haiyun.meal.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.haiyun.meal.constant.MessageConstant;
import com.haiyun.meal.context.BaseContext;
import com.haiyun.meal.dto.*;
import com.haiyun.meal.entity.*;
import com.haiyun.meal.enumeration.OrderStatus;
import com.haiyun.meal.exception.OrderBusinessException;
import com.haiyun.meal.mapper.*;
import com.haiyun.meal.result.PageResult;
import com.haiyun.meal.service.OrderService;
import com.haiyun.meal.vo.OrderPaymentVO;
import com.haiyun.meal.vo.OrderStatisticsVO;
import com.haiyun.meal.vo.OrderSubmitVO;
import com.haiyun.meal.vo.OrderVO;
import com.haiyun.meal.vo.PickupVerifyVO;
import com.haiyun.meal.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订单业务实现。
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired private OrderMapper orderMapper;
    @Autowired private PaymentCallbackLogMapper paymentCallbackLogMapper;
    @Autowired private OrderDetailMapper orderDetailMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private ShoppingCartMapper shoppingCartMapper;
    @Autowired private PickupSlotMapper pickupSlotMapper;
    @Autowired private StallMapper stallMapper;
    @Autowired private WebSocketServer webSocketServer;

    /**
     * 校验购物车与取餐时段后创建预约订单。
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO dto) {
        if (dto.getSlotId() == null) throw new OrderBusinessException("请选择取餐时段");
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> carts = shoppingCartMapper.list(ShoppingCart.builder().userId(userId).build());
        if (carts == null || carts.isEmpty()) throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        Long stallId = carts.get(0).getStallId();
        if (stallId == null || carts.stream().anyMatch(c -> !stallId.equals(c.getStallId()))) {
            throw new OrderBusinessException("购物车不能跨档口下单");
        }
        PickupSlot slot = pickupSlotMapper.getById(dto.getSlotId());
        validateSlot(slot, stallId);
        if (pickupSlotMapper.occupy(slot.getId()) == 0) {
            throw new OrderBusinessException("该时段已约满或已关闭");
        }

        Stall stall = getStallForSlot(slot);
        User user = userMapper.getById(userId);
        Orders order = Orders.builder()
                .number("HY" + System.currentTimeMillis())
                .status(Orders.PENDING_PAYMENT)
                .userId(userId)
                .canteenId(stall == null ? null : stall.getCanteenId())
                .stallId(stallId)
                .slotId(slot.getId())
                .pickupCode(generatePickupCode())
                .orderTime(LocalDateTime.now())
                .payMethod(dto.getPayMethod() == null ? 1 : dto.getPayMethod())
                .payStatus(Orders.UN_PAID)
                .amount(calculateAmount(carts))
                .remark(dto.getRemark())
                .userName(user == null ? null : user.getName())
                .slotReleased(0)
                .build();
        orderMapper.insert(order);

        List<OrderDetail> details = carts.stream().map(cart -> {
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(cart, detail);
            detail.setOrderId(order.getId());
            return detail;
        }).collect(Collectors.toList());
        orderDetailMapper.insertBatch(details);
        shoppingCartMapper.deleteByUserId(userId);

        return OrderSubmitVO.builder()
                .id(order.getId()).orderNumber(order.getNumber()).orderAmount(order.getAmount())
                .orderTime(order.getOrderTime()).pickupCode(order.getPickupCode())
                .slotId(slot.getId()).slotDate(slot.getSlotDate())
                .startTime(slot.getStartTime()).endTime(slot.getEndTime()).build();
    }

    /**
     * 完成本地演示环境的模拟支付。
     */
    @Override
    @Transactional
    public OrderPaymentVO payment(OrdersPaymentDTO dto) {
        Long userId = BaseContext.getCurrentId();
        Orders db = orderMapper.getByNumber(dto.getOrderNumber());
        if (db == null || !userId.equals(db.getUserId())) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (Orders.PAID.equals(db.getPayStatus()) || Integer.valueOf(20).equals(db.getStatus())) {
            return OrderPaymentVO.builder().packageStr("mock-paid").build();
        }
        Orders update = Orders.builder().id(db.getId()).status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID).paidAt(LocalDateTime.now()).build();
        if (orderMapper.updateStatusIfMatch(update, Orders.PENDING_PAYMENT) == 0) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        paymentCallbackLogMapper.insertIgnore(db.getNumber(), "MOCK-" + db.getNumber());
        return OrderPaymentVO.builder().packageStr("mock-paid").build();
    }

    /**
     * 幂等处理支付成功回调。
     */
    @Override
    @Transactional
    public void paySuccess(String outTradeNo, String transactionId) {
        if (paymentCallbackLogMapper.insertIgnore(outTradeNo, transactionId) == 0) {
            log.info("重复支付回调，忽略：{}", outTradeNo);
            return;
        }
        Orders db = orderMapper.getByNumber(outTradeNo);
        if (db == null) return;
        Orders update = Orders.builder().id(db.getId()).status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID).paidAt(LocalDateTime.now()).build();
        if (orderMapper.updateStatusIfMatch(update, Orders.PENDING_PAYMENT) == 0) return;
        sendOrderEvent(db.getId(), "订单已支付：" + outTradeNo);
    }

    /**
     * 分页查询当前学生的历史订单。
     */
    @Override
    public PageResult pageQueryUser(int page, int pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        OrdersPageQueryDTO dto = new OrdersPageQueryDTO();
        dto.setUserId(BaseContext.getCurrentId());
        dto.setStatus(status);
        Page<Orders> pageOrders = orderMapper.pageQuery(dto);
        List<OrderVO> list = new ArrayList<>();
        for (Orders order : pageOrders) {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            List<OrderDetail> details = orderDetailMapper.getByOrderId(order.getId());
            vo.setOrderDetailList(details);
            vo.setOrderDishes(details.stream()
                    .map(d -> d.getName() + " × " + d.getNumber())
                    .collect(Collectors.joining("、")));
            list.add(vo);
        }
        return new PageResult(pageOrders.getTotal(), list);
    }

    /**
     * 查询当前学生拥有的订单详情。
     */
    @Override
    public OrderVO orderDetail(Long id) {
        Orders order = orderMapper.getById(id);
        if (order == null || !java.util.Objects.equals(BaseContext.getCurrentId(), order.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        return buildOrderVO(order);
    }

    /**
     * 查询管理端订单详情。
     */
    @Override
    public OrderVO orderDetailForAdmin(Long id) {
        Orders order = requireOrder(id);
        return buildOrderVO(order);
    }

    /**
     * 组装包含订单明细的订单视图。
     */
    private OrderVO buildOrderVO(Orders order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setOrderDetailList(orderDetailMapper.getByOrderId(order.getId()));
        return vo;
    }

    /**
     * 取消当前学生尚未取餐的订单。
     */
    @Override
    @Transactional
    public void userCancelById(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders db = orderMapper.getById(id);
        if (db == null || !userId.equals(db.getUserId())) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (!List.of(Orders.PENDING_PAYMENT, Orders.TO_BE_CONFIRMED).contains(db.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        OrderStatus.checkTransfer(db.getStatus(), db.getStatus().equals(Orders.PENDING_PAYMENT) ? Orders.CANCELLED : Orders.REFUND);
        closeOrder(db, db.getStatus().equals(Orders.PENDING_PAYMENT) ? Orders.CANCELLED : Orders.REFUND, "用户取消");
    }

    /**
     * 将历史订单商品重新加入购物车。
     */
    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders order = orderMapper.getById(id);
        if (order == null || !userId.equals(order.getUserId())) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        List<OrderDetail> details = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> carts = details.stream().map(detail -> {
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(detail, cart, "id");
            cart.setUserId(userId);
            cart.setStallId(order.getStallId());
            cart.setCreateTime(LocalDateTime.now());
            return cart;
        }).collect(Collectors.toList());
        if (!carts.isEmpty()) shoppingCartMapper.insertBatch(carts);
    }

    /**
     * 按条件分页查询订单。
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(dto);
        List<OrderVO> orderVOList = page.getResult().stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setOrderDishes(orderDetailMapper.getByOrderId(order.getId()).stream()
                    .map(d -> d.getName() + " × " + d.getNumber()).collect(Collectors.joining("、")));
            return vo;
        }).collect(Collectors.toList());
        return new PageResult(page.getTotal(), orderVOList);
    }

    /**
     * 统计待接单、制作中和待取餐订单数量。
     */
    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO vo = new OrderStatisticsVO();
        vo.setToBeConfirmed(orderMapper.countStatus(Orders.TO_BE_CONFIRMED));
        vo.setMaking(orderMapper.countStatus(Orders.MAKING));
        vo.setReadyForPickup(orderMapper.countStatus(Orders.READY_FOR_PICKUP));
        return vo;
    }

    /**
     * 将订单从待接单迁移到制作中。
     */
    @Override
    public void confirm(OrdersConfirmDTO dto) {
        transition(dto.getId(), Orders.TO_BE_CONFIRMED, Orders.MAKING, null);
    }

    /**
     * 拒绝订单并释放占用的时段库存。
     */
    @Override
    @Transactional
    public void rejection(OrdersRejectionDTO dto) {
        Orders db = requireOrder(dto.getId());
        OrderStatus.checkTransfer(db.getStatus(), Orders.REFUND);
        closeOrder(db, Orders.REFUND, dto.getRejectionReason());
    }

    /**
     * 管理端取消订单并按规则处理退款。
     */
    @Override
    @Transactional
    public void cancel(OrdersCancelDTO dto) {
        Orders db = requireOrder(dto.getId());
        Integer target = Orders.PAID.equals(db.getPayStatus()) ? Orders.REFUND : Orders.CANCELLED;
        OrderStatus.checkTransfer(db.getStatus(), target);
        closeOrder(db, target, dto.getCancelReason());
    }

    /**
     * 将制作中订单迁移为待取餐。
     */
    @Override
    public void ready(Long id) {
        transition(id, Orders.MAKING, Orders.READY_FOR_PICKUP, LocalDateTime.now());
    }

    /**
     * 校验并核销待取餐订单。
     */
    @Override
    @Transactional
    public PickupVerifyVO verifyPickup(PickupVerifyDTO dto) {
        Orders db = orderMapper.getByPickupCode(dto.getPickupCode());
        if (db == null) throw new OrderBusinessException("取餐码不存在");
        if (dto.getStallId() != null && !dto.getStallId().equals(db.getStallId())) {
            throw new OrderBusinessException("订单不属于当前档口");
        }
        if (Orders.COMPLETED.equals(db.getStatus())) {
            return new PickupVerifyVO(db.getId(), db.getNumber(), db.getPickupCode(), db.getStatus(), "已核销");
        }
        if (!Orders.READY_FOR_PICKUP.equals(db.getStatus())) {
            throw new OrderBusinessException("订单尚未进入待取餐状态");
        }
        Orders update = Orders.builder().id(db.getId()).status(Orders.COMPLETED).pickedAt(LocalDateTime.now()).build();
        if (orderMapper.updateStatusIfMatch(update, Orders.READY_FOR_PICKUP) == 0) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        return new PickupVerifyVO(db.getId(), db.getNumber(), db.getPickupCode(), Orders.COMPLETED, "核销成功");
    }

    /**
     * 关闭超时订单并释放时段库存。
     */
    @Override
    @Transactional
    public void cancelTimeout(Long id) {
        Orders db = requireOrder(id);
        if (!Orders.PENDING_PAYMENT.equals(db.getStatus()) && !Orders.READY_FOR_PICKUP.equals(db.getStatus())) return;
        closeOrder(db, Orders.CANCELLED,
                Orders.PENDING_PAYMENT.equals(db.getStatus()) ? "支付超时，自动取消" : "超时未取餐，自动关闭");
    }

    /**
     * 发送取餐提醒事件。
     */
    @Override
    public void reminder(Long id) {
        Orders order = requireOrder(id);
        sendOrderEvent(id, "取餐提醒：" + order.getNumber());
    }

    /**
     * 按状态机规则执行订单状态迁移。
     */
    private void transition(Long id, Integer expected, Integer target, LocalDateTime stamp) {
        Orders db = requireOrder(id);
        OrderStatus.checkTransfer(db.getStatus(), target);
        Orders update = Orders.builder().id(id).status(target).build();
        if (target.equals(Orders.READY_FOR_PICKUP)) update.setReadyAt(stamp);
        if (orderMapper.updateStatusIfMatch(update, expected) == 0) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    /**
     * 查询订单，不存在时抛出业务异常。
     */
    private Orders requireOrder(Long id) {
        Orders order = orderMapper.getById(id);
        if (order == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        return order;
    }

    /**
     * 关闭订单并保证时段库存只释放一次。
     */
    private void closeOrder(Orders db, Integer target, String reason) {
        Orders update = Orders.builder().id(db.getId()).status(target)
                .cancelReason(reason).cancelTime(LocalDateTime.now()).slotReleased(1).build();
        if (target.equals(Orders.REFUND)) update.setPayStatus(Orders.REFUNDED);
        if (orderMapper.updateStatusIfMatch(update, db.getStatus()) == 0) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (db.getSlotId() != null && !Integer.valueOf(1).equals(db.getSlotReleased())) {
            pickupSlotMapper.release(db.getSlotId());
        }
    }

    /**
     * 校验取餐时段是否属于当前档口且可用。
     */
    private void validateSlot(PickupSlot slot, Long stallId) {
        if (slot == null || !stallId.equals(slot.getStallId()) || !Integer.valueOf(1).equals(slot.getStatus())) {
            throw new OrderBusinessException("取餐时段不可用");
        }
        LocalDate today = LocalDate.now();
        if (slot.getSlotDate().isBefore(today)
                || (slot.getSlotDate().equals(today) && slot.getEndTime().isBefore(LocalTime.now()))) {
            throw new OrderBusinessException("该取餐时段已过期");
        }
        if (slot.getBooked() >= slot.getCapacity()) throw new OrderBusinessException("该时段已约满");
    }

    /**
     * 查询取餐时段所属档口。
     */
    private Stall getStallForSlot(PickupSlot slot) {
        Stall stall = stallMapper.getById(slot.getStallId());
        if (stall == null) throw new OrderBusinessException("档口不存在");
        return stall;
    }

    /**
     * 计算购物车商品总金额。
     */
    private java.math.BigDecimal calculateAmount(List<ShoppingCart> carts) {
        return carts.stream().map(c -> c.getAmount().multiply(java.math.BigDecimal.valueOf(c.getNumber())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * 生成未被占用的取餐码。
     */
    private String generatePickupCode() {
        for (int i = 0; i < 10; i++) {
            String code = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
            if (orderMapper.getByPickupCode(code) == null) return code;
        }
        throw new OrderBusinessException("取餐码生成失败，请重试");
    }

    /**
     * 通过 WebSocket 推送订单事件。
     */
    private void sendOrderEvent(Long orderId, String content) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", orderId);
        map.put("content", content);
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
