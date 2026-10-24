package com.haiyun.meal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/** 某个档口某天的取餐时段库存。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long stallId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate slotDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private Integer capacity;
    private Integer booked;
    /** 1 启用，0 停用。 */
    private Integer status;
    private Integer version;
    private java.time.LocalDateTime createTime;
    private java.time.LocalDateTime updateTime;
}
