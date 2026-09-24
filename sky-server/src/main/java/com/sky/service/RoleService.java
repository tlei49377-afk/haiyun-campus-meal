package com.sky.service;

import com.sky.entity.Role;

import java.util.List;

public interface RoleService {

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
    List<Long> listPermissionIds(Long roleId);

    /**
     * 给角色分配权限
     * @param roleId
     * @param permissionIds
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 查询员工已分配的角色id
     * @param employeeId
     * @return
     */
    List<Long> listRoleIdsByEmployeeId(Long employeeId);

    /**
     * 给员工分配角色
     * @param employeeId
     * @param roleIds
     */
    void assignRoles(Long employeeId, List<Long> roleIds);
}
