package com.jiangou.common.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.entity.ArticleTagEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.article.mapper.ArticleTagMapper;
import com.jiangou.category.entity.CategoryEntity;
import com.jiangou.category.mapper.CategoryMapper;
import com.jiangou.comment.entity.CommentEntity;
import com.jiangou.comment.mapper.CommentMapper;
import com.jiangou.note.entity.NoteEntity;
import com.jiangou.note.mapper.NoteMapper;
import com.jiangou.tag.entity.TagEntity;
import com.jiangou.tag.mapper.TagMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContentCounterService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final NoteMapper noteMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public ContentCounterService(ArticleMapper articleMapper, CommentMapper commentMapper,
                                 NoteMapper noteMapper,
                                 ArticleTagMapper articleTagMapper,
                                 CategoryMapper categoryMapper,
                                 TagMapper tagMapper) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
        this.noteMapper = noteMapper;
        this.articleTagMapper = articleTagMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    public long incrementArticleViewCount(ArticleEntity article) {
        long current = article.getViewCount() == null ? 0L : article.getViewCount();
        articleMapper.update(null, new LambdaUpdateWrapper<ArticleEntity>()
                .eq(ArticleEntity::getId, article.getId())
                .isNull(ArticleEntity::getDeletedAt)
                .setSql("view_count = COALESCE(view_count, 0) + 1"));
        long next = current + 1;
        article.setViewCount(next);
        return next;
    }

    public void adjustApprovedCommentCount(CommentEntity comment, int delta) {
        if (comment == null || delta == 0) {
            return;
        }
        if (comment.getParentId() == null) {
            if ("article".equals(comment.getTargetType())) {
                adjustArticleCommentCount(comment.getTargetId(), delta);
            } else if ("note".equals(comment.getTargetType())) {
                adjustNoteCommentCount(comment.getTargetId(), delta);
            }
        } else {
            adjustCommentReplyCount(comment.getParentId(), delta);
        }
    }

    public void adjustPublishedArticleTaxonomyCounts(ArticleEntity article, int delta) {
        if (article == null || article.getId() == null || delta == 0) {
            return;
        }
        adjustPublishedArticleTaxonomyCounts(article.getCategoryId(), loadArticleTagIds(article.getId()), delta);
    }

    public void adjustPublishedArticleTaxonomyCounts(Long categoryId, Collection<Long> tagIds, int delta) {
        if (delta == 0) {
            return;
        }
        adjustCategoryPostCount(categoryId, delta);
        adjustTagUsageCounts(tagIds, delta);
    }

    public void movePublishedArticleCategory(Long oldCategoryId, Long newCategoryId) {
        if (oldCategoryId == null ? newCategoryId == null : oldCategoryId.equals(newCategoryId)) {
            return;
        }
        adjustCategoryPostCount(oldCategoryId, -1);
        adjustCategoryPostCount(newCategoryId, 1);
    }

    public void syncPublishedArticleTags(List<Long> oldTagIds, List<Long> newTagIds) {
        Set<Long> oldIds = uniqueIds(oldTagIds);
        Set<Long> newIds = uniqueIds(newTagIds);

        List<Long> removed = oldIds.stream()
                .filter(id -> !newIds.contains(id))
                .collect(Collectors.toList());
        List<Long> added = newIds.stream()
                .filter(id -> !oldIds.contains(id))
                .collect(Collectors.toList());

        adjustTagUsageCounts(removed, -1);
        adjustTagUsageCounts(added, 1);
    }

    private void adjustArticleCommentCount(Long articleId, int delta) {
        if (articleId == null) {
            return;
        }
        articleMapper.update(null, new LambdaUpdateWrapper<ArticleEntity>()
                .eq(ArticleEntity::getId, articleId)
                .isNull(ArticleEntity::getDeletedAt)
                .setSql(counterSql("comment_count", delta)));
    }

    private void adjustNoteCommentCount(Long noteId, int delta) {
        if (noteId == null) {
            return;
        }
        noteMapper.update(null, new LambdaUpdateWrapper<NoteEntity>()
                .eq(NoteEntity::getId, noteId)
                .isNull(NoteEntity::getDeletedAt)
                .setSql(counterSql("comment_count", delta)));
    }

    private void adjustCommentReplyCount(Long commentId, int delta) {
        if (commentId == null) {
            return;
        }
        commentMapper.update(null, new LambdaUpdateWrapper<CommentEntity>()
                .eq(CommentEntity::getId, commentId)
                .isNull(CommentEntity::getDeletedAt)
                .setSql(counterSql("reply_count", delta)));
    }

    private void adjustCategoryPostCount(Long categoryId, int delta) {
        if (categoryId == null || delta == 0) {
            return;
        }
        categoryMapper.update(null, new LambdaUpdateWrapper<CategoryEntity>()
                .eq(CategoryEntity::getId, categoryId)
                .isNull(CategoryEntity::getDeletedAt)
                .setSql(counterSql("post_count", delta)));
    }

    private void adjustTagUsageCounts(Collection<Long> tagIds, int delta) {
        if (tagIds == null || tagIds.isEmpty() || delta == 0) {
            return;
        }
        for (Long tagId : uniqueIds(tagIds)) {
            tagMapper.update(null, new LambdaUpdateWrapper<TagEntity>()
                    .eq(TagEntity::getId, tagId)
                    .isNull(TagEntity::getDeletedAt)
                    .setSql(counterSql("usage_count", delta)));
        }
    }

    private List<Long> loadArticleTagIds(Long articleId) {
        if (articleId == null) {
            return new ArrayList<Long>();
        }
        return articleTagMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ArticleTagEntity>()
                        .eq(ArticleTagEntity::getArticleId, articleId))
                .stream()
                .map(ArticleTagEntity::getTagId)
                .collect(Collectors.toList());
    }

    private Set<Long> uniqueIds(Collection<Long> ids) {
        Set<Long> result = new LinkedHashSet<Long>();
        if (ids == null) {
            return result;
        }
        for (Long id : ids) {
            if (id != null && id > 0) {
                result.add(id);
            }
        }
        return result;
    }

    private String counterSql(String column, int delta) {
        if (delta > 0) {
            return column + " = COALESCE(" + column + ", 0) + " + delta;
        }
        return column + " = GREATEST(COALESCE(" + column + ", 0) - " + Math.abs(delta) + ", 0)";
    }
}
