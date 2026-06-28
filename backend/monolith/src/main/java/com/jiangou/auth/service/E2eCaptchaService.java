package com.jiangou.auth.service;

import com.jiangou.auth.vo.CaptchaVO;
import com.jiangou.config.AuthProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Fixed captcha for Playwright E2E runs ({@code spring.profiles.active=e2e}).
 */
@Service
@Profile("e2e")
@Primary
public class E2eCaptchaService extends CaptchaService {

    public static final String FIXED_CODE = "E2E1";

    public E2eCaptchaService(StringRedisTemplate redisTemplate, AuthProperties authProperties) {
        super(redisTemplate, authProperties);
    }

    @Override
    public CaptchaVO generate() {
        return generateWithCode(FIXED_CODE);
    }
}
