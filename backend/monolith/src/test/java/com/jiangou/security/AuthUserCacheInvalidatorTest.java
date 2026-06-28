package com.jiangou.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthUserCacheInvalidatorTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private AuthUserCacheInvalidator authUserCacheInvalidator;

    @Test
    void evictAfterCommit_publishesSingleUserEvent() {
        authUserCacheInvalidator.evictAfterCommit(3L);

        org.mockito.ArgumentCaptor<AuthUserCacheEvictEvent> eventCaptor = forClass(AuthUserCacheEvictEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(java.util.Collections.singleton(3L), eventCaptor.getValue().getUserIds());
    }

    @Test
    void evictAllAfterCommit_deduplicatesAndSkipsNullUsers() {
        authUserCacheInvalidator.evictAllAfterCommit(Arrays.asList(5L, null, 5L, 9L));

        org.mockito.ArgumentCaptor<AuthUserCacheEvictEvent> eventCaptor = forClass(AuthUserCacheEvictEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(new LinkedHashSet<Long>(Arrays.asList(5L, 9L)), eventCaptor.getValue().getUserIds());
    }
}
