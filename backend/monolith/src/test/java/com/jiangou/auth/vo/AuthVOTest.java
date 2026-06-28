package com.jiangou.auth.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class AuthVOTest {

    @Test
    void withoutTokensStripsSecrets() {
        AuthVO auth = AuthVO.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .expiresIn(3600L)
                .user(AuthVO.UserBriefVO.builder().id(1L).username("u").build())
                .build();

        AuthVO publicView = auth.withoutTokens();

        assertNull(publicView.getAccessToken());
        assertNull(publicView.getRefreshToken());
    }
}
