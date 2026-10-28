package com.haiyun.meal.exception;

/**
 * 账号不存在异常
 */
public class AccountNotFoundException extends BaseException {

    /**
     * 创建AccountNotFoundException。
     */
    public AccountNotFoundException() {
    }

    /**
     * 使用指定消息创建AccountNotFoundException。
     */
    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
