package com.jiangou.snippet.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SnippetVO {

    private Long id;
    private String title;
    private String slug;
    private String language;
    private String code;
    private String highlightedHtml;
    private String descriptionHtml;
    private Long viewCount;
    private Long copyCount;
    private Long likeCount;
    private LocalDateTime createdAt;
}
