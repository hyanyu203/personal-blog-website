package com.jiangou.system.service;

import com.jiangou.article.service.ArticleService;
import com.jiangou.category.service.CategoryService;
import com.jiangou.system.vo.HomeVO;
import com.jiangou.tag.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final ArticleService articleService;
    private final StatsService statsService;
    private final SystemSettingService systemSettingService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public HomeService(ArticleService articleService, StatsService statsService,
                       SystemSettingService systemSettingService,
                       CategoryService categoryService, TagService tagService) {
        this.articleService = articleService;
        this.statsService = statsService;
        this.systemSettingService = systemSettingService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    public HomeVO loadHome() {
        return HomeVO.builder()
                .articles(articleService.listPublic(1, 10, null, null, null))
                .stats(statsService.publicStats())
                .settings(systemSettingService.getPublicSettings())
                .categories(categoryService.listAll())
                .tags(tagService.listAll())
                .build();
    }
}
