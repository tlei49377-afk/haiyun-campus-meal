package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 取餐核销请求数据。
 */
@Data
public class PickupVerifyDTO implements Serializable {
    private String pickupCode;
    private Long stallId;
}
