package com.haiyun.meal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** 校园食堂。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Canteen implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String address;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    /** 1 启用，0 停用。 */
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
