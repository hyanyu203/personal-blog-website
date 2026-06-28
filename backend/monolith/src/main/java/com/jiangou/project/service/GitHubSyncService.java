package com.jiangou.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.project.entity.ProjectEntity;
import com.jiangou.project.entity.GithubSyncLogEntity;
import com.jiangou.project.mapper.GithubSyncLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class GitHubSyncService {

    private static final Logger log = LoggerFactory.getLogger(GitHubSyncService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GithubSyncLogMapper syncLogMapper;

    @Value("${jiangou.github.token:}")
    private String githubToken;

    public GitHubSyncService(GithubSyncLogMapper syncLogMapper) {
        this.syncLogMapper = syncLogMapper;
    }

    public boolean syncProject(ProjectEntity entity) {
        String url = "https://api.github.com/repos/" + entity.getOwner() + "/" + entity.getRepo();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("User-Agent", "JianGou-Blog");
        if (githubToken != null && !githubToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + githubToken);
        }

        GithubSyncLogEntity logEntity = new GithubSyncLogEntity();
        logEntity.setProjectId(entity.getId());
        logEntity.setRequestCount(1);
        logEntity.setMetadata("{}");
        logEntity.setCreatedAt(LocalDateTime.now());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<Void>(headers), String.class);
            JsonNode node = objectMapper.readTree(response.getBody());

            entity.setName(node.path("name").asText(entity.getRepo()));
            entity.setDescription(node.path("description").isNull() ? null : node.path("description").asText());
            entity.setHomepageUrl(node.path("homepage").isNull() ? null : node.path("homepage").asText());
            entity.setGithubUrl(node.path("html_url").asText(entity.getGithubUrl()));
            entity.setLanguage(node.path("language").isNull() ? null : node.path("language").asText());
            entity.setStars(node.path("stargazers_count").asInt(0));
            entity.setForks(node.path("forks_count").asInt(0));
            entity.setOpenIssues(node.path("open_issues_count").asInt(0));
            JsonNode licenseNode = node.path("license");
            if (!licenseNode.isMissingNode() && !licenseNode.isNull()) {
                entity.setLicense(licenseNode.path("spdx_id").asText(null));
            }
            if (!node.path("pushed_at").isNull()) {
                Instant instant = Instant.parse(node.path("pushed_at").asText());
                entity.setPushedAt(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
            }
            entity.setSyncedAt(LocalDateTime.now());
            entity.setSyncStatus("ok");

            logEntity.setStatus("success");
            syncLogMapper.insert(logEntity);
            return true;
        } catch (HttpStatusCodeException e) {
            log.warn("GitHub sync failed for {}/{}: {}", entity.getOwner(), entity.getRepo(), e.getStatusCode());
            entity.setSyncStatus("stale");
            logEntity.setStatus("failed");
            logEntity.setErrorMessage(e.getStatusCode() + ": " + e.getResponseBodyAsString());
            syncLogMapper.insert(logEntity);
            return false;
        } catch (Exception e) {
            log.warn("GitHub sync error: {}", e.getMessage());
            entity.setSyncStatus("failed");
            logEntity.setStatus("failed");
            logEntity.setErrorMessage(e.getMessage());
            syncLogMapper.insert(logEntity);
            return false;
        }
    }
}
