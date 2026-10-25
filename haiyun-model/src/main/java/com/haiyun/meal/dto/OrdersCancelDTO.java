package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单取消请求数据。
 */
@Data
public class OrdersCancelDTO implements Serializable {

    private Long id;
    //订单取消原因
    private String cancelReason;

}
