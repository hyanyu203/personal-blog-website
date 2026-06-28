package com.jiangou.search.engine;

import com.jiangou.config.SearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SearchEngineConfig {

    @Bean
    @Primary
    public SearchEngine searchEngine(SearchProperties searchProperties,
                                     MysqlSearchEngine mysqlSearchEngine,
                                     org.springframework.beans.factory.ObjectProvider<MeilisearchSearchEngine> meilisearch) {
        if (searchProperties.isMeilisearch()) {
            MeilisearchSearchEngine engine = meilisearch.getIfAvailable();
            if (engine != null) {
                return engine;
            }
        }
        return mysqlSearchEngine;
    }
}
