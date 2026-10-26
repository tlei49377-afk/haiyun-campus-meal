package com.haiyun.meal.controller.admin;

import com.haiyun.meal.annotation.RequirePermission;
import com.haiyun.meal.dto.CanteenDTO;
import com.haiyun.meal.entity.Canteen;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端食堂管理接口。
 */
@RestController
@RequestMapping("/api/admin/canteen")
public class CanteenController {
    @Autowired
    private CanteenService canteenService;

    /**
     * 查询食堂列表。
     */
    @RequirePermission("canteen:query")
    @GetMapping("/list")
    public Result<List<Canteen>> list(Integer status) { return Result.success(canteenService.list(status)); }

    /**
     * 查询食堂详情。
     */
    @RequirePermission("canteen:query")
    @GetMapping("/{id}")
    public Result<Canteen> get(@PathVariable Long id) { return Result.success(canteenService.getById(id)); }

    /**
     * 新增食堂。
     */
    @RequirePermission("canteen:add")
    @PostMapping
    public Result save(@RequestBody CanteenDTO dto) { canteenService.save(dto); return Result.success(); }

    /**
     * 修改食堂。
     */
    @RequirePermission("canteen:update")
    @PutMapping
    public Result update(@RequestBody CanteenDTO dto) { canteenService.update(dto); return Result.success(); }

    /**
     * 修改食堂启停状态。
     */
    @RequirePermission("canteen:update")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status, Long id) { canteenService.changeStatus(id, status); return Result.success(); }
}
