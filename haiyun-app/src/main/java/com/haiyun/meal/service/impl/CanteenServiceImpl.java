package com.haiyun.meal.service.impl;

import com.haiyun.meal.dto.CanteenDTO;
import com.haiyun.meal.entity.Canteen;
import com.haiyun.meal.exception.BaseException;
import com.haiyun.meal.mapper.CanteenMapper;
import com.haiyun.meal.service.CanteenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 食堂业务实现。
 */
@Service
public class CanteenServiceImpl implements CanteenService {
    @Autowired
    private CanteenMapper canteenMapper;

    /**
     * 查询食堂列表。
     */
    @Override
    public List<Canteen> list(Integer status) { return canteenMapper.list(status); }

    /**
     * 根据主键查询食堂。
     */
    @Override
    public Canteen getById(Long id) { return canteenMapper.getById(id); }

    /**
     * 新增食堂。
     */
    @Override
    public void save(CanteenDTO dto) {
        if (!StringUtils.hasText(dto.getName())) throw new BaseException("食堂名称不能为空");
        Canteen canteen = new Canteen();
        BeanUtils.copyProperties(dto, canteen);
        if (canteen.getStatus() == null) canteen.setStatus(1);
        if (canteen.getSort() == null) canteen.setSort(0);
        canteenMapper.insert(canteen);
    }

    /**
     * 修改食堂。
     */
    @Override
    public void update(CanteenDTO dto) {
        Canteen canteen = new Canteen();
        BeanUtils.copyProperties(dto, canteen);
        canteenMapper.update(canteen);
    }

    /**
     * 修改食堂启停状态。
     */
    @Override
    public void changeStatus(Long id, Integer status) {
        Canteen canteen = Canteen.builder().id(id).status(status).build();
        canteenMapper.update(canteen);
    }
}
