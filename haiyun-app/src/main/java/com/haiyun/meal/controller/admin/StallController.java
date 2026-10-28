package com.haiyun.meal.controller.admin;

import com.haiyun.meal.annotation.RequirePermission;
import com.haiyun.meal.dto.StallDTO;
import com.haiyun.meal.entity.Stall;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.StallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端档口管理接口。
 */
@RestController
@RequestMapping("/api/admin/stall")
public class StallController {
    @Autowired
    private StallService stallService;

    /**
     * 查询档口列表。
     */
    @RequirePermission("stall:query")
    @GetMapping("/list")
    public Result<List<Stall>> list(Long canteenId, Integer status) { return Result.success(stallService.list(canteenId, status)); }

    /**
     * 查询档口详情。
     */
    @RequirePermission("stall:query")
    @GetMapping("/{id}")
    public Result<Stall> get(@PathVariable Long id) { return Result.success(stallService.getById(id)); }

    /**
     * 新增档口。
     */
    @RequirePermission("stall:add")
    @PostMapping
    public Result save(@RequestBody StallDTO dto) { stallService.save(dto); return Result.success(); }

    /**
     * 修改档口。
     */
    @RequirePermission("stall:update")
    @PutMapping
    public Result update(@RequestBody StallDTO dto) { stallService.update(dto); return Result.success(); }

    /**
     * 修改档口启停状态。
     */
    @RequirePermission("stall:update")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status, Long id) { stallService.changeStatus(id, status); return Result.success(); }
}
