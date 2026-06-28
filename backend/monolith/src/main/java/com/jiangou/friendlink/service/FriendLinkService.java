package com.jiangou.friendlink.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.util.UrlSafetyUtils;
import com.jiangou.common.result.PageResult;
import com.jiangou.friendlink.dto.FriendLinkApplyDTO;
import com.jiangou.friendlink.dto.FriendLinkDTO;
import com.jiangou.friendlink.entity.FriendLinkEntity;
import com.jiangou.friendlink.mapper.FriendLinkMapper;
import com.jiangou.friendlink.vo.FriendLinkVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendLinkService {

    private final FriendLinkMapper friendLinkMapper;

    public FriendLinkService(FriendLinkMapper friendLinkMapper) {
        this.friendLinkMapper = friendLinkMapper;
    }

    public List<FriendLinkVO> listApproved() {
        return friendLinkMapper.selectList(new LambdaQueryWrapper<FriendLinkEntity>()
                        .eq(FriendLinkEntity::getStatus, "approved")
                        .isNull(FriendLinkEntity::getDeletedAt)
                        .orderByAsc(FriendLinkEntity::getSortOrder))
                .stream().map(this::toVo).collect(Collectors.toList());
    }

    public PageResult<FriendLinkVO> listAdmin(long page, long pageSize, String status) {
        LambdaQueryWrapper<FriendLinkEntity> w = new LambdaQueryWrapper<FriendLinkEntity>()
                .isNull(FriendLinkEntity::getDeletedAt)
                .orderByDesc(FriendLinkEntity::getCreatedAt);
        if (status != null) {
            w.eq(FriendLinkEntity::getStatus, status);
        }
        Page<FriendLinkEntity> result = friendLinkMapper.selectPage(new Page<>(page, pageSize), w);
        List<FriendLinkVO> items = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }

    @Transactional
    public FriendLinkVO create(FriendLinkDTO dto) {
        FriendLinkEntity entity = new FriendLinkEntity();
        entity.setName(dto.getName());
        entity.setUrl(requireHttpUrl(dto.getUrl()));
        entity.setAvatarUrl(UrlSafetyUtils.normalizeOptionalHttpUrl(dto.getAvatarUrl()));
        entity.setDescription(dto.getDescription());
        entity.setOwnerEmail(dto.getOwnerEmail());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "approved");
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        friendLinkMapper.insert(entity);
        return toVo(entity);
    }

    @Transactional
    public FriendLinkVO update(Long id, FriendLinkDTO dto) {
        FriendLinkEntity entity = findActive(id);
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getUrl() != null) {
            entity.setUrl(UrlSafetyUtils.normalizeOptionalHttpUrl(dto.getUrl()));
        }
        if (dto.getAvatarUrl() != null) {
            entity.setAvatarUrl(UrlSafetyUtils.normalizeOptionalHttpUrl(dto.getAvatarUrl()));
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getOwnerEmail() != null) {
            entity.setOwnerEmail(dto.getOwnerEmail());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        friendLinkMapper.updateById(entity);
        return toVo(entity);
    }

    @Transactional
    public FriendLinkVO apply(FriendLinkApplyDTO dto) {
        FriendLinkEntity entity = new FriendLinkEntity();
        entity.setName(dto.getName());
        entity.setUrl(requireHttpUrl(dto.getUrl()));
        entity.setAvatarUrl(UrlSafetyUtils.normalizeOptionalHttpUrl(dto.getAvatarUrl()));
        entity.setDescription(dto.getDescription());
        entity.setOwnerEmail(dto.getOwnerEmail());
        entity.setStatus("pending");
        entity.setSortOrder(0);
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        friendLinkMapper.insert(entity);
        return toVo(entity);
    }

    @Transactional
    public void approve(Long id) {
        updateStatus(id, "approved");
    }

    @Transactional
    public void reject(Long id) {
        updateStatus(id, "rejected");
    }

    @Transactional
    public void delete(Long id) {
        FriendLinkEntity entity = findActive(id);
        entity.setDeletedAt(LocalDateTime.now());
        friendLinkMapper.updateById(entity);
    }

    private void updateStatus(Long id, String status) {
        FriendLinkEntity entity = findActive(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        friendLinkMapper.updateById(entity);
    }

    private FriendLinkEntity findActive(Long id) {
        FriendLinkEntity entity = friendLinkMapper.selectOne(new LambdaQueryWrapper<FriendLinkEntity>()
                .eq(FriendLinkEntity::getId, id).isNull(FriendLinkEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("友链不存在");
        }
        return entity;
    }

    private String requireHttpUrl(String url) {
        String normalized = UrlSafetyUtils.normalizeOptionalHttpUrl(url);
        if (normalized == null) {
            throw new ValidationException("URL 不能为空");
        }
        return normalized;
    }

    private FriendLinkVO toVo(FriendLinkEntity e) {
        return FriendLinkVO.builder()
                .id(e.getId()).name(e.getName()).url(e.getUrl())
                .avatarUrl(e.getAvatarUrl()).description(e.getDescription())
                .status(e.getStatus()).build();
    }
}
