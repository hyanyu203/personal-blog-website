package com.jiangou.tag.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.tag.service.TagService;
import com.jiangou.tag.vo.TagVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tags")
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ApiResult<List<TagVO>> list() {
        return ApiResult.ok(tagService.listAll());
    }

    @GetMapping("/{slug}")
    public ApiResult<TagVO> detail(@PathVariable String slug) {
        return ApiResult.ok(tagService.getBySlug(slug));
    }
}
