package com.jiangou.subscription.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.common.util.AuditSupport;
import com.jiangou.security.AdminPermissions;
import com.jiangou.subscription.dto.NewsletterDTO;
import com.jiangou.subscription.service.SubscriptionService;
import com.jiangou.subscription.vo.SubscriptionVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Tag(name = "Admin Subscriptions")
@RestController
@RequestMapping("/api/v1/admin/subscriptions")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class SubscriptionAdminController {

    private final SubscriptionService subscriptionService;
    private final AuditSupport auditSupport;

    public SubscriptionAdminController(SubscriptionService subscriptionService, AuditSupport auditSupport) {
        this.subscriptionService = subscriptionService;
        this.auditSupport = auditSupport;
    }

    @GetMapping
    public ApiResult<PageResult<SubscriptionVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String status) {
        return ApiResult.ok(subscriptionService.listAdmin(page, pageSize, status));
    }

    @PostMapping("/newsletter")
    public ApiResult<Map<String, Object>> sendNewsletter(@Valid @RequestBody NewsletterDTO dto) {
        int sent = subscriptionService.sendNewsletter(dto.getSubject(), dto.getBody());
        auditSupport.log("subscription:newsletter", "subscription", 0L);
        return ApiResult.ok(Collections.singletonMap("sent", sent));
    }
}
