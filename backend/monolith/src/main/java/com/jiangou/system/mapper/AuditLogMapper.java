package com.jiangou.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangou.system.entity.AuditLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLogEntity> {
}
