package com.jiangou.search.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.note.mapper.NoteMapper;
import com.jiangou.project.entity.ProjectEntity;
import com.jiangou.project.mapper.ProjectMapper;
import com.jiangou.search.engine.MeilisearchSearchEngine;
import com.jiangou.search.entity.SearchDocumentEntity;
import com.jiangou.search.mapper.SearchDocumentMapper;
import com.jiangou.snippet.mapper.SnippetMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchIndexServiceTest {

    @Mock
    private SearchDocumentMapper searchDocumentMapper;
    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private SnippetMapper snippetMapper;
    @Mock
    private NoteMapper noteMapper;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ObjectProvider<MeilisearchSearchEngine> meilisearchSearchEngine;

    private SearchIndexService service;

    @BeforeEach
    void setUp() {
        when(meilisearchSearchEngine.getIfAvailable()).thenReturn(null);
        service = new SearchIndexService(searchDocumentMapper, articleMapper, snippetMapper,
                noteMapper, projectMapper, meilisearchSearchEngine);
    }

    @Test
    void rebuildAll_indexesOnlyPublicPublishedContent() {
        when(articleMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(snippetMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(noteMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(projectMapper.selectList(any())).thenReturn(Collections.emptyList());

        int count = service.rebuildAll();

        assertEquals(0, count);
        verify(articleMapper).selectList(any());
        verify(snippetMapper).selectList(any());
        verify(noteMapper).selectList(any());
        verify(projectMapper).selectList(any());
        verify(searchDocumentMapper).delete(null);
    }

    @Test
    void syncArticle_privatePublishedArticleIsRemovedFromIndex() {
        ArticleEntity article = new ArticleEntity();
        article.setId(9L);
        article.setStatus("published");
        article.setVisibility("private");
        article.setSlug("hidden");
        article.setTitle("Hidden");
        article.setSummary("");
        article.setContentText("secret");

        service.syncArticle(article);

        verify(searchDocumentMapper).delete(any());
        verify(searchDocumentMapper, never()).insert(any(SearchDocumentEntity.class));
    }

    @Test
    void syncArticle_publicPublishedArticleIsIndexed() {
        ArticleEntity article = new ArticleEntity();
        article.setId(10L);
        article.setStatus("published");
        article.setVisibility("public");
        article.setSlug("visible");
        article.setTitle("Visible");
        article.setSummary("sum");
        article.setContentText("content");

        service.syncArticle(article);

        verify(searchDocumentMapper).delete(any());
        verify(searchDocumentMapper).insert(any(SearchDocumentEntity.class));
    }

    @Test
    void syncProject_activeProjectIsIndexed() {
        ProjectEntity project = new ProjectEntity();
        project.setId(3L);
        project.setOwner("jiangou");
        project.setRepo("demo");
        project.setName("Demo");
        project.setDescription("desc");

        service.syncProject(project);

        verify(searchDocumentMapper).delete(any());
        verify(searchDocumentMapper).insert(any(SearchDocumentEntity.class));
    }

    @Test
    void syncProject_deletedProjectIsRemovedFromIndex() {
        ProjectEntity project = new ProjectEntity();
        project.setId(4L);
        project.setOwner("jiangou");
        project.setRepo("gone");
        project.setDeletedAt(java.time.LocalDateTime.now());

        service.syncProject(project);

        verify(searchDocumentMapper).delete(any());
        verify(searchDocumentMapper, never()).insert(any(SearchDocumentEntity.class));
    }
}
