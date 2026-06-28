package com.jiangou.subscription.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SubscriptionTokenDTO {

    @NotBlank
    private String token;
}
