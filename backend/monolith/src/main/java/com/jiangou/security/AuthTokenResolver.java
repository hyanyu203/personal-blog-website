package com.jiangou.security;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthTokenResolver {

    private final AuthCookieService authCookieService;

    public AuthTokenResolver(AuthCookieService authCookieService) {
        this.authCookieService = authCookieService;
    }

    public String resolveAccessToken(HttpServletRequest request, String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return authCookieService.readAccessToken(request);
    }
}
