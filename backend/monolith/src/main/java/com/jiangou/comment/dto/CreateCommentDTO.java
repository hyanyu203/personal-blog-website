package com.jiangou.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateCommentDTO {

    @NotBlank
    private String targetType;

    @NotNull
    private Long targetId;

    private Long parentId;

    // nickname and email are intentionally omitted: the server derives them
    // from the authenticated UserEntity (displayName / username).
    private String website;

    @NotBlank
    @Size(max = 50000)
    private String contentMd;
}
