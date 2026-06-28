package com.jiangou.user.service;

import com.jiangou.security.AuthUserCacheInvalidator;
import com.jiangou.user.entity.RoleEntity;
import com.jiangou.user.mapper.PermissionMapper;
import com.jiangou.user.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;
    @Mock
    private PermissionMapper permissionMapper;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private AuthUserCacheInvalidator authUserCacheInvalidator;

    @InjectMocks
    private RoleService roleService;

    @Test
    void setRoles_evictsUserAfterBatchRoleUpdate() {
        RoleEntity role = roleEntity(9L, "USER");
        when(roleMapper.findByCode("USER")).thenReturn(role);
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), eq(3L), eq(9L))).thenReturn(0);

        roleService.setRoles(3L, Collections.singletonList("USER"));

        verify(jdbcTemplate).update("DELETE FROM user_roles WHERE user_id = ?", 3L);
        verify(authUserCacheInvalidator).evictAfterCommit(3L);
    }

    @Test
    void assignRole_skipsEvictWhenUserAlreadyHasRole() {
        RoleEntity role = roleEntity(9L, "USER");
        when(roleMapper.findByCode("USER")).thenReturn(role);
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), eq(3L), eq(9L))).thenReturn(1);

        roleService.assignRole(3L, "USER");

        verify(authUserCacheInvalidator, never()).evictAfterCommit(3L);
    }

    private RoleEntity roleEntity(Long id, String code) {
        RoleEntity role = new RoleEntity();
        role.setId(id);
        role.setCode(code);
        return role;
    }
}
