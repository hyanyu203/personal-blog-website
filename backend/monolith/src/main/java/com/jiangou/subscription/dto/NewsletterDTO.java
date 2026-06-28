package com.jiangou.subscription.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NewsletterDTO {

    @NotBlank
    private String subject;

    @NotBlank
    private String body;
}
