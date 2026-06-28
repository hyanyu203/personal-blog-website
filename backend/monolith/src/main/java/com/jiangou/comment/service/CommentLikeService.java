package com.jiangou.comment.service;

import com.jiangou.comment.entity.CommentEntity;
import com.jiangou.comment.mapper.CommentMapper;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.service.LikeCounterService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentLikeService {

    private final CommentMapper commentMapper;
    private final LikeCounterService likeCounterService;

    public CommentLikeService(CommentMapper commentMapper, LikeCounterService likeCounterService) {
        this.commentMapper = commentMapper;
        this.likeCounterService = likeCounterService;
    }

    public long like(Long commentId, Long userId) {
        CommentEntity entity = commentMapper.selectById(commentId);
        if (entity == null || entity.getDeletedAt() != null
                || !"approved".equals(entity.getStatus())) {
            throw new NotFoundException("评论不存在");
        }
        long base = entity.getLikeCount() == null ? 0L : entity.getLikeCount();
        return likeCounterService.incrementByUser("comment", commentId, userId, base);
    }

    public long getLikeCount(CommentEntity entity) {
        long base = entity.getLikeCount() == null ? 0L : entity.getLikeCount();
        return likeCounterService.getCount("comment", entity.getId(), base);
    }

    public Map<Long, Long> getLikeCounts(List<CommentEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new HashMap<Long, Long>();
        }
        List<Long> ids = entities.stream().map(CommentEntity::getId).collect(Collectors.toList());
        Map<Long, Long> fallbacks = new HashMap<Long, Long>();
        for (CommentEntity entity : entities) {
            fallbacks.put(entity.getId(), entity.getLikeCount() == null ? 0L : entity.getLikeCount());
        }
        return likeCounterService.getCounts("comment", ids, fallbacks);
    }
}
