package com.sky.service;

import java.util.List;

public interface PermissionService {

    /**
     * 查询员工的权限码
     * @param employeeId
     * @return
     */
    List<String> listCodesByEmployeeId(Long employeeId);

    /**
     * 删除员工的权限缓存
     * @param employeeId
     */
    void evictCache(Long employeeId);
}
