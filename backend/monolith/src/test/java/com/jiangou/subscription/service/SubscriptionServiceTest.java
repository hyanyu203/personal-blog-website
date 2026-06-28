package com.jiangou.subscription.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.subscription.entity.SubscriptionEntity;
import com.jiangou.subscription.mapper.SubscriptionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionMapper subscriptionMapper;
    @Mock
    private JavaMailSender mailSender;

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(subscriptionMapper, mailSender);
        ReflectionTestUtils.setField(subscriptionService, "siteUrl", "https://example.com");
        ReflectionTestUtils.setField(subscriptionService, "siteTitle", "Test");
    }

    @Test
    void confirm_rejectsUnsubscribedStatus() {
        SubscriptionEntity entity = pendingEntity("confirm-token");
        entity.setStatus("unsubscribed");
        when(subscriptionMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(entity);

        assertThrows(ValidationException.class, () -> subscriptionService.confirm("confirm-token"));
    }

    @Test
    void confirm_clearsTokenAfterSuccess() {
        SubscriptionEntity entity = pendingEntity("confirm-token");
        when(subscriptionMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(entity);

        subscriptionService.confirm("confirm-token");

        ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
        verify(subscriptionMapper).updateById(captor.capture());
        SubscriptionEntity updated = captor.getValue();
        assertEquals("confirmed", updated.getStatus());
        assertNull(updated.getConfirmToken());
    }

    @Test
    void confirm_isIdempotentForConfirmedStatus() {
        SubscriptionEntity entity = pendingEntity("confirm-token");
        entity.setStatus("confirmed");
        when(subscriptionMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(entity);

        subscriptionService.confirm("confirm-token");

        verify(subscriptionMapper, never()).updateById(any(SubscriptionEntity.class));
    }

    @Test
    void unsubscribe_clearsConfirmToken() {
        SubscriptionEntity entity = pendingEntity("confirm-token");
        entity.setStatus("confirmed");
        entity.setUnsubscribeToken("unsub-token");
        when(subscriptionMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(entity);

        subscriptionService.unsubscribe("unsub-token");

        ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
        verify(subscriptionMapper).updateById(captor.capture());
        SubscriptionEntity updated = captor.getValue();
        assertEquals("unsubscribed", updated.getStatus());
        assertNull(updated.getConfirmToken());
    }

    @Test
    void confirm_throwsWhenTokenMissing() {
        when(subscriptionMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(NotFoundException.class, () -> subscriptionService.confirm("missing"));
    }

    private SubscriptionEntity pendingEntity(String confirmToken) {
        SubscriptionEntity entity = new SubscriptionEntity();
        entity.setId(1L);
        entity.setEmail("user@example.com");
        entity.setStatus("pending");
        entity.setConfirmToken(confirmToken);
        entity.setUnsubscribeToken("unsub-token");
        return entity;
    }
}
