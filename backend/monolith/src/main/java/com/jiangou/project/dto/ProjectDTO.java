package com.jiangou.project.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProjectDTO {

    @NotBlank
    private String owner;

    @NotBlank
    private String repo;

    private String name;
    private String description;
    private String homepageUrl;
    private Boolean pinned;
    private Integer sortOrder;
}
