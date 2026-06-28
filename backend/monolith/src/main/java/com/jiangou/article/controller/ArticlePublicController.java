package com.jiangou.article.controller;

import com.jiangou.article.service.ArticleLikeService;
import com.jiangou.article.service.ArticleService;
import com.jiangou.article.vo.ArchiveGroupVO;
import com.jiangou.article.vo.ArticleDetailVO;
import com.jiangou.article.vo.ArticleListItemVO;
import com.jiangou.article.vo.TocItemVO;
import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Articles")
@RestController
@RequestMapping("/api/v1/articles")
public class ArticlePublicController {

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final SecurityUserDetailsService userDetailsService;

    public ArticlePublicController(ArticleService articleService, ArticleLikeService articleLikeService,
                                   SecurityUserDetailsService userDetailsService) {
        this.articleService = articleService;
        this.articleLikeService = articleLikeService;
        this.userDetailsService = userDetailsService;
    }

    @Operation(summary = "文章列表")
    @GetMapping
    public ApiResult<PageResult<ArticleListItemVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword) {
        return ApiResult.ok(articleService.listPublic(page, pageSize, category, tag, keyword));
    }

    @Operation(summary = "文章详情（slug）")
    @GetMapping("/slug/{slug}")
    public ApiResult<ArticleDetailVO> detail(@PathVariable String slug) {
        return ApiResult.ok(articleService.getBySlugPublic(slug));
    }

    @Operation(summary = "归档时间线")
    @GetMapping("/archives")
    public ApiResult<List<ArchiveGroupVO>> archives() {
        return ApiResult.ok(articleService.listArchives());
    }

    @Operation(summary = "相关文章")
    @GetMapping("/{id}/related")
    public ApiResult<List<ArticleListItemVO>> related(@PathVariable Long id,
                                                      @RequestParam(defaultValue = "5") int limit) {
        return ApiResult.ok(articleService.listRelated(id, limit));
    }

    @Operation(summary = "文章目录")
    @GetMapping("/{id}/toc")
    public ApiResult<List<TocItemVO>> toc(@PathVariable Long id) {
        return ApiResult.ok(articleService.getToc(id));
    }

    @Operation(summary = "文章点赞")
    @PostMapping("/{id}/like")
    public ApiResult<Map<String, Long>> like(@PathVariable Long id) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        long count = articleLikeService.like(id, userId);
        Map<String, Long> result = new HashMap<String, Long>();
        result.put("likeCount", count);
        return ApiResult.ok(result);
    }
}
