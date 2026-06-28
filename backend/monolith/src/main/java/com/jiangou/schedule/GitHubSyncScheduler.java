package com.jiangou.schedule;

import com.jiangou.project.entity.ProjectEntity;
import com.jiangou.project.mapper.ProjectMapper;
import com.jiangou.project.service.GitHubSyncService;
import com.jiangou.search.service.SearchIndexService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GitHubSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(GitHubSyncScheduler.class);

    private final ProjectMapper projectMapper;
    private final GitHubSyncService gitHubSyncService;
    private final SearchIndexService searchIndexService;

    public GitHubSyncScheduler(ProjectMapper projectMapper, GitHubSyncService gitHubSyncService,
                               SearchIndexService searchIndexService) {
        this.projectMapper = projectMapper;
        this.gitHubSyncService = gitHubSyncService;
        this.searchIndexService = searchIndexService;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void syncDaily() {
        List<ProjectEntity> projects = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                .isNull(ProjectEntity::getDeletedAt));
        log.info("GitHub daily sync started, {} projects", projects.size());
        for (ProjectEntity p : projects) {
            gitHubSyncService.syncProject(p);
            projectMapper.updateById(p);
            searchIndexService.syncProject(p);
        }
    }
}
