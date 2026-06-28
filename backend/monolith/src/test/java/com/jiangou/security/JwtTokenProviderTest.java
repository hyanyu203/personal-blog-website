package com.jiangou.security;

import com.jiangou.common.exception.UnauthorizedException;
import com.jiangou.config.JwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-key-at-least-32-characters-long");
        props.setAccessTokenExpirationMs(3600000L);
        props.setRefreshTokenExpirationMs(604800000L);
        provider = new JwtTokenProvider(props);
    }

    @Test
    void validateAccessToken_acceptsAccessToken() {
        String token = provider.createAccessToken(1L, "admin", 0);
        Claims claims = provider.validateAccessToken(token);
        assertEquals("1", claims.getSubject());
    }

    @Test
    void validateAccessToken_rejectsRefreshToken() {
        String refresh = provider.createRefreshToken(1L, "admin", 0);
        assertThrows(UnauthorizedException.class, () -> provider.validateAccessToken(refresh));
    }

    @Test
    void validateRefreshToken_rejectsAccessToken() {
        String access = provider.createAccessToken(1L, "admin", 0);
        assertThrows(UnauthorizedException.class, () -> provider.validateRefreshToken(access));
    }

    @Test
    void requireUserIdFromBearer_parsesAccessToken() {
        String access = provider.createAccessToken(42L, "admin", 0);
        Long userId = provider.requireUserIdFromBearer("Bearer " + access);
        assertEquals(42L, userId);
    }
}
