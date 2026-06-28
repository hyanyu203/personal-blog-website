package com.jiangou.comment.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentVO {

    private Long id;
    private Long parentId;
    private Integer depth;
    private String nickname;
    private String website;
    private String contentHtml;
    private Long likeCount;
    private Integer replyCount;
    private LocalDateTime createdAt;
    private String status;
    private List<CommentVO> replies;
}
