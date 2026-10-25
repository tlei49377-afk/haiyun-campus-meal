package com.haiyun.meal.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderStatisticsVO implements Serializable {
    //待接单数量
    private Integer toBeConfirmed;

    //制作中数量
    private Integer making;

    //待取餐数量
    private Integer readyForPickup;
}
