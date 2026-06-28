package com.jiangou.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangou.comment.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
}
