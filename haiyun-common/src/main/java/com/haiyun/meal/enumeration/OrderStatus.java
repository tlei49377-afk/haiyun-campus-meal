package com.haiyun.meal.enumeration;

import com.haiyun.meal.constant.MessageConstant;
import com.haiyun.meal.exception.OrderBusinessException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 预约取餐订单状态机。
 */
public enum OrderStatus {
    /**
     * 待支付。
     */
    PENDING_PAYMENT(10, "待支付"),
    /**
     * 待接单。
     */
    TO_BE_CONFIRMED(20, "待接单"),
    /**
     * 制作中。
     */
    MAKING(30, "制作中"),
    /**
     * 待取餐。
     */
    READY_FOR_PICKUP(40, "待取餐"),
    /**
     * 已取餐。
     */
    COMPLETED(50, "已取餐"),
    /**
     * 已取消。
     */
    CANCELLED(60, "已取消"),
    /**
     * 已退款。
     */
    REFUND(70, "已退款");

    /**
     * 状态码。
     */
    private final Integer code;
    /**
     * 状态说明。
     */
    private final String desc;

    /**
     * 创建订单状态枚举项。
     */
    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 允许的订单状态迁移关系。
     */
    private static final Map<Integer, Set<Integer>> TRANSITIONS;
    static {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        map.put(PENDING_PAYMENT.code, Set.of(TO_BE_CONFIRMED.code, CANCELLED.code));
        map.put(TO_BE_CONFIRMED.code, Set.of(MAKING.code, CANCELLED.code, REFUND.code));
        map.put(MAKING.code, Set.of(READY_FOR_PICKUP.code, CANCELLED.code, REFUND.code));
        map.put(READY_FOR_PICKUP.code, Set.of(COMPLETED.code, CANCELLED.code, REFUND.code));
        map.put(COMPLETED.code, Collections.emptySet());
        map.put(CANCELLED.code, Collections.emptySet());
        map.put(REFUND.code, Collections.emptySet());
        TRANSITIONS = Collections.unmodifiableMap(map);
    }

    /**
     * 获取订单状态码。
     */
    public Integer getCode() { return code; }
    /**
     * 获取订单状态说明。
     */
    public String getDesc() { return desc; }

    /**
     * 判断订单状态是否允许迁移。
     */
    public static boolean canTransfer(Integer from, Integer to) {
        if (from == null || to == null) return false;
        Set<Integer> allowed = TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    /**
     * 校验订单状态迁移是否合法。
     */
    public static void checkTransfer(Integer from, Integer to) {
        if (!canTransfer(from, to)) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
}
