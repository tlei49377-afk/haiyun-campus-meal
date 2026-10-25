package com.haiyun.meal.service.impl;

import com.haiyun.meal.dto.PickupSlotGenerateDTO;
import com.haiyun.meal.dto.PickupSlotQueryDTO;
import com.haiyun.meal.dto.PickupSlotUpdateDTO;
import com.haiyun.meal.entity.PickupSlot;
import com.haiyun.meal.exception.BaseException;
import com.haiyun.meal.mapper.PickupSlotMapper;
import com.haiyun.meal.mapper.StallMapper;
import com.haiyun.meal.service.PickupSlotService;
import com.haiyun.meal.vo.PickupSlotVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PickupSlotServiceImpl implements PickupSlotService {
    @Autowired
    private PickupSlotMapper pickupSlotMapper;
    @Autowired
    private StallMapper stallMapper;

    @Override
    public List<PickupSlotVO> list(PickupSlotQueryDTO query) {
        if (query.getSlotDate() == null) query.setSlotDate(LocalDate.now());
        List<PickupSlotVO> result = new ArrayList<>();
        for (PickupSlot slot : pickupSlotMapper.list(query)) {
            PickupSlotVO vo = new PickupSlotVO();
            BeanUtils.copyProperties(slot, vo);
            vo.setRemaining(Math.max(0, slot.getCapacity() - slot.getBooked()));
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional
    public int generate(PickupSlotGenerateDTO dto) {
        if (dto.getStallId() == null || stallMapper.getById(dto.getStallId()) == null) {
            throw new BaseException("档口不存在");
        }
        if (dto.getSlotDate() == null || dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new BaseException("日期和时段不能为空");
        }
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new BaseException("开始时间必须早于结束时间");
        }
        int interval = dto.getIntervalMinutes() == null ? 30 : dto.getIntervalMinutes();
        int capacity = dto.getCapacity() == null ? 50 : dto.getCapacity();
        if (interval < 5 || capacity < 1) throw new BaseException("间隔或容量不合法");

        int created = 0;
        LocalTime cursor = dto.getStartTime();
        while (cursor.isBefore(dto.getEndTime())) {
            LocalTime end = cursor.plusMinutes(interval);
            if (end.isAfter(dto.getEndTime())) break;
            if (pickupSlotMapper.getByUniqueKey(dto.getStallId(), dto.getSlotDate(), cursor) == null) {
                PickupSlot slot = PickupSlot.builder()
                        .stallId(dto.getStallId())
                        .slotDate(dto.getSlotDate())
                        .startTime(cursor)
                        .endTime(end)
                        .capacity(capacity)
                        .booked(0)
                        .status(1)
                        .version(0)
                        .build();
                pickupSlotMapper.insert(slot);
                created++;
            }
            cursor = end;
        }
        return created;
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) throw new BaseException("状态不合法");
        pickupSlotMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void update(PickupSlotUpdateDTO dto) {
        if (dto.getId() == null) throw new BaseException("时段id不能为空");
        PickupSlot current = pickupSlotMapper.getById(dto.getId());
        if (current == null) throw new BaseException("取餐时段不存在");
        if (dto.getStartTime() == null || dto.getEndTime() == null || !dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new BaseException("开始时间必须早于结束时间");
        }
        if (dto.getCapacity() == null || dto.getCapacity() < current.getBooked()) {
            throw new BaseException("容量不能小于已预约人数");
        }
        if (dto.getStatus() != null && dto.getStatus() != 0 && dto.getStatus() != 1) {
            throw new BaseException("状态不合法");
        }
        PickupSlot collision = pickupSlotMapper.getByUniqueKey(current.getStallId(), current.getSlotDate(), dto.getStartTime());
        if (collision != null && !collision.getId().equals(current.getId())) {
            throw new BaseException("该开始时间已存在时段");
        }
        PickupSlot update = PickupSlot.builder()
                .id(dto.getId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .capacity(dto.getCapacity())
                .status(dto.getStatus() == null ? current.getStatus() : dto.getStatus())
                .build();
        pickupSlotMapper.update(update);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PickupSlot slot = pickupSlotMapper.getById(id);
        if (slot == null) throw new BaseException("取餐时段不存在");
        if (slot.getBooked() != null && slot.getBooked() > 0) {
            throw new BaseException("该时段已有预约，不能删除，请改为停用");
        }
        if (pickupSlotMapper.deleteIfUnbooked(id) == 0) {
            throw new BaseException("时段删除失败，请刷新后重试");
        }
    }
}
