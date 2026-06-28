package com.jiangou.upload.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.security.AdminPermissions;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
import com.jiangou.upload.service.UploadService;
import com.jiangou.upload.vo.AttachmentVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Admin Attachments")
@RestController
@RequestMapping("/api/v1/admin/attachments")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class AttachmentAdminController {

    private final UploadService uploadService;
    private final SecurityUserDetailsService userDetailsService;

    public AttachmentAdminController(UploadService uploadService, SecurityUserDetailsService userDetailsService) {
        this.uploadService = uploadService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    public ApiResult<PageResult<AttachmentVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize) {
        return ApiResult.ok(uploadService.listAdmin(page, pageSize));
    }

    @PostMapping
    public ApiResult<AttachmentVO> upload(@RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        return ApiResult.ok(uploadService.upload(file, userId));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        uploadService.delete(id);
        return ApiResult.ok();
    }
}
