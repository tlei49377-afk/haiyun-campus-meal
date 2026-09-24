package com.sky.service.impl;

import com.sky.entity.Role;
import com.sky.mapper.RoleMapper;
import com.sky.service.PermissionService;
import com.sky.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionService permissionService;

    /**
     * 查询全部角色
     * @return
     */
    public List<Role> list() {
        return roleMapper.list();
    }

    /**
     * 查询角色已分配的权限id
     * @param roleId
     * @return
     */
    public List<Long> listPermissionIds(Long roleId) {
        return roleMapper.listPermissionIdsByRoleId(roleId);
    }

    /**
     * 给角色分配权限
     * @param roleId
     * @param permissionIds
     */
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        roleMapper.deleteRolePermission(roleId);
        if (permissionIds != null && permissionIds.size() > 0) {
            roleMapper.insertRolePermission(roleId, permissionIds);
        }

        // 角色权限变了，持有该角色的员工缓存全部失效
        List<Long> employeeIds = roleMapper.listEmployeeIdsByRoleId(roleId);
        for (Long employeeId : employeeIds) {
            permissionService.evictCache(employeeId);
        }
        log.info("角色 {} 权限已更新，失效 {} 个员工缓存", roleId, employeeIds.size());
    }

    /**
     * 查询员工已分配的角色id
     * @param employeeId
     * @return
     */
    public List<Long> listRoleIdsByEmployeeId(Long employeeId) {
        return roleMapper.listRoleIdsByEmployeeId(employeeId);
    }

    /**
     * 给员工分配角色
     * @param employeeId
     * @param roleIds
     */
    @Transactional
    public void assignRoles(Long employeeId, List<Long> roleIds) {
        roleMapper.deleteEmployeeRole(employeeId);
        if (roleIds != null && roleIds.size() > 0) {
            roleMapper.insertEmployeeRole(employeeId, roleIds);
        }
        permissionService.evictCache(employeeId);
        log.info("员工 {} 角色已更新", employeeId);
    }
}
