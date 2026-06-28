package com.jiangou.subscription.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.result.PageResult;
import com.jiangou.subscription.dto.SubscribeDTO;
import com.jiangou.subscription.entity.SubscriptionEntity;
import com.jiangou.subscription.mapper.SubscriptionMapper;
import com.jiangou.subscription.vo.SubscriptionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionMapper subscriptionMapper;
    private final JavaMailSender mailSender;

    @Value("${jiangou.site-url:http://localhost:3000}")
    private String siteUrl;

    @Value("${jiangou.site-title:渐构}")
    private String siteTitle;

    public SubscriptionService(SubscriptionMapper subscriptionMapper, JavaMailSender mailSender) {
        this.subscriptionMapper = subscriptionMapper;
        this.mailSender = mailSender;
    }

    @Transactional
    public void subscribe(SubscribeDTO dto) {
        SubscriptionEntity confirmed = subscriptionMapper.selectOne(new LambdaQueryWrapper<SubscriptionEntity>()
                .eq(SubscriptionEntity::getEmail, dto.getEmail())
                .eq(SubscriptionEntity::getStatus, "confirmed"));
        if (confirmed != null) {
            return;
        }

        // Store SHA-256 hash of the confirm token; send the raw token in the email.
        // If DB is dumped, the hash cannot be replayed to confirm a subscription.
        String confirmToken = UUID.randomUUID().toString().replace("-", "");
        String confirmTokenHash = sha256Hex(confirmToken);
        String unsubscribeToken = UUID.randomUUID().toString().replace("-", "");

        SubscriptionEntity entity = subscriptionMapper.selectOne(new LambdaQueryWrapper<SubscriptionEntity>()
                .eq(SubscriptionEntity::getEmail, dto.getEmail()));
        if (entity == null) {
            entity = new SubscriptionEntity();
            entity.setEmail(dto.getEmail());
            entity.setMetadata("{}");
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setStatus("pending");
        entity.setConfirmToken(confirmTokenHash);
        entity.setUnsubscribeToken(unsubscribeToken);
        entity.setUpdatedAt(LocalDateTime.now());
        if (entity.getId() == null) {
            subscriptionMapper.insert(entity);
        } else {
            subscriptionMapper.updateById(entity);
        }

        sendConfirmEmail(dto.getEmail(), confirmToken);
    }

    @Transactional
    public void confirm(String token) {
        // confirm_token column stores SHA-256(raw token); hash the incoming value before lookup.
        SubscriptionEntity entity = subscriptionMapper.selectOne(new LambdaQueryWrapper<SubscriptionEntity>()
                .eq(SubscriptionEntity::getConfirmToken, sha256Hex(token)));
        if (entity == null) {
            throw new NotFoundException("无效的确认链接");
        }
        if ("confirmed".equals(entity.getStatus())) {
            return;
        }
        if (!"pending".equals(entity.getStatus())) {
            throw new ValidationException("该订阅链接已失效，请重新订阅");
        }
        entity.setStatus("confirmed");
        entity.setConfirmToken(null);
        entity.setUpdatedAt(LocalDateTime.now());
        subscriptionMapper.updateById(entity);
    }

    @Transactional
    public void unsubscribe(String token) {
        SubscriptionEntity entity = subscriptionMapper.selectOne(new LambdaQueryWrapper<SubscriptionEntity>()
                .eq(SubscriptionEntity::getUnsubscribeToken, token));
        if (entity == null) {
            throw new NotFoundException("无效的退订链接");
        }
        if ("unsubscribed".equals(entity.getStatus())) {
            return;
        }
        entity.setStatus("unsubscribed");
        entity.setConfirmToken(null);
        entity.setUpdatedAt(LocalDateTime.now());
        subscriptionMapper.updateById(entity);
    }

    public PageResult<SubscriptionVO> listAdmin(long page, long pageSize, String status) {
        LambdaQueryWrapper<SubscriptionEntity> wrapper = new LambdaQueryWrapper<SubscriptionEntity>()
                .orderByDesc(SubscriptionEntity::getCreatedAt);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SubscriptionEntity::getStatus, status);
        }
        Page<SubscriptionEntity> result = subscriptionMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<SubscriptionVO> items = result.getRecords().stream()
                .map(e -> SubscriptionVO.builder()
                        .id(e.getId())
                        .email(e.getEmail())
                        .status(e.getStatus())
                        .createdAt(e.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }

    public int sendNewsletter(String subject, String body) {
        List<SubscriptionEntity> subs = subscriptionMapper.selectList(
                new LambdaQueryWrapper<SubscriptionEntity>()
                        .eq(SubscriptionEntity::getStatus, "confirmed"));
        int success = 0;
        for (SubscriptionEntity sub : subs) {
            if (sendNewsletterEmail(sub.getEmail(), subject, body, sub.getUnsubscribeToken())) {
                success++;
            }
        }
        return success;
    }

    public void notifyNewArticle(String title, String slug, String summary) {
        List<SubscriptionEntity> subs = subscriptionMapper.selectList(
                new LambdaQueryWrapper<SubscriptionEntity>()
                        .eq(SubscriptionEntity::getStatus, "confirmed"));
        if (subs.isEmpty()) {
            return;
        }
        String url = siteUrl + "/posts/" + slug;
        String body = siteTitle + " 发布了新文章：《" + title + "》\n\n"
                + (summary != null ? summary + "\n\n" : "")
                + "阅读全文：" + url + "\n\n"
                + "退订请访问邮件中的退订链接。";
        int success = 0;
        int failed = 0;
        for (SubscriptionEntity sub : subs) {
            if (sendNewsletterEmail(sub.getEmail(), title, body, sub.getUnsubscribeToken())) {
                success++;
            } else {
                failed++;
            }
        }
        if (failed > 0) {
            log.warn("Newsletter 部分发送失败 slug={}: 成功 {}, 失败 {}", slug, success, failed);
        }
    }

    private boolean sendNewsletterEmail(String email, String subject, String body, String unsubscribeToken) {
        String unsubscribeLink = siteUrl + "/subscribe/unsubscribe?token=" + unsubscribeToken;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject(siteTitle + " · " + subject);
            msg.setText(body + "\n\n退订：" + unsubscribeLink);
            mailSender.send(msg);
            return true;
        } catch (Exception e) {
            log.warn("Newsletter 发送失败 {}: {}", email, e.getMessage());
            return false;
        }
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private void sendConfirmEmail(String email, String token) {
        String link = siteUrl + "/subscribe/confirm?token=" + token;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("确认订阅渐构");
            msg.setText("请点击链接确认订阅：\n" + link);
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("邮件发送失败: {}", e.getMessage());
            throw new ValidationException("邮件发送失败，请稍后重试");
        }
    }
}
