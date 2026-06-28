package com.jiangou.search.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class SearchMetadataUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SearchMetadataUtils() {
    }

    public static String withUrl(String url) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("url", url == null ? "/" : url);
        return node.toString();
    }

    public static String readUrl(String metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "/";
        }
        try {
            JsonNode node = MAPPER.readTree(metadata);
            JsonNode url = node.get("url");
            return url == null ? "/" : url.asText("/");
        } catch (Exception e) {
            return "/";
        }
    }
}
