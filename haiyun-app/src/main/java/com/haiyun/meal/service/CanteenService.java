package com.haiyun.meal.service;

import com.haiyun.meal.dto.CanteenDTO;
import com.haiyun.meal.entity.Canteen;

import java.util.List;

/**
 * 食堂业务接口。
 */
public interface CanteenService {
    /**
     * 查询食堂列表。
     */
    List<Canteen> list(Integer status);
    /**
     * 根据主键查询食堂。
     */
    Canteen getById(Long id);
    /**
     * 新增食堂。
     */
    void save(CanteenDTO dto);
    /**
     * 修改食堂。
     */
    void update(CanteenDTO dto);
    /**
     * 修改食堂启停状态。
     */
    void changeStatus(Long id, Integer status);
}
