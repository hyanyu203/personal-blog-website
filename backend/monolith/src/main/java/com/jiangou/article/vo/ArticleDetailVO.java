package com.jiangou.article.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ArticleDetailVO {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String contentHtml;
    private String contentMd;
    private String status;
    private Boolean pinned;
    private Integer readingMinutes;
    private Integer wordCount;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime publishedAt;
    private List<String> tags;
    private List<Long> tagIds;
    private Long categoryId;
    private Map<String, String> category;
    private Map<String, String> github;

    @Data
    @Builder
    public static class CategoryBrief {
        private String name;
        private String slug;
    }
}
