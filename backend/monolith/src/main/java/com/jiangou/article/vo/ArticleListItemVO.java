package com.jiangou.article.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ArticleListItemVO {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String status;
    private Boolean pinned;
    private Integer readingMinutes;
    private Integer wordCount;
    private Long viewCount;
    private LocalDateTime publishedAt;
    private CategoryBriefVO category;
    private List<String> tags;

    @Data
    @Builder
    public static class CategoryBriefVO {
        private String name;
        private String slug;
    }
}
