package com.jiangou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class UserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private Boolean emailVerified;
    private String passwordHash;
    private String avatarUrl;
    private String provider;
    private String providerId;
    private String status;
    private Integer tokenVersion;
    private String bio;
    private LocalDateTime lastLoginAt;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
