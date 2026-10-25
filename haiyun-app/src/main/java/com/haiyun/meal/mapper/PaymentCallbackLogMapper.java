package com.haiyun.meal.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 支付回调流水数据访问接口。
 */
@Mapper
public interface PaymentCallbackLogMapper {

    /**
     * 记录支付回调流水，重复的回调返回 0
     * @param outTradeNo
     * @param transactionId
     * @return
     */
    int insertIgnore(String outTradeNo, String transactionId);
}
