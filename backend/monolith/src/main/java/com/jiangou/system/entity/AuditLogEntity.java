package com.jiangou.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("audit_logs")
public class AuditLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long actorId;
    private String action;
    private String targetType;
    private Long targetId;
    private String ipHash;
    private String metadata;
    private LocalDateTime createdAt;
}
