package com.jiangou.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component("prometheusAccess")
public class PrometheusAccessEvaluator {

    @Value("${jiangou.prometheus.scrape-token:}")
    private String scrapeToken;

    public boolean canScrape(HttpServletRequest request, Authentication authentication) {
        if (StringUtils.hasText(scrapeToken) && request != null) {
            String token = request.getHeader("X-Prometheus-Token");
            if (!StringUtils.hasText(token)) {
                String authorization = request.getHeader("Authorization");
                if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
                    token = authorization.substring(7);
                }
            }
            return scrapeToken.equals(token);
        }
        if (request != null && isInternalNetwork(request.getRemoteAddr())) {
            return true;
        }
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInternalNetwork(String remoteAddr) {
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            return false;
        }
        try {
            InetAddress address = InetAddress.getByName(remoteAddr);
            return address.isLoopbackAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
