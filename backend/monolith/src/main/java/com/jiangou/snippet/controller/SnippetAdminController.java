package com.jiangou.snippet.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.search.service.SearchIndexService;
import com.jiangou.security.AdminPermissions;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
import com.jiangou.snippet.dto.SnippetDTO;
import com.jiangou.snippet.service.SnippetService;
import com.jiangou.snippet.vo.SnippetVO;
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

@Tag(name = "Admin Snippets")
@RestController
@RequestMapping("/api/v1/admin/snippets")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class SnippetAdminController {

    private final SnippetService snippetService;
    private final SecurityUserDetailsService userDetailsService;
    private final SearchIndexService searchIndexService;

    public SnippetAdminController(SnippetService snippetService, SecurityUserDetailsService userDetailsService,
                                  SearchIndexService searchIndexService) {
        this.snippetService = snippetService;
        this.userDetailsService = userDetailsService;
        this.searchIndexService = searchIndexService;
    }

    @GetMapping
    public ApiResult<PageResult<SnippetVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize) {
        return ApiResult.ok(snippetService.listAdmin(page, pageSize));
    }

    @PostMapping
    public ApiResult<SnippetVO> create(@Valid @RequestBody SnippetDTO dto) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        SnippetVO result = snippetService.create(dto, userId);
        searchIndexService.syncSnippetById(result.getId());
        return ApiResult.ok(result);
    }

    @PatchMapping("/{id}")
    public ApiResult<SnippetVO> update(@PathVariable Long id, @RequestBody SnippetDTO dto) {
        SnippetVO result = snippetService.update(id, dto);
        searchIndexService.syncSnippetById(id);
        return ApiResult.ok(result);
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        snippetService.delete(id);
        searchIndexService.syncSnippetById(id);
        return ApiResult.ok();
    }
}
