package com.jiangou.snippet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("snippets")
public class SnippetEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long authorId;
    private String title;
    private String slug;
    private String language;
    private String code;
    private String highlightedHtml;
    private String descriptionMd;
    private String descriptionHtml;
    private String visibility;
    private String rawToken;
    private Long viewCount;
    private Long copyCount;
    private Long likeCount;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
