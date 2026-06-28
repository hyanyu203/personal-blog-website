package com.jiangou.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.result.PageResult;
import com.jiangou.system.entity.AuditLogEntity;
import com.jiangou.system.mapper.AuditLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogMapper auditLogMapper;

    public AuditLogService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    public void log(Long actorId, String action, String targetType, Long targetId) {
        AuditLogEntity log = new AuditLogEntity();
        log.setActorId(actorId);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setMetadata("{}");
        log.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(log);
    }

    public PageResult<Map<String, Object>> list(long page, long pageSize) {
        Page<AuditLogEntity> result = auditLogMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<AuditLogEntity>().orderByDesc(AuditLogEntity::getCreatedAt));
        List<Map<String, Object>> items = result.getRecords().stream().map(e -> {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("id", e.getId());
            m.put("actorId", e.getActorId());
            m.put("action", e.getAction());
            m.put("targetType", e.getTargetType());
            m.put("targetId", e.getTargetId());
            m.put("createdAt", e.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }
}
