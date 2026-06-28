package com.jiangou.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthVO {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserBriefVO user;

    /** Strips tokens for HTTP response when tokens are delivered via HttpOnly cookies. */
    public AuthVO withoutTokens() {
        return AuthVO.builder()
                .expiresIn(this.expiresIn)
                .user(this.user)
                .build();
    }

    @Data
    @Builder
    public static class UserBriefVO {
        private Long id;
        private String username;
        private String displayName;
        private List<String> roles;
        private List<String> permissions;
    }
}
