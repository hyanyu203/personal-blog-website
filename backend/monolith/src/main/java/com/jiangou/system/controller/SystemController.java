package com.jiangou.system.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.system.service.StatsService;
import com.jiangou.system.service.SystemSettingService;
import com.jiangou.system.service.HomeService;
import com.jiangou.system.vo.HomeVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "System")
@RestController
@RequestMapping("/api/v1")
public class SystemController {

    private final SystemSettingService systemSettingService;
    private final StatsService statsService;
    private final HomeService homeService;

    public SystemController(SystemSettingService systemSettingService, StatsService statsService,
                            HomeService homeService) {
        this.systemSettingService = systemSettingService;
        this.statsService = statsService;
        this.homeService = homeService;
    }

    @GetMapping("/home")
    public ApiResult<HomeVO> home() {
        return ApiResult.ok(homeService.loadHome());
    }

    @GetMapping("/settings/public")
    public ApiResult<Map<String, Object>> publicSettings() {
        return ApiResult.ok(systemSettingService.getPublicSettings());
    }

    @GetMapping("/stats")
    public ApiResult<Map<String, Object>> stats() {
        return ApiResult.ok(statsService.publicStats());
    }
}
