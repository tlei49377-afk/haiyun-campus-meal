package com.haiyun.meal.service;

import com.haiyun.meal.dto.StallDTO;
import com.haiyun.meal.entity.Stall;

import java.util.List;

/**
 * 档口业务接口。
 */
public interface StallService {
    /**
     * 查询档口列表。
     */
    List<Stall> list(Long canteenId, Integer status);
    /**
     * 根据主键查询档口。
     */
    Stall getById(Long id);
    /**
     * 新增档口。
     */
    void save(StallDTO dto);
    /**
     * 修改档口。
     */
    void update(StallDTO dto);
    /**
     * 修改档口启停状态。
     */
    void changeStatus(Long id, Integer status);
}
