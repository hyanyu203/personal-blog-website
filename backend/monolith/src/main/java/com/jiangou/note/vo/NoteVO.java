package com.jiangou.note.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoteVO {

    private Long id;
    private String contentHtml;
    private String contentMd;
    private String status;
    private Long likeCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}
