package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PickupVerifyDTO implements Serializable {
    private String pickupCode;
    private Long stallId;
}
