package com.jiangou.snippet.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SnippetDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String slug;

    @NotBlank
    private String language;

    @NotBlank
    private String code;

    private String descriptionMd;
    private String visibility;
}
