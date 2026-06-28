package com.jiangou.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.service.RoleService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleService roleService;

    public SecurityUserDetailsService(UserMapper userMapper, RoleService roleService) {
        this.userMapper = userMapper;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username)
                .isNull(UserEntity::getDeletedAt));
        if (entity == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return toUserDetails(entity);
    }

    public UserDetails loadUserById(Long userId) {
        UserEntity entity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getId, userId)
                .isNull(UserEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("用户不存在");
        }
        return toUserDetails(entity);
    }

    public Long resolveUserId(String username) {
        UserEntity entity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username)
                .isNull(UserEntity::getDeletedAt));
        return entity != null ? entity.getId() : null;
    }

    private UserDetails toUserDetails(UserEntity entity) {
        List<String> roles = roleService.getRoleCodes(entity.getId());
        List<String> permissions = roleService.getPermissionCodes(entity.getId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(roles.stream()
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code.toUpperCase()))
                .collect(Collectors.toList()));
        permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
        return new User(entity.getUsername(), entity.getPasswordHash() == null ? "" : entity.getPasswordHash(),
                "active".equals(entity.getStatus()),
                true, true, true,
                authorities);
    }
}
