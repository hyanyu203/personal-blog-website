package com.jiangou.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtTokenBlacklistService {

    private static final String PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public JwtTokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(token)));
    }

    /**
     * Atomically blacklist a token. Returns false if already blacklisted (e.g. concurrent refresh reuse).
     */
    public boolean blacklistIfAbsent(String token, Date expiresAt) {
        long ttlMs = expiresAt.getTime() - System.currentTimeMillis();
        if (ttlMs <= 0) {
            return false;
        }
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key(token), "1", ttlMs, TimeUnit.MILLISECONDS));
    }

    public void blacklist(String token, Date expiresAt) {
        long ttlMs = expiresAt.getTime() - System.currentTimeMillis();
        if (ttlMs <= 0) {
            return;
        }
        redisTemplate.opsForValue().set(key(token), "1", ttlMs, TimeUnit.MILLISECONDS);
    }

    private String key(String token) {
        return PREFIX + sha256Hex(token);
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
}
