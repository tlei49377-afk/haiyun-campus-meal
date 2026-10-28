package com.haiyun.meal.exception;

/**
 * 订单业务异常。
 */
public class OrderBusinessException extends BaseException {

    /**
     * 使用指定消息创建OrderBusinessException。
     */
    public OrderBusinessException(String msg) {
        super(msg);
    }

}
