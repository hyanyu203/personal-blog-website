package com.jiangou.friendlink.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FriendLinkDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    private String avatarUrl;
    private String description;
    private String ownerEmail;
    private Integer sortOrder;
    /** approved | pending — 默认 approved */
    private String status;
}
