package com.jiangou.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.comment.entity.CommentEntity;
import com.jiangou.comment.mapper.CommentMapper;
import com.jiangou.note.entity.NoteEntity;
import com.jiangou.note.mapper.NoteMapper;
import com.jiangou.snippet.entity.SnippetEntity;
import com.jiangou.snippet.mapper.SnippetMapper;
import com.jiangou.subscription.entity.SubscriptionEntity;
import com.jiangou.subscription.mapper.SubscriptionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatsService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final SnippetMapper snippetMapper;
    private final NoteMapper noteMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final SystemSettingService systemSettingService;

    public StatsService(ArticleMapper articleMapper, CommentMapper commentMapper,
                        SnippetMapper snippetMapper, NoteMapper noteMapper,
                        SubscriptionMapper subscriptionMapper,
                        SystemSettingService systemSettingService) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
        this.snippetMapper = snippetMapper;
        this.noteMapper = noteMapper;
        this.subscriptionMapper = subscriptionMapper;
        this.systemSettingService = systemSettingService;
    }

    public Map<String, Object> publicStats() {
        Map<String, Object> stats = new HashMap<String, Object>();
        stats.put("articleCount", countPublishedArticles());
        stats.put("snippetCount", countPublicSnippets());
        stats.put("noteCount", countPublishedNotes());
        stats.put("runningDays", computeRunningDays());
        return stats;
    }

    public Map<String, Object> adminStats() {
        Map<String, Object> stats = new HashMap<String, Object>();
        stats.put("articleCount", countPublishedArticles());
        stats.put("draftCount", articleMapper.selectCount(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getStatus, "draft")
                .isNull(ArticleEntity::getDeletedAt)));
        stats.put("pendingComments", commentMapper.selectCount(new LambdaQueryWrapper<CommentEntity>()
                .eq(CommentEntity::getStatus, "pending")
                .isNull(CommentEntity::getDeletedAt)));
        stats.put("subscriberCount", subscriptionMapper.selectCount(
                new LambdaQueryWrapper<SubscriptionEntity>()
                        .eq(SubscriptionEntity::getStatus, "confirmed")));
        stats.put("snippetCount", snippetMapper.selectCount(new LambdaQueryWrapper<SnippetEntity>()
                .isNull(SnippetEntity::getDeletedAt)));
        stats.put("noteCount", noteMapper.selectCount(new LambdaQueryWrapper<NoteEntity>()
                .isNull(NoteEntity::getDeletedAt)));
        stats.put("runningDays", computeRunningDays());
        return stats;
    }

    private long countPublishedArticles() {
        return articleMapper.selectCount(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getStatus, "published")
                .eq(ArticleEntity::getVisibility, "public")
                .isNull(ArticleEntity::getDeletedAt));
    }

    private long countPublicSnippets() {
        return snippetMapper.selectCount(new LambdaQueryWrapper<SnippetEntity>()
                .eq(SnippetEntity::getVisibility, "public")
                .isNull(SnippetEntity::getDeletedAt));
    }

    private long countPublishedNotes() {
        return noteMapper.selectCount(new LambdaQueryWrapper<NoteEntity>()
                .eq(NoteEntity::getStatus, "published")
                .eq(NoteEntity::getVisibility, "public")
                .isNull(NoteEntity::getDeletedAt));
    }

    private long computeRunningDays() {
        Map<String, Object> settings = systemSettingService.getPublicSettings();
        Object launch = settings.get("siteLaunchDate");
        if (launch == null) {
            return 1L;
        }
        try {
            LocalDate launchDate = LocalDate.parse(launch.toString());
            return Math.max(1L, ChronoUnit.DAYS.between(launchDate, LocalDate.now()) + 1);
        } catch (Exception e) {
            return 1L;
        }
    }
}
