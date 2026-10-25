package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单确认请求数据。
 */
@Data
public class OrdersConfirmDTO implements Serializable {
    private Long id;
}
