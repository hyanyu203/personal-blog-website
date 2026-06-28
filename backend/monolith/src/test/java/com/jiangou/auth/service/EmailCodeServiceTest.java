package com.jiangou.auth.service;

import com.jiangou.common.constant.ErrorCodes;
import com.jiangou.common.exception.BusinessException;
import com.jiangou.config.AuthProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailCodeServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private ValueOperations<String, String> valueOps;

    private AuthProperties authProperties;
    private EmailCodeService service;

    @BeforeEach
    void setUp() {
        authProperties = new AuthProperties();
        authProperties.setEmailCodeVerifyMaxAttempts(3);
        authProperties.setEmailCodeVerifyAttemptWindowSeconds(600);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        service = new EmailCodeService(redisTemplate, mailSender, authProperties);
    }

    @Test
    void verifyAndConsume_rejectsAfterTooManyFailedAttempts() {
        when(valueOps.get("email-code:attempts:register:user@example.com")).thenReturn("3");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.verifyAndConsume("user@example.com", EmailCodeService.Purpose.REGISTER, "000000"));
        assertEquals(ErrorCodes.CODE_RATE_LIMIT, ex.getCode());
        verify(redisTemplate, never()).execute(any(DefaultRedisScript.class), anyList(), any());
    }

    @Test
    void verifyAndConsume_keepsCodeOnWrongAttempt() {
        when(valueOps.get("email-code:attempts:register:user@example.com")).thenReturn("0");
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any())).thenReturn(0L);
        when(valueOps.increment("email-code:attempts:register:user@example.com")).thenReturn(1L);

        assertThrows(BusinessException.class,
                () -> service.verifyAndConsume("user@example.com", EmailCodeService.Purpose.REGISTER, "123456"));

        verify(redisTemplate).execute(any(DefaultRedisScript.class),
                eq(Arrays.asList("email-code:register:user@example.com",
                        "email-code:attempts:register:user@example.com")),
                any());
        verify(valueOps).increment("email-code:attempts:register:user@example.com");
        verify(redisTemplate).expire(eq("email-code:attempts:register:user@example.com"), eq(600L), eq(TimeUnit.SECONDS));
    }

    @Test
    void verifyAndConsume_deletesCodeOnSuccess() {
        when(valueOps.get("email-code:attempts:register:user@example.com")).thenReturn("0");
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any())).thenReturn(1L);

        service.verifyAndConsume("user@example.com", EmailCodeService.Purpose.REGISTER, "123456");

        verify(redisTemplate).execute(any(DefaultRedisScript.class),
                eq(Arrays.asList("email-code:register:user@example.com",
                        "email-code:attempts:register:user@example.com")),
                any());
        verify(valueOps, never()).increment("email-code:attempts:register:user@example.com");
    }
}
