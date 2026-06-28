package com.jiangou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jiangou.auth")
public class AuthProperties {

    private boolean registrationEnabled = true;
    private int emailCodeTtlSeconds = 300;
    private int emailCodeSendIntervalSeconds = 60;
    private int emailCodeDailyLimitPerEmail = 10;
    private int emailCodeDailyLimitPerIp = 20;
    /** Max failed verify attempts per email within the verify attempt window. */
    private int emailCodeVerifyMaxAttempts = 5;
    /** Window (seconds) for counting failed email-code verify attempts. */
    private int emailCodeVerifyAttemptWindowSeconds = 600;
    private int captchaTtlSeconds = 300;
    /** BCrypt cost factor; 10–12 is common in production (OWASP / Spring Boot). */
    private int bcryptStrength = 12;
}
