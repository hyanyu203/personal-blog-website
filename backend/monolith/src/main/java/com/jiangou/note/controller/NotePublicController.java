package com.jiangou.note.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.note.service.NoteLikeService;
import com.jiangou.note.service.NoteService;
import com.jiangou.note.vo.NoteVO;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Notes")
@RestController
@RequestMapping("/api/v1/notes")
public class NotePublicController {

    private final NoteService noteService;
    private final NoteLikeService noteLikeService;
    private final SecurityUserDetailsService userDetailsService;

    public NotePublicController(NoteService noteService, NoteLikeService noteLikeService,
                                SecurityUserDetailsService userDetailsService) {
        this.noteService = noteService;
        this.noteLikeService = noteLikeService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    public ApiResult<PageResult<NoteVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize) {
        return ApiResult.ok(noteService.listPublic(page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResult<NoteVO> detail(@PathVariable Long id) {
        return ApiResult.ok(noteService.getByIdPublic(id));
    }

    @PostMapping("/{id}/like")
    public ApiResult<Map<String, Long>> like(@PathVariable Long id) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        long count = noteLikeService.like(id, userId);
        Map<String, Long> result = new HashMap<String, Long>();
        result.put("likeCount", count);
        return ApiResult.ok(result);
    }
}
