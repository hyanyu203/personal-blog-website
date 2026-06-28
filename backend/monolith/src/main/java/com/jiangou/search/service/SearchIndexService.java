package com.jiangou.search.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.note.entity.NoteEntity;
import com.jiangou.note.mapper.NoteMapper;
import com.jiangou.project.entity.ProjectEntity;
import com.jiangou.project.mapper.ProjectMapper;
import com.jiangou.search.engine.MeilisearchSearchEngine;
import com.jiangou.search.entity.SearchDocumentEntity;
import com.jiangou.search.mapper.SearchDocumentMapper;
import com.jiangou.search.util.SearchMetadataUtils;
import com.jiangou.snippet.entity.SnippetEntity;
import com.jiangou.snippet.mapper.SnippetMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchIndexService {

    private final SearchDocumentMapper searchDocumentMapper;
    private final ArticleMapper articleMapper;
    private final SnippetMapper snippetMapper;
    private final NoteMapper noteMapper;
    private final ProjectMapper projectMapper;
    private final ObjectProvider<MeilisearchSearchEngine> meilisearchSearchEngine;

    public SearchIndexService(SearchDocumentMapper searchDocumentMapper, ArticleMapper articleMapper,
                              SnippetMapper snippetMapper, NoteMapper noteMapper, ProjectMapper projectMapper,
                              ObjectProvider<MeilisearchSearchEngine> meilisearchSearchEngine) {
        this.searchDocumentMapper = searchDocumentMapper;
        this.articleMapper = articleMapper;
        this.snippetMapper = snippetMapper;
        this.noteMapper = noteMapper;
        this.projectMapper = projectMapper;
        this.meilisearchSearchEngine = meilisearchSearchEngine;
    }

    @Transactional
    public int rebuildAll() {
        searchDocumentMapper.delete(null);
        int count = 0;
        count += indexArticles();
        count += indexSnippets();
        count += indexNotes();
        count += indexProjects();
        syncMeilisearchAll();
        return count;
    }

    @Transactional
    public void syncArticleById(Long id) {
        if (id == null) {
            return;
        }
        ArticleEntity article = articleMapper.selectById(id);
        syncArticle(article);
    }

    @Transactional
    public void syncNoteById(Long id) {
        if (id == null) {
            return;
        }
        NoteEntity note = noteMapper.selectById(id);
        syncNote(note);
    }

    @Transactional
    public void syncSnippetById(Long id) {
        if (id == null) {
            return;
        }
        SnippetEntity snippet = snippetMapper.selectById(id);
        syncSnippet(snippet);
    }

    @Transactional
    public void syncProjectById(Long id) {
        if (id == null) {
            return;
        }
        syncProject(projectMapper.selectById(id));
    }

    public void syncProject(ProjectEntity project) {
        if (project == null || project.getId() == null) {
            return;
        }
        if (project.getDeletedAt() == null) {
            String title = project.getName() != null ? project.getName()
                    : project.getOwner() + "/" + project.getRepo();
            upsertDocument("project", project.getId(), title,
                    project.getDescription() == null ? "" : project.getDescription(),
                    SearchMetadataUtils.withUrl("/projects/" + project.getOwner() + "/" + project.getRepo()));
        } else {
            removeDocument("project", project.getId());
        }
    }

    public void replayMeilisearchDocument(String targetType, Long targetId) {
        MeilisearchSearchEngine engine = meilisearchSearchEngine.getIfAvailable();
        if (engine == null || targetType == null || targetId == null) {
            return;
        }
        SearchDocumentEntity doc = searchDocumentMapper.selectOne(new LambdaQueryWrapper<SearchDocumentEntity>()
                .eq(SearchDocumentEntity::getTargetType, targetType)
                .eq(SearchDocumentEntity::getTargetId, targetId)
                .eq(SearchDocumentEntity::getStatus, "active"));
        if (doc != null) {
            engine.upsertDocument(doc);
        } else {
            engine.deleteDocument(targetType, targetId);
        }
    }

    public void syncArticle(ArticleEntity article) {
        if (article == null || article.getId() == null) {
            return;
        }
        if (isArticleSearchable(article)) {
            upsertDocument("article", article.getId(), article.getTitle(),
                    (article.getSummary() == null ? "" : article.getSummary()) + " "
                            + (article.getContentText() == null ? "" : article.getContentText()),
                    SearchMetadataUtils.withUrl("/posts/" + article.getSlug()));
        } else {
            removeDocument("article", article.getId());
        }
    }

    public void syncNote(NoteEntity note) {
        if (note == null || note.getId() == null) {
            return;
        }
        if (isNoteSearchable(note)) {
            upsertDocument("note", note.getId(), "碎碎念",
                    note.getContentText() == null ? "" : note.getContentText(),
                    SearchMetadataUtils.withUrl("/notes#" + note.getId()));
        } else {
            removeDocument("note", note.getId());
        }
    }

    public void syncSnippet(SnippetEntity snippet) {
        if (snippet == null || snippet.getId() == null) {
            return;
        }
        if (isSnippetSearchable(snippet)) {
            upsertDocument("snippet", snippet.getId(), snippet.getTitle(), snippet.getCode(),
                    SearchMetadataUtils.withUrl("/snippets/" + snippet.getSlug()));
        } else {
            removeDocument("snippet", snippet.getId());
        }
    }

    static boolean isArticleSearchable(ArticleEntity article) {
        return article.getDeletedAt() == null
                && "published".equals(article.getStatus())
                && "public".equals(article.getVisibility());
    }

    static boolean isNoteSearchable(NoteEntity note) {
        return note.getDeletedAt() == null
                && "published".equals(note.getStatus())
                && "public".equals(note.getVisibility());
    }

    static boolean isSnippetSearchable(SnippetEntity snippet) {
        return snippet.getDeletedAt() == null && "public".equals(snippet.getVisibility());
    }

    private void syncMeilisearchAll() {
        MeilisearchSearchEngine engine = meilisearchSearchEngine.getIfAvailable();
        if (engine == null) {
            return;
        }
        List<SearchDocumentEntity> docs = searchDocumentMapper.selectList(
                new LambdaQueryWrapper<SearchDocumentEntity>().eq(SearchDocumentEntity::getStatus, "active"));
        engine.replaceAll(docs);
    }

    private int indexArticles() {
        List<ArticleEntity> articles = articleMapper.selectList(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getStatus, "published")
                .eq(ArticleEntity::getVisibility, "public")
                .isNull(ArticleEntity::getDeletedAt));
        for (ArticleEntity a : articles) {
            upsertDocument("article", a.getId(), a.getTitle(),
                    (a.getSummary() == null ? "" : a.getSummary()) + " "
                            + (a.getContentText() == null ? "" : a.getContentText()),
                    SearchMetadataUtils.withUrl("/posts/" + a.getSlug()));
        }
        return articles.size();
    }

    private int indexSnippets() {
        List<SnippetEntity> list = snippetMapper.selectList(new LambdaQueryWrapper<SnippetEntity>()
                .eq(SnippetEntity::getVisibility, "public")
                .isNull(SnippetEntity::getDeletedAt));
        for (SnippetEntity s : list) {
            upsertDocument("snippet", s.getId(), s.getTitle(), s.getCode(),
                    SearchMetadataUtils.withUrl("/snippets/" + s.getSlug()));
        }
        return list.size();
    }

    private int indexNotes() {
        List<NoteEntity> list = noteMapper.selectList(new LambdaQueryWrapper<NoteEntity>()
                .eq(NoteEntity::getStatus, "published")
                .eq(NoteEntity::getVisibility, "public")
                .isNull(NoteEntity::getDeletedAt));
        for (NoteEntity n : list) {
            upsertDocument("note", n.getId(), "碎碎念", n.getContentText() == null ? "" : n.getContentText(),
                    SearchMetadataUtils.withUrl("/notes#" + n.getId()));
        }
        return list.size();
    }

    private int indexProjects() {
        List<ProjectEntity> list = projectMapper.selectList(new LambdaQueryWrapper<ProjectEntity>()
                .isNull(ProjectEntity::getDeletedAt));
        for (ProjectEntity p : list) {
            String title = p.getName() != null ? p.getName() : p.getOwner() + "/" + p.getRepo();
            upsertDocument("project", p.getId(), title, p.getDescription() == null ? "" : p.getDescription(),
                    SearchMetadataUtils.withUrl("/projects/" + p.getOwner() + "/" + p.getRepo()));
        }
        return list.size();
    }

    private void upsertDocument(String type, Long targetId, String title, String content, String metadata) {
        removeDocument(type, targetId);
        SearchDocumentEntity doc = new SearchDocumentEntity();
        doc.setTargetType(type);
        doc.setTargetId(targetId);
        doc.setTitle(title);
        doc.setContent(content);
        doc.setStatus("active");
        doc.setBoost(1.0f);
        doc.setMetadata(metadata);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());
        searchDocumentMapper.insert(doc);
        MeilisearchSearchEngine engine = meilisearchSearchEngine.getIfAvailable();
        if (engine != null) {
            engine.upsertDocument(doc);
        }
    }

    private void removeDocument(String type, Long targetId) {
        searchDocumentMapper.delete(new LambdaQueryWrapper<SearchDocumentEntity>()
                .eq(SearchDocumentEntity::getTargetType, type)
                .eq(SearchDocumentEntity::getTargetId, targetId));
        MeilisearchSearchEngine engine = meilisearchSearchEngine.getIfAvailable();
        if (engine != null) {
            engine.deleteDocument(type, targetId);
        }
    }
}
