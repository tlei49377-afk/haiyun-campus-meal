package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色权限请求数据。
 */
@Data
public class RolePermissionDTO implements Serializable {

    //权限id列表
    private List<Long> permissionIds;

}
