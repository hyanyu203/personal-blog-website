package com.jiangou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jiangou.search")
public class SearchProperties {

    /** mysql | meilisearch */
    private String engine = "mysql";

    private Meilisearch meilisearch = new Meilisearch();

    @Data
    public static class Meilisearch {
        private String host = "http://localhost:7700";
        private String apiKey = "masterKey";
        private String index = "jiangou";
        private int retryMaxAttempts = 3;
        private long retryDelayMs = 200L;
    }

    public boolean isMeilisearch() {
        return "meilisearch".equalsIgnoreCase(engine);
    }
}
