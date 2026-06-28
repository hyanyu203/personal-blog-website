package com.jiangou.article.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.article.dto.CreateArticleDTO;
import com.jiangou.article.dto.UpdateArticleDTO;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.entity.ArticleTagEntity;
import com.jiangou.article.entity.ArticleVersionEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.article.mapper.ArticleTagMapper;
import com.jiangou.article.mapper.ArticleVersionMapper;
import com.jiangou.article.vo.ArchiveArticleRow;
import com.jiangou.article.vo.ArchiveGroupVO;
import com.jiangou.article.vo.ArticleDetailVO;
import com.jiangou.article.vo.ArticlePublishResult;
import com.jiangou.article.vo.ArticleListItemVO;
import com.jiangou.article.vo.ArticleVersionDiffVO;
import com.jiangou.article.vo.ArticleVersionVO;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.jiangou.article.vo.TocItemVO;
import com.jiangou.category.entity.CategoryEntity;
import com.jiangou.category.mapper.CategoryMapper;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.result.PageResult;
import com.jiangou.common.service.ContentCounterService;
import com.jiangou.common.util.MarkdownUtils;
import com.jiangou.tag.entity.TagEntity;
import com.jiangou.tag.mapper.TagMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final ArticleVersionMapper articleVersionMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleLikeService articleLikeService;
    private final ContentCounterService contentCounterService;

    public ArticleService(ArticleMapper articleMapper, ArticleTagMapper articleTagMapper,
                           ArticleVersionMapper articleVersionMapper,
                           CategoryMapper categoryMapper, TagMapper tagMapper,
                           ArticleLikeService articleLikeService,
                           ContentCounterService contentCounterService) {
        this.articleMapper = articleMapper;
        this.articleTagMapper = articleTagMapper;
        this.articleVersionMapper = articleVersionMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.articleLikeService = articleLikeService;
        this.contentCounterService = contentCounterService;
    }

    public PageResult<ArticleListItemVO> listPublic(long page, long pageSize, String categorySlug,
                                                    String tagSlug, String keyword) {
        LambdaQueryWrapper<ArticleEntity> wrapper = basePublishedWrapper();
        applyFilters(wrapper, categorySlug, tagSlug, keyword);
        wrapper.orderByDesc(ArticleEntity::getPinned).orderByDesc(ArticleEntity::getPublishedAt);
        Page<ArticleEntity> result = articleMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<ArticleListItemVO> items = toListItems(result.getRecords());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }

    public PageResult<ArticleListItemVO> listAdmin(long page, long pageSize, String status, String keyword) {
        LambdaQueryWrapper<ArticleEntity> wrapper = new LambdaQueryWrapper<ArticleEntity>()
                .isNull(ArticleEntity::getDeletedAt);
        if (StringUtils.hasText(status)) {
            wrapper.eq(ArticleEntity::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ArticleEntity::getTitle, keyword)
                    .or().like(ArticleEntity::getSummary, keyword));
        }
        wrapper.orderByDesc(ArticleEntity::getUpdatedAt);
        Page<ArticleEntity> result = articleMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<ArticleListItemVO> items = toListItems(result.getRecords());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }

    public ArticleDetailVO getBySlugPublic(String slug) {
        ArticleEntity entity = articleMapper.selectOne(basePublishedWrapper().eq(ArticleEntity::getSlug, slug));
        if (entity == null) {
            throw new NotFoundException("文章不存在");
        }
        contentCounterService.incrementArticleViewCount(entity);
        return toDetail(entity, false);
    }

    public ArticleDetailVO getByIdAdmin(Long id) {
        ArticleEntity entity = findActive(id);
        return toDetail(entity, true);
    }

    @Transactional
    public ArticleDetailVO create(CreateArticleDTO dto, Long authorId) {
        validateCategoryId(dto.getCategoryId());
        validateTagIds(dto.getTagIds());
        ensureSlugUnique(dto.getSlug(), null);
        ArticleEntity entity = new ArticleEntity();
        entity.setAuthorId(authorId);
        entity.setTitle(dto.getTitle());
        entity.setSlug(dto.getSlug());
        entity.setSummary(dto.getSummary());
        entity.setContentMd(dto.getContentMd() == null ? "" : dto.getContentMd());
        entity.setCategoryId(dto.getCategoryId());
        entity.setStatus("draft");
        entity.setVisibility(dto.getVisibility() == null ? "public" : dto.getVisibility());
        entity.setPinned(Boolean.TRUE.equals(dto.getPinned()));
        entity.setGithubRepo(dto.getGithubRepo());
        entity.setGithubCommitSha(dto.getGithubCommitSha());
        entity.setVersion(1);
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        applyContentFields(entity);
        articleMapper.insert(entity);
        saveTags(entity.getId(), dto.getTagIds());
        return toDetail(entity, true);
    }

    @Transactional
    public ArticleDetailVO update(Long id, UpdateArticleDTO dto) {
        ArticleEntity entity = findActive(id);
        boolean wasCounted = isCountedInTaxonomy(entity);
        Long oldCategoryId = entity.getCategoryId();
        List<Long> oldTagIds = loadTagIds(id);
        if (StringUtils.hasText(dto.getSlug())) {
            ensureSlugUnique(dto.getSlug(), id);
            entity.setSlug(dto.getSlug());
        }
        if (StringUtils.hasText(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getSummary() != null) {
            entity.setSummary(dto.getSummary());
        }
        if (dto.getContentMd() != null) {
            entity.setContentMd(dto.getContentMd());
        }
        if (dto.getCategoryId() != null) {
            validateCategoryId(dto.getCategoryId());
            entity.setCategoryId(dto.getCategoryId());
        }
        if (dto.getTagIds() != null) {
            validateTagIds(dto.getTagIds());
        }
        if (dto.getVisibility() != null) {
            entity.setVisibility(dto.getVisibility());
        }
        if (dto.getPinned() != null) {
            entity.setPinned(dto.getPinned());
        }
        if (dto.getGithubRepo() != null) {
            entity.setGithubRepo(dto.getGithubRepo());
        }
        if (dto.getGithubCommitSha() != null) {
            entity.setGithubCommitSha(dto.getGithubCommitSha());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        applyContentFields(entity);
        articleMapper.updateById(entity);
        if (dto.getTagIds() != null) {
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTagEntity>()
                    .eq(ArticleTagEntity::getArticleId, id));
            saveTags(id, dto.getTagIds());
        }
        syncTaxonomyCountersAfterUpdate(entity, wasCounted, oldCategoryId, oldTagIds, dto.getTagIds() != null);
        return toDetail(entity, true);
    }

    @Transactional
    public void delete(Long id) {
        ArticleEntity entity = findActive(id);
        boolean wasCounted = isCountedInTaxonomy(entity);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(entity);
        if (wasCounted) {
            contentCounterService.adjustPublishedArticleTaxonomyCounts(entity, -1);
        }
    }

    @Transactional
    public ArticlePublishResult publish(Long id, Long userId) {
        ArticleEntity entity = findActive(id);
        boolean wasCounted = isCountedInTaxonomy(entity);
        if (!StringUtils.hasText(entity.getTitle()) || !StringUtils.hasText(entity.getSlug())
                || !StringUtils.hasText(entity.getContentMd())) {
            throw new ValidationException("发布前需填写标题、slug 和正文");
        }
        validateCategoryId(entity.getCategoryId());
        validateTagIds(loadTagIds(id));
        saveVersion(entity, userId, "发布");
        entity.setStatus("published");
        entity.setPublishedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        applyContentFields(entity);
        articleMapper.updateById(entity);
        if (!wasCounted && isCountedInTaxonomy(entity)) {
            contentCounterService.adjustPublishedArticleTaxonomyCounts(entity, 1);
        }
        boolean sendNewsletter = isNewsletterEligible(entity) && claimNewsletterSend(id);
        return new ArticlePublishResult(toDetail(entity, true), sendNewsletter);
    }

    private boolean isNewsletterEligible(ArticleEntity entity) {
        return entity.getNewsletterSentAt() == null && "public".equalsIgnoreCase(entity.getVisibility());
    }

    /**
     * Atomically marks newsletter as dispatched so concurrent publish calls cannot double-send.
     */
    public boolean claimNewsletterSend(Long id) {
        int updated = articleMapper.update(null, new LambdaUpdateWrapper<ArticleEntity>()
                .eq(ArticleEntity::getId, id)
                .isNull(ArticleEntity::getDeletedAt)
                .isNull(ArticleEntity::getNewsletterSentAt)
                .set(ArticleEntity::getNewsletterSentAt, LocalDateTime.now())
                .set(ArticleEntity::getUpdatedAt, LocalDateTime.now()));
        return updated > 0;
    }

    @Transactional
    public ArticleDetailVO publish(Long id) {
        return publish(id, null).getArticle();
    }

    @Transactional
    public void markNewsletterSent(Long id) {
        ArticleEntity entity = findActive(id);
        entity.setNewsletterSentAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(entity);
    }

    @Transactional
    public ArticleDetailVO unpublish(Long id) {
        ArticleEntity entity = findActive(id);
        boolean wasCounted = isCountedInTaxonomy(entity);
        entity.setStatus("draft");
        entity.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(entity);
        if (wasCounted) {
            contentCounterService.adjustPublishedArticleTaxonomyCounts(entity, -1);
        }
        return toDetail(entity, true);
    }

    @Transactional
    public ArticleDetailVO archive(Long id) {
        ArticleEntity entity = findActive(id);
        boolean wasCounted = isCountedInTaxonomy(entity);
        entity.setStatus("archived");
        entity.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(entity);
        if (wasCounted) {
            contentCounterService.adjustPublishedArticleTaxonomyCounts(entity, -1);
        }
        return toDetail(entity, true);
    }

    public List<TocItemVO> getToc(Long id) {
        ArticleEntity entity = articleMapper.selectOne(basePublishedWrapper().eq(ArticleEntity::getId, id));
        if (entity == null) {
            throw new NotFoundException("文章不存在");
        }
        return MarkdownUtils.extractToc(entity.getContentMd());
    }

    public List<ArticleListItemVO> listRelated(Long id, int limit) {
        int cappedLimit = Math.min(Math.max(1, limit), 100);
        ArticleEntity entity = articleMapper.selectOne(basePublishedWrapper().eq(ArticleEntity::getId, id));
        if (entity == null) {
            throw new NotFoundException("文章不存在");
        }
        List<ArticleTagEntity> links = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTagEntity>()
                .eq(ArticleTagEntity::getArticleId, id));
        if (links.isEmpty()) {
            return listRelatedByCategory(entity, cappedLimit);
        }
        List<Long> tagIds = links.stream().map(ArticleTagEntity::getTagId).collect(Collectors.toList());
        List<ArticleTagEntity> relatedLinks = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTagEntity>()
                .in(ArticleTagEntity::getTagId, tagIds)
                .ne(ArticleTagEntity::getArticleId, id));
        if (relatedLinks.isEmpty()) {
            return listRelatedByCategory(entity, cappedLimit);
        }
        List<Long> articleIds = relatedLinks.stream()
                .map(ArticleTagEntity::getArticleId)
                .distinct()
                .collect(Collectors.toList());
        List<ArticleEntity> articles = articleMapper.selectList(basePublishedWrapper()
                .in(ArticleEntity::getId, articleIds)
                .orderByDesc(ArticleEntity::getPublishedAt)
                .last("LIMIT " + cappedLimit));
        return toListItems(articles);
    }

    public List<ArticleVersionVO> listVersions(Long articleId) {
        findActive(articleId);
        List<ArticleVersionEntity> versions = articleVersionMapper.selectList(
                new LambdaQueryWrapper<ArticleVersionEntity>()
                        .eq(ArticleVersionEntity::getArticleId, articleId)
                        .orderByDesc(ArticleVersionEntity::getVersion));
        return versions.stream().map(v -> ArticleVersionVO.builder()
                .version(v.getVersion())
                .title(v.getTitle())
                .changeNote(v.getChangeNote())
                .createdAt(v.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    public ArticleVersionDiffVO diffVersions(Long articleId, Integer fromVersion, Integer toVersion) {
        ArticleEntity article = findActive(articleId);
        Integer effectiveTo = toVersion != null ? toVersion : article.getVersion();
        String fromContent = resolveVersionContent(articleId, fromVersion, article);
        String toContent = resolveVersionContent(articleId, effectiveTo, article);

        List<String> fromLines = splitLines(fromContent);
        List<String> toLines = splitLines(toContent);
        Patch<String> patch = DiffUtils.diff(fromLines, toLines);

        List<ArticleVersionDiffVO.DiffLine> lines = new ArrayList<ArticleVersionDiffVO.DiffLine>();
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DeltaType type = delta.getType();
            if (type == DeltaType.DELETE || type == DeltaType.CHANGE) {
                for (String line : delta.getSource().getLines()) {
                    lines.add(ArticleVersionDiffVO.DiffLine.builder().type("DELETE").content(line).build());
                }
            }
            if (type == DeltaType.INSERT || type == DeltaType.CHANGE) {
                for (String line : delta.getTarget().getLines()) {
                    lines.add(ArticleVersionDiffVO.DiffLine.builder().type("INSERT").content(line).build());
                }
            }
        }

        return ArticleVersionDiffVO.builder()
                .fromVersion(fromVersion)
                .toVersion(effectiveTo)
                .lines(lines)
                .build();
    }

    private String resolveVersionContent(Long articleId, Integer version, ArticleEntity current) {
        if (version == null || version.equals(current.getVersion())) {
            return current.getContentMd() == null ? "" : current.getContentMd();
        }
        ArticleVersionEntity snapshot = articleVersionMapper.selectOne(
                new LambdaQueryWrapper<ArticleVersionEntity>()
                        .eq(ArticleVersionEntity::getArticleId, articleId)
                        .eq(ArticleVersionEntity::getVersion, version));
        if (snapshot == null) {
            throw new NotFoundException("版本不存在: " + version);
        }
        return snapshot.getContentMd() == null ? "" : snapshot.getContentMd();
    }

    private List<String> splitLines(String content) {
        if (!StringUtils.hasText(content)) {
            return Collections.singletonList("");
        }
        String normalized = content.replace("\r\n", "\n");
        return java.util.Arrays.asList(normalized.split("\n", -1));
    }

    @Transactional
    public ArticleDetailVO restoreVersion(Long articleId, Integer version, Long userId) {
        ArticleEntity entity = findActive(articleId);
        ArticleVersionEntity snapshot = articleVersionMapper.selectOne(
                new LambdaQueryWrapper<ArticleVersionEntity>()
                        .eq(ArticleVersionEntity::getArticleId, articleId)
                        .eq(ArticleVersionEntity::getVersion, version));
        if (snapshot == null) {
            throw new NotFoundException("版本不存在");
        }
        saveVersion(entity, userId, "恢复前快照");
        entity.setTitle(snapshot.getTitle());
        entity.setContentMd(snapshot.getContentMd());
        entity.setUpdatedAt(LocalDateTime.now());
        applyContentFields(entity);
        articleMapper.updateById(entity);
        return toDetail(entity, true);
    }

    private List<ArticleListItemVO> listRelatedByCategory(ArticleEntity entity, int limit) {
        if (entity.getCategoryId() == null) {
            return Collections.emptyList();
        }
        List<ArticleEntity> articles = articleMapper.selectList(basePublishedWrapper()
                .eq(ArticleEntity::getCategoryId, entity.getCategoryId())
                .ne(ArticleEntity::getId, entity.getId())
                .orderByDesc(ArticleEntity::getPublishedAt)
                .last("LIMIT " + limit));
        return toListItems(articles);
    }

    private void saveVersion(ArticleEntity entity, Long userId, String changeNote) {
        ArticleVersionEntity version = new ArticleVersionEntity();
        version.setArticleId(entity.getId());
        version.setVersion(entity.getVersion() == null ? 1 : entity.getVersion());
        version.setTitle(entity.getTitle());
        version.setContentMd(entity.getContentMd() == null ? "" : entity.getContentMd());
        version.setContentHtml(entity.getContentHtml());
        version.setChangeNote(changeNote);
        version.setCreatedBy(userId == null ? entity.getAuthorId() : userId);
        version.setMetadata("{}");
        version.setCreatedAt(LocalDateTime.now());
        articleVersionMapper.insert(version);
        entity.setVersion((entity.getVersion() == null ? 1 : entity.getVersion()) + 1);
    }

    private void syncTaxonomyCountersAfterUpdate(ArticleEntity entity, boolean wasCounted,
                                                 Long oldCategoryId, List<Long> oldTagIds,
                                                 boolean tagsChanged) {
        boolean isCounted = isCountedInTaxonomy(entity);
        if (wasCounted && isCounted) {
            contentCounterService.movePublishedArticleCategory(oldCategoryId, entity.getCategoryId());
            if (tagsChanged) {
                contentCounterService.syncPublishedArticleTags(oldTagIds, loadTagIds(entity.getId()));
            }
            return;
        }
        if (wasCounted) {
            contentCounterService.adjustPublishedArticleTaxonomyCounts(oldCategoryId, oldTagIds, -1);
            return;
        }
        if (isCounted) {
            contentCounterService.adjustPublishedArticleTaxonomyCounts(entity, 1);
        }
    }

    private boolean isCountedInTaxonomy(ArticleEntity entity) {
        return entity != null
                && "published".equals(entity.getStatus())
                && "public".equals(entity.getVisibility())
                && entity.getDeletedAt() == null;
    }

    private LambdaQueryWrapper<ArticleEntity> basePublishedWrapper() {
        return new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getStatus, "published")
                .eq(ArticleEntity::getVisibility, "public")
                .isNull(ArticleEntity::getDeletedAt);
    }

    private void applyFilters(LambdaQueryWrapper<ArticleEntity> wrapper, String categorySlug,
                              String tagSlug, String keyword) {
        if (StringUtils.hasText(categorySlug)) {
            CategoryEntity category = categoryMapper.selectOne(new LambdaQueryWrapper<CategoryEntity>()
                    .eq(CategoryEntity::getSlug, categorySlug).isNull(CategoryEntity::getDeletedAt));
            if (category != null) {
                wrapper.eq(ArticleEntity::getCategoryId, category.getId());
            } else {
                wrapper.eq(ArticleEntity::getId, -1L);
            }
        }
        if (StringUtils.hasText(tagSlug)) {
            TagEntity tag = tagMapper.selectOne(new LambdaQueryWrapper<TagEntity>()
                    .eq(TagEntity::getSlug, tagSlug).isNull(TagEntity::getDeletedAt));
            if (tag != null) {
                wrapper.apply("EXISTS (SELECT 1 FROM article_tags at WHERE at.article_id = articles.id "
                        + "AND at.tag_id = {0})", tag.getId());
            } else {
                wrapper.eq(ArticleEntity::getId, -1L);
            }
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ArticleEntity::getTitle, keyword)
                    .or().like(ArticleEntity::getSummary, keyword)
                    .or().like(ArticleEntity::getContentText, keyword));
        }
    }

    private void validateCategoryId(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        CategoryEntity category = categoryMapper.selectOne(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getId, categoryId)
                .isNull(CategoryEntity::getDeletedAt));
        if (category == null) {
            throw new ValidationException("分类不存在");
        }
    }

    private void validateTagIds(List<Long> tagIds) {
        List<Long> normalized = normalizeTagIds(tagIds);
        if (normalized.isEmpty()) {
            return;
        }
        List<TagEntity> tags = tagMapper.selectBatchIds(normalized);
        Set<Long> activeIds = tags.stream()
                .filter(tag -> tag.getDeletedAt() == null)
                .map(TagEntity::getId)
                .collect(Collectors.toSet());
        for (Long tagId : normalized) {
            if (!activeIds.contains(tagId)) {
                throw new ValidationException("标签不存在");
            }
        }
    }

    private void ensureSlugUnique(String slug, Long excludeId) {
        LambdaQueryWrapper<ArticleEntity> wrapper = new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getSlug, slug)
                .isNull(ArticleEntity::getDeletedAt);
        if (excludeId != null) {
            wrapper.ne(ArticleEntity::getId, excludeId);
        }
        if (articleMapper.selectCount(wrapper) > 0) {
            throw new ValidationException("slug 已存在");
        }
    }

    private ArticleEntity findActive(Long id) {
        ArticleEntity entity = articleMapper.selectOne(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getId, id)
                .isNull(ArticleEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("文章不存在");
        }
        return entity;
    }

    private void applyContentFields(ArticleEntity entity) {
        String md = entity.getContentMd() == null ? "" : entity.getContentMd();
        entity.setContentHtml(MarkdownUtils.toHtml(md));
        entity.setContentText(MarkdownUtils.toPlainText(md));
        entity.setWordCount(MarkdownUtils.countWords(entity.getContentText()));
        entity.setReadingMinutes(MarkdownUtils.estimateReadingMinutes(entity.getWordCount()));
        if (entity.getViewCount() == null) {
            entity.setViewCount(0L);
        }
        if (entity.getLikeCount() == null) {
            entity.setLikeCount(0L);
        }
        if (entity.getCommentCount() == null) {
            entity.setCommentCount(0L);
        }
    }

    private void saveTags(Long articleId, List<Long> tagIds) {
        List<Long> normalizedTagIds = normalizeTagIds(tagIds);
        if (normalizedTagIds.isEmpty()) {
            return;
        }
        for (Long tagId : normalizedTagIds) {
            ArticleTagEntity link = new ArticleTagEntity();
            link.setArticleId(articleId);
            link.setTagId(tagId);
            link.setCreatedAt(LocalDateTime.now());
            articleTagMapper.insert(link);
        }
    }

    private List<Long> normalizeTagIds(List<Long> tagIds) {
        Set<Long> unique = new LinkedHashSet<Long>();
        if (tagIds == null) {
            return new ArrayList<Long>();
        }
        for (Long tagId : tagIds) {
            if (tagId != null && tagId > 0) {
                unique.add(tagId);
            }
        }
        return new ArrayList<Long>(unique);
    }

    private List<String> loadTagNames(Long articleId) {
        List<ArticleTagEntity> links = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTagEntity>()
                .eq(ArticleTagEntity::getArticleId, articleId));
        if (links.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> tagIds = links.stream().map(ArticleTagEntity::getTagId).collect(Collectors.toList());
        return tagMapper.selectBatchIds(tagIds).stream().map(TagEntity::getName).collect(Collectors.toList());
    }

    private List<Long> loadTagIds(Long articleId) {
        return articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTagEntity>()
                        .eq(ArticleTagEntity::getArticleId, articleId))
                .stream().map(ArticleTagEntity::getTagId).collect(Collectors.toList());
    }

    public List<ArchiveGroupVO> listArchives() {
        List<ArchiveArticleRow> rows = articleMapper.listArchiveArticles();
        Map<String, ArchiveGroupVO> groups = new LinkedHashMap<String, ArchiveGroupVO>();
        for (ArchiveArticleRow row : rows) {
            if (row.getYear() == null || row.getMonth() == null) {
                continue;
            }
            String key = row.getYear() + "-" + row.getMonth();
            ArchiveGroupVO group = groups.get(key);
            if (group == null) {
                group = ArchiveGroupVO.builder()
                        .year(row.getYear())
                        .month(row.getMonth())
                        .articles(new ArrayList<ArchiveGroupVO.ArchiveItemVO>())
                        .build();
                groups.put(key, group);
            }
            group.getArticles().add(ArchiveGroupVO.ArchiveItemVO.builder()
                    .id(row.getId())
                    .title(row.getTitle())
                    .slug(row.getSlug())
                    .publishedAt(row.getPublishedAt() == null ? null : row.getPublishedAt().toString())
                    .build());
        }
        return new ArrayList<ArchiveGroupVO>(groups.values());
    }

    private List<ArticleListItemVO> toListItems(List<ArticleEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> categoryIds = entities.stream()
                .map(ArticleEntity::getCategoryId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        Map<Long, CategoryEntity> categoryMap = categoryIds.isEmpty()
                ? Collections.<Long, CategoryEntity>emptyMap()
                : categoryMapper.selectBatchIds(new ArrayList<Long>(categoryIds)).stream()
                .collect(Collectors.toMap(CategoryEntity::getId, c -> c, (a, b) -> a));

        List<Long> articleIds = entities.stream().map(ArticleEntity::getId).collect(Collectors.toList());
        Map<Long, List<String>> tagNamesByArticle = loadTagNamesBatch(articleIds);

        return entities.stream()
                .map(entity -> toListItem(entity, categoryMap, tagNamesByArticle))
                .collect(Collectors.toList());
    }

    private ArticleListItemVO toListItem(ArticleEntity entity) {
        Map<Long, CategoryEntity> categoryMap = Collections.emptyMap();
        Map<Long, List<String>> tagNamesByArticle = loadTagNamesBatch(
                Collections.singletonList(entity.getId()));
        return toListItem(entity, categoryMap, tagNamesByArticle);
    }

    private ArticleListItemVO toListItem(ArticleEntity entity,
                                         Map<Long, CategoryEntity> categoryMap,
                                         Map<Long, List<String>> tagNamesByArticle) {
        ArticleListItemVO.CategoryBriefVO categoryBrief = null;
        if (entity.getCategoryId() != null) {
            CategoryEntity category = categoryMap.get(entity.getCategoryId());
            if (category == null && categoryMap.isEmpty()) {
                category = categoryMapper.selectById(entity.getCategoryId());
            }
            if (category != null) {
                categoryBrief = ArticleListItemVO.CategoryBriefVO.builder()
                        .name(category.getName())
                        .slug(category.getSlug())
                        .build();
            }
        }
        List<String> tags = tagNamesByArticle.get(entity.getId());
        if (tags == null) {
            tags = Collections.emptyList();
        }
        return ArticleListItemVO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .summary(entity.getSummary())
                .status(entity.getStatus())
                .pinned(entity.getPinned())
                .readingMinutes(entity.getReadingMinutes())
                .wordCount(entity.getWordCount())
                .viewCount(entity.getViewCount())
                .publishedAt(entity.getPublishedAt())
                .category(categoryBrief)
                .tags(tags)
                .build();
    }

    private Map<Long, List<String>> loadTagNamesBatch(List<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ArticleTagEntity> links = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTagEntity>()
                .in(ArticleTagEntity::getArticleId, articleIds));
        if (links.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> tagIds = links.stream().map(ArticleTagEntity::getTagId).collect(Collectors.toSet());
        Map<Long, TagEntity> tagMap = tagMapper.selectBatchIds(new ArrayList<Long>(tagIds)).stream()
                .collect(Collectors.toMap(TagEntity::getId, t -> t, (a, b) -> a));
        Map<Long, List<String>> result = new HashMap<Long, List<String>>();
        for (ArticleTagEntity link : links) {
            TagEntity tag = tagMap.get(link.getTagId());
            if (tag != null) {
                result.computeIfAbsent(link.getArticleId(), k -> new ArrayList<String>()).add(tag.getName());
            }
        }
        return result;
    }

    private ArticleDetailVO toDetail(ArticleEntity entity, boolean includeMd) {
        Map<String, String> category = new HashMap<String, String>();
        if (entity.getCategoryId() != null) {
            CategoryEntity cat = categoryMapper.selectById(entity.getCategoryId());
            if (cat != null) {
                category.put("name", cat.getName());
                category.put("slug", cat.getSlug());
            }
        }
        Map<String, String> github = new HashMap<String, String>();
        if (StringUtils.hasText(entity.getGithubRepo())) {
            github.put("repo", entity.getGithubRepo());
            github.put("commitSha", entity.getGithubCommitSha());
        }
        ArticleDetailVO.ArticleDetailVOBuilder builder = ArticleDetailVO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .summary(entity.getSummary())
                .contentHtml(entity.getContentHtml())
                .status(entity.getStatus())
                .pinned(entity.getPinned())
                .readingMinutes(entity.getReadingMinutes())
                .wordCount(entity.getWordCount())
                .viewCount(entity.getViewCount())
                .likeCount(articleLikeService.getLikeCount(entity))
                .commentCount(entity.getCommentCount())
                .publishedAt(entity.getPublishedAt())
                .tags(loadTagNames(entity.getId()))
                .category(category)
                .github(github.isEmpty() ? null : github);
        if (includeMd) {
            builder.contentMd(entity.getContentMd());
            builder.categoryId(entity.getCategoryId());
            builder.tagIds(loadTagIds(entity.getId()));
        }
        return builder.build();
    }
}
