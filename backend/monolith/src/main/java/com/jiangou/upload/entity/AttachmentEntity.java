package com.jiangou.upload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attachments")
public class AttachmentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long uploaderId;
    private String filename;
    private String objectKey;
    private String url;
    private String mimeType;
    private Long sizeBytes;
    private Integer width;
    private Integer height;
    private String sha256;
    private String status;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
