package com.jiangou.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class BootstrapAdminRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminRunner.class);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${BOOTSTRAP_ADMIN_USERNAME:}")
    private String username;

    @Value("${BOOTSTRAP_ADMIN_EMAIL:}")
    private String email;

    @Value("${BOOTSTRAP_ADMIN_PASSWORD:}")
    private String password;

    @Value("${BOOTSTRAP_ADMIN_DISPLAY_NAME:}")
    private String displayName;

    public BootstrapAdminRunner(UserMapper userMapper, RoleService roleService,
                                PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!hasAnyBootstrapValue()) {
            return;
        }
        validateBootstrapConfig();
        if (hasExistingAdmin()) {
            log.info("Detected existing ADMIN user, skipping bootstrap admin creation.");
            return;
        }
        ensureUserNotTaken();

        String normalizedUsername = username.trim();
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        UserEntity admin = new UserEntity();
        admin.setUsername(normalizedUsername);
        admin.setDisplayName(resolveDisplayName());
        admin.setEmail(normalizedEmail);
        admin.setEmailVerified(true);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setStatus("active");
        admin.setTokenVersion(0);
        admin.setMetadata("{}");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(admin);
        if (admin.getId() == null) {
            throw new IllegalStateException("首个 ADMIN 创建失败：未获取到用户 ID");
        }

        roleService.ensureRole("ADMIN", "管理员");
        roleService.assignRole(admin.getId(), "ADMIN");
        log.warn("Bootstrap ADMIN user [{}] created. Make sure later backend starts do not include BOOTSTRAP_ADMIN_* secrets.", admin.getUsername());
    }

    private boolean hasAnyBootstrapValue() {
        return StringUtils.hasText(username)
                || StringUtils.hasText(email)
                || StringUtils.hasText(password)
                || StringUtils.hasText(displayName);
    }

    private void validateBootstrapConfig() {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new IllegalStateException("BOOTSTRAP_ADMIN_USERNAME / EMAIL / PASSWORD 必须同时配置");
        }
        if (!email.contains("@")) {
            throw new IllegalStateException("BOOTSTRAP_ADMIN_EMAIL 格式不正确");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD 至少 8 位且包含字母和数字");
        }
    }

    private boolean hasExistingAdmin() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_roles ur "
                        + "INNER JOIN roles r ON r.id = ur.role_id "
                        + "INNER JOIN users u ON u.id = ur.user_id "
                        + "WHERE r.code = 'ADMIN' AND r.deleted_at IS NULL AND u.deleted_at IS NULL",
                Integer.class);
        return count != null && count > 0;
    }

    private void ensureUserNotTaken() {
        UserEntity existing = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .and(wrapper -> wrapper
                        .eq(UserEntity::getUsername, username.trim())
                        .or()
                        .eq(UserEntity::getEmail, email.trim().toLowerCase(Locale.ROOT)))
                .isNull(UserEntity::getDeletedAt));
        if (existing != null) {
            throw new IllegalStateException("BOOTSTRAP_ADMIN 用户名或邮箱已存在，请改用新的凭据或手动处理该账号");
        }
    }

    private String resolveDisplayName() {
        return StringUtils.hasText(displayName) ? displayName.trim() : username.trim();
    }
}
