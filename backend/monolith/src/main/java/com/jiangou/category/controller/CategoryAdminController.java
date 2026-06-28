package com.jiangou.category.controller;

import com.jiangou.category.dto.CategoryDTO;
import com.jiangou.category.service.CategoryService;
import com.jiangou.category.vo.CategoryVO;
import com.jiangou.common.result.ApiResult;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Admin Categories")
@RestController
@RequestMapping("/api/v1/admin/categories")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class CategoryAdminController {

    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize(AdminPermissions.ARTICLE_READ)
    public ApiResult<List<CategoryVO>> list() {
        return ApiResult.ok(categoryService.listAll());
    }

    @PostMapping
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<CategoryVO> create(@Valid @RequestBody CategoryDTO dto) {
        return ApiResult.ok(categoryService.create(dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<CategoryVO> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return ApiResult.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(AdminPermissions.ADMIN_ONLY)
    public ApiResult<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResult.ok();
    }
}
