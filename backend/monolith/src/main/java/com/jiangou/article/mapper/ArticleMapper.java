package com.jiangou.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.vo.ArchiveArticleRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<ArticleEntity> {

    @Select("SELECT id, title, slug, published_at AS publishedAt, "
            + "YEAR(published_at) AS year, MONTH(published_at) AS month "
            + "FROM articles "
            + "WHERE status = 'published' AND visibility = 'public' AND deleted_at IS NULL "
            + "ORDER BY published_at DESC")
    List<ArchiveArticleRow> listArchiveArticles();
}
