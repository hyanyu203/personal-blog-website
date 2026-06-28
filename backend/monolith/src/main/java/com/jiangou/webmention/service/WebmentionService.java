package com.jiangou.webmention.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.util.UrlSafetyUtils;
import com.jiangou.common.result.PageResult;
import com.jiangou.system.service.SystemSettingService;
import com.jiangou.webmention.entity.WebmentionEntity;
import com.jiangou.webmention.mapper.WebmentionMapper;
import com.jiangou.webmention.vo.WebmentionVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebmentionService {

    private final WebmentionMapper webmentionMapper;
    private final SystemSettingService systemSettingService;
    private final WebmentionVerificationWorker verificationWorker;

    @Value("${jiangou.site-url:http://localhost:3000}")
    private String siteUrl;

    public WebmentionService(WebmentionMapper webmentionMapper,
                             SystemSettingService systemSettingService,
                             WebmentionVerificationWorker verificationWorker) {
        this.webmentionMapper = webmentionMapper;
        this.systemSettingService = systemSettingService;
        this.verificationWorker = verificationWorker;
    }

    public boolean isEnabled() {
        String value = systemSettingService.getValue("webmentionEnabled");
        return "true".equalsIgnoreCase(value);
    }

    @Transactional
    public WebmentionVO receive(String source, String target) {
        if (!isEnabled()) {
            throw new ValidationException("Webmention 未启用");
        }
        if (!StringUtils.hasText(source) || !StringUtils.hasText(target)) {
            throw new ValidationException("source 与 target 必填");
        }
        UrlSafetyUtils.validateSameSiteTarget(target, siteUrl);
        UrlSafetyUtils.validateExternalHttpUrl(source.trim());

        WebmentionEntity entity = new WebmentionEntity();
        entity.setSourceUrl(source.trim());
        entity.setTargetUrl(target.trim());
        entity.setType("mention");
        entity.setStatus("pending");
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        webmentionMapper.insert(entity);

        verificationWorker.verify(entity.getId(), source.trim(), target.trim());
        return toVo(entity);
    }

    public PageResult<WebmentionVO> listAdmin(long page, long pageSize, String status) {
        LambdaQueryWrapper<WebmentionEntity> wrapper = new LambdaQueryWrapper<WebmentionEntity>()
                .orderByDesc(WebmentionEntity::getCreatedAt);
        if (StringUtils.hasText(status)) {
            wrapper.eq(WebmentionEntity::getStatus, status);
        }
        Page<WebmentionEntity> result = webmentionMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<WebmentionVO> items = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }

    private WebmentionVO toVo(WebmentionEntity e) {
        return WebmentionVO.builder()
                .id(e.getId())
                .sourceUrl(e.getSourceUrl())
                .targetUrl(e.getTargetUrl())
                .type(e.getType())
                .status(e.getStatus())
                .verifiedAt(e.getVerifiedAt())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
