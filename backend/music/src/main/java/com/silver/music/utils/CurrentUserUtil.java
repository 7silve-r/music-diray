package com.silver.music.utils;

import com.silver.diary.utils.SecurityUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;

public class CurrentUserUtil {
    public static Map<String, Object> get() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUtil.Account account)) return null;
        return Map.of("userId", account.id().longValue(), "username", account.username(),
                "role", SecurityUtil.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER");
    }
}
