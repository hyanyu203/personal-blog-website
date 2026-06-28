package com.jiangou.config;

import lombok.Data;

@Data
public class JwtProperties {

    private String secret;
    private long accessTokenExpirationMs;
    private long refreshTokenExpirationMs;
    private boolean cookieSecure;
}
