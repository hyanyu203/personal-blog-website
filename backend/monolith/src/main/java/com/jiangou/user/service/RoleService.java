package com.jiangou.user.service;

import com.jiangou.security.AuthUserCacheInvalidator;
import com.jiangou.user.entity.RoleEntity;
import com.jiangou.user.mapper.PermissionMapper;
import com.jiangou.user.mapper.RoleMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final JdbcTemplate jdbcTemplate;
    private final AuthUserCacheInvalidator authUserCacheInvalidator;

    public RoleService(RoleMapper roleMapper, PermissionMapper permissionMapper,
                       JdbcTemplate jdbcTemplate, AuthUserCacheInvalidator authUserCacheInvalidator) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.authUserCacheInvalidator = authUserCacheInvalidator;
    }

    public List<String> getPermissionCodes(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return permissionMapper.findPermissionCodesByUserId(userId);
    }

    @Transactional
    public void setRoles(Long userId, List<String> roleCodes) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", userId);
        if (roleCodes == null) {
            authUserCacheInvalidator.evictAfterCommit(userId);
            return;
        }
        for (String code : roleCodes) {
            assignRole(userId, code, false);
        }
        authUserCacheInvalidator.evictAfterCommit(userId);
    }

    public List<String> getRoleCodes(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return roleMapper.findRoleCodesByUserId(userId);
    }

    @Transactional
    public RoleEntity ensureRole(String code, String name) {
        RoleEntity role = roleMapper.findByCode(code);
        if (role != null) {
            return role;
        }
        role = new RoleEntity();
        role.setCode(code);
        role.setName(name);
        role.setDescription("");
        role.setMetadata("{}");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.insert(role);
        return role;
    }

    public void assignRole(Long userId, String roleCode) {
        assignRole(userId, roleCode, true);
    }

    private void assignRole(Long userId, String roleCode, boolean evictAfterCommit) {
        RoleEntity role = ensureRole(roleCode, roleCode);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_roles WHERE user_id = ? AND role_id = ?",
                Integer.class, userId, role.getId());
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.update(
                "INSERT INTO user_roles (user_id, role_id, created_at) VALUES (?, ?, ?)",
                userId, role.getId(), LocalDateTime.now());
        if (evictAfterCommit) {
            authUserCacheInvalidator.evictAfterCommit(userId);
        }
    }

    public boolean hasRole(Long userId, String roleCode) {
        return getRoleCodes(userId).stream().anyMatch(code -> code.equalsIgnoreCase(roleCode));
    }

    public Map<Long, List<String>> getRoleCodesByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        String placeholders = userIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT ur.user_id, r.code FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id "
                + "WHERE ur.user_id IN (" + placeholders + ") AND r.deleted_at IS NULL";
        List<Object> params = new ArrayList<Object>(userIds);
        Map<Long, List<String>> result = new HashMap<Long, List<String>>();
        jdbcTemplate.query(sql, params.toArray(), rs -> {
            Long userId = rs.getLong("user_id");
            result.computeIfAbsent(userId, id -> new ArrayList<String>()).add(rs.getString("code"));
        });
        return result;
    }

    public Map<Long, List<String>> getPermissionCodesByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        String placeholders = userIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT ur.user_id, p.code FROM permissions p "
                + "INNER JOIN role_permissions rp ON p.id = rp.permission_id "
                + "INNER JOIN user_roles ur ON rp.role_id = ur.role_id "
                + "WHERE ur.user_id IN (" + placeholders + ")";
        List<Object> params = new ArrayList<Object>(userIds);
        Map<Long, List<String>> result = new HashMap<Long, List<String>>();
        jdbcTemplate.query(sql, params.toArray(), rs -> {
            Long userId = rs.getLong("user_id");
            String code = rs.getString("code");
            List<String> codes = result.computeIfAbsent(userId, id -> new ArrayList<String>());
            if (!codes.contains(code)) {
                codes.add(code);
            }
        });
        return result;
    }
}
