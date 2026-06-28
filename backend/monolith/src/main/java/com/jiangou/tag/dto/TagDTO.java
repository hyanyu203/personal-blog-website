package com.jiangou.tag.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TagDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    private String description;
    private String color;
}
