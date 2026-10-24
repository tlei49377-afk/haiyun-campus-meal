package com.haiyun.meal.controller.admin;

import com.haiyun.meal.annotation.RequirePermission;
import com.haiyun.meal.cache.CacheHelper;
import com.haiyun.meal.constant.CacheConstant;
import com.haiyun.meal.dto.DishDTO;
import com.haiyun.meal.dto.DishPageQueryDTO;
import com.haiyun.meal.entity.Dish;
import com.haiyun.meal.result.PageResult;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.DishService;
import com.haiyun.meal.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/api/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CacheHelper cacheHelper;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @RequirePermission("dish:add")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        // 清理缓存数据
        cacheHelper.evictByPrefix(CacheConstant.DISH_KEY_PREFIX + dishDTO.getCategoryId());

        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @RequirePermission("dish:delete")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除：{}", ids);
        dishService.deleteBatch(ids);

        // 清理所有的缓存数据
        cacheHelper.evictByPrefix(CacheConstant.DISH_KEY_PREFIX + "*");

        return Result.success();
    }

    /**
     * 根据id查询菜品和对应的口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品和口味：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @RequirePermission("dish:update")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("编辑菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        // 清理所有的缓存数据
        cacheHelper.evictByPrefix(CacheConstant.DISH_KEY_PREFIX + "*");

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @RequirePermission("dish:status")
    @Operation(summary = "菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status,id);

        // 清理所有的缓存数据
        cacheHelper.evictByPrefix(CacheConstant.DISH_KEY_PREFIX + "*");

        return Result.success();
    }

}
