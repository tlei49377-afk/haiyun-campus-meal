package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 取餐时段查询请求数据。
 */
@Data
public class PickupSlotQueryDTO implements Serializable {
    private Long stallId;
    private LocalDate slotDate;
    private Integer status;
}
