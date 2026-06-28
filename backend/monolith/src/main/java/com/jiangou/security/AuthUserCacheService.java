package com.jiangou.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.service.RoleService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthUserCacheService {

    private static final String PREFIX = "auth:userdetails:";
    private static final String INDEX_PREFIX = "auth:userdetails:index:";
    private static final long TTL_MINUTES = 5;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private final RoleService roleService;

    public AuthUserCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper,
                                UserMapper userMapper, RoleService roleService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
        this.roleService = roleService;
    }

    public UserDetails loadUserById(Long userId, int tokenVersion) {
        String key = PREFIX + userId + ":" + tokenVersion;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                return fromJson(cached);
            } catch (JsonProcessingException ignored) {
                redisTemplate.delete(key);
            }
        }
        UserEntity entity = userMapper.selectById(userId);
        if (entity == null || entity.getDeletedAt() != null) {
            return null;
        }
        int userVersion = entity.getTokenVersion() == null ? 0 : entity.getTokenVersion();
        if (userVersion != tokenVersion) {
            return null;
        }
        UserDetails details = toUserDetails(entity);
        try {
            redisTemplate.opsForValue().set(key, toJson(details), TTL_MINUTES, TimeUnit.MINUTES);
            String indexKey = INDEX_PREFIX + userId;
            redisTemplate.opsForSet().add(indexKey, key);
            redisTemplate.expire(indexKey, TTL_MINUTES, TimeUnit.MINUTES);
        } catch (JsonProcessingException ignored) {
            // skip cache write
        }
        return details;
    }

    public void evict(Long userId) {
        if (userId == null) {
            return;
        }
        String indexKey = INDEX_PREFIX + userId;
        try {
            Set<String> keys = redisTemplate.opsForSet().members(indexKey);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            redisTemplate.delete(indexKey);
        } catch (Exception ignored) {
            // best-effort; entries still expire via TTL
        }
    }

    private UserDetails toUserDetails(UserEntity entity) {
        List<String> roles = roleService.getRoleCodes(entity.getId());
        List<String> permissions = roleService.getPermissionCodes(entity.getId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(roles.stream()
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code.toUpperCase()))
                .collect(Collectors.toList()));
        permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
        return new User(entity.getUsername(),
                entity.getPasswordHash() == null ? "" : entity.getPasswordHash(),
                "active".equals(entity.getStatus()),
                true, true, true,
                authorities);
    }

    private String toJson(UserDetails details) throws JsonProcessingException {
        CachedUser cached = new CachedUser();
        cached.username = details.getUsername();
        cached.password = "";
        cached.enabled = details.isEnabled();
        cached.authorities = details.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
        return objectMapper.writeValueAsString(cached);
    }

    private UserDetails fromJson(String json) throws JsonProcessingException {
        CachedUser cached = objectMapper.readValue(json, CachedUser.class);
        List<SimpleGrantedAuthority> authorities = cached.authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new User(cached.username, cached.password, cached.enabled,
                true, true, true, authorities);
    }

    private static class CachedUser {
        public String username;
        public String password;
        public boolean enabled;
        public List<String> authorities;
    }
}
