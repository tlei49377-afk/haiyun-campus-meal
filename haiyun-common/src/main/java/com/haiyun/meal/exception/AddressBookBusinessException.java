package com.haiyun.meal.exception;

/**
 * 地址簿业务异常。
 */
public class AddressBookBusinessException extends BaseException {

    /**
     * 使用指定消息创建AddressBookBusinessException。
     */
    public AddressBookBusinessException(String msg) {
        super(msg);
    }

}
