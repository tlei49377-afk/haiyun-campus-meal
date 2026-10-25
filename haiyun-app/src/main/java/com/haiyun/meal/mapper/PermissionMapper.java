package com.haiyun.meal.mapper;

import com.haiyun.meal.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 权限数据访问接口。
 */
@Mapper
public interface PermissionMapper {

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
}
