package com.jiangou.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.result.PageResult;
import com.jiangou.project.dto.ProjectDTO;
import com.jiangou.project.entity.ProjectEntity;
import com.jiangou.project.mapper.ProjectMapper;
import com.jiangou.project.vo.ProjectVO;
import com.jiangou.search.service.SearchIndexService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final GitHubSyncService gitHubSyncService;
    private final SearchIndexService searchIndexService;

    public ProjectService(ProjectMapper projectMapper, GitHubSyncService gitHubSyncService,
                          SearchIndexService searchIndexService) {
        this.projectMapper = projectMapper;
        this.gitHubSyncService = gitHubSyncService;
        this.searchIndexService = searchIndexService;
    }

    public List<ProjectVO> listPublic() {
        return projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                        .isNull(ProjectEntity::getDeletedAt)
                        .orderByDesc(ProjectEntity::getPinned)
                        .orderByAsc(ProjectEntity::getSortOrder)
                        .orderByDesc(ProjectEntity::getStars))
                .stream().map(this::toVo).collect(Collectors.toList());
    }

    public PageResult<ProjectVO> listAdmin(long page, long pageSize) {
        Page<ProjectEntity> result = projectMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<ProjectEntity>()
                        .isNull(ProjectEntity::getDeletedAt)
                        .orderByDesc(ProjectEntity::getUpdatedAt));
        List<ProjectVO> items = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }

    public ProjectVO getByOwnerRepo(String owner, String repo) {
        ProjectEntity entity = projectMapper.selectOne(new LambdaQueryWrapper<ProjectEntity>()
                .eq(ProjectEntity::getOwner, owner).eq(ProjectEntity::getRepo, repo)
                .isNull(ProjectEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("项目不存在");
        }
        return toVo(entity);
    }

    @Transactional
    public ProjectVO create(ProjectDTO dto) {
        ensureUnique(dto.getOwner(), dto.getRepo(), null);
        ProjectEntity entity = fromDto(dto);
        entity.setGithubUrl("https://github.com/" + dto.getOwner() + "/" + dto.getRepo());
        entity.setSyncStatus("ok");
        entity.setStars(0);
        entity.setForks(0);
        entity.setOpenIssues(0);
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        projectMapper.insert(entity);
        searchIndexService.syncProject(entity);
        return toVo(entity);
    }

    @Transactional
    public ProjectVO update(Long id, ProjectDTO dto) {
        ProjectEntity entity = findActive(id);
        if (StringUtils.hasText(dto.getName())) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getHomepageUrl() != null) {
            entity.setHomepageUrl(dto.getHomepageUrl());
        }
        if (dto.getPinned() != null) {
            entity.setPinned(dto.getPinned());
        }
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateById(entity);
        searchIndexService.syncProject(entity);
        return toVo(entity);
    }

    @Transactional
    public void delete(Long id) {
        ProjectEntity entity = findActive(id);
        entity.setDeletedAt(LocalDateTime.now());
        projectMapper.updateById(entity);
        searchIndexService.syncProject(entity);
    }

    @Transactional
    public ProjectVO syncOne(Long id) {
        ProjectEntity entity = findActive(id);
        gitHubSyncService.syncProject(entity);
        entity.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateById(entity);
        searchIndexService.syncProject(entity);
        return toVo(entity);
    }

    @Transactional
    public int syncAll() {
        List<ProjectEntity> all = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                .isNull(ProjectEntity::getDeletedAt));
        int ok = 0;
        for (ProjectEntity entity : all) {
            if (gitHubSyncService.syncProject(entity)) {
                ok++;
            }
            entity.setUpdatedAt(LocalDateTime.now());
            projectMapper.updateById(entity);
            searchIndexService.syncProject(entity);
        }
        return ok;
    }

    private ProjectEntity fromDto(ProjectDTO dto) {
        ProjectEntity entity = new ProjectEntity();
        entity.setOwner(dto.getOwner());
        entity.setRepo(dto.getRepo());
        entity.setName(dto.getName() == null ? dto.getRepo() : dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setHomepageUrl(dto.getHomepageUrl());
        entity.setPinned(Boolean.TRUE.equals(dto.getPinned()));
        entity.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        return entity;
    }

    private void ensureUnique(String owner, String repo, Long excludeId) {
        LambdaQueryWrapper<ProjectEntity> w = new LambdaQueryWrapper<ProjectEntity>()
                .eq(ProjectEntity::getOwner, owner).eq(ProjectEntity::getRepo, repo)
                .isNull(ProjectEntity::getDeletedAt);
        if (excludeId != null) {
            w.ne(ProjectEntity::getId, excludeId);
        }
        if (projectMapper.selectCount(w) > 0) {
            throw new ValidationException("项目已存在");
        }
    }

    private ProjectEntity findActive(Long id) {
        ProjectEntity entity = projectMapper.selectOne(new LambdaQueryWrapper<ProjectEntity>()
                .eq(ProjectEntity::getId, id).isNull(ProjectEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("项目不存在");
        }
        return entity;
    }

    private ProjectVO toVo(ProjectEntity e) {
        return ProjectVO.builder()
                .id(e.getId()).owner(e.getOwner()).repo(e.getRepo())
                .name(e.getName()).description(e.getDescription())
                .homepageUrl(e.getHomepageUrl()).githubUrl(e.getGithubUrl())
                .language(e.getLanguage()).stars(e.getStars()).forks(e.getForks())
                .openIssues(e.getOpenIssues()).license(e.getLicense())
                .pushedAt(e.getPushedAt()).syncStatus(e.getSyncStatus())
                .pinned(e.getPinned()).build();
    }
}
