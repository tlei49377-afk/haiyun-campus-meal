package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

@Data
public class CanteenDTO implements Serializable {
    private Long id;
    private String name;
    private String address;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer status;
    private Integer sort;
}
