package com.jiangou.snippet.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnippetServiceTest {

    @Test
    void sanitizeLanguage_stripsUnsafeCharacters() throws Exception {
        SnippetService service = new SnippetService(null, null);
        Method method = SnippetService.class.getDeclaredMethod("sanitizeLanguage", String.class);
        method.setAccessible(true);
        String sanitized = (String) method.invoke(service, "java\"><img onerror=alert(1)>");
        assertFalse(sanitized.contains("<"));
        assertFalse(sanitized.contains("\""));
        assertTrue(sanitized.startsWith("java"));
    }
}
