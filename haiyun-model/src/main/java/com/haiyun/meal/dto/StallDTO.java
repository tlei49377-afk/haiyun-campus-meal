package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StallDTO implements Serializable {
    private Long id;
    private Long canteenId;
    private String name;
    private String description;
    private String location;
    private Integer status;
    private Integer sort;
}
