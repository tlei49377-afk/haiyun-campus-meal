package com.haiyun.meal.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 购物车请求数据。
 */
@Data
public class ShoppingCartDTO implements Serializable {

    private Long dishId;
    private Long setmealId;
    private String dishFlavor;

}
