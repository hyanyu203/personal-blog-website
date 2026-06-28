package com.jiangou.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component("adminAccess")
public class AdminAccessEvaluator {

    static final Set<String> ADMIN_API_PERMISSIONS = new HashSet<String>(Arrays.asList(
            "article:create",
            "article:update",
            "article:publish",
            "comment:review",
            "project:sync",
            "setting:update",
            "user:manage"
    ));

    public boolean canAccessAdminApi(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String code = authority.getAuthority();
            if ("ROLE_ADMIN".equals(code)) {
                return true;
            }
            if (ADMIN_API_PERMISSIONS.contains(code)) {
                return true;
            }
        }
        return false;
    }
}
