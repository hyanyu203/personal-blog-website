package com.jiangou.search.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.search.service.SearchIndexService;
import com.jiangou.security.AdminPermissions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Admin Search")
@RestController
@RequestMapping("/api/v1/admin/search")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class SearchAdminController {

    private final SearchIndexService searchIndexService;

    public SearchAdminController(SearchIndexService searchIndexService) {
        this.searchIndexService = searchIndexService;
    }

    @PostMapping("/rebuild")
    public ApiResult<Map<String, Integer>> rebuild() {
        int count = searchIndexService.rebuildAll();
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("indexed", count);
        return ApiResult.ok(result);
    }
}
