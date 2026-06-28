package com.jiangou.snippet.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
import com.jiangou.snippet.service.SnippetLikeService;
import com.jiangou.snippet.service.SnippetService;
import com.jiangou.snippet.vo.SnippetVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Snippets")
@RestController
@RequestMapping("/api/v1/snippets")
public class SnippetPublicController {

    private final SnippetService snippetService;
    private final SnippetLikeService snippetLikeService;
    private final SecurityUserDetailsService userDetailsService;

    public SnippetPublicController(SnippetService snippetService, SnippetLikeService snippetLikeService,
                                   SecurityUserDetailsService userDetailsService) {
        this.snippetService = snippetService;
        this.snippetLikeService = snippetLikeService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    public ApiResult<PageResult<SnippetVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String language) {
        return ApiResult.ok(snippetService.listPublic(page, pageSize, language));
    }

    @GetMapping("/{slug}")
    public ApiResult<SnippetVO> detail(@PathVariable String slug) {
        return ApiResult.ok(snippetService.getBySlug(slug));
    }

    @GetMapping(value = "/{slug}/raw", produces = MediaType.TEXT_PLAIN_VALUE)
    public String raw(@PathVariable String slug) {
        return snippetService.getRawCode(slug);
    }

    @PostMapping("/{id}/copy")
    public ApiResult<Void> copy(@PathVariable Long id) {
        snippetService.recordCopy(id);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/like")
    public ApiResult<Map<String, Long>> like(@PathVariable Long id) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        long count = snippetLikeService.like(id, userId);
        Map<String, Long> result = new HashMap<String, Long>();
        result.put("likeCount", count);
        return ApiResult.ok(result);
    }
}
