package com.jiangou.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder(AuthProperties authProperties) {
        int strength = authProperties.getBcryptStrength();
        if (strength < 4 || strength > 31) {
            throw new IllegalArgumentException("jiangou.auth.bcrypt-strength must be between 4 and 31");
        }
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    public JwtProperties jwtProperties(
            @Value("${jiangou.jwt.secret}") String secret,
            @Value("${jiangou.jwt.access-token-expiration-ms}") long accessExpiration,
            @Value("${jiangou.jwt.refresh-token-expiration-ms}") long refreshExpiration,
            @Value("${jiangou.jwt.cookie-secure:false}") boolean cookieSecure) {
        JwtProperties props = new JwtProperties();
        props.setSecret(secret);
        props.setAccessTokenExpirationMs(accessExpiration);
        props.setRefreshTokenExpirationMs(refreshExpiration);
        props.setCookieSecure(cookieSecure);
        return props;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
