package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/** 学生提交预约取餐订单时传递的数据。 */
@Data
public class OrdersSubmitDTO implements Serializable {
    private Long slotId;
    private Integer payMethod;
    private String remark;
    private BigDecimal amount;
}
