package com.jiangou.webmention.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WebmentionVO {
    private Long id;
    private String sourceUrl;
    private String targetUrl;
    private String type;
    private String status;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}
