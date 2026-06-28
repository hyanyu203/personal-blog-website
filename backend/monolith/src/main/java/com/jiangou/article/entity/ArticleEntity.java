package com.jiangou.article.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("articles")
public class ArticleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long authorId;
    private Long categoryId;
    private String title;
    private String slug;
    private String summary;
    private Long coverAttachmentId;
    private String contentMd;
    private String contentHtml;
    private String contentText;
    private String status;
    private String visibility;
    private Boolean pinned;
    private LocalDateTime publishedAt;
    private LocalDateTime newsletterSentAt;
    private Integer readingMinutes;
    private Integer wordCount;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String githubRepo;
    private String githubCommitSha;
    private Integer version;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
