package com.jiangou.friendlink.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.common.util.AuditSupport;
import com.jiangou.friendlink.dto.FriendLinkDTO;
import com.jiangou.friendlink.service.FriendLinkService;
import com.jiangou.friendlink.vo.FriendLinkVO;
import com.jiangou.security.AdminPermissions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;

@Tag(name = "Admin Friend Links")
@RestController
@RequestMapping("/api/v1/admin/friend-links")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class FriendLinkAdminController {

    private final FriendLinkService friendLinkService;
    private final AuditSupport auditSupport;

    public FriendLinkAdminController(FriendLinkService friendLinkService, AuditSupport auditSupport) {
        this.friendLinkService = friendLinkService;
        this.auditSupport = auditSupport;
    }

    @GetMapping
    public ApiResult<PageResult<FriendLinkVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String status) {
        return ApiResult.ok(friendLinkService.listAdmin(page, pageSize, status));
    }

    @PostMapping
    public ApiResult<FriendLinkVO> create(@Valid @RequestBody FriendLinkDTO dto) {
        FriendLinkVO created = friendLinkService.create(dto);
        auditSupport.log("friendlink:create", "friendlink", created.getId());
        return ApiResult.ok(created);
    }

    @PatchMapping("/{id}")
    public ApiResult<FriendLinkVO> update(@PathVariable Long id, @RequestBody FriendLinkDTO dto) {
        FriendLinkVO updated = friendLinkService.update(id, dto);
        auditSupport.log("friendlink:update", "friendlink", id);
        return ApiResult.ok(updated);
    }

    @PostMapping("/{id}/approve")
    public ApiResult<Void> approve(@PathVariable Long id) {
        friendLinkService.approve(id);
        auditSupport.log("friendlink:approve", "friendlink", id);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/reject")
    public ApiResult<Void> reject(@PathVariable Long id) {
        friendLinkService.reject(id);
        auditSupport.log("friendlink:reject", "friendlink", id);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        friendLinkService.delete(id);
        auditSupport.log("friendlink:delete", "friendlink", id);
        return ApiResult.ok();
    }
}
