package com.jiangou.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthCookieService {

    public static final String ACCESS_COOKIE = "jiangou_access";
    public static final String REFRESH_COOKIE = "jiangou_refresh";

    @Value("${jiangou.jwt.refresh-token-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Value("${jiangou.jwt.cookie-secure:false}")
    private boolean cookieSecure;

    public void writeTokens(HttpServletResponse response, String accessToken, String refreshToken, long accessExpiresSec) {
        response.addHeader("Set-Cookie", buildCookie(ACCESS_COOKIE, accessToken, accessExpiresSec).toString());
        response.addHeader("Set-Cookie", buildCookie(REFRESH_COOKIE, refreshToken, refreshExpirationMs / 1000L).toString());
    }

    public void clearTokens(HttpServletResponse response) {
        response.addHeader("Set-Cookie", clearCookie(ACCESS_COOKIE).toString());
        response.addHeader("Set-Cookie", clearCookie(REFRESH_COOKIE).toString());
    }

    public String readAccessToken(HttpServletRequest request) {
        return readCookie(request, ACCESS_COOKIE);
    }

    public String readRefreshToken(HttpServletRequest request) {
        return readCookie(request, REFRESH_COOKIE);
    }

    private ResponseCookie buildCookie(String name, String value, long maxAgeSec) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAgeSec)
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
