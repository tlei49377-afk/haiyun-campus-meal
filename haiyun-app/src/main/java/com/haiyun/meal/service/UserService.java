package com.haiyun.meal.service;

import com.haiyun.meal.dto.UserLoginDTO;
import com.haiyun.meal.dto.UserRegisterDTO;
import com.haiyun.meal.entity.User;

/**
 * 学生用户业务接口。
 */
public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
    /**
     * 使用学号和密码登录学生账号。
     */
    User studentLogin(UserLoginDTO userLoginDTO);

    /**
     * 注册学生账号。
     */
    User studentRegister(UserRegisterDTO registerDTO);
}
