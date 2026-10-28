package com.haiyun.meal.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 学生注册请求数据。
 */
@Data
public class UserRegisterDTO implements Serializable {
    private String studentNo;
    private String name;
    private String phone;
    private String password;
}
