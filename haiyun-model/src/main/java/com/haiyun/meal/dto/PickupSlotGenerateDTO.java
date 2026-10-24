package com.haiyun.meal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PickupSlotGenerateDTO implements Serializable {
    private Long stallId;
    private LocalDate slotDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private Integer intervalMinutes;
    private Integer capacity;
}
