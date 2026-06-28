package com.jiangou.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article_tags")
public class ArticleTagEntity {

    private Long articleId;
    private Long tagId;
    private LocalDateTime createdAt;
}
