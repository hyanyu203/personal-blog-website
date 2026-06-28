package com.jiangou.schedule;

import com.jiangou.schedule.service.RssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RssScheduler {

    private static final Logger log = LoggerFactory.getLogger(RssScheduler.class);

    private final RssService rssService;

    public RssScheduler(RssService rssService) {
        this.rssService = rssService;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void rebuildHourly() {
        rssService.buildFeed();
        log.debug("RSS feed rebuilt");
    }
}
