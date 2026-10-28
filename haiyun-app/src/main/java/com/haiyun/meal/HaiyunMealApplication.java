package com.haiyun.meal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 海韵食刻后端启动类。
 */
@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching // 开启缓存注解
@EnableScheduling // 开启任务调度
public class HaiyunMealApplication {
    /**
     * 启动海韵食刻后端应用。
     */
    public static void main(String[] args) {
        SpringApplication.run(HaiyunMealApplication.class, args);
        log.info("server started");
    }
}
