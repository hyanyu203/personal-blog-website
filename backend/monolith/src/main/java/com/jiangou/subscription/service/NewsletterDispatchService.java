package com.jiangou.subscription.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NewsletterDispatchService {

    private static final Logger log = LoggerFactory.getLogger(NewsletterDispatchService.class);

    private final SubscriptionService subscriptionService;

    public NewsletterDispatchService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Async("newsletterExecutor")
    public void dispatchNewArticle(Long articleId, String title, String slug, String summary) {
        try {
            subscriptionService.notifyNewArticle(title, slug, summary);
        } catch (Exception e) {
            log.warn("Newsletter 异步发送失败 articleId={}: {}", articleId, e.getMessage());
        }
    }
}
