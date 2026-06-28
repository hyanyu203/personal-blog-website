package com.jiangou.system.service;

import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.comment.mapper.CommentMapper;
import com.jiangou.note.mapper.NoteMapper;
import com.jiangou.snippet.mapper.SnippetMapper;
import com.jiangou.subscription.mapper.SubscriptionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private SnippetMapper snippetMapper;
    @Mock
    private NoteMapper noteMapper;
    @Mock
    private SubscriptionMapper subscriptionMapper;
    @Mock
    private SystemSettingService systemSettingService;

    @InjectMocks
    private StatsService statsService;

    @Test
    void adminStats_aggregatesCounts() {
        when(articleMapper.selectCount(any())).thenReturn(5L, 2L);
        when(commentMapper.selectCount(any())).thenReturn(3L);
        when(subscriptionMapper.selectCount(any())).thenReturn(10L);
        when(snippetMapper.selectCount(any())).thenReturn(4L);
        when(noteMapper.selectCount(any())).thenReturn(6L);
        Map<String, Object> settings = new HashMap<String, Object>();
        settings.put("siteLaunchDate", "2026-01-01");
        when(systemSettingService.getPublicSettings()).thenReturn(settings);

        Map<String, Object> stats = statsService.adminStats();

        assertEquals(5L, stats.get("articleCount"));
        assertEquals(2L, stats.get("draftCount"));
        assertEquals(3L, stats.get("pendingComments"));
        assertEquals(10L, stats.get("subscriberCount"));
    }
}
