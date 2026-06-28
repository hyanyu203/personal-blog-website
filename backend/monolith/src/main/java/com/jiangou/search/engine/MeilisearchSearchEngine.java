package com.jiangou.search.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jiangou.common.result.PageResult;
import com.jiangou.config.SearchProperties;
import com.jiangou.search.entity.SearchDocumentEntity;
import com.jiangou.search.service.MeilisearchIndexRetryService;
import com.jiangou.search.util.SearchMetadataUtils;
import com.jiangou.search.util.SearchQueryUtils;
import com.jiangou.search.vo.SearchItemVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "jiangou.search.engine", havingValue = "meilisearch")
public class MeilisearchSearchEngine implements SearchEngine {

    private static final Logger log = LoggerFactory.getLogger(MeilisearchSearchEngine.class);

    private final SearchProperties searchProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MeilisearchIndexRetryService retryService;

    public MeilisearchSearchEngine(SearchProperties searchProperties, RestTemplate restTemplate,
                                   ObjectMapper objectMapper, MeilisearchIndexRetryService retryService) {
        this.searchProperties = searchProperties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.retryService = retryService;
    }

    @Override
    public PageResult<SearchItemVO> search(String q, String type, long page, long pageSize) {
        if (q == null || q.trim().isEmpty()) {
            return PageResult.of(new ArrayList<SearchItemVO>(), 0, page, pageSize);
        }
        try {
            String index = searchProperties.getMeilisearch().getIndex();
            String url = searchProperties.getMeilisearch().getHost() + "/indexes/" + index + "/search";
            ObjectNode body = objectMapper.createObjectNode();
            body.put("q", q);
            body.put("limit", pageSize);
            body.put("offset", (page - 1) * pageSize);
            if (type != null && !"all".equals(type)) {
                String safeType = SearchQueryUtils.normalizeType(type);
                if (!"all".equals(safeType)) {
                    body.put("filter", "targetType = \"" + safeType + "\"");
                }
            }
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<String>(body.toString(), jsonHeaders()), String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode hits = root.path("hits");
            List<SearchItemVO> items = new ArrayList<SearchItemVO>();
            if (hits.isArray()) {
                for (JsonNode hit : hits) {
                    items.add(toItem(hit));
                }
            }
            long total = root.path("estimatedTotalHits").asLong(items.size());
            return PageResult.of(items, total, page, pageSize);
        } catch (Exception e) {
            log.warn("Meilisearch search failed, returning empty result: {}", e.getMessage());
            return PageResult.of(new ArrayList<SearchItemVO>(), 0, page, pageSize);
        }
    }

    @Override
    public List<String> suggest(String q, int limit) {
        PageResult<SearchItemVO> result = search(q, "all", 1, limit);
        return result.getItems().stream().map(SearchItemVO::getTitle).collect(Collectors.toList());
    }

    public void ensureIndex() {
        try {
            String host = searchProperties.getMeilisearch().getHost();
            String index = searchProperties.getMeilisearch().getIndex();
            restTemplate.exchange(host + "/indexes", HttpMethod.POST,
                    new HttpEntity<String>("{\"uid\":\"" + index + "\",\"primaryKey\":\"id\"}", jsonHeaders()),
                    String.class);
            ObjectNode settings = objectMapper.createObjectNode();
            ArrayNode searchable = objectMapper.createArrayNode();
            searchable.add("title");
            searchable.add("content");
            settings.set("searchableAttributes", searchable);
            ArrayNode filterable = objectMapper.createArrayNode();
            filterable.add("targetType");
            settings.set("filterableAttributes", filterable);
            restTemplate.exchange(host + "/indexes/" + index + "/settings", HttpMethod.PATCH,
                    new HttpEntity<String>(settings.toString(), jsonHeaders()), String.class);
        } catch (Exception e) {
            log.debug("Meilisearch index init: {}", e.getMessage());
        }
    }

    public void replaceAll(List<SearchDocumentEntity> docs) {
        ensureIndex();
        String index = searchProperties.getMeilisearch().getIndex();
        String host = searchProperties.getMeilisearch().getHost();
        boolean ok = runWithRetry("replaceAll", () -> {
            restTemplate.exchange(host + "/indexes/" + index + "/documents", HttpMethod.DELETE,
                    new HttpEntity<String>(jsonHeaders()), String.class);
            if (!docs.isEmpty()) {
                restTemplate.exchange(host + "/indexes/" + index + "/documents", HttpMethod.POST,
                        new HttpEntity<String>(toDocumentBatch(docs).toString(), jsonHeaders()), String.class);
            }
        });
        if (ok) {
            retryService.clearFullRebuildFailed();
        } else {
            retryService.markFullRebuildFailed();
        }
    }

    public void upsertDocument(SearchDocumentEntity doc) {
        ensureIndex();
        String index = searchProperties.getMeilisearch().getIndex();
        String host = searchProperties.getMeilisearch().getHost();
        ArrayNode batch = objectMapper.createArrayNode();
        batch.add(toDocumentNode(doc));
        boolean ok = runWithRetry("upsert " + doc.getTargetType() + ":" + doc.getTargetId(), () ->
                restTemplate.exchange(host + "/indexes/" + index + "/documents", HttpMethod.POST,
                        new HttpEntity<String>(batch.toString(), jsonHeaders()), String.class));
        if (ok) {
            retryService.clearFailed(doc.getTargetType(), doc.getTargetId());
        } else {
            retryService.markFailed(doc.getTargetType(), doc.getTargetId());
        }
    }

    public void deleteDocument(String targetType, Long targetId) {
        ensureIndex();
        String index = searchProperties.getMeilisearch().getIndex();
        String host = searchProperties.getMeilisearch().getHost();
        String documentId = targetType + "-" + targetId;
        boolean ok = runWithRetry("delete " + targetType + ":" + targetId, () ->
                restTemplate.exchange(host + "/indexes/" + index + "/documents/" + documentId, HttpMethod.DELETE,
                        new HttpEntity<String>(jsonHeaders()), String.class));
        if (ok) {
            retryService.clearFailed(targetType, targetId);
        } else {
            retryService.markFailed(targetType, targetId);
        }
    }

    private boolean runWithRetry(String operation, Runnable action) {
        int maxAttempts = Math.max(1, searchProperties.getMeilisearch().getRetryMaxAttempts());
        long delayMs = Math.max(0L, searchProperties.getMeilisearch().getRetryDelayMs());
        Exception last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                action.run();
                return true;
            } catch (Exception e) {
                last = e;
                if (attempt < maxAttempts && delayMs > 0) {
                    try {
                        Thread.sleep(delayMs * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        log.error("Meilisearch {} failed after {} attempts: {}", operation, maxAttempts,
                last == null ? "unknown" : last.getMessage());
        return false;
    }

    private ArrayNode toDocumentBatch(List<SearchDocumentEntity> docs) {
        ArrayNode batch = objectMapper.createArrayNode();
        for (SearchDocumentEntity doc : docs) {
            batch.add(toDocumentNode(doc));
        }
        return batch;
    }

    private ObjectNode toDocumentNode(SearchDocumentEntity doc) {
        ObjectNode item = objectMapper.createObjectNode();
        item.put("id", doc.getTargetType() + "-" + doc.getTargetId());
        item.put("targetType", doc.getTargetType());
        item.put("targetId", doc.getTargetId());
        item.put("title", doc.getTitle());
        item.put("content", doc.getContent() == null ? "" : doc.getContent());
        item.put("url", SearchMetadataUtils.readUrl(doc.getMetadata()));
        return item;
    }

    private SearchItemVO toItem(JsonNode hit) {
        String content = hit.path("content").asText("");
        String snippet = content.length() > 120 ? content.substring(0, 120) + "..." : content;
        return SearchItemVO.builder()
                .type(hit.path("targetType").asText(""))
                .title(hit.path("title").asText(""))
                .url(hit.path("url").asText("/"))
                .snippet(snippet)
                .score(1.0)
                .build();
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + searchProperties.getMeilisearch().getApiKey());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
