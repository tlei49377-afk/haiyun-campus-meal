package com.sky.service.impl;

import com.sky.mapper.PermissionMapper;
import com.sky.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 查询员工的权限码
     * @param employeeId
     * @return
     */
    public List<String> listCodesByEmployeeId(Long employeeId) {
        return permissionMapper.listCodesByEmployeeId(employeeId);
    }
}
