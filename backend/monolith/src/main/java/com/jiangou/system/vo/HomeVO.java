package com.jiangou.system.vo;

import com.jiangou.article.vo.ArticleListItemVO;
import com.jiangou.category.vo.CategoryVO;
import com.jiangou.common.result.PageResult;
import com.jiangou.tag.vo.TagVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class HomeVO {
    private PageResult<ArticleListItemVO> articles;
    private Map<String, Object> stats;
    private Map<String, Object> settings;
    private List<CategoryVO> categories;
    private List<TagVO> tags;
}
