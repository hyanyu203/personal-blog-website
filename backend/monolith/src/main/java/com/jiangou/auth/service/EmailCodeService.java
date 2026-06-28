package com.jiangou.auth.service;

import com.jiangou.common.constant.ErrorCodes;
import com.jiangou.common.exception.BusinessException;
import com.jiangou.config.AuthProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class EmailCodeService {

    private static final Logger log = LoggerFactory.getLogger(EmailCodeService.class);

    private static final DefaultRedisScript<Long> VERIFY_AND_CONSUME_SCRIPT = new DefaultRedisScript<Long>();

    static {
        VERIFY_AND_CONSUME_SCRIPT.setLocation(new ClassPathResource("redis/email_code_verify_and_consume.lua"));
        VERIFY_AND_CONSUME_SCRIPT.setResultType(Long.class);
    }

    public enum Purpose {
        REGISTER("register"),
        RESET_PASSWORD("reset");

        private final String code;

        Purpose(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final AuthProperties authProperties;
    private final SecureRandom random = new SecureRandom();

    @Value("${jiangou.site-title:渐构}")
    private String siteTitle;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    public EmailCodeService(StringRedisTemplate redisTemplate, JavaMailSender mailSender,
                            AuthProperties authProperties) {
        this.redisTemplate = redisTemplate;
        this.mailSender = mailSender;
        this.authProperties = authProperties;
    }

    public void sendCode(String email, Purpose purpose, String clientIp) {
        String normalizedEmail = normalizeEmail(email);
        enforceRateLimit(normalizedEmail, purpose, clientIp);

        String code = randomNumericCode(6);
        String codeKey = codeKey(normalizedEmail, purpose);
        redisTemplate.opsForValue().set(
                codeKey,
                hash(code),
                authProperties.getEmailCodeTtlSeconds(),
                TimeUnit.SECONDS);

        markSent(normalizedEmail, purpose, clientIp);

        sendEmail(normalizedEmail, purpose, code);
    }

    /**
     * Records send rate-limit state without delivering mail (anti-enumeration for reset-password).
     */
    public void recordSendAttempt(String email, Purpose purpose, String clientIp) {
        String normalizedEmail = normalizeEmail(email);
        enforceRateLimit(normalizedEmail, purpose, clientIp);
        markSent(normalizedEmail, purpose, clientIp);
    }

    public void verifyAndConsume(String email, Purpose purpose, String code) {
        String normalizedEmail = normalizeEmail(email);
        enforceVerifyAttemptLimit(normalizedEmail, purpose);
        String codeKey = codeKey(normalizedEmail, purpose);
        String attemptKey = verifyAttemptKey(normalizedEmail, purpose);
        Long consumed = redisTemplate.execute(
                VERIFY_AND_CONSUME_SCRIPT,
                Arrays.asList(codeKey, attemptKey),
                hash(code.trim()));
        if (consumed == null || consumed != 1L) {
            recordFailedVerifyAttempt(normalizedEmail, purpose);
            throw new BusinessException(ErrorCodes.INVALID_CODE, "验证码错误或已过期");
        }
    }

    private void markSent(String normalizedEmail, Purpose purpose, String clientIp) {
        String sentKey = sentKey(normalizedEmail, purpose);
        redisTemplate.opsForValue().set(
                sentKey,
                "1",
                authProperties.getEmailCodeSendIntervalSeconds(),
                TimeUnit.SECONDS);

        incrementDailyCounter("email-code:daily:email:" + normalizedEmail + ":" + today());
        incrementDailyCounter("email-code:daily:ip:" + clientIp + ":" + today());
    }

    private void enforceVerifyAttemptLimit(String email, Purpose purpose) {
        if (count(verifyAttemptKey(email, purpose)) >= authProperties.getEmailCodeVerifyMaxAttempts()) {
            throw new BusinessException(ErrorCodes.CODE_RATE_LIMIT, "验证码尝试次数过多，请重新获取");
        }
    }

    private void recordFailedVerifyAttempt(String email, Purpose purpose) {
        String key = verifyAttemptKey(email, purpose);
        incrementCounter(key, authProperties.getEmailCodeVerifyAttemptWindowSeconds());
    }

    private String verifyAttemptKey(String email, Purpose purpose) {
        return "email-code:attempts:" + purpose.code() + ":" + email;
    }

    private void incrementCounter(String key, long windowSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }
    }

    private void enforceRateLimit(String email, Purpose purpose, String clientIp) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(sentKey(email, purpose)))) {
            throw new BusinessException(ErrorCodes.CODE_RATE_LIMIT, "发送过于频繁，请稍后再试");
        }
        String emailDailyKey = "email-code:daily:email:" + email + ":" + today();
        String ipDailyKey = "email-code:daily:ip:" + clientIp + ":" + today();
        if (count(emailDailyKey) >= authProperties.getEmailCodeDailyLimitPerEmail()) {
            throw new BusinessException(ErrorCodes.CODE_RATE_LIMIT, "今日发送次数已达上限");
        }
        if (count(ipDailyKey) >= authProperties.getEmailCodeDailyLimitPerIp()) {
            throw new BusinessException(ErrorCodes.CODE_RATE_LIMIT, "今日发送次数已达上限");
        }
    }

    private void sendEmail(String email, Purpose purpose, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (StringUtils.hasText(mailFrom)) {
            message.setFrom(mailFrom);
        }
        message.setTo(email);
        if (purpose == Purpose.REGISTER) {
            message.setSubject("【" + siteTitle + "】注册验证码");
            message.setText("您的注册验证码为：" + code + "，" + authProperties.getEmailCodeTtlSeconds() / 60
                    + " 分钟内有效。如非本人操作请忽略。");
        } else {
            message.setSubject("【" + siteTitle + "】重置密码验证码");
            message.setText("您的重置密码验证码为：" + code + "，" + authProperties.getEmailCodeTtlSeconds() / 60
                    + " 分钟内有效。如非本人操作请忽略。");
        }
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send email code to {}", email, e);
            throw new BusinessException(ErrorCodes.VALIDATION, "邮件发送失败，请检查邮箱或稍后重试");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String codeKey(String email, Purpose purpose) {
        return "email-code:" + purpose.code() + ":" + email;
    }

    private String sentKey(String email, Purpose purpose) {
        return "email-code:sent:" + purpose.code() + ":" + email;
    }

    private String hash(String code) {
        return sha256Hex(code.trim());
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return DigestUtils.md5DigestAsHex(value.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String randomNumericCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private void incrementDailyCounter(String key) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }
    }

    private int count(String key) {
        String val = redisTemplate.opsForValue().get(key);
        return val == null ? 0 : Integer.parseInt(val);
    }

    private String today() {
        return LocalDate.now().toString();
    }
}
