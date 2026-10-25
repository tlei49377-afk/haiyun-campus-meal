package com.haiyun.meal.vo;

import com.haiyun.meal.entity.PickupSlot;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 取餐时段视图数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PickupSlotVO extends PickupSlot {
    private Integer remaining;
}
