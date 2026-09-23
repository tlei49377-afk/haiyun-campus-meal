package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper {

    /**
     * 查询员工的权限码
     * @param employeeId
     * @return
     */
    List<String> listCodesByEmployeeId(Long employeeId);
}
