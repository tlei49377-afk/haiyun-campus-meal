package com.haiyun.meal.controller.user;

import com.haiyun.meal.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 学生端店铺营业状态查询接口。
 */
@RestController("usershopcontroller") // 响应数据使用 JSON
@RequestMapping("/api/app/shop")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺营业状态为：", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
