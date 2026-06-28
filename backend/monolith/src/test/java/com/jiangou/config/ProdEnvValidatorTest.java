package com.jiangou.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdEnvValidatorTest {

    private ProdEnvValidator validator() {
        ProdEnvValidator validator = new ProdEnvValidator();
        ReflectionTestUtils.setField(validator, "mysqlPassword", "mysql-pass");
        ReflectionTestUtils.setField(validator, "mysqlUser", "jiangou");
        ReflectionTestUtils.setField(validator, "jwtSecret", "valid-jwt-secret-at-least-32-characters-long");
        ReflectionTestUtils.setField(validator, "minioSecretKey", "secure-minio-secret");
        ReflectionTestUtils.setField(validator, "minioAccessKey", "jiangou-minio");
        ReflectionTestUtils.setField(validator, "redisPassword", "secure-redis-password");
        ReflectionTestUtils.setField(validator, "searchEngine", "mysql");
        ReflectionTestUtils.setField(validator, "meilisearchApiKey", "");
        ReflectionTestUtils.setField(validator, "registrationEnabled", false);
        ReflectionTestUtils.setField(validator, "siteUrl", "https://example.com");
        ReflectionTestUtils.setField(validator, "adminUrl", "https://example.com/admin");
        ReflectionTestUtils.setField(validator, "corsAllowedOrigins", "https://example.com");
        ReflectionTestUtils.setField(validator, "jwtCookieSecure", true);
        ReflectionTestUtils.setField(validator, "prometheusScrapeToken", "secure-prometheus-token");
        return validator;
    }

    @Test
    void rejectsMissingMysqlPassword() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "mysqlPassword", "");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void rejectsShortJwtSecret() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "jwtSecret", "too-short");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void rejectsLocalhostSiteUrl() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "siteUrl", "http://localhost");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void rejectsLocalhostAdminUrl() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "adminUrl", "http://localhost/admin");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void rejectsDefaultMinioSecret() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "minioSecretKey", "minioadmin");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void acceptsValidConfig() {
        assertDoesNotThrow(validator()::validate);
    }

    @Test
    void rejectsMissingMailWhenRegistrationEnabled() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "registrationEnabled", true);
        ReflectionTestUtils.setField(validator, "mailHost", "");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void rejectsMissingRedisPassword() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "redisPassword", "");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void rejectsMissingGitHubAllowlistWhenOAuthEnabled() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "githubOAuthClientId", "client-id");
        ReflectionTestUtils.setField(validator, "githubOAuthAllowedUsernames", "");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void rejectsDefaultMeilisearchKeyWhenEngineEnabled() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "searchEngine", "meilisearch");
        ReflectionTestUtils.setField(validator, "meilisearchApiKey", "masterKey");
        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void rejectsMissingPrometheusScrapeToken() {
        ProdEnvValidator validator = validator();
        ReflectionTestUtils.setField(validator, "prometheusScrapeToken", "");
        assertThrows(IllegalStateException.class, validator::validate);
    }
}
