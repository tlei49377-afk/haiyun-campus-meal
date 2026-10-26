package com.haiyun.meal.config;

import com.haiyun.meal.properties.AliOssProperties;
import com.haiyun.meal.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类, 用于创建AliOssUtil对象
 */
@Configuration
@Slf4j
public class OssConfiguration {

    /**
     * 创建阿里云 OSS 工具实例。
     */
    @Bean
    @ConditionalOnMissingBean // 如果容器中不存在AliOssUtil对象, 则创建该对象
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建 OSS 客户端");
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
