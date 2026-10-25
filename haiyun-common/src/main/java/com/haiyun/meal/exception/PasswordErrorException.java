package com.haiyun.meal.exception;

/**
 * 密码错误异常
 */
public class PasswordErrorException extends BaseException {

    /**
     * 创建PasswordErrorException。
     */
    public PasswordErrorException() {
    }

    /**
     * 使用指定消息创建PasswordErrorException。
     */
    public PasswordErrorException(String msg) {
        super(msg);
    }

}
