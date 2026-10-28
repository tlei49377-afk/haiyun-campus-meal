package com.haiyun.meal.service.impl;

import com.haiyun.meal.dto.StallDTO;
import com.haiyun.meal.entity.PickupSlot;
import com.haiyun.meal.entity.Stall;
import com.haiyun.meal.exception.BaseException;
import com.haiyun.meal.mapper.CanteenMapper;
import com.haiyun.meal.mapper.PickupSlotMapper;
import com.haiyun.meal.mapper.StallMapper;
import com.haiyun.meal.service.StallService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 档口业务实现。
 */
@Service
public class StallServiceImpl implements StallService {
    @Autowired
    private StallMapper stallMapper;
    @Autowired
    private CanteenMapper canteenMapper;
    @Autowired
    private PickupSlotMapper pickupSlotMapper;

    /**
     * 查询档口列表。
     */
    @Override
    public List<Stall> list(Long canteenId, Integer status) {
        return canteenId == null ? stallMapper.list(status) : stallMapper.listByCanteenId(canteenId, status);
    }

    /**
     * 根据主键查询档口。
     */
    @Override
    public Stall getById(Long id) { return stallMapper.getById(id); }

    /**
     * 新增档口。
     */
    @Override
    @Transactional
    public void save(StallDTO dto) {
        if (!StringUtils.hasText(dto.getName())) throw new BaseException("档口名称不能为空");
        if (dto.getCanteenId() == null || canteenMapper.getById(dto.getCanteenId()) == null) {
            throw new BaseException("所属食堂不存在");
        }
        Stall stall = new Stall();
        BeanUtils.copyProperties(dto, stall);
        if (stall.getStatus() == null) stall.setStatus(1);
        if (stall.getSort() == null) stall.setSort(0);
        stallMapper.insert(stall);
        createDefaultSlots(stall.getId());
    }

    /**
     * 修改档口。
     */
    @Override
    public void update(StallDTO dto) {
        if (dto.getCanteenId() != null && canteenMapper.getById(dto.getCanteenId()) == null) {
            throw new BaseException("所属食堂不存在");
        }
        Stall stall = new Stall();
        BeanUtils.copyProperties(dto, stall);
        stallMapper.update(stall);
    }

    /**
     * 修改档口启停状态。
     */
    @Override
    public void changeStatus(Long id, Integer status) {
        Stall stall = Stall.builder().id(id).status(status).build();
        stallMapper.update(stall);
    }

    /** 新档口自动创建未来三天的午餐、晚餐默认时段，后续可在时段页修改或删除。 */
    private void createDefaultSlots(Long stallId) {
        LocalTime[][] periods = {
                {LocalTime.of(11, 0), LocalTime.of(11, 30)},
                {LocalTime.of(11, 30), LocalTime.of(12, 0)},
                {LocalTime.of(17, 30), LocalTime.of(18, 0)},
                {LocalTime.of(18, 0), LocalTime.of(18, 30)}
        };
        for (int day = 0; day < 3; day++) {
            LocalDate date = LocalDate.now().plusDays(day);
            for (LocalTime[] period : periods) {
                if (pickupSlotMapper.getByUniqueKey(stallId, date, period[0]) == null) {
                    pickupSlotMapper.insert(PickupSlot.builder()
                            .stallId(stallId)
                            .slotDate(date)
                            .startTime(period[0])
                            .endTime(period[1])
                            .capacity(50)
                            .booked(0)
                            .status(1)
                            .version(0)
                            .build());
                }
            }
        }
    }
}
