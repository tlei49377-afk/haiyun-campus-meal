package com.haiyun.meal.controller.user;

import com.haiyun.meal.dto.PickupSlotQueryDTO;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.PickupSlotService;
import com.haiyun.meal.vo.PickupSlotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生端取餐时段查询接口。
 */
@RestController("appPickupSlotController")
@RequestMapping("/api/app/slot")
public class PickupSlotController {
    @Autowired
    private PickupSlotService pickupSlotService;

    /**
     * 查询当前可预约的取餐时段。
     */
    @GetMapping("/list")
    public Result<List<PickupSlotVO>> list(PickupSlotQueryDTO query) {
        query.setStatus(1);
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return Result.success(pickupSlotService.list(query).stream()
                .filter(slot -> slot.getSlotDate().isAfter(today)
                        || (slot.getSlotDate().equals(today) && slot.getEndTime().isAfter(now)))
                .collect(Collectors.toList()));
    }
}
