package com.haiyun.meal.exception;

/**
 * 禁止删除异常。
 */
public class DeletionNotAllowedException extends BaseException {

    /**
     * 使用指定消息创建DeletionNotAllowedException。
     */
    public DeletionNotAllowedException(String msg) {
        super(msg);
    }

}
