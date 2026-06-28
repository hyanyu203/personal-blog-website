package com.jiangou.note.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NoteDTO {

    @NotBlank
    private String contentMd;

    private String visibility;
}
