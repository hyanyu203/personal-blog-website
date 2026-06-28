package com.jiangou.schedule.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.schedule.service.RssService;
import com.jiangou.security.AdminPermissions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin RSS")
@RestController
@RequestMapping("/api/v1/admin/rss")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class RssAdminController {

    private final RssService rssService;

    public RssAdminController(RssService rssService) {
        this.rssService = rssService;
    }

    @PostMapping("/rebuild")
    public ApiResult<String> rebuild() {
        return ApiResult.ok(rssService.buildFeed());
    }
}
