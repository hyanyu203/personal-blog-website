package com.jiangou.note.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.common.result.PageResult;
import com.jiangou.note.dto.NoteDTO;
import com.jiangou.note.service.NoteService;
import com.jiangou.note.vo.NoteVO;
import com.jiangou.search.service.SearchIndexService;
import com.jiangou.security.AdminPermissions;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.security.SecurityUtils;
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

@Tag(name = "Admin Notes")
@RestController
@RequestMapping("/api/v1/admin/notes")
@PreAuthorize(AdminPermissions.ADMIN_ONLY)
public class NoteAdminController {

    private final NoteService noteService;
    private final SecurityUserDetailsService userDetailsService;
    private final SearchIndexService searchIndexService;

    public NoteAdminController(NoteService noteService, SecurityUserDetailsService userDetailsService,
                               SearchIndexService searchIndexService) {
        this.noteService = noteService;
        this.userDetailsService = userDetailsService;
        this.searchIndexService = searchIndexService;
    }

    @GetMapping
    public ApiResult<PageResult<NoteVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize) {
        return ApiResult.ok(noteService.listAdmin(page, pageSize));
    }

    @PostMapping
    public ApiResult<NoteVO> create(@Valid @RequestBody NoteDTO dto) {
        Long userId = SecurityUtils.requireUserId(userDetailsService);
        return ApiResult.ok(noteService.create(dto, userId));
    }

    @PatchMapping("/{id}")
    public ApiResult<NoteVO> update(@PathVariable Long id, @RequestBody NoteDTO dto) {
        NoteVO result = noteService.update(id, dto);
        searchIndexService.syncNoteById(id);
        return ApiResult.ok(result);
    }

    @PostMapping("/{id}/publish")
    public ApiResult<NoteVO> publish(@PathVariable Long id) {
        NoteVO result = noteService.publish(id);
        searchIndexService.syncNoteById(id);
        return ApiResult.ok(result);
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        searchIndexService.syncNoteById(id);
        return ApiResult.ok();
    }
}
