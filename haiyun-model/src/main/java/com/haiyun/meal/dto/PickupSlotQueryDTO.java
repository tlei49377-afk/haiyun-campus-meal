package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class PickupSlotQueryDTO implements Serializable {
    private Long stallId;
    private LocalDate slotDate;
    private Integer status;
}
