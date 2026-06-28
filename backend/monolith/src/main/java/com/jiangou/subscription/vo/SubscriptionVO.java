package com.jiangou.subscription.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubscriptionVO {

    private Long id;
    private String email;
    private String status;
    private LocalDateTime createdAt;
}
