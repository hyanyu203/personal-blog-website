package com.jiangou.security;

import io.jsonwebtoken.Claims;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthUserCacheService authUserCacheService;
    private final JwtTokenBlacklistService tokenBlacklistService;
    private final AuthTokenResolver authTokenResolver;
    private final AuthCookieService authCookieService;
    private final Counter authFailureCounter;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   AuthUserCacheService authUserCacheService,
                                   JwtTokenBlacklistService tokenBlacklistService,
                                   AuthTokenResolver authTokenResolver,
                                   AuthCookieService authCookieService,
                                   ObjectProvider<MeterRegistry> meterRegistry) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authUserCacheService = authUserCacheService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.authTokenResolver = authTokenResolver;
        this.authCookieService = authCookieService;
        MeterRegistry registry = meterRegistry.getIfAvailable();
        this.authFailureCounter = registry == null ? null
                : Counter.builder("auth.failures")
                .description("JWT authentication failures")
                .register(registry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = authTokenResolver.resolveAccessToken(request, request.getHeader("Authorization"));
        if (!authenticateToken(token, request)) {
            String cookieToken = authCookieService.readAccessToken(request);
            if (StringUtils.hasText(cookieToken) && !cookieToken.equals(token)) {
                authenticateToken(cookieToken, request);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean authenticateToken(String token, HttpServletRequest request) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new IllegalStateException("token revoked");
            }
            Claims claims = jwtTokenProvider.validateAccessToken(token);
            int tokenVersion = extractTokenVersion(claims);
            Long userId = Long.valueOf(claims.getSubject());
            UserDetails userDetails = authUserCacheService.loadUserById(userId, tokenVersion);
            if (userDetails == null) {
                throw new IllegalStateException("invalid user or token version");
            }
            if (!userDetails.isEnabled()) {
                throw new IllegalStateException("disabled user");
            }
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (Exception e) {
            if (authFailureCounter != null) {
                authFailureCounter.increment();
            }
            log.debug("JWT auth failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            return false;
        }
    }

    private int extractTokenVersion(Claims claims) {
        Object tv = claims.get("tv");
        if (tv instanceof Number) {
            return ((Number) tv).intValue();
        }
        return 0;
    }
}
