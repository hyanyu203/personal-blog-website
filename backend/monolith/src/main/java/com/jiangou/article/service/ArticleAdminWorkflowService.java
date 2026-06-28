package com.jiangou.article.service;

import com.jiangou.article.vo.ArticleDetailVO;
import com.jiangou.article.vo.ArticlePublishResult;
import com.jiangou.search.service.SearchIndexService;
import com.jiangou.subscription.service.NewsletterDispatchService;
import com.jiangou.system.service.AuditLogService;
import org.springframework.stereotype.Service;

@Service
public class ArticleAdminWorkflowService {

    private final ArticleService articleService;
    private final AuditLogService auditLogService;
    private final SearchIndexService searchIndexService;
    private final NewsletterDispatchService newsletterDispatchService;

    public ArticleAdminWorkflowService(ArticleService articleService,
                                       AuditLogService auditLogService,
                                       SearchIndexService searchIndexService,
                                       NewsletterDispatchService newsletterDispatchService) {
        this.articleService = articleService;
        this.auditLogService = auditLogService;
        this.searchIndexService = searchIndexService;
        this.newsletterDispatchService = newsletterDispatchService;
    }

    public ArticleDetailVO update(Long id, com.jiangou.article.dto.UpdateArticleDTO dto) {
        ArticleDetailVO result = articleService.update(id, dto);
        searchIndexService.syncArticleById(id);
        return result;
    }

    public void delete(Long id) {
        articleService.delete(id);
        searchIndexService.syncArticleById(id);
    }

    public ArticleDetailVO publish(Long id, Long userId) {
        ArticlePublishResult result = articleService.publish(id, userId);
        auditLogService.log(userId, "article:publish", "article", id);
        searchIndexService.syncArticleById(id);
        if (result.isSendNewsletter()) {
            ArticleDetailVO article = result.getArticle();
            newsletterDispatchService.dispatchNewArticle(
                    id, article.getTitle(), article.getSlug(), article.getSummary());
        }
        return result.getArticle();
    }

    public ArticleDetailVO unpublish(Long id) {
        ArticleDetailVO result = articleService.unpublish(id);
        searchIndexService.syncArticleById(id);
        return result;
    }

    public ArticleDetailVO archive(Long id, Long userId) {
        ArticleDetailVO result = articleService.archive(id);
        auditLogService.log(userId, "article:archive", "article", id);
        searchIndexService.syncArticleById(id);
        return result;
    }

    public ArticleDetailVO restoreVersion(Long id, Integer version, Long userId) {
        ArticleDetailVO result = articleService.restoreVersion(id, version, userId);
        searchIndexService.syncArticleById(id);
        return result;
    }
}
