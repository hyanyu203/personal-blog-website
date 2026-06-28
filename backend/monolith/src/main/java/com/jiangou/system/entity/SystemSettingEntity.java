package com.jiangou.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_settings")
public class SystemSettingEntity {

    @TableId
    private String key;
    private String value;
    private String description;
    private Boolean isPublic;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
