package com.haiyun.meal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 校园食堂预约取餐订单。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {

    /** 10 待支付，20 待接单，30 制作中，40 待取餐，50 已取餐，60 已取消，70 已退款。 */
    public static final Integer PENDING_PAYMENT = 10;
    public static final Integer TO_BE_CONFIRMED = 20;
    public static final Integer MAKING = 30;
    public static final Integer CONFIRMED = MAKING;
    public static final Integer READY_FOR_PICKUP = 40;
    public static final Integer DELIVERY_IN_PROGRESS = READY_FOR_PICKUP;
    public static final Integer COMPLETED = 50;
    public static final Integer CANCELLED = 60;
    public static final Integer REFUND = 70;

    /** 支付状态 0 未支付，1 已支付，2 已退款。 */
    public static final Integer UN_PAID = 0;
    public static final Integer PAID = 1;
    public static final Integer REFUNDED = 2;

    private static final long serialVersionUID = 1L;

    private Long id;
    /** 业务订单号。 */
    private String number;
    private Integer status;
    private Long userId;
    private Long canteenId;
    private Long stallId;
    private Long slotId;
    private String pickupCode;
    private LocalDateTime orderTime;
    private LocalDateTime paidAt;
    private Integer payMethod;
    private Integer payStatus;
    private BigDecimal amount;
    private String remark;
    private String userName;
    private String cancelReason;
    private String rejectionReason;
    private LocalDateTime cancelTime;
    private LocalDateTime readyAt;
    private LocalDateTime pickedAt;
    /** 0 未释放，1 已释放。用于保证库存只释放一次。 */
    private Integer slotReleased;
}
