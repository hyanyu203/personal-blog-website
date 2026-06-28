package com.jiangou.snippet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangou.snippet.entity.SnippetEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SnippetMapper extends BaseMapper<SnippetEntity> {
}
