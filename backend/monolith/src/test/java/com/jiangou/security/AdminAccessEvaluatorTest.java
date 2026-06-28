package com.jiangou.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminAccessEvaluatorTest {

    private final AdminAccessEvaluator evaluator = new AdminAccessEvaluator();

    @Test
    void allowsAdminRole() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin", "x", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(evaluator.canAccessAdminApi(auth));
    }

    @Test
    void allowsDelegatedPermission() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "mod", "x", Collections.singletonList(new SimpleGrantedAuthority("comment:review")));
        assertTrue(evaluator.canAccessAdminApi(auth));
    }

    @Test
    void rejectsUserRoleOnly() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", "x", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        assertFalse(evaluator.canAccessAdminApi(auth));
    }

    @Test
    void rejectsAnonymous() {
        assertFalse(evaluator.canAccessAdminApi(null));
    }
}
