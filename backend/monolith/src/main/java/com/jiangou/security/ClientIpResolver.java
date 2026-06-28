package com.jiangou.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class ClientIpResolver {

    @Value("${jiangou.rate-limit.trust-proxy-headers:false}")
    private boolean trustProxyHeaders;

    public String resolve(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        if (trustProxyHeaders) {
            String realIp = normalize(request.getHeader("X-Real-IP"));
            if (StringUtils.hasText(realIp)) {
                return realIp;
            }
            String forwardedFor = normalizeForwardedFor(request.getHeader("X-Forwarded-For"));
            if (StringUtils.hasText(forwardedFor)) {
                return forwardedFor;
            }
        }
        return normalize(request.getRemoteAddr());
    }

    private String normalizeForwardedFor(String header) {
        if (!StringUtils.hasText(header)) {
            return null;
        }
        int comma = header.indexOf(',');
        return normalize(comma >= 0 ? header.substring(0, comma) : header);
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
