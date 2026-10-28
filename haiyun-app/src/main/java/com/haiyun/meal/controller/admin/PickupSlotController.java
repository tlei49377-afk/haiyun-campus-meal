package com.haiyun.meal.controller.admin;

import com.haiyun.meal.annotation.RequirePermission;
import com.haiyun.meal.dto.PickupSlotGenerateDTO;
import com.haiyun.meal.dto.PickupSlotQueryDTO;
import com.haiyun.meal.dto.PickupSlotUpdateDTO;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.PickupSlotService;
import com.haiyun.meal.vo.PickupSlotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端取餐时段管理接口。
 */
@RestController
@RequestMapping("/api/admin/slot")
public class PickupSlotController {
    @Autowired
    private PickupSlotService pickupSlotService;

    /**
     * 查询取餐时段列表。
     */
    @RequirePermission("slot:query")
    @GetMapping("/list")
    public Result<List<PickupSlotVO>> list(PickupSlotQueryDTO query) { return Result.success(pickupSlotService.list(query)); }

    /**
     * 批量生成取餐时段。
     */
    @RequirePermission("slot:add")
    @PostMapping("/generate")
    public Result<Integer> generate(@RequestBody PickupSlotGenerateDTO dto) { return Result.success(pickupSlotService.generate(dto)); }

    /**
     * 修改取餐时段启停状态。
     */
    @RequirePermission("slot:update")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status, Long id) { pickupSlotService.changeStatus(id, status); return Result.success(); }

    /**
     * 修改取餐时段信息。
     */
    @RequirePermission("slot:update")
    @PutMapping
    public Result update(@RequestBody PickupSlotUpdateDTO dto) { pickupSlotService.update(dto); return Result.success(); }

    /**
     * 删除取餐时段。
     */
    @RequirePermission("slot:update")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) { pickupSlotService.delete(id); return Result.success(); }
}
