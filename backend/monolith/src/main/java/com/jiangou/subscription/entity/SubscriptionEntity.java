package com.jiangou.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("subscriptions")
public class SubscriptionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String email;
    private String status;
    private String confirmToken;
    private String unsubscribeToken;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
