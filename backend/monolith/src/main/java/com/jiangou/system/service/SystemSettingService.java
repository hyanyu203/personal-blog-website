package com.jiangou.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.result.PageResult;
import com.jiangou.system.entity.AuditLogEntity;
import com.jiangou.system.entity.SystemSettingEntity;
import com.jiangou.system.mapper.AuditLogMapper;
import com.jiangou.system.mapper.SystemSettingMapper;
import com.jiangou.common.cache.RedisCacheHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SystemSettingService {

    private static final String PUBLIC_SETTINGS_CACHE_KEY = "cache:settings:public";
    private static final long CACHE_TTL_SEC = 600;
    private static final Set<String> ALLOWED_KEYS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "siteTitle",
            "siteDescription",
            "siteSubtitle",
            "siteLaunchDate",
            "guestbookTargetId",
            "webmentionEnabled",
            "defaultTheme"
    )));
    private static final Set<String> PUBLIC_KEYS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "siteTitle",
            "siteDescription",
            "siteSubtitle",
            "siteLaunchDate",
            "guestbookTargetId",
            "defaultTheme"
    )));

    private final SystemSettingMapper settingMapper;
    private final RedisCacheHelper cacheHelper;
    private final ObjectMapper objectMapper;

    public SystemSettingService(SystemSettingMapper settingMapper,
                                RedisCacheHelper cacheHelper,
                                ObjectMapper objectMapper) {
        this.settingMapper = settingMapper;
        this.cacheHelper = cacheHelper;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getPublicSettings() {
        String cached = cacheHelper.get(PUBLIC_SETTINGS_CACHE_KEY);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, Map.class);
            } catch (JsonProcessingException ignored) {
                cacheHelper.delete(PUBLIC_SETTINGS_CACHE_KEY);
            }
        }
        List<SystemSettingEntity> list = settingMapper.selectList(new LambdaQueryWrapper<SystemSettingEntity>()
                .eq(SystemSettingEntity::getIsPublic, true));
        Map<String, Object> map = new HashMap<String, Object>();
        for (SystemSettingEntity s : list) {
            map.put(s.getKey(), s.getValue());
        }
        if (map.isEmpty()) {
            map.put("siteTitle", "渐构");
            map.put("siteDescription", "渐次构建，理解计算机世界");
            map.put("defaultTheme", "system");
        }
        try {
            cacheHelper.set(PUBLIC_SETTINGS_CACHE_KEY, objectMapper.writeValueAsString(map), CACHE_TTL_SEC);
        } catch (JsonProcessingException ignored) {
            // skip cache write
        }
        return map;
    }

    public List<SystemSettingEntity> listAll() {
        return settingMapper.selectList(null);
    }

    public String getValue(String key) {
        SystemSettingEntity entity = settingMapper.selectById(key);
        return entity != null ? entity.getValue() : null;
    }

    @Transactional
    public SystemSettingEntity update(String key, String value, Long updatedBy) {
        if (!ALLOWED_KEYS.contains(key)) {
            throw new ValidationException("不支持的设置项: " + key);
        }
        SystemSettingEntity entity = settingMapper.selectById(key);
        if (entity == null) {
            entity = new SystemSettingEntity();
            entity.setKey(key);
            entity.setDescription("");
            entity.setIsPublic(PUBLIC_KEYS.contains(key));
            entity.setValue(value);
            entity.setUpdatedBy(updatedBy);
            entity.setUpdatedAt(LocalDateTime.now());
            settingMapper.insert(entity);
        } else {
            entity.setValue(value);
            entity.setUpdatedBy(updatedBy);
            entity.setUpdatedAt(LocalDateTime.now());
            settingMapper.updateById(entity);
        }
        cacheHelper.delete(PUBLIC_SETTINGS_CACHE_KEY);
        return entity;
    }
}
