package com.jiangou.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("projects")
public class ProjectEntity {

    @TableId(type = IdType.AUTO)
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
    private LocalDateTime syncedAt;
    private String syncStatus;
    private Boolean pinned;
    private Integer sortOrder;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
