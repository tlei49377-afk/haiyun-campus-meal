package com.sky.service;

import java.util.List;

public interface PermissionService {

    /**
     * 查询员工的权限码
     * @param employeeId
     * @return
     */
    List<String> listCodesByEmployeeId(Long employeeId);
}
