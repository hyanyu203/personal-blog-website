package com.jiangou.search.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class SearchQueryUtils {

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>(
            Arrays.asList("all", "article", "note", "snippet"));
    public static final long MAX_PAGE_SIZE = 100;

    private SearchQueryUtils() {
    }

    public static String normalizeType(String type) {
        if (type == null || !ALLOWED_TYPES.contains(type)) {
            return "all";
        }
        return type;
    }

    public static long normalizePage(long page) {
        return Math.max(1, page);
    }

    public static long normalizePageSize(long pageSize) {
        return Math.min(Math.max(1, pageSize), MAX_PAGE_SIZE);
    }
}
