package com.jiangou.category.controller;

import com.jiangou.category.service.CategoryService;
import com.jiangou.category.vo.CategoryVO;
import com.jiangou.common.result.ApiResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Categories")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResult<List<CategoryVO>> list() {
        return ApiResult.ok(categoryService.listAll());
    }

    @GetMapping("/{slug}")
    public ApiResult<CategoryVO> detail(@PathVariable String slug) {
        return ApiResult.ok(categoryService.getBySlug(slug));
    }
}
