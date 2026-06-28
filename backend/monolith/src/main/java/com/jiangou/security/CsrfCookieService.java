package com.jiangou.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class CsrfCookieService {

    public static final String CSRF_COOKIE = "XSRF-TOKEN";
    public static final String CSRF_HEADER = "X-XSRF-TOKEN";

    private static final long CSRF_MAX_AGE_SEC = 8 * 60 * 60;

    @Value("${jiangou.jwt.cookie-secure:false}")
    private boolean cookieSecure;

    public void ensureToken(HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.hasText(readCookie(request, CSRF_COOKIE))) {
            return;
        }
        issueFreshToken(response);
    }

    public void issueFreshToken(HttpServletResponse response) {
        writeToken(response, generateToken());
    }

    public boolean validate(HttpServletRequest request) {
        String cookie = readCookie(request, CSRF_COOKIE);
        String header = request.getHeader(CSRF_HEADER);
        return StringUtils.hasText(cookie) && cookie.equals(header);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void writeToken(HttpServletResponse response, String token) {
        response.addHeader("Set-Cookie", ResponseCookie.from(CSRF_COOKIE, token)
                .httpOnly(false)
                .secure(cookieSecure)
                .path("/")
                .maxAge(CSRF_MAX_AGE_SEC)
                .sameSite("Lax")
                .build()
                .toString());
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
