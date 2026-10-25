package com.haiyun.meal.exception;

/**
 * 用户未登录异常。
 */
public class UserNotLoginException extends BaseException {

    /**
     * 创建UserNotLoginException。
     */
    public UserNotLoginException() {
    }

    /**
     * 使用指定消息创建UserNotLoginException。
     */
    public UserNotLoginException(String msg) {
        super(msg);
    }

}
