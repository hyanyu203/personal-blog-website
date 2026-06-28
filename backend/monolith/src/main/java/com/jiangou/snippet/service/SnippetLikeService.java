package com.jiangou.snippet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.service.LikeCounterService;
import com.jiangou.snippet.entity.SnippetEntity;
import com.jiangou.snippet.mapper.SnippetMapper;
import org.springframework.stereotype.Service;

@Service
public class SnippetLikeService {

    private final SnippetMapper snippetMapper;
    private final LikeCounterService likeCounterService;

    public SnippetLikeService(SnippetMapper snippetMapper, LikeCounterService likeCounterService) {
        this.snippetMapper = snippetMapper;
        this.likeCounterService = likeCounterService;
    }

    public long like(Long snippetId, Long userId) {
        SnippetEntity entity = snippetMapper.selectOne(new LambdaQueryWrapper<SnippetEntity>()
                .eq(SnippetEntity::getId, snippetId)
                .eq(SnippetEntity::getVisibility, "public")
                .isNull(SnippetEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("代码片段不存在");
        }
        long base = entity.getLikeCount() == null ? 0L : entity.getLikeCount();
        return likeCounterService.incrementByUser("snippet", snippetId, userId, base);
    }

    public long getLikeCount(SnippetEntity entity) {
        long base = entity.getLikeCount() == null ? 0L : entity.getLikeCount();
        return likeCounterService.getCount("snippet", entity.getId(), base);
    }

    public long getLikeCount(Long snippetId) {
        SnippetEntity entity = snippetMapper.selectById(snippetId);
        if (entity == null) {
            return 0L;
        }
        return getLikeCount(entity);
    }
}
