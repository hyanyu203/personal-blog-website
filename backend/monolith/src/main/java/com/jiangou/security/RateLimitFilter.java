package com.jiangou.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.common.constant.ErrorCodes;
import com.jiangou.common.result.ApiResult;
import com.jiangou.common.service.RateLimitService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;
    private final ClientIpResolver clientIpResolver;

    public RateLimitFilter(RateLimitService rateLimitService, ObjectMapper objectMapper,
                           ClientIpResolver clientIpResolver) {
        this.rateLimitService = rateLimitService;
        this.objectMapper = objectMapper;
        this.clientIpResolver = clientIpResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (shouldLimit(uri, method)) {
            String clientKey = clientKey(request);
            int max = maxForPath(uri);
            if (!rateLimitService.tryAcquire(clientKey + ":" + method + ":" + uri, max, 60)) {
                response.setStatus(429);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(
                        ApiResult.fail(ErrorCodes.RATE_LIMIT, "请求过于频繁，请稍后再试")));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean shouldLimit(String uri, String method) {
        if (uri.startsWith("/api/v1/admin")) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method)) {
            return uri.startsWith("/api/v1/auth/captcha")
                    || uri.startsWith("/api/v1/search");
        }
        if (!"POST".equalsIgnoreCase(method) && !"PATCH".equalsIgnoreCase(method)
                && !"PUT".equalsIgnoreCase(method) && !"DELETE".equalsIgnoreCase(method)) {
            return false;
        }
        return uri.startsWith("/api/v1/auth/login")
                || uri.startsWith("/api/v1/auth/refresh")
                || uri.startsWith("/api/v1/auth/oauth/exchange")
                || uri.startsWith("/api/v1/auth/register")
                || uri.startsWith("/api/v1/auth/forgot-password")
                || uri.startsWith("/api/v1/auth/reset-password")
                || uri.startsWith("/api/v1/comments")
                || uri.contains("/like")
                || uri.startsWith("/api/v1/subscriptions")
                || uri.startsWith("/api/v1/webmention");
    }

    private int maxForPath(String uri) {
        if (uri.startsWith("/api/v1/admin")) {
            return 120;
        }
        if (uri.startsWith("/api/v1/auth/captcha")) {
            return 30;
        }
        if (uri.startsWith("/api/v1/search")) {
            return 60;
        }
        if (uri.startsWith("/api/v1/subscriptions/confirm")
                || uri.startsWith("/api/v1/subscriptions/unsubscribe")) {
            return 20;
        }
        if (uri.startsWith("/api/v1/auth/login")) {
            return 10;
        }
        if (uri.startsWith("/api/v1/auth/register/send-code")
                || uri.startsWith("/api/v1/auth/forgot-password/send-code")) {
            return 5;
        }
        if (uri.startsWith("/api/v1/auth/register")
                || uri.startsWith("/api/v1/auth/reset-password")) {
            return 10;
        }
        if (uri.startsWith("/api/v1/auth/refresh") || uri.startsWith("/api/v1/auth/oauth/exchange")) {
            return 20;
        }
        if (uri.startsWith("/api/v1/subscriptions")) {
            return 10;
        }
        if (uri.startsWith("/api/v1/webmention")) {
            return 10;
        }
        if (uri.contains("/like")) {
            return 30;
        }
        return 20;
    }

    private String clientKey(HttpServletRequest request) {
        return clientIpResolver.resolve(request);
    }
}
