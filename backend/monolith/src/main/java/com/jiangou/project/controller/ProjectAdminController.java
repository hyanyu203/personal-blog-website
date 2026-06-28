package com.jiangou.project.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.project.dto.ProjectDTO;
import com.jiangou.project.service.ProjectService;
import com.jiangou.project.vo.ProjectVO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin Projects")
@RestController
@RequestMapping("/api/v1/admin/projects")
public class ProjectAdminController {

    private final ProjectService projectService;

    public ProjectAdminController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @PreAuthorize(AdminPermissions.PROJECT_SYNC)
    public ApiResult<PageResult<ProjectVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize) {
        return ApiResult.ok(projectService.listAdmin(page, pageSize));
    }

    @PostMapping
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<ProjectVO> create(@Valid @RequestBody ProjectDTO dto) {
        return ApiResult.ok(projectService.create(dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<ProjectVO> update(@PathVariable Long id, @RequestBody ProjectDTO dto) {
        return ApiResult.ok(projectService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/sync")
    @PreAuthorize(AdminPermissions.PROJECT_SYNC)
    public ApiResult<ProjectVO> syncOne(@PathVariable Long id) {
        return ApiResult.ok(projectService.syncOne(id));
    }

    @PostMapping("/sync")
    @PreAuthorize(AdminPermissions.PROJECT_SYNC)
    public ApiResult<Map<String, Integer>> syncAll() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("synced", projectService.syncAll());
        return ApiResult.ok(result);
    }
}
