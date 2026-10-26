package com.haiyun.meal.controller.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.haiyun.meal.cache.CacheHelper;
import com.haiyun.meal.constant.CacheConstant;
import com.haiyun.meal.constant.StatusConstant;
import com.haiyun.meal.entity.Dish;
import com.haiyun.meal.exception.BaseException;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.DishService;
import com.haiyun.meal.vo.DishVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 学生端菜品查询接口。
 */
@RestController("userDishController")
@RequestMapping("/api/app/dish")
@Slf4j
// 菜品浏览接口
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CacheHelper cacheHelper;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        // 缓存 key 的规则：dish_分类id
        String key = CacheConstant.DISH_KEY_PREFIX + categoryId;

        List<DishVO> list = cacheHelper.getOrLoad(key, new TypeReference<List<DishVO>>() {}, CacheConstant.DISH_TTL,
                () -> {
                    Dish dish = new Dish();
                    dish.setCategoryId(categoryId);
                    dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
                    return dishService.listWithFlavor(dish);
                });
        return Result.success(list);
    }

    /**
     * 根据主键查询起售菜品和口味。
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dish = dishService.getByIdWithFlavor(id);
        if (dish == null || !StatusConstant.ENABLE.equals(dish.getStatus())) {
            throw new BaseException("菜品不存在或已停售");
        }
        return Result.success(dish);
    }
}
