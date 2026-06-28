package com.jiangou.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtTokenBlacklistServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private JwtTokenBlacklistService service;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        service = new JwtTokenBlacklistService(redisTemplate);
    }

    @Test
    void blacklistIfAbsent_returnsTrueWhenFirstUse() {
        Date expiresAt = new Date(System.currentTimeMillis() + 60_000);
        when(valueOperations.setIfAbsent(anyString(), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS)))
                .thenReturn(true);

        assertTrue(service.blacklistIfAbsent("refresh-token", expiresAt));
    }

    @Test
    void blacklistIfAbsent_returnsFalseWhenAlreadyBlacklisted() {
        Date expiresAt = new Date(System.currentTimeMillis() + 60_000);
        when(valueOperations.setIfAbsent(anyString(), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS)))
                .thenReturn(false);

        assertFalse(service.blacklistIfAbsent("refresh-token", expiresAt));
    }

    @Test
    void blacklistIfAbsent_returnsFalseWhenExpired() {
        Date expiresAt = new Date(System.currentTimeMillis() - 1_000);
        assertFalse(service.blacklistIfAbsent("refresh-token", expiresAt));
    }

    @Test
    void blacklist_setsKeyWithTtl() {
        Date expiresAt = new Date(System.currentTimeMillis() + 120_000);
        service.blacklist("token-value", expiresAt);

        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(valueOperations).set(anyString(), eq("1"), ttlCaptor.capture(), eq(TimeUnit.MILLISECONDS));
        assertTrue(ttlCaptor.getValue() > 0);
    }
}
