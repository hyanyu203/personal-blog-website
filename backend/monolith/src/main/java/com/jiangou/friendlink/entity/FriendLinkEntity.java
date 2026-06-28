package com.jiangou.friendlink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("friend_links")
public class FriendLinkEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String url;
    private String avatarUrl;
    private String description;
    private String ownerEmail;
    private String status;
    private Integer sortOrder;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
