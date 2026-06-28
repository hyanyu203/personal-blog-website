package com.jiangou.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitHubOAuthPropertiesTest {

    @Test
    void isEnabled_requiresClientIdAndSecret() {
        GitHubOAuthProperties props = new GitHubOAuthProperties();
        assertFalse(props.isEnabled());

        props.setClientId("id");
        assertFalse(props.isEnabled());

        props.setClientSecret("secret");
        assertTrue(props.isEnabled());
    }
}
