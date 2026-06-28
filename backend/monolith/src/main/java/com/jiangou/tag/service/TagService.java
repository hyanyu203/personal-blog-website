package com.jiangou.tag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.entity.ArticleTagEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.article.mapper.ArticleTagMapper;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.cache.RedisCacheHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.tag.dto.TagDTO;
import com.jiangou.tag.entity.TagEntity;
import com.jiangou.tag.mapper.TagMapper;
import com.jiangou.tag.vo.TagVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private static final String LIST_CACHE_KEY = "cache:tags:all";
    private static final long CACHE_TTL_SEC = 600;

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final RedisCacheHelper cacheHelper;
    private final ObjectMapper objectMapper;

    public TagService(TagMapper tagMapper, ArticleTagMapper articleTagMapper,
                      RedisCacheHelper cacheHelper, ObjectMapper objectMapper) {
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
        this.cacheHelper = cacheHelper;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public List<TagVO> listAll() {
        String cached = cacheHelper.get(LIST_CACHE_KEY);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, TagVO.class));
            } catch (JsonProcessingException ignored) {
                cacheHelper.delete(LIST_CACHE_KEY);
            }
        }
        List<TagVO> list = tagMapper.selectList(new LambdaQueryWrapper<TagEntity>()
                        .isNull(TagEntity::getDeletedAt)
                        .orderByDesc(TagEntity::getUsageCount))
                .stream()
                .map(t -> TagVO.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .slug(t.getSlug())
                        .color(t.getColor())
                        .usageCount(t.getUsageCount())
                        .build())
                .collect(Collectors.toList());
        try {
            cacheHelper.set(LIST_CACHE_KEY, objectMapper.writeValueAsString(list), CACHE_TTL_SEC);
        } catch (JsonProcessingException ignored) {
            // skip cache write
        }
        return list;
    }

    public TagVO getBySlug(String slug) {
        TagEntity entity = tagMapper.selectOne(new LambdaQueryWrapper<TagEntity>()
                .eq(TagEntity::getSlug, slug).isNull(TagEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("标签不存在");
        }
        return TagVO.builder()
                .id(entity.getId()).name(entity.getName()).slug(entity.getSlug())
                .color(entity.getColor()).usageCount(entity.getUsageCount()).build();
    }

    @Transactional
    public TagVO create(TagDTO dto) {
        ensureSlugUnique(dto.getSlug(), null);
        TagEntity entity = new TagEntity();
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setColor(dto.getColor());
        entity.setUsageCount(0);
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        tagMapper.insert(entity);
        cacheHelper.delete(LIST_CACHE_KEY);
        return getBySlug(entity.getSlug());
    }

    @Transactional
    public TagVO update(Long id, TagDTO dto) {
        TagEntity entity = findActive(id);
        if (dto.getSlug() != null) {
            ensureSlugUnique(dto.getSlug(), id);
            entity.setSlug(dto.getSlug());
        }
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getColor() != null) {
            entity.setColor(dto.getColor());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        tagMapper.updateById(entity);
        cacheHelper.delete(LIST_CACHE_KEY);
        return getBySlug(entity.getSlug());
    }

    @Transactional
    public void delete(Long id) {
        TagEntity entity = findActive(id);
        long refs = articleTagMapper.selectCount(new LambdaQueryWrapper<ArticleTagEntity>()
                .eq(ArticleTagEntity::getTagId, id));
        if (refs > 0) {
            throw new ValidationException("标签仍被文章引用，无法删除");
        }
        entity.setDeletedAt(LocalDateTime.now());
        tagMapper.updateById(entity);
        cacheHelper.delete(LIST_CACHE_KEY);
    }

    private TagEntity findActive(Long id) {
        TagEntity entity = tagMapper.selectOne(new LambdaQueryWrapper<TagEntity>()
                .eq(TagEntity::getId, id).isNull(TagEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("标签不存在");
        }
        return entity;
    }

    private void ensureSlugUnique(String slug, Long excludeId) {
        LambdaQueryWrapper<TagEntity> w = new LambdaQueryWrapper<TagEntity>()
                .eq(TagEntity::getSlug, slug).isNull(TagEntity::getDeletedAt);
        if (excludeId != null) {
            w.ne(TagEntity::getId, excludeId);
        }
        if (tagMapper.selectCount(w) > 0) {
            throw new ValidationException("slug 已存在");
        }
    }
}
