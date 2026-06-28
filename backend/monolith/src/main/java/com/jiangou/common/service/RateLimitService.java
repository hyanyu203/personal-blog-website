package com.jiangou.common.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RateLimitService {

    /**
     * Atomically increment the counter and set expiry on first increment.
     * Using a Lua script ensures INCR + EXPIRE are executed as a single atomic unit,
     * preventing a stuck key with no TTL if the process crashes between the two commands.
     */
    private static final RedisScript<Long> RATE_LIMIT_SCRIPT = RedisScript.of(
            "local count = redis.call('INCR', KEYS[1])\n" +
            "if count == 1 then\n" +
            "  redis.call('EXPIRE', KEYS[1], ARGV[1])\n" +
            "end\n" +
            "return count",
            Long.class
    );

    private final StringRedisTemplate redisTemplate;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryAcquire(String key, int maxRequests, long windowSeconds) {
        Long count = redisTemplate.execute(
                RATE_LIMIT_SCRIPT,
                Collections.singletonList("ratelimit:" + key),
                String.valueOf(windowSeconds)
        );
        return count == null || count <= maxRequests;
    }
}
