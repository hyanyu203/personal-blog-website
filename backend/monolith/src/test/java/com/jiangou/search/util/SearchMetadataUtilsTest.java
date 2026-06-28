package com.jiangou.search.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchMetadataUtilsTest {

    @Test
    void withUrl_and_readUrl_roundTrip() {
        String metadata = SearchMetadataUtils.withUrl("/posts/hello");
        assertEquals("/posts/hello", SearchMetadataUtils.readUrl(metadata));
    }

    @Test
    void readUrl_returnsSlashForInvalidJson() {
        assertEquals("/", SearchMetadataUtils.readUrl("not-json"));
    }
}
