package com.jiangou.article.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CreateArticleDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "slug 不能为空")
    private String slug;

    private String summary;
    private String contentMd;
    private Long categoryId;
    private List<Long> tagIds;
    private String visibility;
    private Boolean pinned;
    private String githubRepo;
    private String githubCommitSha;
}
