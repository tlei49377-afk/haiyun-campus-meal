package com.haiyun.meal.enumeration;

import com.haiyun.meal.constant.MessageConstant;
import com.haiyun.meal.exception.OrderBusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {
    @Test
    void shouldAllowNormalPickupFlow() {
        assertTrue(OrderStatus.canTransfer(10, 20));
        assertTrue(OrderStatus.canTransfer(20, 30));
        assertTrue(OrderStatus.canTransfer(30, 40));
        assertTrue(OrderStatus.canTransfer(40, 50));
    }

    @Test
    void shouldAllowCancellationBeforePickup() {
        assertTrue(OrderStatus.canTransfer(10, 60));
        assertTrue(OrderStatus.canTransfer(20, 60));
        assertTrue(OrderStatus.canTransfer(30, 60));
        assertTrue(OrderStatus.canTransfer(40, 60));
        assertFalse(OrderStatus.canTransfer(50, 60));
    }

    @Test
    void shouldRejectSkippingAndBackward() {
        assertFalse(OrderStatus.canTransfer(10, 30));
        assertFalse(OrderStatus.canTransfer(10, 50));
        assertFalse(OrderStatus.canTransfer(30, 20));
        assertFalse(OrderStatus.canTransfer(50, 10));
    }

    @Test
    void shouldRejectSameAndTerminalStatus() {
        assertFalse(OrderStatus.canTransfer(10, 10));
        assertFalse(OrderStatus.canTransfer(50, 50));
        assertFalse(OrderStatus.canTransfer(50, 60));
        assertFalse(OrderStatus.canTransfer(60, 10));
        assertFalse(OrderStatus.canTransfer(70, 10));
    }

    @Test
    void shouldRejectUnknownStatus() {
        assertFalse(OrderStatus.canTransfer(99, 10));
        assertFalse(OrderStatus.canTransfer(0, 10));
        assertFalse(OrderStatus.canTransfer(null, 10));
        assertFalse(OrderStatus.canTransfer(10, null));
    }

    @Test
    void checkTransferShouldThrowOnIllegalTransition() {
        assertDoesNotThrow(() -> OrderStatus.checkTransfer(10, 20));
        OrderBusinessException exception = assertThrows(OrderBusinessException.class,
                () -> OrderStatus.checkTransfer(50, 10));
        assertEquals(MessageConstant.ORDER_STATUS_ERROR, exception.getMessage());
    }
}
