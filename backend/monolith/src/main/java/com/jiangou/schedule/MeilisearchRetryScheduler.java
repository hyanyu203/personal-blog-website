package com.jiangou.schedule;

import com.jiangou.search.service.MeilisearchIndexRetryService;
import com.jiangou.search.service.SearchIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConditionalOnProperty(name = "jiangou.search.engine", havingValue = "meilisearch")
public class MeilisearchRetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(MeilisearchRetryScheduler.class);

    private final MeilisearchIndexRetryService retryService;
    private final SearchIndexService searchIndexService;

    public MeilisearchRetryScheduler(MeilisearchIndexRetryService retryService,
                                     SearchIndexService searchIndexService) {
        this.retryService = retryService;
        this.searchIndexService = searchIndexService;
    }

    @Scheduled(fixedRate = 300000)
    public void replayFailedSyncs() {
        Set<String> failed = retryService.pollFailedKeys();
        if (failed.isEmpty()) {
            return;
        }
        log.info("Meilisearch retry queue size: {}", failed.size());
        for (String key : failed) {
            if (retryService.isFullRebuildKey(key)) {
                log.info("Retrying Meilisearch full rebuild");
                searchIndexService.rebuildAll();
                continue;
            }
            int sep = key.indexOf(':');
            if (sep <= 0) {
                continue;
            }
            String type = key.substring(0, sep);
            try {
                Long targetId = Long.valueOf(key.substring(sep + 1));
                searchIndexService.replayMeilisearchDocument(type, targetId);
            } catch (NumberFormatException e) {
                log.warn("Skipping invalid Meilisearch retry key: {}", key);
            }
        }
    }
}
