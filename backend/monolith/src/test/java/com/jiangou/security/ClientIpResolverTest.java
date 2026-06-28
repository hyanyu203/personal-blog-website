package com.jiangou.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientIpResolverTest {

    @Test
    void prefersProxyHeadersWhenTrusted() {
        ClientIpResolver resolver = new ClientIpResolver();
        ReflectionTestUtils.setField(resolver, "trustProxyHeaders", true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP", " 203.0.113.10 ");
        request.addHeader("X-Forwarded-For", "198.51.100.20, 198.51.100.21");
        request.setRemoteAddr("127.0.0.1");

        assertEquals("203.0.113.10", resolver.resolve(request));
    }

    @Test
    void fallsBackToForwardedForThenRemoteAddr() {
        ClientIpResolver resolver = new ClientIpResolver();
        ReflectionTestUtils.setField(resolver, "trustProxyHeaders", true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "198.51.100.20, 198.51.100.21");
        request.setRemoteAddr("127.0.0.1");

        assertEquals("198.51.100.20", resolver.resolve(request));
    }

    @Test
    void ignoresProxyHeadersWhenNotTrusted() {
        ClientIpResolver resolver = new ClientIpResolver();
        ReflectionTestUtils.setField(resolver, "trustProxyHeaders", false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP", "203.0.113.10");
        request.setRemoteAddr("127.0.0.1");

        assertEquals("127.0.0.1", resolver.resolve(request));
    }
}
