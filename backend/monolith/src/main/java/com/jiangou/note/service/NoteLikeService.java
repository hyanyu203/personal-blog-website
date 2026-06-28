package com.jiangou.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.service.LikeCounterService;
import com.jiangou.note.entity.NoteEntity;
import com.jiangou.note.mapper.NoteMapper;
import org.springframework.stereotype.Service;

@Service
public class NoteLikeService {

    private final NoteMapper noteMapper;
    private final LikeCounterService likeCounterService;

    public NoteLikeService(NoteMapper noteMapper, LikeCounterService likeCounterService) {
        this.noteMapper = noteMapper;
        this.likeCounterService = likeCounterService;
    }

    public long like(Long noteId, Long userId) {
        NoteEntity entity = noteMapper.selectOne(new LambdaQueryWrapper<NoteEntity>()
                .eq(NoteEntity::getId, noteId)
                .eq(NoteEntity::getStatus, "published")
                .eq(NoteEntity::getVisibility, "public")
                .isNull(NoteEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("动态不存在");
        }
        long base = entity.getLikeCount() == null ? 0L : entity.getLikeCount();
        return likeCounterService.incrementByUser("note", noteId, userId, base);
    }

    public long getLikeCount(NoteEntity entity) {
        long base = entity.getLikeCount() == null ? 0L : entity.getLikeCount();
        return likeCounterService.getCount("note", entity.getId(), base);
    }
}
