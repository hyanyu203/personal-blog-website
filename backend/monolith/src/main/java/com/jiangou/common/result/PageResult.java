package com.jiangou.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> items;
    private Long total;
    private Long page;
    private Long pageSize;
    private Boolean hasMore;

    public static <T> PageResult<T> of(List<T> items, long total, long page, long pageSize) {
        boolean hasMore = page * pageSize < total;
        return new PageResult<>(items == null ? Collections.<T>emptyList() : items, total, page, pageSize, hasMore);
    }
}
