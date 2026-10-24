package com.haiyun.meal.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupVerifyVO implements Serializable {
    private Long orderId;
    private String orderNumber;
    private String pickupCode;
    private Integer status;
    private String message;
}
