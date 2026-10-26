package com.haiyun.meal.controller.user;

import com.haiyun.meal.constant.JwtClaimsConstant;
import com.haiyun.meal.dto.UserLoginDTO;
import com.haiyun.meal.dto.UserRegisterDTO;
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
 * 学生端登录与注册接口。
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
     * 学生登录。
     */
    @PostMapping("login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("学生登录：{}", userLoginDTO.getStudentNo() == null ? "微信 code" : userLoginDTO.getStudentNo());
        User user = userLoginDTO.getStudentNo() != null
                ? userService.studentLogin(userLoginDTO)
                : userService.wxLogin(userLoginDTO);
        return Result.success(buildLoginVO(user));
    }

    /**
     * 学生注册并直接登录。
     */
    @PostMapping("register")
    public Result<UserLoginVO> register(@RequestBody UserRegisterDTO registerDTO) {
        log.info("学生注册：{}", registerDTO.getStudentNo());
        return Result.success(buildLoginVO(userService.studentRegister(registerDTO)));
    }

    /**
     * 生成学生登录响应和 JWT 令牌。
     */
    private UserLoginVO buildLoginVO(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(), claims);
        return UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .studentNo(user.getStudentNo())
                .name(user.getName())
                .token(token)
                .build();
    }
}
