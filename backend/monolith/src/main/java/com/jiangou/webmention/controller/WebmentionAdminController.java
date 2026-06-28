package com.jiangou.webmention.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.security.AdminPermissions;
import com.jiangou.webmention.service.WebmentionService;
import com.jiangou.webmention.vo.WebmentionVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Webmentions")
@RestController
@RequestMapping("/api/v1/admin/webmentions")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class WebmentionAdminController {

    private final WebmentionService webmentionService;

    public WebmentionAdminController(WebmentionService webmentionService) {
        this.webmentionService = webmentionService;
    }

    @GetMapping
    public ApiResult<PageResult<WebmentionVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String status) {
        return ApiResult.ok(webmentionService.listAdmin(page, pageSize, status));
    }
}
