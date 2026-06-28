package com.jiangou.article.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArchiveArticleRow {

    private Long id;
    private String title;
    private String slug;
    private LocalDateTime publishedAt;
    private Integer year;
    private Integer month;
}
