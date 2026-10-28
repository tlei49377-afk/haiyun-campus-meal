package com.haiyun.meal.exception;

/**
 * 账号被锁定异常
 */
public class AccountLockedException extends BaseException {

    /**
     * 创建AccountLockedException。
     */
    public AccountLockedException() {
    }

    /**
     * 使用指定消息创建AccountLockedException。
     */
    public AccountLockedException(String msg) {
        super(msg);
    }

}
