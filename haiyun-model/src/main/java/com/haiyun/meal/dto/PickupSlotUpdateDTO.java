package com.haiyun.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * 取餐时段修改请求数据。
 */
@Data
public class PickupSlotUpdateDTO implements Serializable {
    private Long id;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private Integer capacity;
    private Integer status;
}
