package com.jiangou.note.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notes")
public class NoteEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long authorId;
    private String contentMd;
    private String contentHtml;
    private String contentText;
    private String status;
    private String visibility;
    private LocalDateTime publishedAt;
    private Long likeCount;
    private Long commentCount;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
