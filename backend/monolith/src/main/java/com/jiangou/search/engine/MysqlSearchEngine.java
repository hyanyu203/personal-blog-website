package com.jiangou.search.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.result.PageResult;
import com.jiangou.search.entity.SearchDocumentEntity;
import com.jiangou.search.mapper.SearchDocumentMapper;
import com.jiangou.search.util.SearchMetadataUtils;
import com.jiangou.search.vo.SearchItemVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MysqlSearchEngine implements SearchEngine {

    private static final Logger log = LoggerFactory.getLogger(MysqlSearchEngine.class);

    private final SearchDocumentMapper searchDocumentMapper;

    public MysqlSearchEngine(SearchDocumentMapper searchDocumentMapper) {
        this.searchDocumentMapper = searchDocumentMapper;
    }

    @Override
    public PageResult<SearchItemVO> search(String q, String type, long page, long pageSize) {
        if (!StringUtils.hasText(q)) {
            return PageResult.of(new ArrayList<SearchItemVO>(), 0, page, pageSize);
        }
        String query = q.trim();
        try {
            return searchFullText(query, type, page, pageSize);
        } catch (RuntimeException e) {
            log.warn("Fulltext search failed, fallback to LIKE query: {}", e.getMessage());
            return searchLike(query, type, page, pageSize);
        }
    }

    private PageResult<SearchItemVO> searchFullText(String q, String type, long page, long pageSize) {
        long safePage = Math.max(1L, page);
        long safePageSize = Math.max(1L, pageSize);
        long offset = (safePage - 1L) * safePageSize;
        List<SearchDocumentEntity> docs = searchDocumentMapper.searchFullText(q, type, safePageSize, offset);
        long total = searchDocumentMapper.countFullText(q, type);
        List<SearchItemVO> items = docs.stream()
                .map(this::toItem)
                .collect(Collectors.toList());
        return PageResult.of(items, total, safePage, safePageSize);
    }

    private PageResult<SearchItemVO> searchLike(String q, String type, long page, long pageSize) {
        LambdaQueryWrapper<SearchDocumentEntity> wrapper = new LambdaQueryWrapper<SearchDocumentEntity>()
                .eq(SearchDocumentEntity::getStatus, "active")
                .and(w -> w.like(SearchDocumentEntity::getTitle, q)
                        .or().like(SearchDocumentEntity::getContent, q));
        if (StringUtils.hasText(type) && !"all".equals(type)) {
            wrapper.eq(SearchDocumentEntity::getTargetType, type);
        }
        wrapper.orderByDesc(SearchDocumentEntity::getUpdatedAt);
        Page<SearchDocumentEntity> result = searchDocumentMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<SearchItemVO> items = result.getRecords().stream()
                .map(this::toItem)
                .collect(Collectors.toList());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }

    @Override
    public List<String> suggest(String q, int limit) {
        PageResult<SearchItemVO> result = search(q, "all", 1, limit);
        return result.getItems().stream().map(SearchItemVO::getTitle).collect(Collectors.toList());
    }

    private SearchItemVO toItem(SearchDocumentEntity doc) {
        String snippet = doc.getContent();
        if (snippet != null && snippet.length() > 120) {
            snippet = snippet.substring(0, 120) + "...";
        }
        return SearchItemVO.builder()
                .type(doc.getTargetType())
                .title(doc.getTitle())
                .url(SearchMetadataUtils.readUrl(doc.getMetadata()))
                .snippet(snippet)
                .score(doc.getBoost() == null ? 1.0 : doc.getBoost().doubleValue())
                .build();
    }
}
