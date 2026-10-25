package com.haiyun.meal.service;

import com.haiyun.meal.dto.PickupSlotGenerateDTO;
import com.haiyun.meal.dto.PickupSlotQueryDTO;
import com.haiyun.meal.dto.PickupSlotUpdateDTO;
import com.haiyun.meal.vo.PickupSlotVO;

import java.util.List;

public interface PickupSlotService {
    List<PickupSlotVO> list(PickupSlotQueryDTO query);
    int generate(PickupSlotGenerateDTO dto);
    void changeStatus(Long id, Integer status);
    void update(PickupSlotUpdateDTO dto);
    void delete(Long id);
}
