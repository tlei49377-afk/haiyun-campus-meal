package com.haiyun.meal.enumeration;

import com.haiyun.meal.constant.MessageConstant;
import com.haiyun.meal.exception.OrderBusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单状态机测试。
 */
class OrderStatusTest {
    /**
     * 验证正常取餐流程允许逐级迁移。
     */
    @Test
    void shouldAllowNormalPickupFlow() {
        assertTrue(OrderStatus.canTransfer(10, 20));
        assertTrue(OrderStatus.canTransfer(20, 30));
        assertTrue(OrderStatus.canTransfer(30, 40));
        assertTrue(OrderStatus.canTransfer(40, 50));
    }

    /**
     * 验证取餐前各状态允许取消。
     */
    @Test
    void shouldAllowCancellationBeforePickup() {
        assertTrue(OrderStatus.canTransfer(10, 60));
        assertTrue(OrderStatus.canTransfer(20, 60));
        assertTrue(OrderStatus.canTransfer(30, 60));
        assertTrue(OrderStatus.canTransfer(40, 60));
        assertFalse(OrderStatus.canTransfer(50, 60));
    }

    /**
     * 验证不允许跳过状态或回退。
     */
    @Test
    void shouldRejectSkippingAndBackward() {
        assertFalse(OrderStatus.canTransfer(10, 30));
        assertFalse(OrderStatus.canTransfer(10, 50));
        assertFalse(OrderStatus.canTransfer(30, 20));
        assertFalse(OrderStatus.canTransfer(50, 10));
    }

    /**
     * 验证相同状态和终态不可再次迁移。
     */
    @Test
    void shouldRejectSameAndTerminalStatus() {
        assertFalse(OrderStatus.canTransfer(10, 10));
        assertFalse(OrderStatus.canTransfer(50, 50));
        assertFalse(OrderStatus.canTransfer(50, 60));
        assertFalse(OrderStatus.canTransfer(60, 10));
        assertFalse(OrderStatus.canTransfer(70, 10));
    }

    /**
     * 验证未知状态不能迁移。
     */
    @Test
    void shouldRejectUnknownStatus() {
        assertFalse(OrderStatus.canTransfer(99, 10));
        assertFalse(OrderStatus.canTransfer(0, 10));
        assertFalse(OrderStatus.canTransfer(null, 10));
        assertFalse(OrderStatus.canTransfer(10, null));
    }

    /**
     * 验证非法迁移会抛出业务异常。
     */
    @Test
    void checkTransferShouldThrowOnIllegalTransition() {
        assertDoesNotThrow(() -> OrderStatus.checkTransfer(10, 20));
        OrderBusinessException exception = assertThrows(OrderBusinessException.class,
                () -> OrderStatus.checkTransfer(50, 10));
        assertEquals(MessageConstant.ORDER_STATUS_ERROR, exception.getMessage());
    }
}
