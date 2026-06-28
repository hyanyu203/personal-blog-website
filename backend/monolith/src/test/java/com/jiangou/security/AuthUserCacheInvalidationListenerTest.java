package com.jiangou.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthUserCacheInvalidationListenerTest {

    @Mock
    private AuthUserCacheService authUserCacheService;

    @InjectMocks
    private AuthUserCacheInvalidationListener authUserCacheInvalidationListener;

    @Test
    void onEvict_evictsEveryAffectedUser() {
        authUserCacheInvalidationListener.onEvict(new AuthUserCacheEvictEvent(Arrays.asList(3L, 5L)));

        verify(authUserCacheService).evict(3L);
        verify(authUserCacheService).evict(5L);
    }
}
