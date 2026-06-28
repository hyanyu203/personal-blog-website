package com.jiangou.snippet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.result.PageResult;
import com.jiangou.common.util.MarkdownUtils;
import com.jiangou.snippet.dto.SnippetDTO;
import com.jiangou.snippet.entity.SnippetEntity;
import com.jiangou.snippet.mapper.SnippetMapper;
import com.jiangou.snippet.vo.SnippetVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnippetService {

    private final SnippetMapper snippetMapper;
    private final SnippetLikeService snippetLikeService;

    public SnippetService(SnippetMapper snippetMapper, SnippetLikeService snippetLikeService) {
        this.snippetMapper = snippetMapper;
        this.snippetLikeService = snippetLikeService;
    }

    public PageResult<SnippetVO> listPublic(long page, long pageSize, String language) {
        LambdaQueryWrapper<SnippetEntity> wrapper = publicWrapper();
        if (StringUtils.hasText(language)) {
            wrapper.eq(SnippetEntity::getLanguage, language);
        }
        wrapper.orderByDesc(SnippetEntity::getCreatedAt);
        Page<SnippetEntity> result = snippetMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(result.getRecords().stream().map(this::toVo).collect(Collectors.toList()),
                result.getTotal(), page, pageSize);
    }

    public PageResult<SnippetVO> listAdmin(long page, long pageSize) {
        Page<SnippetEntity> result = snippetMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<SnippetEntity>().isNull(SnippetEntity::getDeletedAt)
                        .orderByDesc(SnippetEntity::getUpdatedAt));
        return PageResult.of(result.getRecords().stream().map(this::toVo).collect(Collectors.toList()),
                result.getTotal(), page, pageSize);
    }

    public SnippetVO getBySlug(String slug) {
        SnippetEntity entity = snippetMapper.selectOne(publicWrapper().eq(SnippetEntity::getSlug, slug));
        if (entity == null) {
            throw new NotFoundException("代码片段不存在");
        }
        snippetMapper.update(null, new LambdaUpdateWrapper<SnippetEntity>()
                .eq(SnippetEntity::getId, entity.getId())
                .isNull(SnippetEntity::getDeletedAt)
                .setSql("view_count = COALESCE(view_count, 0) + 1"));
        long current = entity.getViewCount() == null ? 0L : entity.getViewCount();
        entity.setViewCount(current + 1);
        return toVo(entity);
    }

    public String getRawCode(String slug) {
        SnippetEntity entity = snippetMapper.selectOne(publicWrapper().eq(SnippetEntity::getSlug, slug));
        if (entity == null) {
            throw new NotFoundException("代码片段不存在");
        }
        return entity.getCode();
    }

    @Transactional
    public SnippetVO create(SnippetDTO dto, Long authorId) {
        ensureSlugUnique(dto.getSlug(), null);
        SnippetEntity entity = fromDto(dto, authorId);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        snippetMapper.insert(entity);
        return toVo(entity);
    }

    @Transactional
    public SnippetVO update(Long id, SnippetDTO dto) {
        SnippetEntity entity = findActive(id);
        if (StringUtils.hasText(dto.getSlug())) {
            ensureSlugUnique(dto.getSlug(), id);
            entity.setSlug(dto.getSlug());
        }
        if (StringUtils.hasText(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }
        if (StringUtils.hasText(dto.getLanguage())) {
            entity.setLanguage(dto.getLanguage());
        }
        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getDescriptionMd() != null) {
            entity.setDescriptionMd(dto.getDescriptionMd());
            entity.setDescriptionHtml(MarkdownUtils.toHtml(dto.getDescriptionMd()));
        }
        if (dto.getVisibility() != null) {
            entity.setVisibility(dto.getVisibility());
        }
        entity.setHighlightedHtml(wrapCode(entity.getCode(), entity.getLanguage()));
        entity.setUpdatedAt(LocalDateTime.now());
        snippetMapper.updateById(entity);
        return toVo(entity);
    }

    @Transactional
    public void delete(Long id) {
        SnippetEntity entity = findActive(id);
        entity.setDeletedAt(LocalDateTime.now());
        snippetMapper.updateById(entity);
    }

    @Transactional
    public void recordCopy(Long id) {
        SnippetEntity entity = snippetMapper.selectOne(publicWrapper().eq(SnippetEntity::getId, id));
        if (entity == null) {
            throw new NotFoundException("代码片段不存在");
        }
        entity.setCopyCount(entity.getCopyCount() + 1);
        snippetMapper.updateById(entity);
    }

    private LambdaQueryWrapper<SnippetEntity> publicWrapper() {
        return new LambdaQueryWrapper<SnippetEntity>()
                .eq(SnippetEntity::getVisibility, "public")
                .isNull(SnippetEntity::getDeletedAt);
    }

    private SnippetEntity fromDto(SnippetDTO dto, Long authorId) {
        SnippetEntity entity = new SnippetEntity();
        entity.setAuthorId(authorId);
        entity.setTitle(dto.getTitle());
        entity.setSlug(dto.getSlug());
        entity.setLanguage(dto.getLanguage());
        entity.setCode(dto.getCode());
        entity.setHighlightedHtml(wrapCode(dto.getCode(), dto.getLanguage()));
        entity.setDescriptionMd(dto.getDescriptionMd());
        entity.setDescriptionHtml(MarkdownUtils.toHtml(dto.getDescriptionMd() == null ? "" : dto.getDescriptionMd()));
        entity.setVisibility(dto.getVisibility() == null ? "public" : dto.getVisibility());
        entity.setViewCount(0L);
        entity.setCopyCount(0L);
        entity.setLikeCount(0L);
        entity.setMetadata("{}");
        return entity;
    }

    private String wrapCode(String code, String language) {
        String lang = sanitizeLanguage(language);
        return "<pre><code class=\"language-" + lang + "\">" + escapeHtml(code) + "</code></pre>";
    }

    private String sanitizeLanguage(String language) {
        if (!StringUtils.hasText(language)) {
            return "text";
        }
        String sanitized = language.replaceAll("[^a-zA-Z0-9+#.-]", "");
        return sanitized.isEmpty() ? "text" : sanitized;
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void ensureSlugUnique(String slug, Long excludeId) {
        LambdaQueryWrapper<SnippetEntity> w = new LambdaQueryWrapper<SnippetEntity>()
                .eq(SnippetEntity::getSlug, slug).isNull(SnippetEntity::getDeletedAt);
        if (excludeId != null) {
            w.ne(SnippetEntity::getId, excludeId);
        }
        if (snippetMapper.selectCount(w) > 0) {
            throw new ValidationException("slug 已存在");
        }
    }

    private SnippetEntity findActive(Long id) {
        SnippetEntity entity = snippetMapper.selectOne(new LambdaQueryWrapper<SnippetEntity>()
                .eq(SnippetEntity::getId, id).isNull(SnippetEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("代码片段不存在");
        }
        return entity;
    }

    private SnippetVO toVo(SnippetEntity e) {
        return SnippetVO.builder()
                .id(e.getId()).title(e.getTitle()).slug(e.getSlug())
                .language(e.getLanguage()).code(e.getCode())
                .highlightedHtml(e.getHighlightedHtml())
                .descriptionHtml(e.getDescriptionHtml())
                .viewCount(e.getViewCount()).copyCount(e.getCopyCount())
                .likeCount(snippetLikeService.getLikeCount(e))
                .createdAt(e.getCreatedAt()).build();
    }
}
