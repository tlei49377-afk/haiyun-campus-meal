package com.haiyun.meal.controller.user;

import com.haiyun.meal.entity.Canteen;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生端食堂查询接口。
 */
@RestController("appCanteenController")
@RequestMapping("/api/app/canteen")
public class CanteenController {
    @Autowired
    private CanteenService canteenService;

    /**
     * 查询启用的食堂列表。
     */
    @GetMapping("/list")
    public Result<List<Canteen>> list() { return Result.success(canteenService.list(1)); }
}
