package com.jiangou.project.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.project.service.ProjectService;
import com.jiangou.project.vo.ProjectVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Projects")
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectPublicController {

    private final ProjectService projectService;

    public ProjectPublicController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ApiResult<List<ProjectVO>> list() {
        return ApiResult.ok(projectService.listPublic());
    }

    @GetMapping("/{owner}/{repo}")
    public ApiResult<ProjectVO> detail(@PathVariable String owner, @PathVariable String repo) {
        return ApiResult.ok(projectService.getByOwnerRepo(owner, repo));
    }
}
