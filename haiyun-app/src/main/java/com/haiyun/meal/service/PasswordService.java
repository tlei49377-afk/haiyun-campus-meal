package com.haiyun.meal.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/** 统一密码编码，兼容历史 MD5 数据并在登录后升级。 */
@Component
public class PasswordService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(String raw) { return encoder.encode(raw); }

    public boolean matches(String raw, String stored) {
        if (raw == null || stored == null) return false;
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return encoder.matches(raw, stored);
        }
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8)).equalsIgnoreCase(stored);
    }

    public boolean isLegacy(String stored) {
        return stored != null && !(stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"));
    }
}
