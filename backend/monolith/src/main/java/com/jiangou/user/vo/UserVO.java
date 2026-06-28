package com.jiangou.user.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserVO {
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private String avatarUrl;
    private String status;
    private String provider;
    private List<String> roles;
    private List<String> permissions;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
