package com.haiyun.meal.controller.admin;

import com.haiyun.meal.annotation.RequirePermission;
import com.haiyun.meal.context.BaseContext;
import com.haiyun.meal.entity.Permission;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permission")
@Slf4j
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 查询当前登录员工的权限
     * @return
     */
    @GetMapping("/me")
    public Result<List<String>> me() {
        return Result.success(permissionService.listCodesByEmployeeId(BaseContext.getCurrentId()));
    }

    /**
     * 查询全部权限点
     * @return
     */
    @RequirePermission("role:query")
    @GetMapping("/list")
    public Result<List<Permission>> list() {
        return Result.success(permissionService.list());
    }
}
