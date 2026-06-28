package com.jiangou.auth.service;

import com.jiangou.auth.vo.AuthVO;
import com.jiangou.common.exception.UnauthorizedException;
import com.jiangou.config.JwtProperties;
import com.jiangou.security.JwtTokenBlacklistService;
import com.jiangou.security.JwtTokenProvider;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServicePermissionsTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private JwtTokenBlacklistService tokenBlacklistService;
    @Mock
    private RoleService roleService;
    @Mock
    private JwtProperties jwtProperties;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userMapper, passwordEncoder, jwtTokenProvider,
                tokenBlacklistService, roleService, jwtProperties);
    }

    @Test
    void me_includesRolesAndPermissions() {
        UserEntity user = new UserEntity();
        user.setId(7L);
        user.setUsername("editor");
        user.setDisplayName("Editor");
        user.setStatus("active");
        when(userMapper.selectById(7L)).thenReturn(user);
        when(roleService.getRoleCodes(7L)).thenReturn(Collections.singletonList("USER"));
        when(roleService.getPermissionCodes(7L)).thenReturn(Arrays.asList("comment:review", "article:create"));

        AuthVO.UserBriefVO brief = authService.me(7L);

        assertEquals("editor", brief.getUsername());
        assertTrue(brief.getRoles().contains("USER"));
        assertTrue(brief.getPermissions().contains("comment:review"));
        assertTrue(brief.getPermissions().contains("article:create"));
    }
}
