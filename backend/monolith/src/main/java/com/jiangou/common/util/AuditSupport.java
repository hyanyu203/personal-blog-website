package com.jiangou.common.util;

import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.system.service.AuditLogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AuditSupport {

    private final AuditLogService auditLogService;
    private final SecurityUserDetailsService userDetailsService;

    public AuditSupport(AuditLogService auditLogService, SecurityUserDetailsService userDetailsService) {
        this.auditLogService = auditLogService;
        this.userDetailsService = userDetailsService;
    }

    public void log(String action, String targetType, Long targetId) {
        Long actorId = currentUserId();
        if (actorId != null) {
            auditLogService.log(actorId, action, targetType, targetId);
        }
    }

    public Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return null;
        }
        return userDetailsService.resolveUserId(((User) auth.getPrincipal()).getUsername());
    }
}
