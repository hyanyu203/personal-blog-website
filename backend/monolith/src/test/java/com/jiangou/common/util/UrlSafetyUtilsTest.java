package com.jiangou.common.util;

import com.jiangou.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlSafetyUtilsTest {

    @Test
    void rejectsLocalhost() {
        assertThrows(ValidationException.class, () -> UrlSafetyUtils.validateExternalHttpUrl("http://localhost/post"));
    }

    @Test
    void rejectsPrivateIp() {
        assertThrows(ValidationException.class, () -> UrlSafetyUtils.validateExternalHttpUrl("http://192.168.1.1/"));
    }

    @Test
    void allowsPublicHttps() {
        assertDoesNotThrow(() -> UrlSafetyUtils.validateExternalHttpUrl("https://example.com/article"));
    }

    @Test
    void rejectsJavascriptUrl() {
        assertThrows(ValidationException.class,
                () -> UrlSafetyUtils.normalizeOptionalHttpUrl("javascript:alert(1)"));
    }

    @Test
    void rejectsHttpsLocalhost() {
        assertThrows(ValidationException.class,
                () -> UrlSafetyUtils.validateExternalHttpUrl("https://localhost/post"));
    }

    @Test
    void allowsEmptyOptionalUrl() {
        assertDoesNotThrow(() -> UrlSafetyUtils.normalizeOptionalHttpUrl(null));
        assertDoesNotThrow(() -> UrlSafetyUtils.normalizeOptionalHttpUrl(""));
    }

    @Test
    void validateSameSiteTarget_rejectsPrefixBypassHost() {
        assertThrows(ValidationException.class,
                () -> UrlSafetyUtils.validateSameSiteTarget(
                        "https://example.com.evil.com/post",
                        "https://example.com"));
    }

    @Test
    void validateSameSiteTarget_acceptsSameHostPath() {
        assertDoesNotThrow(() -> UrlSafetyUtils.validateSameSiteTarget(
                "https://example.com/posts/1",
                "https://example.com"));
    }

    @Test
    void validateSameSiteTarget_rejectsDifferentHost() {
        assertThrows(ValidationException.class,
                () -> UrlSafetyUtils.validateSameSiteTarget(
                        "https://evil.com/posts/1",
                        "https://example.com"));
    }
}
