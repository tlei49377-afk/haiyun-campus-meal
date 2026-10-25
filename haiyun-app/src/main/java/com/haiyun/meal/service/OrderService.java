package com.haiyun.meal.service;

import com.haiyun.meal.dto.*;
import com.haiyun.meal.result.PageResult;
import com.haiyun.meal.vo.OrderPaymentVO;
import com.haiyun.meal.vo.OrderStatisticsVO;
import com.haiyun.meal.vo.OrderSubmitVO;
import com.haiyun.meal.vo.OrderVO;
import com.haiyun.meal.vo.PickupVerifyVO;

/**
 * 订单业务接口。
 */
public interface OrderService {
    /**
     * 提交预约取餐订单。
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO dto);
    /**
     * 完成模拟支付。
     */
    OrderPaymentVO payment(OrdersPaymentDTO dto) throws Exception;
    /**
     * 处理支付成功回调。
     */
    void paySuccess(String outTradeNo, String transactionId);
    /**
     * 分页查询当前学生的历史订单。
     */
    PageResult pageQueryUser(int page, int pageSize, Integer status);
    /**
     * 查询当前学生的订单详情。
     */
    OrderVO orderDetail(Long id);
    /**
     * 查询管理端订单详情。
     */
    OrderVO orderDetailForAdmin(Long id);
    /**
     * 学生取消订单。
     */
    void userCancelById(Long id) throws Exception;
    /**
     * 将历史订单商品重新加入购物车。
     */
    void repetition(Long id);
    /**
     * 按条件分页查询订单。
     */
    PageResult conditionSearch(OrdersPageQueryDTO dto);
    /**
     * 统计各处理状态的订单数量。
     */
    OrderStatisticsVO statistics();
    /**
     * 确认接单。
     */
    void confirm(OrdersConfirmDTO dto);
    /**
     * 拒绝订单。
     */
    void rejection(OrdersRejectionDTO dto);
    /**
     * 管理端取消订单。
     */
    void cancel(OrdersCancelDTO dto);
    /**
     * 将制作完成的订单标记为待取餐。
     */
    void ready(Long id);
    /**
     * 核销取餐码。
     */
    PickupVerifyVO verifyPickup(PickupVerifyDTO dto);
    /**
     * 关闭超时订单并释放时段库存。
     */
    void cancelTimeout(Long id);
    /**
     * 发送取餐提醒。
     */
    void reminder(Long id);
}
