package com.jiangou.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.result.PageResult;
import com.jiangou.common.util.MarkdownUtils;
import com.jiangou.note.dto.NoteDTO;
import com.jiangou.note.entity.NoteEntity;
import com.jiangou.note.mapper.NoteMapper;
import com.jiangou.note.vo.NoteVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteMapper noteMapper;
    private final NoteLikeService noteLikeService;

    public NoteService(NoteMapper noteMapper, NoteLikeService noteLikeService) {
        this.noteMapper = noteMapper;
        this.noteLikeService = noteLikeService;
    }

    public PageResult<NoteVO> listPublic(long page, long pageSize) {
        Page<NoteEntity> result = noteMapper.selectPage(new Page<>(page, pageSize), publishedWrapper());
        return PageResult.of(result.getRecords().stream().map(this::toVo).collect(Collectors.toList()),
                result.getTotal(), page, pageSize);
    }

    public PageResult<NoteVO> listAdmin(long page, long pageSize) {
        Page<NoteEntity> result = noteMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<NoteEntity>().isNull(NoteEntity::getDeletedAt)
                        .orderByDesc(NoteEntity::getCreatedAt));
        return PageResult.of(result.getRecords().stream().map(this::toVo).collect(Collectors.toList()),
                result.getTotal(), page, pageSize);
    }

    public NoteVO getByIdPublic(Long id) {
        NoteEntity entity = noteMapper.selectOne(publishedWrapper().eq(NoteEntity::getId, id));
        if (entity == null) {
            throw new NotFoundException("动态不存在");
        }
        return toVo(entity);
    }

    @Transactional
    public NoteVO create(NoteDTO dto, Long authorId) {
        NoteEntity entity = new NoteEntity();
        entity.setAuthorId(authorId);
        applyContent(entity, dto.getContentMd());
        entity.setStatus("draft");
        entity.setVisibility(dto.getVisibility() == null ? "public" : dto.getVisibility());
        entity.setLikeCount(0L);
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        noteMapper.insert(entity);
        return toVo(entity);
    }

    @Transactional
    public NoteVO update(Long id, NoteDTO dto) {
        NoteEntity entity = findActive(id);
        if (dto.getContentMd() != null) {
            applyContent(entity, dto.getContentMd());
        }
        if (dto.getVisibility() != null) {
            entity.setVisibility(dto.getVisibility());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        noteMapper.updateById(entity);
        return toVo(entity);
    }

    @Transactional
    public NoteVO publish(Long id) {
        NoteEntity entity = findActive(id);
        entity.setStatus("published");
        entity.setPublishedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        noteMapper.updateById(entity);
        return toVo(entity);
    }

    @Transactional
    public void delete(Long id) {
        NoteEntity entity = findActive(id);
        entity.setDeletedAt(LocalDateTime.now());
        noteMapper.updateById(entity);
    }

    private LambdaQueryWrapper<NoteEntity> publishedWrapper() {
        return new LambdaQueryWrapper<NoteEntity>()
                .eq(NoteEntity::getStatus, "published")
                .eq(NoteEntity::getVisibility, "public")
                .isNull(NoteEntity::getDeletedAt)
                .orderByDesc(NoteEntity::getPublishedAt);
    }

    private void applyContent(NoteEntity entity, String md) {
        entity.setContentMd(md);
        entity.setContentHtml(MarkdownUtils.toHtml(md));
        entity.setContentText(MarkdownUtils.toPlainText(md));
    }

    private NoteEntity findActive(Long id) {
        NoteEntity entity = noteMapper.selectOne(new LambdaQueryWrapper<NoteEntity>()
                .eq(NoteEntity::getId, id).isNull(NoteEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("动态不存在");
        }
        return entity;
    }

    private NoteVO toVo(NoteEntity e) {
        return NoteVO.builder()
                .id(e.getId()).contentHtml(e.getContentHtml()).contentMd(e.getContentMd())
                .status(e.getStatus()).likeCount(noteLikeService.getLikeCount(e))
                .publishedAt(e.getPublishedAt()).createdAt(e.getCreatedAt()).build();
    }
}
