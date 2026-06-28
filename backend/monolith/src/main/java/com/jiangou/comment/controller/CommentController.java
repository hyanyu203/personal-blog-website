package com.jiangou.comment.controller;

import com.jiangou.comment.dto.CreateCommentDTO;
import com.jiangou.comment.service.CommentLikeService;
import com.jiangou.comment.service.CommentService;
import com.jiangou.comment.vo.CommentVO;
import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
import com.jiangou.security.AdminPermissions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Comments")
@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final SecurityUserDetailsService userDetailsService;

    public CommentController(CommentService commentService, CommentLikeService commentLikeService,
                             SecurityUserDetailsService userDetailsService) {
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/comments")
    public ApiResult<List<CommentVO>> list(@RequestParam String targetType, @RequestParam Long targetId) {
        return ApiResult.ok(commentService.listApproved(targetType, targetId));
    }

    @PostMapping("/comments")
    public ApiResult<CommentVO> create(@Valid @RequestBody CreateCommentDTO dto) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        return ApiResult.ok(commentService.create(dto, userId));
    }

    @PostMapping("/comments/{id}/like")
    public ApiResult<Map<String, Long>> like(@PathVariable Long id) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        long count = commentLikeService.like(id, userId);
        Map<String, Long> result = new HashMap<String, Long>();
        result.put("likeCount", count);
        return ApiResult.ok(result);
    }

    @GetMapping("/admin/comments")
    @PreAuthorize(AdminPermissions.COMMENT_REVIEW)
    public ApiResult<PageResult<CommentVO>> adminList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String status) {
        return ApiResult.ok(commentService.listAdmin(page, pageSize, status));
    }

    @PostMapping("/admin/comments/{id}/approve")
    @PreAuthorize(AdminPermissions.COMMENT_REVIEW)
    public ApiResult<Void> approve(@PathVariable Long id) {
        commentService.approve(id);
        return ApiResult.ok();
    }

    @PostMapping("/admin/comments/{id}/reject")
    @PreAuthorize(AdminPermissions.COMMENT_REVIEW)
    public ApiResult<Void> reject(@PathVariable Long id) {
        commentService.reject(id);
        return ApiResult.ok();
    }

    @PostMapping("/admin/comments/{id}/spam")
    @PreAuthorize(AdminPermissions.COMMENT_REVIEW)
    public ApiResult<Void> spam(@PathVariable Long id) {
        commentService.spam(id);
        return ApiResult.ok();
    }

    @DeleteMapping("/admin/comments/{id}")
    @PreAuthorize(AdminPermissions.COMMENT_REVIEW)
    public ApiResult<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ApiResult.ok();
    }
}
