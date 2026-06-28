package com.jiangou.search.engine;

import com.jiangou.common.result.PageResult;
import com.jiangou.search.vo.SearchItemVO;

import java.util.List;

public interface SearchEngine {

    PageResult<SearchItemVO> search(String q, String type, long page, long pageSize);

    List<String> suggest(String q, int limit);
}
