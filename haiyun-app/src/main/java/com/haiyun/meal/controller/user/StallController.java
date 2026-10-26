package com.haiyun.meal.controller.user;

import com.haiyun.meal.entity.Stall;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.StallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生端档口查询接口。
 */
@RestController("appStallController")
@RequestMapping("/api/app/stall")
public class StallController {
    @Autowired
    private StallService stallService;

    /**
     * 按食堂查询启用的档口列表。
     */
    @GetMapping("/list")
    public Result<List<Stall>> list(Long canteenId) { return Result.success(stallService.list(canteenId, 1)); }
}
