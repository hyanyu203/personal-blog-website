package com.jiangou.article.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateArticleDTO {

    @Size(max = 255)
    private String title;
    @Size(max = 255)
    private String slug;
    @Size(max = 2000)
    private String summary;
    @Size(max = 500000)
    private String contentMd;
    private Long categoryId;
    private List<Long> tagIds;
    private String visibility;
    private Boolean pinned;
    private String githubRepo;
    private String githubCommitSha;
}
