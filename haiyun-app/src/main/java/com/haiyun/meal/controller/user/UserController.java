package com.haiyun.meal.controller.user;

import com.haiyun.meal.constant.JwtClaimsConstant;
import com.haiyun.meal.dto.UserLoginDTO;
import com.haiyun.meal.entity.User;
import com.haiyun.meal.properties.JwtProperties;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.UserService;
import com.haiyun.meal.utils.JwtUtil;
import com.haiyun.meal.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生端登录接口。
 */
@RestController
@RequestMapping("/api/app/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("学生登录：{}", userLoginDTO.getStudentNo() == null ? "微信 code" : userLoginDTO.getStudentNo());

        // 本地账号优先；没有学号时继续兼容微信 code 登录
        User user = userLoginDTO.getStudentNo() != null
                ? userService.studentLogin(userLoginDTO)
                : userService.wxLogin(userLoginDTO);

        // 生成 jwt 令牌，返回给用户
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .studentNo(user.getStudentNo())
                .name(user.getName())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }
}
