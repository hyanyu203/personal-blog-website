package com.jiangou.security;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class AuthUserCacheInvalidator {

    private final ApplicationEventPublisher eventPublisher;

    public AuthUserCacheInvalidator(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void evictAfterCommit(Long userId) {
        if (userId == null) {
            return;
        }
        Set<Long> userIds = new LinkedHashSet<Long>();
        userIds.add(userId);
        evictAllAfterCommit(userIds);
    }

    public void evictAllAfterCommit(Collection<Long> userIds) {
        Set<Long> normalized = normalizeUserIds(userIds);
        if (normalized.isEmpty()) {
            return;
        }
        eventPublisher.publishEvent(new AuthUserCacheEvictEvent(normalized));
    }

    private Set<Long> normalizeUserIds(Collection<Long> userIds) {
        Set<Long> normalized = new LinkedHashSet<Long>();
        if (userIds == null) {
            return normalized;
        }
        for (Long userId : userIds) {
            if (userId != null) {
                normalized.add(userId);
            }
        }
        return normalized;
    }
}
