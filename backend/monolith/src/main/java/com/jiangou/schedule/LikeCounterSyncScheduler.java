package com.jiangou.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.comment.entity.CommentEntity;
import com.jiangou.comment.mapper.CommentMapper;
import com.jiangou.common.service.LikeCounterService;
import com.jiangou.note.entity.NoteEntity;
import com.jiangou.note.mapper.NoteMapper;
import com.jiangou.snippet.entity.SnippetEntity;
import com.jiangou.snippet.mapper.SnippetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class LikeCounterSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(LikeCounterSyncScheduler.class);

    private final LikeCounterService likeCounterService;
    private final StringRedisTemplate redisTemplate;
    private final ArticleMapper articleMapper;
    private final NoteMapper noteMapper;
    private final CommentMapper commentMapper;
    private final SnippetMapper snippetMapper;

    public LikeCounterSyncScheduler(LikeCounterService likeCounterService,
                                    StringRedisTemplate redisTemplate,
                                    ArticleMapper articleMapper,
                                    NoteMapper noteMapper,
                                    CommentMapper commentMapper,
                                    SnippetMapper snippetMapper) {
        this.likeCounterService = likeCounterService;
        this.redisTemplate = redisTemplate;
        this.articleMapper = articleMapper;
        this.noteMapper = noteMapper;
        this.commentMapper = commentMapper;
        this.snippetMapper = snippetMapper;
    }

    @Scheduled(fixedRate = 300000)
    public void syncLikeCounts() {
        Set<String> dirty = likeCounterService.pollDirtyKeys();
        for (String item : dirty) {
            int sep = item.indexOf(':');
            if (sep <= 0) {
                likeCounterService.clearDirtyKey(item);
                continue;
            }
            String type = item.substring(0, sep);
            Long id;
            try {
                id = Long.valueOf(item.substring(sep + 1));
            } catch (NumberFormatException e) {
                likeCounterService.clearDirtyKey(item);
                continue;
            }
            String countKey = "counter:" + type + ":" + id;
            String val = redisTemplate.opsForValue().get(countKey);
            if (val == null) {
                likeCounterService.clearDirtyKey(item);
                continue;
            }
            long count;
            try {
                count = Long.parseLong(val);
            } catch (NumberFormatException e) {
                log.warn("Invalid like counter value {}:{} - {}", type, id, val);
                likeCounterService.clearDirtyKey(item);
                continue;
            }
            try {
                syncOne(type, id, count);
                likeCounterService.clearDirtyKey(item);
            } catch (Exception e) {
                log.warn("同步点赞计数失败 {}:{} - {}", type, id, e.getMessage());
            }
        }
        if (!dirty.isEmpty()) {
            log.debug("已同步 {} 条点赞计数", dirty.size());
        }
    }

    private void syncOne(String type, Long id, long count) {
        if ("article".equals(type)) {
            articleMapper.update(null, new LambdaUpdateWrapper<ArticleEntity>()
                    .eq(ArticleEntity::getId, id)
                    .isNull(ArticleEntity::getDeletedAt)
                    .setSql("like_count = GREATEST(COALESCE(like_count, 0), " + count + ")"));
        } else if ("note".equals(type)) {
            noteMapper.update(null, new LambdaUpdateWrapper<NoteEntity>()
                    .eq(NoteEntity::getId, id)
                    .isNull(NoteEntity::getDeletedAt)
                    .setSql("like_count = GREATEST(COALESCE(like_count, 0), " + count + ")"));
        } else if ("comment".equals(type)) {
            commentMapper.update(null, new LambdaUpdateWrapper<CommentEntity>()
                    .eq(CommentEntity::getId, id)
                    .isNull(CommentEntity::getDeletedAt)
                    .setSql("like_count = GREATEST(COALESCE(like_count, 0), " + count + ")"));
        } else if ("snippet".equals(type)) {
            snippetMapper.update(null, new LambdaUpdateWrapper<SnippetEntity>()
                    .eq(SnippetEntity::getId, id)
                    .isNull(SnippetEntity::getDeletedAt)
                    .setSql("like_count = GREATEST(COALESCE(like_count, 0), " + count + ")"));
        }
    }
}
