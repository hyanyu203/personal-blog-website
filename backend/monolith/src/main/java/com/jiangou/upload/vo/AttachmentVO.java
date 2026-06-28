package com.jiangou.upload.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttachmentVO {

    private Long id;
    private String filename;
    private String url;
    private String mimeType;
    private Long sizeBytes;
    private Integer width;
    private Integer height;
    private LocalDateTime createdAt;
}
