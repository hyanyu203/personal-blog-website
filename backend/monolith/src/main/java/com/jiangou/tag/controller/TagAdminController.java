package com.jiangou.tag.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.security.AdminPermissions;
import com.jiangou.tag.dto.TagDTO;
import com.jiangou.tag.service.TagService;
import com.jiangou.tag.vo.TagVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Admin Tags")
@RestController
@RequestMapping("/api/v1/admin/tags")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class TagAdminController {

    private final TagService tagService;

    public TagAdminController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @PreAuthorize(AdminPermissions.ARTICLE_READ)
    public ApiResult<List<TagVO>> list() {
        return ApiResult.ok(tagService.listAll());
    }

    @PostMapping
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<TagVO> create(@Valid @RequestBody TagDTO dto) {
        return ApiResult.ok(tagService.create(dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<TagVO> update(@PathVariable Long id, @RequestBody TagDTO dto) {
        return ApiResult.ok(tagService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ApiResult.ok();
    }
}
