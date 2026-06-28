package com.jiangou.webmention.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("webmentions")
public class WebmentionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String sourceUrl;
    private String targetUrl;
    private String type;
    private String status;
    private LocalDateTime verifiedAt;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
