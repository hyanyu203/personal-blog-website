package com.jiangou.security;

import com.jiangou.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long currentUserId(SecurityUserDetailsService userDetailsService) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return null;
        }
        return userDetailsService.resolveUserId(((User) auth.getPrincipal()).getUsername());
    }

    public static Long requireUserId(SecurityUserDetailsService userDetailsService) {
        Long userId = currentUserId(userDetailsService);
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        return userId;
    }
}
