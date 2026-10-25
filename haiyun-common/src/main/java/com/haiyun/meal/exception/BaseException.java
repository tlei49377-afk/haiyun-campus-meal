package com.haiyun.meal.exception;

/**
 * 业务异常
 */
public class BaseException extends RuntimeException {

    /**
     * 创建BaseException。
     */
    public BaseException() {
    }

    /**
     * 使用指定消息创建BaseException。
     */
    public BaseException(String msg) {
        super(msg);
    }

}
