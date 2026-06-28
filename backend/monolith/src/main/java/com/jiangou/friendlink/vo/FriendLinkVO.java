package com.jiangou.friendlink.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendLinkVO {

    private Long id;
    private String name;
    private String url;
    private String avatarUrl;
    private String description;
    private String status;
}
