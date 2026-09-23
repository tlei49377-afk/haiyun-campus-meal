package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper {

    /**
     * 查询员工的权限码
     * @param employeeId
     * @return
     */
    @Select("select distinct p.code " +
            "from employee_role er " +
            "join role_permission rp on rp.role_id = er.role_id " +
            "join permission p on p.id = rp.permission_id " +
            "where er.employee_id = #{employeeId}")
    List<String> listCodesByEmployeeId(@Param("employeeId") Long employeeId);
}
