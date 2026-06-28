package com.jiangou.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.ForbiddenException;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.result.PageResult;
import com.jiangou.security.AuthUserCacheInvalidator;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.user.dto.UserUpdateDTO;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.vo.UserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Set<String> KNOWN_ROLES = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("USER", "ADMIN")));

    private final UserMapper userMapper;
    private final RoleService roleService;
    private final AuthUserCacheInvalidator authUserCacheInvalidator;
    private final SecurityUserDetailsService userDetailsService;

    public UserService(UserMapper userMapper, RoleService roleService,
                       AuthUserCacheInvalidator authUserCacheInvalidator,
                       SecurityUserDetailsService userDetailsService) {
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.authUserCacheInvalidator = authUserCacheInvalidator;
        this.userDetailsService = userDetailsService;
    }

    public PageResult<UserVO> list(long page, long pageSize, String status) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
                .isNull(UserEntity::getDeletedAt)
                .orderByDesc(UserEntity::getCreatedAt);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(UserEntity::getStatus, status);
        }
        Page<UserEntity> result = userMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<Long> userIds = result.getRecords().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
        java.util.Map<Long, List<String>> rolesByUser = roleService.getRoleCodesByUserIds(userIds);
        java.util.Map<Long, List<String>> permissionsByUser = roleService.getPermissionCodesByUserIds(userIds);
        List<UserVO> items = result.getRecords().stream()
                .map(entity -> toVo(entity, rolesByUser, permissionsByUser))
                .collect(Collectors.toList());
        return PageResult.of(items, result.getTotal(), page, pageSize);
    }

    public UserVO getById(Long id) {
        return toVo(findActive(id));
    }

    @Transactional
    public UserVO update(Long id, UserUpdateDTO dto) {
        UserEntity entity = findActive(id);
        validateAdminTargetMutation(id, dto);
        boolean evictAuthCache = false;
        boolean revokeSessions = false;
        if (dto.getDisplayName() != null) {
            entity.setDisplayName(dto.getDisplayName());
        }
        if (dto.getEmail() != null) {
            String email = dto.getEmail().trim().toLowerCase();
            if (!email.equals(entity.getEmail())) {
                UserEntity duplicate = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getEmail, email)
                        .ne(UserEntity::getId, id)
                        .isNull(UserEntity::getDeletedAt));
                if (duplicate != null) {
                    throw new ValidationException("邮箱已被占用");
                }
                entity.setEmail(email);
            }
        }
        if (dto.getStatus() != null) {
            String status = dto.getStatus().trim().toLowerCase();
            if (!"active".equals(status) && !"disabled".equals(status)) {
                throw new ValidationException("状态仅允许 active 或 disabled");
            }
            if (!status.equals(entity.getStatus())) {
                entity.setStatus(status);
                evictAuthCache = true;
                if ("disabled".equals(status)) {
                    revokeSessions = true;
                }
            }
        }
        if (dto.getRoles() != null) {
            List<String> normalizedRoles = normalizeRoles(dto.getRoles());
            validateRoleAssignment(id, normalizedRoles);
            roleService.setRoles(id, normalizedRoles);
        }
        if (revokeSessions) {
            bumpTokenVersion(entity);
        }
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
        if (evictAuthCache) {
            authUserCacheInvalidator.evictAfterCommit(id);
        }
        return toVo(findActive(id));
    }

    private List<String> normalizeRoles(List<String> roles) {
        return roles.stream()
                .map(role -> role == null ? "" : role.trim().toUpperCase(Locale.ROOT))
                .filter(role -> !role.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateAdminTargetMutation(Long targetUserId, UserUpdateDTO dto) {
        if (!roleService.hasRole(targetUserId, "ADMIN")) {
            return;
        }
        Long operatorId = currentUserId();
        boolean operatorIsAdmin = operatorId != null && roleService.hasRole(operatorId, "ADMIN");
        if (operatorIsAdmin) {
            return;
        }
        if (dto.getStatus() != null || dto.getEmail() != null || dto.getDisplayName() != null) {
            throw new ForbiddenException("无权修改管理员账号");
        }
    }

    private void validateRoleAssignment(Long targetUserId, List<String> requestedRoles) {
        for (String role : requestedRoles) {
            if (!KNOWN_ROLES.contains(role)) {
                throw new ValidationException("不支持的角色: " + role);
            }
        }

        Long operatorId = currentUserId();
        boolean operatorIsAdmin = operatorId != null && roleService.hasRole(operatorId, "ADMIN");
        if (operatorIsAdmin) {
            return;
        }

        for (String role : requestedRoles) {
            if ("ADMIN".equals(role)) {
                throw new ForbiddenException("无权分配 ADMIN 角色");
            }
        }
        for (String role : requestedRoles) {
            if (!"USER".equals(role)) {
                throw new ForbiddenException("仅可分配 USER 角色");
            }
        }
        if (roleService.hasRole(targetUserId, "ADMIN")) {
            throw new ForbiddenException("无权修改管理员账号的角色");
        }
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return null;
        }
        return userDetailsService.resolveUserId(((User) auth.getPrincipal()).getUsername());
    }

    private void bumpTokenVersion(UserEntity entity) {
        entity.setTokenVersion(entity.getTokenVersion() == null ? 1 : entity.getTokenVersion() + 1);
    }

    private UserEntity findActive(Long id) {
        UserEntity entity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getId, id).isNull(UserEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("用户不存在");
        }
        return entity;
    }

    private UserVO toVo(UserEntity entity) {
        return toVo(entity,
                java.util.Collections.singletonMap(entity.getId(), roleService.getRoleCodes(entity.getId())),
                java.util.Collections.singletonMap(entity.getId(), roleService.getPermissionCodes(entity.getId())));
    }

    private UserVO toVo(UserEntity entity, java.util.Map<Long, List<String>> rolesByUser,
                        java.util.Map<Long, List<String>> permissionsByUser) {
        List<String> roles = rolesByUser.getOrDefault(entity.getId(), Collections.emptyList());
        List<String> permissions = permissionsByUser.getOrDefault(entity.getId(), Collections.emptyList());
        return UserVO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .displayName(entity.getDisplayName())
                .email(entity.getEmail())
                .avatarUrl(entity.getAvatarUrl())
                .status(entity.getStatus())
                .provider(entity.getProvider())
                .roles(roles)
                .permissions(permissions)
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
