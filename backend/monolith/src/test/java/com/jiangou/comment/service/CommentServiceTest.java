package com.jiangou.comment.service;

import com.jiangou.comment.dto.CreateCommentDTO;
import com.jiangou.comment.entity.CommentEntity;
import com.jiangou.comment.mapper.CommentMapper;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.service.ContentCounterService;
import com.jiangou.system.service.SystemSettingService;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentLikeService commentLikeService;
    @Mock
    private ContentCounterService contentCounterService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private SystemSettingService systemSettingService;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentMapper, commentLikeService, contentCounterService,
                null, null, systemSettingService, userMapper);
    }

    @Test
    void create_rejectsReplyToNonApprovedParent() {
        UserEntity user = activeUser();
        when(userMapper.selectById(1L)).thenReturn(user);

        CommentEntity parent = new CommentEntity();
        parent.setId(10L);
        parent.setStatus("pending");
        parent.setDepth(0);
        parent.setTargetType("guestbook");
        parent.setTargetId(1L);
        parent.setPath("10");
        when(commentMapper.selectById(10L)).thenReturn(parent);

        CreateCommentDTO dto = new CreateCommentDTO();
        dto.setTargetType("guestbook");
        dto.setTargetId(1L);
        dto.setParentId(10L);
        dto.setContentMd("reply");

        assertThrows(ValidationException.class, () -> commentService.create(dto, 1L));
        verify(commentMapper, never()).insert(any(CommentEntity.class));
    }

    @Test
    void approve_rejectsWhenParentNotApproved() {
        CommentEntity reply = new CommentEntity();
        reply.setId(20L);
        reply.setParentId(10L);
        reply.setStatus("pending");
        reply.setTargetType("guestbook");
        reply.setTargetId(1L);

        CommentEntity parent = new CommentEntity();
        parent.setId(10L);
        parent.setStatus("pending");

        when(commentMapper.selectById(20L)).thenReturn(reply);
        when(commentMapper.selectById(10L)).thenReturn(parent);

        assertThrows(ValidationException.class, () -> commentService.approve(20L));
        verify(commentMapper, never()).updateById(any(CommentEntity.class));
    }

    private UserEntity activeUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("user");
        user.setDisplayName("User");
        user.setStatus("active");
        return user;
    }
}
