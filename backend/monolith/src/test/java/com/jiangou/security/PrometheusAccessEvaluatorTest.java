package com.jiangou.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrometheusAccessEvaluatorTest {

    private PrometheusAccessEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new PrometheusAccessEvaluator();
    }

    @Test
    void allowsScrapeFromDockerInternalNetwork() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("172.18.0.5");
        assertTrue(evaluator.canScrape(request, null));
    }

    @Test
    void allowsScrapeFromLoopback() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        assertTrue(evaluator.canScrape(request, null));
    }

    @Test
    void allowsScrapeForAdminUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("203.0.113.10");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(evaluator.canScrape(request, auth));
    }

    @Test
    void deniesScrapeFromPublicInternet() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("203.0.113.10");
        assertFalse(evaluator.canScrape(request, null));
    }

    @Test
    void deniesScrapeForNonAdminUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("203.0.113.10");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        assertFalse(evaluator.canScrape(request, auth));
    }
}
