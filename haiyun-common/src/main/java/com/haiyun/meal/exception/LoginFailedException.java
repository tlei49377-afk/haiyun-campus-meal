package com.haiyun.meal.exception;

/**
 * 登录失败
 */
public class LoginFailedException extends BaseException{
    /**
     * 使用指定消息创建LoginFailedException。
     */
    public LoginFailedException(String msg){
        super(msg);
    }
}
