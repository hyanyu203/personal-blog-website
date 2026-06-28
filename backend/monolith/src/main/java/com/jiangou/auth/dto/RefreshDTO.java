package com.jiangou.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RefreshDTO {

    @NotBlank
    private String refreshToken;
}
