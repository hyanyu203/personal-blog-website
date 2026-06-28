package com.jiangou.article.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article_versions")
public class ArticleVersionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Integer version;
    private String title;
    private String contentMd;
    private String contentHtml;
    private String changeNote;
    private Long createdBy;
    private String metadata;
    private LocalDateTime createdAt;
}
