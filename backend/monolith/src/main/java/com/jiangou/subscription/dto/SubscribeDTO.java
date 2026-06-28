package com.jiangou.subscription.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SubscribeDTO {

    @NotBlank
    @Email
    private String email;
}
