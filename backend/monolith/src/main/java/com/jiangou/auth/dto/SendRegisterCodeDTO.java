package com.jiangou.auth.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SendRegisterCodeDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String captchaId;

    @NotBlank
    private String captchaCode;
}
