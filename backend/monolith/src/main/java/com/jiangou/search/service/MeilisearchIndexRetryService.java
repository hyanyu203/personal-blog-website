package com.jiangou.search.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class MeilisearchIndexRetryService {

    private static final String FAILED_SET = "meilisearch:sync:failed";
    private static final String FULL_REBUILD_KEY = "__full_rebuild__";

    private final StringRedisTemplate redisTemplate;

    public MeilisearchIndexRetryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void markFailed(String targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            return;
        }
        redisTemplate.opsForSet().add(FAILED_SET, key(targetType, targetId));
    }

    public void clearFailed(String targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            return;
        }
        redisTemplate.opsForSet().remove(FAILED_SET, key(targetType, targetId));
    }

    public void markFullRebuildFailed() {
        redisTemplate.opsForSet().add(FAILED_SET, FULL_REBUILD_KEY);
    }

    public void clearFullRebuildFailed() {
        redisTemplate.opsForSet().remove(FAILED_SET, FULL_REBUILD_KEY);
    }

    public Set<String> pollFailedKeys() {
        Set<String> members = redisTemplate.opsForSet().members(FAILED_SET);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        return members;
    }

    public boolean isFullRebuildKey(String key) {
        return FULL_REBUILD_KEY.equals(key);
    }

    static String key(String targetType, Long targetId) {
        return targetType + ":" + targetId;
    }
}
