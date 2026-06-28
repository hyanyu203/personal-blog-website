package com.jiangou.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.common.constant.ErrorCodes;
import com.jiangou.common.result.ApiResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class CsrfFilter extends OncePerRequestFilter {

    private static final Set<String> EXEMPT_PATHS = new HashSet<String>(Arrays.asList(
            "/api/v1/webmention"
    ));

    private final CsrfCookieService csrfCookieService;
    private final ObjectMapper objectMapper;

    public CsrfFilter(CsrfCookieService csrfCookieService, ObjectMapper objectMapper) {
        this.csrfCookieService = csrfCookieService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method) || HttpMethod.OPTIONS.matches(method)) {
            csrfCookieService.ensureToken(request, response);
            filterChain.doFilter(request, response);
            return;
        }
        if (EXEMPT_PATHS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!csrfCookieService.validate(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(
                    ApiResult.fail(ErrorCodes.CSRF, "CSRF 校验失败，请刷新页面后重试")));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
