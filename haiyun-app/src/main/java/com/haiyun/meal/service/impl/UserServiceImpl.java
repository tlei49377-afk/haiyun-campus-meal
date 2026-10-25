package com.haiyun.meal.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haiyun.meal.constant.MessageConstant;
import com.haiyun.meal.dto.UserLoginDTO;
import com.haiyun.meal.entity.User;
import com.haiyun.meal.exception.LoginFailedException;
import com.haiyun.meal.mapper.UserMapper;
import com.haiyun.meal.properties.JwtProperties;
import com.haiyun.meal.properties.WeChatProperties;
import com.haiyun.meal.service.UserService;
import com.haiyun.meal.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // 微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        log.info("微信用户登录：{}", userLoginDTO.getCode());
        // 调用微信接口服务，获得当前微信用户的openid
        String openid = getOpenid(userLoginDTO.getCode());

        // 判断openid是否为空，如果为空则代表登录失败，抛出登录失败异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 判断是否为新用户，如果为新用户，自动完成注册
        User user = userMapper.getByOpenid(openid);

        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 返回用户对象
        return user;
    }

    /**
     * 学生本地登录，便于没有微信开发者账号时演示 H5 全链路。
     */
    public User studentLogin(UserLoginDTO dto) {
        if (!StringUtils.hasText(dto.getStudentNo()) || !StringUtils.hasText(dto.getPassword())) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.getByStudentNo(dto.getStudentNo().trim());
        if (user == null || user.getPassword() == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        return user;
    }

    /**
     * 调用微信接口服务, 获取微信用户openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
