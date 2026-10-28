package com.haiyun.meal.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiyun.meal.constant.CacheConstant;
import com.haiyun.meal.json.JacksonObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * 热点数据缓存：写缓存带随机过期时间，空结果写短过期时间，重建时加互斥锁
 */
@Component
@Slf4j
public class CacheHelper {

    private static final ObjectMapper objectMapper = new JacksonObjectMapper();

    //缓存命中率的指标名
    private static final String METRIC_NAME = "haiyun.cache.requests";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    /**
     * 先读缓存，读不到再查库并写回缓存
     * @param key
     * @param type
     * @param ttl
     * @param loader
     * @return
     */
    public <T> T getOrLoad(String key, TypeReference<T> type, Duration ttl, Supplier<T> loader) {
        String json = null;
        try {
            json = redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("缓存读取失败，直接回源数据库：key={}, reason={}", key, e.getMessage());
            return loader.get();
        }
        if (json != null) {
            /**
             * 记录缓存指标。
             */
            count(key, "hit");
            /**
             * 将 JSON 反序列化为指定类型。
             */
            return parse(json, type);
        }

        count(key, "miss");

        // 热点 key 失效时只放一个请求去查库，其他的等它把缓存写好
        String lockKey = key + CacheConstant.LOCK_SUFFIX;
        Boolean locked;
        try {
            locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", CacheConstant.LOCK_TTL);
        } catch (Exception e) {
            log.warn("缓存互斥锁不可用，直接回源数据库：key={}, reason={}", key, e.getMessage());
            return loader.get();
        }
        if (!Boolean.TRUE.equals(locked)) {
            for (int i = 0; i < CacheConstant.LOCK_WAIT_RETRY; i++) {
                /**
                 * 短暂休眠后重试。
                 */
                sleep(CacheConstant.LOCK_WAIT_MILLIS);
                try {
                    json = redisTemplate.opsForValue().get(key);
                } catch (Exception e) {
                    return loader.get();
                }
                if (json != null) {
                    count(key, "hit");
                    return parse(json, type);
                }
            }
            // 等到超时说明持锁的请求出问题了，自己查库兜底，保证接口可用
            return loader.get();
        }

        try {
            count(key, "load");
            T value = loader.get();
            try {
                /**
                 * 写入缓存数据。
                 */
                write(key, value, ttl);
            } catch (Exception e) {
                log.warn("缓存写入失败，本次返回数据库结果：key={}, reason={}", key, e.getMessage());
            }
            return value;
        } finally {
            try {
                redisTemplate.delete(lockKey);
            } catch (Exception e) {
                log.warn("缓存锁释放失败：key={}, reason={}", lockKey, e.getMessage());
            }
        }
    }

    /**
     * 按前缀清理缓存
     * @param pattern
     */
    public void evictByPrefix(String pattern) {
        try {
            Set<String> keys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(500).build();
            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(cursor.next());
                }
            }
            if (keys.isEmpty()) {
                return;
            }
            redisTemplate.delete(keys);
            log.info("清理缓存 {} 个，pattern={}", keys.size(), pattern);
        } catch (Exception e) {
            log.warn("缓存清理失败，忽略并依赖 TTL 兜底：pattern={}, reason={}", pattern, e.getMessage());
        }
    }

    /**
     * 写入缓存，并控制空结果的有效期。
     */
    private <T> void write(String key, T value, Duration ttl) {
        // 空结果说明这个条件确实没数据，缓存一小会儿就够，别把内存占住
        Duration expire = isEmpty(value) ? CacheConstant.EMPTY_TTL : ttl;
        redisTemplate.opsForValue().set(key, toJson(value), withJitter(expire));
    }

    /**
     * 判断缓存值是否为空。
     */
    private boolean isEmpty(Object value) {
        return value == null || (value instanceof Collection && ((Collection<?>) value).isEmpty());
    }

    /**
     * 按缓存名称记录命中、未命中和回源指标。
     */
    private void count(String key, String result) {
        int index = key.indexOf('_');
        String cache = index > 0 ? key.substring(0, index) : key;
        meterRegistry.counter(METRIC_NAME, "cache", cache, "result", result).increment();
    }

    /**
     * 将对象序列化为 JSON。
     */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("缓存序列化失败", e);
        }
    }

    /**
     * 将 JSON 反序列化为指定类型。
     */
    private <T> T parse(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("缓存反序列化失败", e);
        }
    }

    /**
     * 过期时间加随机抖动，避免一批 key 同时失效全部落到数据库
     * @param ttl
     * @return
     */
    public static Duration withJitter(Duration ttl) {
        long base = ttl.toMillis();
        long jitter = (long) (base * CacheConstant.TTL_JITTER);
        long offset = ThreadLocalRandom.current().nextLong(-jitter, jitter + 1);
        return Duration.ofMillis(base + offset);
    }

    /**
     * 短暂休眠指定毫秒数。
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
