package com.jiangou.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.auth.dto.LoginDTO;
import com.jiangou.auth.dto.RefreshDTO;
import io.jsonwebtoken.Claims;
import com.jiangou.auth.vo.AuthVO;
import com.jiangou.common.exception.UnauthorizedException;
import com.jiangou.config.JwtProperties;
import com.jiangou.security.JwtTokenBlacklistService;
import com.jiangou.security.JwtTokenProvider;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.service.RoleService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenBlacklistService tokenBlacklistService;
    private final RoleService roleService;
    private final JwtProperties jwtProperties;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, JwtTokenBlacklistService tokenBlacklistService,
                       RoleService roleService, JwtProperties jwtProperties) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
        this.roleService = roleService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthVO login(LoginDTO dto) {
        UserEntity user = findByUsername(dto.getUsername());
        if (user == null || user.getPasswordHash() == null
                || !passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("用户名或密码错误");
        }
        if (passwordEncoder.upgradeEncoding(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        ensureActiveUser(user);
        ensureCanAuthenticate(user);
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);
        return buildAuthForUser(user);
    }

    public AuthVO refresh(RefreshDTO dto) {
        String oldRefreshToken = dto.getRefreshToken();
        Claims claims = jwtTokenProvider.validateRefreshToken(oldRefreshToken);
        Long userId = Long.valueOf(claims.getSubject());
        UserEntity user = userMapper.selectById(userId);
        validateTokenVersion(claims, user);
        ensureActiveUser(user);
        ensureCanAuthenticate(user);
        if (!tokenBlacklistService.blacklistIfAbsent(oldRefreshToken, claims.getExpiration())) {
            throw new UnauthorizedException("refresh token 已失效");
        }
        return buildAuthForUser(user);
    }

    public AuthVO.UserBriefVO me(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        ensureActiveUser(user);
        return toUserBrief(user);
    }

    public AuthVO buildAuthForUser(UserEntity user) {
        int tokenVersion = user.getTokenVersion() == null ? 0 : user.getTokenVersion();
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUsername(), tokenVersion);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getUsername(), tokenVersion);
        long expiresInSec = jwtProperties.getAccessTokenExpirationMs() / 1000L;
        return AuthVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresInSec)
                .user(toUserBrief(user))
                .build();
    }

    public void ensureActiveAdmin(UserEntity user) {
        ensureActiveUser(user);
        if (!roleService.hasRole(user.getId(), "ADMIN")) {
            throw new UnauthorizedException("无后台访问权限");
        }
    }

    private UserEntity findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username)
                .isNull(UserEntity::getDeletedAt));
    }

    private void ensureCanAuthenticate(UserEntity user) {
        List<String> roles = roleService.getRoleCodes(user.getId());
        if (roles.stream().noneMatch(code -> "USER".equalsIgnoreCase(code) || "ADMIN".equalsIgnoreCase(code))) {
            throw new UnauthorizedException("无登录权限");
        }
    }

    private void validateTokenVersion(Claims claims, UserEntity user) {
        int claimVersion = 0;
        Object tv = claims.get("tv");
        if (tv instanceof Number) {
            claimVersion = ((Number) tv).intValue();
        }
        int userVersion = user == null || user.getTokenVersion() == null ? 0 : user.getTokenVersion();
        if (claimVersion != userVersion) {
            throw new UnauthorizedException("token 已失效，请重新登录");
        }
    }

    private void ensureActiveUser(UserEntity user) {
        if (user == null || user.getDeletedAt() != null) {
            throw new UnauthorizedException("用户不存在");
        }
        if (!"active".equals(user.getStatus())) {
            throw new UnauthorizedException("账号已禁用");
        }
    }

    private AuthVO.UserBriefVO toUserBrief(UserEntity user) {
        return AuthVO.UserBriefVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .roles(roleService.getRoleCodes(user.getId()))
                .permissions(roleService.getPermissionCodes(user.getId()))
                .build();
    }
}
