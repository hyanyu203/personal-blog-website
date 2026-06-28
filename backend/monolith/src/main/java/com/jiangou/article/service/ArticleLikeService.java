package com.jiangou.article.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.service.LikeCounterService;
import org.springframework.stereotype.Service;

@Service
public class ArticleLikeService {

    private final ArticleMapper articleMapper;
    private final LikeCounterService likeCounterService;

    public ArticleLikeService(ArticleMapper articleMapper, LikeCounterService likeCounterService) {
        this.articleMapper = articleMapper;
        this.likeCounterService = likeCounterService;
    }

    public long like(Long articleId, Long userId) {
        ArticleEntity entity = articleMapper.selectOne(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getId, articleId)
                .eq(ArticleEntity::getStatus, "published")
                .eq(ArticleEntity::getVisibility, "public")
                .isNull(ArticleEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("文章不存在");
        }
        long base = entity.getLikeCount() == null ? 0L : entity.getLikeCount();
        return likeCounterService.incrementByUser("article", articleId, userId, base);
    }

    public long getLikeCount(ArticleEntity entity) {
        long base = entity.getLikeCount() == null ? 0L : entity.getLikeCount();
        return likeCounterService.getCount("article", entity.getId(), base);
    }
}
