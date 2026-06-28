package com.jiangou;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.dto.CreateArticleDTO;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.article.service.ArticleService;
import com.jiangou.article.vo.ArticlePublishResult;
import com.jiangou.category.entity.CategoryEntity;
import com.jiangou.category.mapper.CategoryMapper;
import com.jiangou.search.entity.SearchDocumentEntity;
import com.jiangou.search.mapper.SearchDocumentMapper;
import com.jiangou.search.service.SearchIndexService;
import com.jiangou.schedule.service.RssService;
import com.jiangou.system.service.StatsService;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@Testcontainers
@SpringBootTest
@ActiveProfiles("integration-test")
class PlatformIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("jiangou")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379).toString());
        registry.add("jiangou.jwt.secret", () -> "integration-test-jwt-secret-min-32-chars");
    }

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private SearchIndexService searchIndexService;
    @Autowired
    private SearchDocumentMapper searchDocumentMapper;
    @Autowired
    private StatsService statsService;
    @Autowired
    private RssService rssService;

    private Long categoryId;
    private Long authorId;

    @BeforeEach
    void seedFixtures() {
        UserEntity author = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, "it-author"));
        if (author == null) {
            author = new UserEntity();
            author.setUsername("it-author");
            author.setDisplayName("Integration Author");
            author.setEmail("it-author@jiangou.local");
            author.setEmailVerified(true);
            author.setPasswordHash("unused");
            author.setStatus("active");
            author.setTokenVersion(0);
            author.setMetadata("{}");
            author.setCreatedAt(LocalDateTime.now());
            author.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(author);
        }
        authorId = author.getId();

        CategoryEntity category = categoryMapper.selectOne(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getSlug, "integration"));
        if (category == null) {
            category = new CategoryEntity();
            category.setName("Integration");
            category.setSlug("integration");
            category.setDescription("integration tests");
            category.setSortOrder(0);
            category.setPostCount(0);
            category.setMetadata("{}");
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            categoryMapper.insert(category);
        }
        categoryId = category.getId();
    }

    @Test
    void searchIndex_syncRemovesPrivatePublishedArticle() {
        ArticleEntity article = insertArticle("private-post", "private", "published");

        searchIndexService.syncArticle(article);
        assertEquals(0L, countSearchDocs("article", article.getId()));

        article.setVisibility("public");
        articleMapper.updateById(article);
        searchIndexService.syncArticle(article);
        assertEquals(1L, countSearchDocs("article", article.getId()));

        article.setStatus("draft");
        articleMapper.updateById(article);
        searchIndexService.syncArticle(article);
        assertEquals(0L, countSearchDocs("article", article.getId()));
    }

    @Test
    void stats_publicCountsOnlyPublicVisibility() {
        insertArticle("public-post", "public", "published");
        insertArticle("hidden-post", "private", "published");

        Map<String, Object> stats = statsService.publicStats();
        assertEquals(1L, stats.get("articleCount"));
    }

    @Test
    void publishArticle_sendsNewsletterOnlyOnce() {
        CreateArticleDTO dto = new CreateArticleDTO();
        dto.setTitle("Newsletter Once");
        dto.setSlug("newsletter-once-" + System.nanoTime());
        dto.setSummary("summary");
        dto.setContentMd("# Hello");
        dto.setCategoryId(categoryId);
        dto.setTagIds(Collections.emptyList());
        dto.setVisibility("public");

        Long articleId = articleService.create(dto, authorId).getId();

        ArticlePublishResult first = articleService.publish(articleId, authorId);
        ArticlePublishResult second = articleService.publish(articleId, authorId);

        assertTrue(first.isSendNewsletter());
        assertFalse(second.isSendNewsletter());

        ArticleEntity stored = articleMapper.selectById(articleId);
        assertTrue(stored.getNewsletterSentAt() != null);
    }

    @Test
    void publishPrivateArticle_doesNotSendNewsletter() {
        CreateArticleDTO dto = new CreateArticleDTO();
        dto.setTitle("Private Newsletter");
        dto.setSlug("private-newsletter-" + System.nanoTime());
        dto.setSummary("summary");
        dto.setContentMd("# Hello");
        dto.setCategoryId(categoryId);
        dto.setTagIds(Collections.emptyList());
        dto.setVisibility("private");

        Long articleId = articleService.create(dto, authorId).getId();
        ArticlePublishResult result = articleService.publish(articleId, authorId);

        assertFalse(result.isSendNewsletter());
        ArticleEntity stored = articleMapper.selectById(articleId);
        assertTrue(stored.getNewsletterSentAt() == null);
    }

    @Test
    void rssFeed_excludesPrivatePublishedArticles() {
        ArticleEntity publicArticle = insertArticle("rss-public", "public", "published");
        publicArticle.setPublishedAt(LocalDateTime.now());
        articleMapper.updateById(publicArticle);

        ArticleEntity privateArticle = insertArticle("rss-private", "private", "published");
        privateArticle.setPublishedAt(LocalDateTime.now());
        articleMapper.updateById(privateArticle);

        String feed = rssService.buildFeed();
        assertTrue(feed.contains(publicArticle.getSlug()));
        assertFalse(feed.contains(privateArticle.getSlug()));
    }

    private ArticleEntity insertArticle(String slug, String visibility, String status) {
        ArticleEntity article = new ArticleEntity();
        article.setAuthorId(authorId);
        article.setCategoryId(categoryId);
        article.setTitle(slug);
        article.setSlug(slug + "-" + System.nanoTime());
        article.setSummary("summary");
        article.setContentMd("# body");
        article.setContentHtml("<h1>body</h1>");
        article.setContentText("body");
        article.setStatus(status);
        article.setVisibility(visibility);
        article.setPinned(false);
        article.setVersion(1);
        article.setMetadata("{}");
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.insert(article);
        return article;
    }

    private long countSearchDocs(String type, Long targetId) {
        return searchDocumentMapper.selectCount(new LambdaQueryWrapper<SearchDocumentEntity>()
                .eq(SearchDocumentEntity::getTargetType, type)
                .eq(SearchDocumentEntity::getTargetId, targetId));
    }
}
