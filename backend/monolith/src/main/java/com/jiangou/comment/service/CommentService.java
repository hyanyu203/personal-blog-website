package com.jiangou.comment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import com.jiangou.comment.dto.CreateCommentDTO;
import com.jiangou.comment.entity.CommentEntity;
import com.jiangou.comment.mapper.CommentMapper;
import com.jiangou.comment.vo.CommentVO;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.result.PageResult;
import com.jiangou.common.service.ContentCounterService;
import com.jiangou.common.util.MarkdownUtils;
import com.jiangou.common.util.UrlSafetyUtils;
import com.jiangou.note.entity.NoteEntity;
import com.jiangou.note.mapper.NoteMapper;
import com.jiangou.system.service.SystemSettingService;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private static final Set<String> ALLOWED_TARGET_TYPES = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("article", "note", "guestbook")));

    private final CommentMapper commentMapper;
    private final CommentLikeService commentLikeService;
    private final ContentCounterService contentCounterService;
    private final ArticleMapper articleMapper;
    private final NoteMapper noteMapper;
    private final SystemSettingService systemSettingService;
    private final UserMapper userMapper;

    public CommentService(CommentMapper commentMapper, CommentLikeService commentLikeService,
                          ContentCounterService contentCounterService,
                          ArticleMapper articleMapper,
                          NoteMapper noteMapper,
                          SystemSettingService systemSettingService,
                          UserMapper userMapper) {
        this.commentMapper = commentMapper;
        this.commentLikeService = commentLikeService;
        this.contentCounterService = contentCounterService;
        this.articleMapper = articleMapper;
        this.noteMapper = noteMapper;
        this.systemSettingService = systemSettingService;
        this.userMapper = userMapper;
    }

    public List<CommentVO> listApproved(String targetType, Long targetId) {
        String normalizedTargetType = normalizeTargetType(targetType);
        if (targetId == null || targetId < 1) {
            throw new ValidationException("Invalid comment target id");
        }
        List<CommentEntity> all = commentMapper.selectList(new LambdaQueryWrapper<CommentEntity>()
                .eq(CommentEntity::getTargetType, normalizedTargetType)
                .eq(CommentEntity::getTargetId, targetId)
                .eq(CommentEntity::getStatus, "approved")
                .isNull(CommentEntity::getDeletedAt)
                .orderByAsc(CommentEntity::getPath));
        return buildTree(all, commentLikeService.getLikeCounts(all));
    }

    @Transactional
    public CommentVO create(CreateCommentDTO dto, Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null || user.getDeletedAt() != null) {
            throw new ValidationException("用户不存在");
        }
        if (!"active".equals(user.getStatus())) {
            throw new ValidationException("账号已禁用");
        }
        String targetType = normalizeTargetType(dto.getTargetType());
        if (dto.getTargetId() == null || dto.getTargetId() < 1) {
            throw new ValidationException("Invalid comment target id");
        }
        validateTargetExists(targetType, dto.getTargetId());
        int depth = 0;
        Long rootId = null;
        String path;
        if (dto.getParentId() != null) {
            CommentEntity parent = commentMapper.selectById(dto.getParentId());
            if (parent == null || parent.getDeletedAt() != null) {
                throw new ValidationException("父评论不存在");
            }
            if (!"approved".equals(parent.getStatus())) {
                throw new ValidationException("只能回复已通过的评论");
            }
            if (parent.getDepth() >= 1) {
                throw new ValidationException("最多两层评论");
            }
            if (!targetType.equals(parent.getTargetType()) || !dto.getTargetId().equals(parent.getTargetId())) {
                throw new ValidationException("Parent comment target mismatch");
            }
            depth = 1;
            rootId = parent.getRootId() != null ? parent.getRootId() : parent.getId();
        }

        CommentEntity entity = new CommentEntity();
        entity.setTargetType(targetType);
        entity.setTargetId(dto.getTargetId());
        entity.setParentId(dto.getParentId());
        entity.setRootId(rootId);
        entity.setDepth(depth);
        entity.setUserId(userId);
        entity.setNickname(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        entity.setWebsite(UrlSafetyUtils.normalizeOptionalHttpUrl(dto.getWebsite()));
        entity.setContentMd(dto.getContentMd());
        entity.setContentHtml(MarkdownUtils.toHtml(dto.getContentMd()));
        entity.setStatus("pending");
        entity.setLikeCount(0L);
        entity.setReplyCount(0);
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        commentMapper.insert(entity);

        path = dto.getParentId() == null
                ? String.valueOf(entity.getId())
                : parentPath(dto.getParentId()) + "." + entity.getId();
        entity.setPath(path);
        commentMapper.updateById(entity);

        return toVo(entity, commentLikeService.getLikeCounts(Collections.singletonList(entity)));
    }

    private String normalizeTargetType(String targetType) {
        String normalized = targetType == null ? null : targetType.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_TARGET_TYPES.contains(normalized)) {
            throw new ValidationException("Invalid comment target type");
        }
        return normalized;
    }

    private void validateTargetExists(String targetType, Long targetId) {
        if ("article".equals(targetType)) {
            ArticleEntity article = articleMapper.selectOne(new LambdaQueryWrapper<ArticleEntity>()
                    .eq(ArticleEntity::getId, targetId)
                    .eq(ArticleEntity::getStatus, "published")
                    .eq(ArticleEntity::getVisibility, "public")
                    .isNull(ArticleEntity::getDeletedAt));
            if (article == null) {
                throw new ValidationException("Comment target article does not exist");
            }
            return;
        }
        if ("note".equals(targetType)) {
            NoteEntity note = noteMapper.selectOne(new LambdaQueryWrapper<NoteEntity>()
                    .eq(NoteEntity::getId, targetId)
                    .eq(NoteEntity::getStatus, "published")
                    .eq(NoteEntity::getVisibility, "public")
                    .isNull(NoteEntity::getDeletedAt));
            if (note == null) {
                throw new ValidationException("Comment target note does not exist");
            }
            return;
        }
        String configuredTargetId = systemSettingService.getValue("guestbookTargetId");
        long guestbookTargetId = parseGuestbookTargetId(configuredTargetId);
        if (!Long.valueOf(guestbookTargetId).equals(targetId)) {
            throw new ValidationException("Comment target guestbook does not exist");
        }
    }

    private long parseGuestbookTargetId(String configuredTargetId) {
        if (configuredTargetId == null || configuredTargetId.trim().isEmpty()) {
            return 1L;
        }
        try {
            return Long.parseLong(configuredTargetId.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid guestbook target setting");
        }
    }

    public PageResult<CommentVO> listAdmin(long page, long pageSize, String status) {
        LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<CommentEntity>()
                .isNull(CommentEntity::getDeletedAt)
                .orderByDesc(CommentEntity::getCreatedAt);
        if (status != null) {
            wrapper.eq(CommentEntity::getStatus, status);
        }
        Page<CommentEntity> result = commentMapper.selectPage(new Page<>(page, pageSize), wrapper);
        Map<Long, Long> likeCounts = commentLikeService.getLikeCounts(result.getRecords());
        List<CommentVO> items = result.getRecords().stream()
                .map(entity -> toVo(entity, likeCounts))
                .collect(Collectors.toList());
        return PageResult.of(items, result.getTotal(), page, pageSize);
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
    public void spam(Long id) {
        updateStatus(id, "spam");
    }

    @Transactional
    public void delete(Long id) {
        CommentEntity entity = commentMapper.selectById(id);
        if (entity == null || entity.getDeletedAt() != null) {
            throw new NotFoundException("评论不存在");
        }
        boolean wasApproved = "approved".equals(entity.getStatus());
        entity.setDeletedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(entity);
        if (wasApproved) {
            contentCounterService.adjustApprovedCommentCount(entity, -1);
        }
        if (entity.getParentId() == null) {
            cascadeSoftDeleteApprovedReplies(entity.getId());
        }
    }

    private void cascadeSoftDeleteApprovedReplies(Long rootId) {
        List<CommentEntity> replies = commentMapper.selectList(new LambdaQueryWrapper<CommentEntity>()
                .eq(CommentEntity::getRootId, rootId)
                .eq(CommentEntity::getStatus, "approved")
                .isNull(CommentEntity::getDeletedAt));
        LocalDateTime now = LocalDateTime.now();
        for (CommentEntity reply : replies) {
            reply.setDeletedAt(now);
            reply.setUpdatedAt(now);
            commentMapper.updateById(reply);
            contentCounterService.adjustApprovedCommentCount(reply, -1);
        }
    }

    private void updateStatus(Long id, String status) {
        CommentEntity entity = commentMapper.selectById(id);
        if (entity == null || entity.getDeletedAt() != null) {
            throw new NotFoundException("评论不存在");
        }
        boolean wasApproved = "approved".equals(entity.getStatus());
        boolean willBeApproved = "approved".equals(status);
        if (wasApproved == willBeApproved && status.equals(entity.getStatus())) {
            return;
        }
        if (willBeApproved) {
            ensureParentApprovedForApproval(entity);
        }
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(entity);
        if (wasApproved != willBeApproved) {
            contentCounterService.adjustApprovedCommentCount(entity, willBeApproved ? 1 : -1);
        }
    }

    private void ensureParentApprovedForApproval(CommentEntity entity) {
        if (entity.getParentId() == null) {
            return;
        }
        CommentEntity parent = commentMapper.selectById(entity.getParentId());
        if (parent == null || parent.getDeletedAt() != null) {
            throw new ValidationException("父评论不存在，无法通过该回复");
        }
        if (!"approved".equals(parent.getStatus())) {
            throw new ValidationException("请先通过父评论，再批准此回复");
        }
    }

    private String parentPath(Long parentId) {
        CommentEntity parent = commentMapper.selectById(parentId);
        return parent == null ? String.valueOf(parentId) : parent.getPath();
    }

    private List<CommentVO> buildTree(List<CommentEntity> all, Map<Long, Long> likeCounts) {
        Map<Long, CommentVO> map = new LinkedHashMap<Long, CommentVO>();
        List<CommentVO> roots = new ArrayList<CommentVO>();
        for (CommentEntity entity : all) {
            map.put(entity.getId(), toVo(entity, likeCounts));
        }
        for (CommentEntity entity : all) {
            CommentVO vo = map.get(entity.getId());
            if (entity.getParentId() == null) {
                roots.add(vo);
            } else {
                CommentVO parent = map.get(entity.getParentId());
                if (parent != null) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<CommentVO>());
                    }
                    parent.getReplies().add(vo);
                }
            }
        }
        return roots;
    }

    private CommentVO toVo(CommentEntity entity, Map<Long, Long> likeCounts) {
        long likeCount = likeCounts != null && likeCounts.containsKey(entity.getId())
                ? likeCounts.get(entity.getId())
                : commentLikeService.getLikeCount(entity);
        return CommentVO.builder()
                .id(entity.getId())
                .parentId(entity.getParentId())
                .depth(entity.getDepth())
                .nickname(entity.getNickname())
                .website(entity.getWebsite())
                .contentHtml(entity.getContentHtml())
                .likeCount(likeCount)
                .replyCount(entity.getReplyCount())
                .createdAt(entity.getCreatedAt())
                .status(entity.getStatus())
                .build();
    }
}
