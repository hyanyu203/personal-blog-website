package com.jiangou.common.service;

import com.jiangou.common.exception.UnauthorizedException;
import com.jiangou.common.exception.ValidationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class LikeCounterService {

    private static final String COUNT_PREFIX = "counter:";
    private static final String DEDUP_PREFIX = "like:";
    private static final String DIRTY_SET = "counter:dirty";

    private final StringRedisTemplate redisTemplate;

    public LikeCounterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long incrementByUser(String type, Long id, Long userId, long dbFallback) {
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        String dedupKey = DEDUP_PREFIX + type + ":" + id + ":user:" + userId;
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(dedupKey, "1", 30, TimeUnit.DAYS);
        if (!Boolean.TRUE.equals(acquired)) {
            throw new ValidationException("已经点赞过了");
        }
        String countKey = COUNT_PREFIX + type + ":" + id;
        try {
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(countKey)) && dbFallback > 0) {
                redisTemplate.opsForValue().set(countKey, String.valueOf(dbFallback));
            }
            Long count = redisTemplate.opsForValue().increment(countKey);
            redisTemplate.opsForSet().add(DIRTY_SET, type + ":" + id);
            return count == null ? 1L : count;
        } catch (RuntimeException e) {
            redisTemplate.delete(dedupKey);
            throw e;
        }
    }

    public long getCount(String type, Long id, long dbFallback) {
        String countKey = COUNT_PREFIX + type + ":" + id;
        String val = redisTemplate.opsForValue().get(countKey);
        if (val != null) {
            return Long.parseLong(val);
        }
        if (dbFallback > 0) {
            redisTemplate.opsForValue().set(countKey, String.valueOf(dbFallback));
        }
        return dbFallback;
    }

    public Map<Long, Long> getCounts(String type, List<Long> ids, Map<Long, Long> dbFallbacks) {
        Map<Long, Long> result = new HashMap<Long, Long>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        List<String> keys = new java.util.ArrayList<String>();
        for (Long id : ids) {
            keys.add(COUNT_PREFIX + type + ":" + id);
        }
        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            String val = values != null && i < values.size() ? values.get(i) : null;
            if (val != null) {
                result.put(id, Long.parseLong(val));
            } else {
                long fallback = dbFallbacks != null && dbFallbacks.containsKey(id) ? dbFallbacks.get(id) : 0L;
                if (fallback > 0) {
                    redisTemplate.opsForValue().set(COUNT_PREFIX + type + ":" + id, String.valueOf(fallback));
                }
                result.put(id, fallback);
            }
        }
        return result;
    }

    public Set<String> pollDirtyKeys() {
        Set<String> members = redisTemplate.opsForSet().members(DIRTY_SET);
        if (members == null || members.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return members;
    }

    public void clearDirtyKey(String member) {
        redisTemplate.opsForSet().remove(DIRTY_SET, member);
    }
}
