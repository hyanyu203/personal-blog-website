package com.jiangou.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthCookieServiceTest {

    @Test
    void writeTokens_setsHttpOnlyCookies() {
        AuthCookieService service = new AuthCookieService();
        MockHttpServletResponse response = new MockHttpServletResponse();
        service.writeTokens(response, "access123", "refresh456", 3600L);
        boolean hasAccess = response.getHeaders("Set-Cookie").stream()
                .anyMatch(h -> h.contains("jiangou_access=access123") && h.contains("HttpOnly"));
        assertTrue(hasAccess);
    }
}
