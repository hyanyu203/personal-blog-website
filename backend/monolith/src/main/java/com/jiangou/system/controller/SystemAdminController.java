package com.jiangou.system.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.security.AdminPermissions;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
import com.jiangou.system.entity.SystemSettingEntity;
import com.jiangou.system.service.AuditLogService;
import com.jiangou.system.service.StatsService;
import com.jiangou.system.service.SystemSettingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin System")
@RestController
@RequestMapping("/api/v1/admin")
public class SystemAdminController {

    private final SystemSettingService systemSettingService;
    private final AuditLogService auditLogService;
    private final SecurityUserDetailsService userDetailsService;
    private final StatsService statsService;

    public SystemAdminController(SystemSettingService systemSettingService,
                                 AuditLogService auditLogService,
                                 SecurityUserDetailsService userDetailsService,
                                 StatsService statsService) {
        this.systemSettingService = systemSettingService;
        this.auditLogService = auditLogService;
        this.userDetailsService = userDetailsService;
        this.statsService = statsService;
    }

    @GetMapping("/settings")
    @PreAuthorize(AdminPermissions.SETTING_UPDATE)
    public ApiResult<List<SystemSettingEntity>> settings() {
        return ApiResult.ok(systemSettingService.listAll());
    }

    @PatchMapping("/settings/{key}")
    @PreAuthorize(AdminPermissions.SETTING_UPDATE)
    public ApiResult<SystemSettingEntity> updateSetting(@PathVariable String key,
                                                        @RequestBody Map<String, String> body) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        return ApiResult.ok(systemSettingService.update(key, body.get("value"), userId));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<PageResult<Map<String, Object>>> auditLogs(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize) {
        return ApiResult.ok(auditLogService.list(page, pageSize));
    }

    @GetMapping("/stats")
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<Map<String, Object>> stats() {
        return ApiResult.ok(statsService.adminStats());
    }
}
