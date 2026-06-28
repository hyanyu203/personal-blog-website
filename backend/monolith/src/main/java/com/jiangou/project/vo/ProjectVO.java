package com.jiangou.project.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectVO {

    private Long id;
    private String owner;
    private String repo;
    private String name;
    private String description;
    private String homepageUrl;
    private String githubUrl;
    private String language;
    private Integer stars;
    private Integer forks;
    private Integer openIssues;
    private String license;
    private LocalDateTime pushedAt;
    private String syncStatus;
    private Boolean pinned;
}
