package com.jiangou.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangou.search.entity.SearchDocumentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchDocumentMapper extends BaseMapper<SearchDocumentEntity> {

    @Select({
            "<script>",
            "SELECT id, target_type, target_id, title, content, tags, status, boost, metadata, created_at, updated_at",
            "FROM search_documents",
            "WHERE status = 'active'",
            "AND MATCH(title, content) AGAINST(#{query} IN NATURAL LANGUAGE MODE)",
            "<if test=\"type != null and type != '' and type != 'all'\">",
            "AND target_type = #{type}",
            "</if>",
            "ORDER BY (MATCH(title, content) AGAINST(#{query} IN NATURAL LANGUAGE MODE) * COALESCE(boost, 1.0)) DESC, updated_at DESC",
            "LIMIT #{limit} OFFSET #{offset}",
            "</script>"
    })
    List<SearchDocumentEntity> searchFullText(@Param("query") String query,
                                               @Param("type") String type,
                                               @Param("limit") long limit,
                                               @Param("offset") long offset);

    @Select({
            "<script>",
            "SELECT COUNT(*)",
            "FROM search_documents",
            "WHERE status = 'active'",
            "AND MATCH(title, content) AGAINST(#{query} IN NATURAL LANGUAGE MODE)",
            "<if test=\"type != null and type != '' and type != 'all'\">",
            "AND target_type = #{type}",
            "</if>",
            "</script>"
    })
    long countFullText(@Param("query") String query,
                       @Param("type") String type);
}
