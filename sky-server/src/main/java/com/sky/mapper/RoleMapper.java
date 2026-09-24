package com.sky.mapper;

import com.sky.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    /**
     * 查询全部角色
     * @return
     */
    List<Role> list();

    /**
     * 查询角色已分配的权限id
     * @param roleId
     * @return
     */
    List<Long> listPermissionIdsByRoleId(Long roleId);

    /**
     * 删除角色的权限关联
     * @param roleId
     */
    void deleteRolePermission(Long roleId);

    /**
     * 批量插入角色权限关联
     * @param roleId
     * @param permissionIds
     */
    void insertRolePermission(Long roleId, List<Long> permissionIds);

    /**
     * 查询拥有该角色的员工id
     * @param roleId
     * @return
     */
    List<Long> listEmployeeIdsByRoleId(Long roleId);

    /**
     * 查询员工已分配的角色id
     * @param employeeId
     * @return
     */
    List<Long> listRoleIdsByEmployeeId(Long employeeId);

    /**
     * 删除员工的角色关联
     * @param employeeId
     */
    void deleteEmployeeRole(Long employeeId);

    /**
     * 批量插入员工角色关联
     * @param employeeId
     * @param roleIds
     */
    void insertEmployeeRole(Long employeeId, List<Long> roleIds);
}
