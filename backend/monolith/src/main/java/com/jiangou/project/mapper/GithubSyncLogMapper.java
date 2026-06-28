package com.jiangou.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangou.project.entity.GithubSyncLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GithubSyncLogMapper extends BaseMapper<GithubSyncLogEntity> {
}
