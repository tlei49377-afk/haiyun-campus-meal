package com.haiyun.meal.service.impl;

import com.alibaba.fastjson.JSON;
import com.haiyun.meal.entity.Permission;
import com.haiyun.meal.mapper.PermissionMapper;
import com.haiyun.meal.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * 权限业务实现。
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    // 权限缓存 key 前缀
    private static final String CACHE_KEY_PREFIX = "perm:emp:";

    // 权限变更不频繁，缓存 30 分钟
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 查询员工的权限码
     * @param employeeId
     * @return
     */
    public List<String> listCodesByEmployeeId(Long employeeId) {
        String key = CACHE_KEY_PREFIX + employeeId;

        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            return JSON.parseArray(json, String.class);
        }

        log.info("权限缓存未命中，查询数据库：employeeId={}", employeeId);
        List<String> codes = permissionMapper.listCodesByEmployeeId(employeeId);
        // 查不到也缓存空列表，防止每次请求都打库
        redisTemplate.opsForValue().set(key, JSON.toJSONString(codes), CACHE_TTL);
        return codes;
    }

    /**
     * 删除员工的权限缓存
     * @param employeeId
     */
    public void evictCache(Long employeeId) {
        redisTemplate.delete(CACHE_KEY_PREFIX + employeeId);
    }

    /**
     * 查询全部权限点
     * @return
     */
    public List<Permission> list() {
        return permissionMapper.list();
    }
}
