package com.haiyun.meal.exception;

/**
 * 套餐启用失败异常
 */
public class SetmealEnableFailedException extends BaseException {

    /**
     * 创建SetmealEnableFailedException。
     */
    public SetmealEnableFailedException(){}

    /**
     * 使用指定消息创建SetmealEnableFailedException。
     */
    public SetmealEnableFailedException(String msg){
        super(msg);
    }
}
