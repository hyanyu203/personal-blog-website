package com.jiangou.auth.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptchaVO {

    private String captchaId;
    private String imageBase64;
}
