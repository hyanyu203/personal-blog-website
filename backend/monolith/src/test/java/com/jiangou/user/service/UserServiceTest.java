package com.jiangou.user.service;

import com.jiangou.common.exception.ForbiddenException;
import com.jiangou.security.AuthUserCacheInvalidator;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.user.dto.UserUpdateDTO;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleService roleService;
    @Mock
    private AuthUserCacheInvalidator authUserCacheInvalidator;
    @Mock
    private SecurityUserDetailsService userDetailsService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new User("editor", "pwd", Collections.emptyList()),
                        null,
                        Collections.emptyList()));
        lenient().when(userDetailsService.resolveUserId("editor")).thenReturn(2L);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void update_rejectsAdminRoleForNonAdminOperator() {
        UserEntity target = activeUser(3L);
        when(userMapper.selectOne(any())).thenReturn(target);
        when(roleService.hasRole(2L, "ADMIN")).thenReturn(false);
        when(roleService.hasRole(3L, "ADMIN")).thenReturn(false);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setRoles(Collections.singletonList("ADMIN"));

        assertThrows(ForbiddenException.class, () -> userService.update(3L, dto));
        verify(roleService, never()).setRoles(eq(3L), any());
    }

    @Test
    void update_rejectsStatusChangeOnAdminTargetForNonAdminOperator() {
        UserEntity target = activeUser(1L);
        when(userMapper.selectOne(any())).thenReturn(target);
        when(roleService.hasRole(2L, "ADMIN")).thenReturn(false);
        when(roleService.hasRole(1L, "ADMIN")).thenReturn(true);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setStatus("disabled");

        assertThrows(ForbiddenException.class, () -> userService.update(1L, dto));
    }

    @Test
    void update_rejectsRoleChangesOnAdminTargetForNonAdminOperator() {
        UserEntity target = activeUser(1L);
        when(userMapper.selectOne(any())).thenReturn(target);
        when(roleService.hasRole(2L, "ADMIN")).thenReturn(false);
        when(roleService.hasRole(1L, "ADMIN")).thenReturn(true);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setRoles(Collections.singletonList("USER"));

        assertThrows(ForbiddenException.class, () -> userService.update(1L, dto));
    }

    @Test
    void update_allowsUserRoleForNonAdminOperator() {
        UserEntity target = activeUser(3L);
        when(userMapper.selectOne(any())).thenReturn(target, target);
        when(roleService.hasRole(2L, "ADMIN")).thenReturn(false);
        when(roleService.hasRole(3L, "ADMIN")).thenReturn(false);
        when(roleService.getRoleCodes(3L)).thenReturn(Collections.singletonList("USER"));
        when(roleService.getPermissionCodes(3L)).thenReturn(Collections.emptyList());

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setRoles(Collections.singletonList("USER"));

        userService.update(3L, dto);

        verify(roleService).setRoles(3L, Collections.singletonList("USER"));
        verifyNoInteractions(authUserCacheInvalidator);
    }

    @Test
    void update_roleChangeDoesNotBumpTokenVersion() {
        UserEntity target = activeUser(3L);
        target.setTokenVersion(7);
        when(userMapper.selectOne(any())).thenReturn(target, target);
        when(roleService.hasRole(2L, "ADMIN")).thenReturn(false);
        when(roleService.hasRole(3L, "ADMIN")).thenReturn(false);
        when(roleService.getRoleCodes(3L)).thenReturn(Collections.singletonList("USER"));
        when(roleService.getPermissionCodes(3L)).thenReturn(Collections.emptyList());

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setRoles(Collections.singletonList("USER"));

        userService.update(3L, dto);

        verify(userMapper).updateById(target);
        verifyNoInteractions(authUserCacheInvalidator);
        org.junit.jupiter.api.Assertions.assertEquals(Integer.valueOf(7), target.getTokenVersion());
    }

    @Test
    void update_disabledStatusBumpsTokenVersionAndEvictsCache() {
        UserEntity target = activeUser(3L);
        when(userMapper.selectOne(any())).thenReturn(target, target);
        when(roleService.hasRole(3L, "ADMIN")).thenReturn(false);
        when(roleService.getRoleCodes(3L)).thenReturn(Collections.singletonList("USER"));
        when(roleService.getPermissionCodes(3L)).thenReturn(Collections.emptyList());

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setStatus("disabled");

        userService.update(3L, dto);

        verify(userMapper).updateById(target);
        verify(authUserCacheInvalidator).evictAfterCommit(3L);
        org.junit.jupiter.api.Assertions.assertEquals(Integer.valueOf(1), target.getTokenVersion());
    }

    private UserEntity activeUser(Long id) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setUsername("user" + id);
        entity.setStatus("active");
        entity.setTokenVersion(0);
        return entity;
    }
}
