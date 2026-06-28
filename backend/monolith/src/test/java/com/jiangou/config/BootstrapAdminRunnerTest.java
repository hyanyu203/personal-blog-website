package com.jiangou.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapAdminRunnerTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BootstrapAdminRunner runner;

    @Test
    void skipsWhenBootstrapEnvMissing() throws Exception {
        runner.run(null);

        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void createsAdminWhenNoExistingAdmin() throws Exception {
        setBootstrapConfig("root", "root@example.com", "Passw0rd!", "Root Admin");
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class))).thenReturn(0);
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(passwordEncoder.encode("Passw0rd!")).thenReturn("encoded-password");
        doAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return 1;
        }).when(userMapper).insert(any(UserEntity.class));

        runner.run(null);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userMapper).insert(captor.capture());
        verify(roleService).ensureRole("ADMIN", "管理员");
        verify(roleService).assignRole(1L, "ADMIN");
        UserEntity created = captor.getValue();
        assertEquals("root", created.getUsername());
        assertEquals("Root Admin", created.getDisplayName());
        assertEquals("root@example.com", created.getEmail());
        assertEquals("encoded-password", created.getPasswordHash());
    }

    @Test
    void skipsWhenAdminAlreadyExists() throws Exception {
        setBootstrapConfig("root", "root@example.com", "Passw0rd!", "");
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class))).thenReturn(1);

        runner.run(null);

        verify(userMapper, never()).insert(any(UserEntity.class));
        verify(roleService, never()).assignRole(any(Long.class), any(String.class));
    }

    @Test
    void rejectsPartialBootstrapConfig() {
        ReflectionTestUtils.setField(runner, "username", "root");
        ReflectionTestUtils.setField(runner, "email", "");
        ReflectionTestUtils.setField(runner, "password", "Passw0rd!");

        assertThrows(IllegalStateException.class, () -> runner.run(null));
    }

    @Test
    void defaultsDisplayNameToUsername() throws Exception {
        setBootstrapConfig("yanyu", "yanyu@example.com", "Passw0rd!", " ");
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class))).thenReturn(0);
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(passwordEncoder.encode("Passw0rd!")).thenReturn("encoded-password");
        doAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(2L);
            return 1;
        }).when(userMapper).insert(any(UserEntity.class));

        runner.run(null);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userMapper).insert(captor.capture());
        assertEquals("yanyu", captor.getValue().getDisplayName());
    }

    @Test
    void rejectsExistingUsernameOrEmail() {
        setBootstrapConfig("yanyu", "yanyu@example.com", "Passw0rd!", "Yanyu");
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class))).thenReturn(0);
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(new UserEntity());

        assertThrows(IllegalStateException.class, () -> runner.run(null));

        verify(userMapper, never()).insert(any(UserEntity.class));
        verify(roleService, never()).assignRole(any(Long.class), any(String.class));
    }

    private void setBootstrapConfig(String username, String email, String password, String displayName) {
        ReflectionTestUtils.setField(runner, "username", username);
        ReflectionTestUtils.setField(runner, "email", email);
        ReflectionTestUtils.setField(runner, "password", password);
        ReflectionTestUtils.setField(runner, "displayName", displayName);
    }
}
