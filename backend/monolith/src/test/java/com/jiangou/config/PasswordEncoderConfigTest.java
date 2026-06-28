package com.jiangou.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderConfigTest {

    @Test
    void bcryptStrength12_producesModernHashPrefix() {
        PasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String hash = encoder.encode("TestPass1");
        assertTrue(hash.startsWith("$2a$12$") || hash.startsWith("$2b$12$") || hash.startsWith("$2y$12$"));
    }

    @Test
    void upgradeEncoding_detectsWeakerLegacyHash() {
        PasswordEncoder legacy = new BCryptPasswordEncoder(10);
        PasswordEncoder current = new BCryptPasswordEncoder(12);
        String oldHash = legacy.encode("TestPass1");
        assertTrue(current.upgradeEncoding(oldHash));
        assertFalse(current.upgradeEncoding(current.encode("TestPass1")));
    }

    @Test
    void matches_acrossSameAlgorithm() {
        PasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String hash = encoder.encode("SecurePass99");
        assertTrue(encoder.matches("SecurePass99", hash));
        assertFalse(encoder.matches("wrong", hash));
    }
}
