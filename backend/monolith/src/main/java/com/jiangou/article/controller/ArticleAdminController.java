package com.jiangou.article.controller;

import com.jiangou.article.dto.CreateArticleDTO;
import com.jiangou.article.dto.UpdateArticleDTO;
import com.jiangou.article.service.ArticleAdminWorkflowService;
import com.jiangou.article.service.ArticleService;
import com.jiangou.article.vo.ArticleDetailVO;
import com.jiangou.article.vo.ArticleListItemVO;
import com.jiangou.article.vo.ArticleVersionVO;
import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.security.AdminPermissions;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Admin Articles")
@RestController
@RequestMapping("/api/v1/admin/articles")
@PreAuthorize(AdminPermissions.ARTICLE_READ)
public class ArticleAdminController {

    private final ArticleService articleService;
    private final ArticleAdminWorkflowService articleAdminWorkflowService;
    private final SecurityUserDetailsService userDetailsService;

    public ArticleAdminController(ArticleService articleService,
                                  ArticleAdminWorkflowService articleAdminWorkflowService,
                                  SecurityUserDetailsService userDetailsService) {
        this.articleService = articleService;
        this.articleAdminWorkflowService = articleAdminWorkflowService;
        this.userDetailsService = userDetailsService;
    }

    @Operation(summary = "管理端文章列表")
    @GetMapping
    public ApiResult<PageResult<ArticleListItemVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return ApiResult.ok(articleService.listAdmin(page, pageSize, status, keyword));
    }

    @Operation(summary = "创建文章")
    @PostMapping
    @PreAuthorize(AdminPermissions.ARTICLE_CREATE)
    public ApiResult<ArticleDetailVO> create(@Valid @RequestBody CreateArticleDTO dto) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        return ApiResult.ok(articleService.create(dto, userId));
    }

    @Operation(summary = "文章详情")
    @GetMapping("/{id}")
    public ApiResult<ArticleDetailVO> detail(@PathVariable Long id) {
        return ApiResult.ok(articleService.getByIdAdmin(id));
    }

    @Operation(summary = "更新文章")
    @PatchMapping("/{id}")
    @PreAuthorize(AdminPermissions.ARTICLE_UPDATE)
    public ApiResult<ArticleDetailVO> update(@PathVariable Long id, @Valid @RequestBody UpdateArticleDTO dto) {
        return ApiResult.ok(articleAdminWorkflowService.update(id, dto));
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    @PreAuthorize(AdminPermissions.ARTICLE_UPDATE)
    public ApiResult<Void> delete(@PathVariable Long id) {
        articleAdminWorkflowService.delete(id);
        return ApiResult.ok();
    }

    @Operation(summary = "发布文章")
    @PostMapping("/{id}/publish")
    @PreAuthorize(AdminPermissions.ARTICLE_PUBLISH)
    public ApiResult<ArticleDetailVO> publish(@PathVariable Long id) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        return ApiResult.ok(articleAdminWorkflowService.publish(id, userId));
    }

    @Operation(summary = "下线文章")
    @PostMapping("/{id}/unpublish")
    @PreAuthorize(AdminPermissions.ARTICLE_UPDATE)
    public ApiResult<ArticleDetailVO> unpublish(@PathVariable Long id) {
        return ApiResult.ok(articleAdminWorkflowService.unpublish(id));
    }

    @Operation(summary = "归档文章")
    @PostMapping("/{id}/archive")
    @PreAuthorize(AdminPermissions.ARTICLE_UPDATE)
    public ApiResult<ArticleDetailVO> archive(@PathVariable Long id) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        return ApiResult.ok(articleAdminWorkflowService.archive(id, userId));
    }

    @Operation(summary = "版本历史")
    @GetMapping("/{id}/versions")
    public ApiResult<List<ArticleVersionVO>> versions(@PathVariable Long id) {
        return ApiResult.ok(articleService.listVersions(id));
    }

    @Operation(summary = "版本 Diff")
    @GetMapping("/{id}/versions/diff")
    public ApiResult<com.jiangou.article.vo.ArticleVersionDiffVO> versionDiff(
            @PathVariable Long id,
            @RequestParam Integer from,
            @RequestParam(required = false) Integer to) {
        return ApiResult.ok(articleService.diffVersions(id, from, to));
    }

    @Operation(summary = "恢复指定版本")
    @PostMapping("/{id}/restore/{version}")
    @PreAuthorize(AdminPermissions.ARTICLE_UPDATE)
    public ApiResult<ArticleDetailVO> restore(@PathVariable Long id,
                                              @PathVariable Integer version) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        return ApiResult.ok(articleAdminWorkflowService.restoreVersion(id, version, userId));
    }
}
