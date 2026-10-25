package com.haiyun.meal.task;

import com.haiyun.meal.entity.Orders;
import com.haiyun.meal.mapper.OrderMapper;
import com.haiyun.meal.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/** 预约订单超时处理。 */
@Component
@Slf4j
public class OrderTask {
    private static final int BATCH_SIZE = 100;
    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderService orderService;

    /**
     * 扫描并关闭待支付超时订单。
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrders() {
        cancelBatch(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(15), "待支付超时");
    }

    /**
     * 扫描并关闭待取餐超时订单。
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void processPickupTimeoutOrders() {
        cancelBatch(Orders.READY_FOR_PICKUP, LocalDateTime.now().minusHours(2), "待取餐超时");
    }

    /**
     * 按批次处理指定状态的超时订单。
     */
    private void cancelBatch(Integer status, LocalDateTime deadline, String label) {
        int handled = 0;
        while (true) {
            List<Orders> orders = orderMapper.getByStatusAudOrderTimeLT(status, deadline, BATCH_SIZE);
            if (orders == null || orders.isEmpty()) break;
            for (Orders order : orders) {
                try {
                    orderService.cancelTimeout(order.getId());
                    handled++;
                } catch (Exception e) {
                    log.warn("{}订单 {} 取消失败：{}", label, order.getId(), e.getMessage());
                }
            }
            if (orders.size() < BATCH_SIZE) break;
        }
        log.info("{}处理完成，关闭 {} 笔", label, handled);
    }
}
