package com.haiyun.meal.service;

import com.haiyun.meal.entity.Permission;

import java.util.List;

/**
 * 权限业务接口。
 */
public interface PermissionService {

    /**
     * 查询员工的权限码
     * @param employeeId
     * @return
     */
    List<String> listCodesByEmployeeId(Long employeeId);

    /**
     * 查询全部权限点
     * @return
     */
    List<Permission> list();

    /**
     * 删除员工的权限缓存
     * @param employeeId
     */
    void evictCache(Long employeeId);
}
