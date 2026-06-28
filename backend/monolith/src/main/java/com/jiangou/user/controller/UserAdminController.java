package com.jiangou.user.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.common.util.AuditSupport;
import com.jiangou.user.dto.UserUpdateDTO;
import com.jiangou.user.service.UserService;
import com.jiangou.user.vo.UserVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.jiangou.security.AdminPermissions;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Admin Users")
@RestController
@RequestMapping("/api/v1/admin/users")
public class UserAdminController {

    private final UserService userService;
    private final AuditSupport auditSupport;

    public UserAdminController(UserService userService, AuditSupport auditSupport) {
        this.userService = userService;
        this.auditSupport = auditSupport;
    }

    @GetMapping
    @PreAuthorize(AdminPermissions.USER_MANAGE)
    public ApiResult<PageResult<UserVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String status) {
        return ApiResult.ok(userService.list(page, pageSize, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize(AdminPermissions.USER_MANAGE)
    public ApiResult<UserVO> get(@PathVariable Long id) {
        return ApiResult.ok(userService.getById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize(AdminPermissions.USER_MANAGE)
    public ApiResult<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        UserVO updated = userService.update(id, dto);
        auditSupport.log("user:update", "user", id);
        return ApiResult.ok(updated);
    }
}
