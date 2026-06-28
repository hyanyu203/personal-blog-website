package com.jiangou.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("github_sync_logs")
public class GithubSyncLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String status;
    private Integer requestCount;
    private String errorMessage;
    private String metadata;
    private LocalDateTime createdAt;
}
