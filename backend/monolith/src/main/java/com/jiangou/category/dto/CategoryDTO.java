package com.jiangou.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    private String description;
    private Long parentId;
    private Integer sortOrder;
}
