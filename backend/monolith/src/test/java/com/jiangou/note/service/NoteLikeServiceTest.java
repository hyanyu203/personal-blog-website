package com.jiangou.note.service;

import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.service.LikeCounterService;
import com.jiangou.note.entity.NoteEntity;
import com.jiangou.note.mapper.NoteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteLikeServiceTest {

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private LikeCounterService likeCounterService;

    @InjectMocks
    private NoteLikeService noteLikeService;

    @Test
    void like_usesStoredCountWhenNoteExists() {
        NoteEntity entity = new NoteEntity();
        entity.setId(6L);
        entity.setLikeCount(2L);
        when(noteMapper.selectOne(any())).thenReturn(entity);
        when(likeCounterService.incrementByUser("note", 6L, 9L, 2L)).thenReturn(3L);

        long result = noteLikeService.like(6L, 9L);

        verify(noteMapper).selectOne(any());
        verify(likeCounterService).incrementByUser("note", 6L, 9L, 2L);
        assertEquals(3L, result);
    }

    @Test
    void like_throwsWhenNoteDoesNotExist() {
        when(noteMapper.selectOne(any())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> noteLikeService.like(6L, 9L));
    }
}
