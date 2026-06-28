package com.jiangou.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("search_documents")
public class SearchDocumentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private String tags;
    private String status;
    private Float boost;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
