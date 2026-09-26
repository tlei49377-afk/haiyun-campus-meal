package com.sky.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.cache.CacheHelper;
import com.sky.constant.CacheConstant;
import com.sky.json.JacksonObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 配置类，给 @Cacheable 用的缓存管理器设置序列化和过期时间
 */
@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new JacksonObjectMapper();
        Jackson2JsonRedisSerializer<Object> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                // 默认不过期太危险，这里统一给过期时间，再按 key 加抖动
                .entryTtl((key, value) -> CacheHelper.withJitter(CacheConstant.SETMEAL_TTL))
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .withCacheConfiguration(CacheConstant.SETMEAL_CACHE_NAME, configuration)
                .build();
    }
}
