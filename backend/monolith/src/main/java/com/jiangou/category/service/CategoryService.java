package com.jiangou.category.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.category.dto.CategoryDTO;
import com.jiangou.category.entity.CategoryEntity;
import com.jiangou.category.mapper.CategoryMapper;
import com.jiangou.category.vo.CategoryVO;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.cache.RedisCacheHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final String LIST_CACHE_KEY = "cache:categories:all";
    private static final long CACHE_TTL_SEC = 600;

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final RedisCacheHelper cacheHelper;
    private final ObjectMapper objectMapper;

    public CategoryService(CategoryMapper categoryMapper, ArticleMapper articleMapper,
                           RedisCacheHelper cacheHelper, ObjectMapper objectMapper) {
        this.categoryMapper = categoryMapper;
        this.articleMapper = articleMapper;
        this.cacheHelper = cacheHelper;
        this.objectMapper = objectMapper;
    }

    public List<CategoryVO> listAll() {
        String cached = cacheHelper.get(LIST_CACHE_KEY);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryVO.class));
            } catch (JsonProcessingException ignored) {
                cacheHelper.delete(LIST_CACHE_KEY);
            }
        }
        List<CategoryVO> list = categoryMapper.selectList(new LambdaQueryWrapper<CategoryEntity>()
                        .isNull(CategoryEntity::getDeletedAt)
                        .orderByAsc(CategoryEntity::getSortOrder))
                .stream()
                .map(c -> CategoryVO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .slug(c.getSlug())
                        .description(c.getDescription())
                        .postCount(c.getPostCount())
                        .build())
                .collect(Collectors.toList());
        try {
            cacheHelper.set(LIST_CACHE_KEY, objectMapper.writeValueAsString(list), CACHE_TTL_SEC);
        } catch (JsonProcessingException ignored) {
            // skip cache write
        }
        return list;
    }

    public CategoryVO getBySlug(String slug) {
        CategoryEntity entity = categoryMapper.selectOne(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getSlug, slug).isNull(CategoryEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("分类不存在");
        }
        return CategoryVO.builder()
                .id(entity.getId()).name(entity.getName()).slug(entity.getSlug())
                .description(entity.getDescription()).postCount(entity.getPostCount()).build();
    }

    @Transactional
    public CategoryVO create(CategoryDTO dto) {
        ensureSlugUnique(dto.getSlug(), null);
        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setParentId(dto.getParentId());
        entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        entity.setPostCount(0);
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        categoryMapper.insert(entity);
        cacheHelper.delete(LIST_CACHE_KEY);
        return getBySlug(entity.getSlug());
    }

    @Transactional
    public CategoryVO update(Long id, CategoryDTO dto) {
        CategoryEntity entity = findActive(id);
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
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(entity);
        cacheHelper.delete(LIST_CACHE_KEY);
        return getBySlug(entity.getSlug());
    }

    @Transactional
    public void delete(Long id) {
        CategoryEntity entity = findActive(id);
        long refs = articleMapper.selectCount(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getCategoryId, id)
                .isNull(ArticleEntity::getDeletedAt));
        if (refs > 0) {
            throw new ValidationException("分类仍被文章引用，无法删除");
        }
        entity.setDeletedAt(LocalDateTime.now());
        categoryMapper.updateById(entity);
        cacheHelper.delete(LIST_CACHE_KEY);
    }

    private CategoryEntity findActive(Long id) {
        CategoryEntity entity = categoryMapper.selectOne(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getId, id).isNull(CategoryEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("分类不存在");
        }
        return entity;
    }

    private void ensureSlugUnique(String slug, Long excludeId) {
        LambdaQueryWrapper<CategoryEntity> w = new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getSlug, slug).isNull(CategoryEntity::getDeletedAt);
        if (excludeId != null) {
            w.ne(CategoryEntity::getId, excludeId);
        }
        if (categoryMapper.selectCount(w) > 0) {
            throw new ValidationException("slug 已存在");
        }
    }
}
