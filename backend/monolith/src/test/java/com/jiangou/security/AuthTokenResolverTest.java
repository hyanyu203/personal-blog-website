package com.jiangou.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenResolverTest {

    @Mock
    private AuthCookieService authCookieService;
    @Mock
    private HttpServletRequest request;

    private AuthTokenResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new AuthTokenResolver(authCookieService);
    }

    @Test
    void resolveAccessToken_prefersBearerHeader() {
        String token = resolver.resolveAccessToken(request, "Bearer header-token");
        assertEquals("header-token", token);
        verifyNoInteractions(authCookieService);
    }

    @Test
    void resolveAccessToken_fallsBackToCookie() {
        when(authCookieService.readAccessToken(request)).thenReturn("cookie-token");
        String token = resolver.resolveAccessToken(request, null);
        assertEquals("cookie-token", token);
    }

    @Test
    void resolveAccessToken_returnsNullWhenMissing() {
        when(authCookieService.readAccessToken(request)).thenReturn(null);
        assertNull(resolver.resolveAccessToken(request, null));
    }
}
