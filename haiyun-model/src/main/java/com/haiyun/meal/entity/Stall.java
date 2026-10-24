package com.haiyun.meal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 食堂档口。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stall implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long canteenId;
    private String name;
    private String description;
    private String location;
    /** 1 启用，0 停用。 */
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
