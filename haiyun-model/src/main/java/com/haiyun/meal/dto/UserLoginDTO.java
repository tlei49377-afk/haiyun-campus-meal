package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;

/** 学生端登录参数：本地演示账号使用学号 + 密码，微信登录仍可传 code。 */
@Data
public class UserLoginDTO implements Serializable {
    private String code;
    private String studentNo;
    private String password;
}
