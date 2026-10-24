package com.haiyun.meal.service;

import com.haiyun.meal.dto.UserLoginDTO;
import com.haiyun.meal.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
