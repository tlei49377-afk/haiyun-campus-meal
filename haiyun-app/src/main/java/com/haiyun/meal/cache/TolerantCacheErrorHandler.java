package com.haiyun.meal.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * 缓存出错时只记日志：缓存是加速用的，读不出来就回源查库，不能让它把接口带崩
 */
@Slf4j
public class TolerantCacheErrorHandler implements CacheErrorHandler {

    /**
     * 忽略缓存读取异常并记录日志。
     */
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("读缓存失败，跳过缓存直接查库：cache={}, key={}", cache.getName(), key, exception);
    }

    /**
     * 忽略缓存写入异常并记录日志。
     */
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("写缓存失败：cache={}, key={}", cache.getName(), key, exception);
    }

    /**
     * 忽略缓存删除异常并记录日志。
     */
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("清理缓存失败：cache={}, key={}", cache.getName(), key, exception);
    }

    /**
     * 忽略缓存清空异常并记录日志。
     */
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("清空缓存失败：cache={}", cache.getName(), exception);
    }
}
