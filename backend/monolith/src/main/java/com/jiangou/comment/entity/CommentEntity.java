package com.jiangou.comment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comments")
public class CommentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String targetType;
    private Long targetId;
    private Long parentId;
    private Long rootId;
    private String path;
    private Integer depth;
    private Long userId;
    private String nickname;
    private String emailHash;
    private String website;
    private String contentMd;
    private String contentHtml;
    private String status;
    private String ipHash;
    private String userAgentHash;
    private Long likeCount;
    private Integer replyCount;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
