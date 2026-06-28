package com.jiangou.security;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AuthUserCacheInvalidationListener {

    private final AuthUserCacheService authUserCacheService;

    public AuthUserCacheInvalidationListener(AuthUserCacheService authUserCacheService) {
        this.authUserCacheService = authUserCacheService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onEvict(AuthUserCacheEvictEvent event) {
        for (Long userId : event.getUserIds()) {
            authUserCacheService.evict(userId);
        }
    }
}
