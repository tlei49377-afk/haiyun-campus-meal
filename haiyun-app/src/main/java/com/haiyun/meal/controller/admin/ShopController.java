package com.haiyun.meal.controller.admin;

import com.haiyun.meal.annotation.RequirePermission;
import com.haiyun.meal.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

/**
 * 管理端店铺营业状态接口。
 */
@RestController("adminshopcontroller") // 响应数据使用 JSON
@RequestMapping("/api/admin/shop")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    //服务启动时补一个默认的营业状态，Redis 被清空后前端不至于读到 null
    /**
     * 初始化店铺营业状态缓存。
     */
    @PostConstruct
    public void initShopStatus() {
        try {
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(KEY, 1))) {
                log.info("店铺营业状态缺失，已初始化为营业中");
            }
        } catch (Exception e) {
            log.warn("初始化店铺营业状态失败，Redis 可能未就绪：{}", e.getMessage());
        }
    }

    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @RequirePermission("shop:status")
    @PutMapping("/{status}")
    public Result get(@PathVariable Integer status) {
        log.info("设置店铺营业状态：{}", status == 1 ? "营业中" : "打烊中");
        try {
            redisTemplate.opsForValue().set(KEY, status);
        } catch (Exception e) {
            log.warn("营业状态写入 Redis 失败：{}", e.getMessage());
        }
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = 1;
        try {
            Integer cached = (Integer) redisTemplate.opsForValue().get(KEY);
            if (cached != null) status = cached;
        } catch (Exception e) {
            log.warn("营业状态读取 Redis 失败，使用默认营业中：{}", e.getMessage());
        }
        log.info("获取食堂营业状态：{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
