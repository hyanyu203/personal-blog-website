package com.jiangou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
@ConfigurationProperties(prefix = "jiangou.github.oauth")
public class GitHubOAuthProperties {

    private String clientId = "";
    private String clientSecret = "";
    private String redirectUri = "http://localhost:8080/api/v1/auth/github/callback";
    /** Comma-separated GitHub usernames allowed to sign in (required for new users). */
    private String allowedUsernames = "";

    public boolean isEnabled() {
        return StringUtils.hasText(clientId) && StringUtils.hasText(clientSecret);
    }

    public List<String> getAllowedUsernameList() {
        if (!StringUtils.hasText(allowedUsernames)) {
            return Collections.emptyList();
        }
        return Arrays.stream(allowedUsernames.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    public boolean isUsernameAllowed(String username) {
        return getAllowedUsernameList().stream()
                .anyMatch(u -> u.equalsIgnoreCase(username));
    }
}
