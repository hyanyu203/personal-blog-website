package com.jiangou.search.service;

import com.jiangou.common.result.PageResult;
import com.jiangou.search.engine.SearchEngine;
import com.jiangou.search.util.SearchQueryUtils;
import com.jiangou.search.vo.SearchItemVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private final SearchEngine searchEngine;

    public SearchService(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public PageResult<SearchItemVO> search(String q, String type, long page, long pageSize) {
        return searchEngine.search(q,
                SearchQueryUtils.normalizeType(type),
                SearchQueryUtils.normalizePage(page),
                SearchQueryUtils.normalizePageSize(pageSize));
    }

    public List<String> suggest(String q) {
        if (q == null || q.trim().isEmpty()) {
            return new ArrayList<String>();
        }
        return searchEngine.suggest(q, 8);
    }
}
