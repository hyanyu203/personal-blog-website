package com.jiangou.friendlink.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FriendLinkApplyDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    private String avatarUrl;
    private String description;

    @NotBlank
    private String ownerEmail;
}
