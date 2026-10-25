package com.haiyun.meal.exception;

/**
 * 购物车业务异常。
 */
public class ShoppingCartBusinessException extends BaseException {

    /**
     * 使用指定消息创建ShoppingCartBusinessException。
     */
    public ShoppingCartBusinessException(String msg) {
        super(msg);
    }

}
