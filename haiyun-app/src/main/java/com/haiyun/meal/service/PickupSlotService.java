package com.haiyun.meal.service;

import com.haiyun.meal.dto.PickupSlotGenerateDTO;
import com.haiyun.meal.dto.PickupSlotQueryDTO;
import com.haiyun.meal.dto.PickupSlotUpdateDTO;
import com.haiyun.meal.vo.PickupSlotVO;

import java.util.List;

/**
 * 取餐时段业务接口。
 */
public interface PickupSlotService {
    /**
     * 查询取餐时段列表。
     */
    List<PickupSlotVO> list(PickupSlotQueryDTO query);
    /**
     * 批量生成取餐时段。
     */
    int generate(PickupSlotGenerateDTO dto);
    /**
     * 修改取餐时段启停状态。
     */
    void changeStatus(Long id, Integer status);
    /**
     * 修改取餐时段信息。
     */
    void update(PickupSlotUpdateDTO dto);
    /**
     * 删除尚未被预约的取餐时段。
     */
    void delete(Long id);
}
