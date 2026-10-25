package com.haiyun.meal.exception;

/**
 * 密码修改失败异常
 */
public class PasswordEditFailedException extends BaseException{

    /**
     * 使用指定消息创建PasswordEditFailedException。
     */
    public PasswordEditFailedException(String msg){
        super(msg);
    }

}
