package com.haiyun.meal.service;

import com.haiyun.meal.dto.*;
import com.haiyun.meal.result.PageResult;
import com.haiyun.meal.vo.OrderPaymentVO;
import com.haiyun.meal.vo.OrderStatisticsVO;
import com.haiyun.meal.vo.OrderSubmitVO;
import com.haiyun.meal.vo.OrderVO;
import com.haiyun.meal.vo.PickupVerifyVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO dto);
    OrderPaymentVO payment(OrdersPaymentDTO dto) throws Exception;
    void paySuccess(String outTradeNo, String transactionId);
    PageResult pageQueryUser(int page, int pageSize, Integer status);
    OrderVO orderDetail(Long id);
    OrderVO orderDetailForAdmin(Long id);
    void userCancelById(Long id) throws Exception;
    void repetition(Long id);
    PageResult conditionSearch(OrdersPageQueryDTO dto);
    OrderStatisticsVO statistics();
    void confirm(OrdersConfirmDTO dto);
    void rejection(OrdersRejectionDTO dto);
    void cancel(OrdersCancelDTO dto);
    void ready(Long id);
    PickupVerifyVO verifyPickup(PickupVerifyDTO dto);
    void cancelTimeout(Long id);
    void reminder(Long id);
}
