package com.jiangou.article.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVersionVO {

    private Integer version;
    private String title;
    private String changeNote;
    private LocalDateTime createdAt;
}
