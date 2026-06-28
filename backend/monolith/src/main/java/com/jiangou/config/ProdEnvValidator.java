package com.jiangou.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

@Component
@Profile("prod")
public class ProdEnvValidator {

    private static final String DEFAULT_SECRET = "jiangou-dev-secret-change-in-production-min-32-chars";
    private static final String DEFAULT_MINIO_SECRET = "minioadmin";
    private static final String DEFAULT_MINIO_USER = "minioadmin";
    private static final String DEFAULT_MEILI_KEY = "masterKey";

    @Value("${spring.datasource.password:}")
    private String mysqlPassword;

    @Value("${spring.datasource.username:}")
    private String mysqlUser;

    @Value("${jiangou.jwt.secret:}")
    private String jwtSecret;

    @Value("${jiangou.jwt.cookie-secure:true}")
    private boolean jwtCookieSecure;

    @Value("${jiangou.auth.registration-enabled:false}")
    private boolean registrationEnabled;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${jiangou.minio.secret-key:}")
    private String minioSecretKey;

    @Value("${jiangou.minio.access-key:}")
    private String minioAccessKey;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${jiangou.search.engine:mysql}")
    private String searchEngine;

    @Value("${jiangou.search.meilisearch.api-key:}")
    private String meilisearchApiKey;

    @Value("${jiangou.github.oauth.client-id:}")
    private String githubOAuthClientId;

    @Value("${jiangou.github.oauth.client-secret:}")
    private String githubOAuthClientSecret;

    @Value("${jiangou.github.oauth.allowed-usernames:}")
    private String githubOAuthAllowedUsernames;

    @Value("${jiangou.site-url:}")
    private String siteUrl;

    @Value("${jiangou.admin-url:}")
    private String adminUrl;

    @Value("${jiangou.cors.allowed-origin-patterns:}")
    private String corsAllowedOrigins;

    @Value("${jiangou.prometheus.scrape-token:}")
    private String prometheusScrapeToken;

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(mysqlPassword)) {
            throw new IllegalStateException("生产环境必须设置 MYSQL_PASSWORD 环境变量");
        }
        if ("root".equalsIgnoreCase(mysqlUser)) {
            throw new IllegalStateException("生产环境 MYSQL_USER 不应使用 root");
        }
        if (!StringUtils.hasText(jwtSecret) || DEFAULT_SECRET.equals(jwtSecret)) {
            throw new IllegalStateException("生产环境必须设置安全的 JWT_SECRET 环境变量");
        }
        if (jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET 长度至少 32 字符");
        }
        if (!StringUtils.hasText(minioSecretKey) || DEFAULT_MINIO_SECRET.equals(minioSecretKey)) {
            throw new IllegalStateException("生产环境必须设置安全的 MINIO_SECRET_KEY 环境变量");
        }
        if (!StringUtils.hasText(minioAccessKey) || DEFAULT_MINIO_USER.equals(minioAccessKey)) {
            throw new IllegalStateException("生产环境 MINIO_ACCESS_KEY 不应使用默认 minioadmin");
        }
        if (!StringUtils.hasText(redisPassword)) {
            throw new IllegalStateException("生产环境必须设置 REDIS_PASSWORD 环境变量");
        }
        if ("meilisearch".equalsIgnoreCase(searchEngine)) {
            if (!StringUtils.hasText(meilisearchApiKey) || DEFAULT_MEILI_KEY.equals(meilisearchApiKey)) {
                throw new IllegalStateException("启用 Meilisearch 时必须设置安全的 MEILISEARCH_API_KEY");
            }
        }
        if (registrationEnabled && !StringUtils.hasText(mailHost)) {
            throw new IllegalStateException("启用用户注册时 MAIL_HOST 必填");
        }
        if (registrationEnabled && !StringUtils.hasText(mailPassword)) {
            throw new IllegalStateException("启用用户注册时 MAIL_PASSWORD 必填");
        }
        if (StringUtils.hasText(githubOAuthClientId)) {
            if (!StringUtils.hasText(githubOAuthClientSecret)) {
                throw new IllegalStateException("启用 GitHub OAuth 时必须配置 GITHUB_OAUTH_CLIENT_SECRET");
            }
            if (!StringUtils.hasText(githubOAuthAllowedUsernames)) {
                throw new IllegalStateException("启用 GitHub OAuth 时必须配置 GITHUB_OAUTH_ALLOWED_USERNAMES");
            }
        }
        rejectLocalhostUrl(siteUrl, "SITE_URL");
        rejectLocalhostUrl(adminUrl, "ADMIN_URL");
        rejectLocalhostUrl(corsAllowedOrigins, "CORS_ALLOWED_ORIGINS");
        if (isHttpsSiteUrl(siteUrl) && !jwtCookieSecure) {
            throw new IllegalStateException("HTTPS 站点必须启用 JWT_COOKIE_SECURE=true");
        }
        if (!StringUtils.hasText(prometheusScrapeToken)) {
            throw new IllegalStateException("生产环境必须设置 PROMETHEUS_SCRAPE_TOKEN 环境变量");
        }
    }

    private void rejectLocalhostUrl(String value, String name) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .forEach(origin -> {
                    if (isLocalhostOrigin(origin)) {
                        throw new IllegalStateException(name + " 不应使用 localhost: " + origin);
                    }
                });
    }

    private boolean isLocalhostOrigin(String origin) {
        try {
            URI uri = URI.create(origin.contains("://") ? origin : "http://" + origin);
            String host = uri.getHost();
            if (host == null) {
                return false;
            }
            host = host.toLowerCase(Locale.ROOT);
            return "localhost".equals(host) || "127.0.0.1".equals(host) || host.endsWith(".local");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isHttpsSiteUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        try {
            return "https".equalsIgnoreCase(URI.create(url.trim()).getScheme());
        } catch (Exception e) {
            return false;
        }
    }
}
