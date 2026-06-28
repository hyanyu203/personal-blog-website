package com.jiangou.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.auth.dto.RegisterDTO;
import com.jiangou.auth.dto.ResetPasswordDTO;
import com.jiangou.auth.dto.SendRegisterCodeDTO;
import com.jiangou.auth.dto.SendResetCodeDTO;
import com.jiangou.auth.vo.AuthVO;
import com.jiangou.common.constant.ErrorCodes;
import com.jiangou.common.exception.BusinessException;
import com.jiangou.common.exception.ConflictException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.config.AuthProperties;
import com.jiangou.security.AuthUserCacheInvalidator;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.service.RoleService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class UserAuthService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,32}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthService authService;
    private final CaptchaService captchaService;
    private final EmailCodeService emailCodeService;
    private final AuthProperties authProperties;
    private final AuthUserCacheInvalidator authUserCacheInvalidator;

    public UserAuthService(UserMapper userMapper, PasswordEncoder passwordEncoder,
                           RoleService roleService, AuthService authService,
                           CaptchaService captchaService, EmailCodeService emailCodeService,
                           AuthProperties authProperties, AuthUserCacheInvalidator authUserCacheInvalidator) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authService = authService;
        this.captchaService = captchaService;
        this.emailCodeService = emailCodeService;
        this.authProperties = authProperties;
        this.authUserCacheInvalidator = authUserCacheInvalidator;
    }

    public void sendRegisterCode(SendRegisterCodeDTO dto, String clientIp) {
        ensureRegistrationEnabled();
        captchaService.verifyAndConsume(dto.getCaptchaId(), dto.getCaptchaCode());
        UserEntity existing = findByEmail(dto.getEmail());
        if (existing == null) {
            emailCodeService.sendCode(dto.getEmail(), EmailCodeService.Purpose.REGISTER, clientIp);
        } else {
            emailCodeService.recordSendAttempt(dto.getEmail(), EmailCodeService.Purpose.REGISTER, clientIp);
        }
    }

    @Transactional
    public AuthVO register(RegisterDTO dto) {
        ensureRegistrationEnabled();
        captchaService.verifyAndConsume(dto.getCaptchaId(), dto.getCaptchaCode());
        validateUsername(dto.getUsername());
        validatePassword(dto.getPassword());
        ensureEmailAvailable(dto.getEmail());
        ensureUsernameAvailable(dto.getUsername());
        emailCodeService.verifyAndConsume(dto.getEmail(), EmailCodeService.Purpose.REGISTER, dto.getEmailCode());

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername().trim());
        user.setDisplayName(dto.getUsername().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setEmailVerified(true);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setStatus("active");
        user.setTokenVersion(0);
        user.setMetadata("{}");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.insert(user);

        roleService.ensureRole("USER", "注册用户");
        roleService.assignRole(user.getId(), "USER");

        return authService.buildAuthForUser(user);
    }

    public void sendResetCode(SendResetCodeDTO dto, String clientIp) {
        captchaService.verifyAndConsume(dto.getCaptchaId(), dto.getCaptchaCode());
        UserEntity user = findByEmail(dto.getEmail());
        if (user != null) {
            emailCodeService.sendCode(dto.getEmail(), EmailCodeService.Purpose.RESET_PASSWORD, clientIp);
        } else {
            emailCodeService.recordSendAttempt(dto.getEmail(), EmailCodeService.Purpose.RESET_PASSWORD, clientIp);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        validatePassword(dto.getNewPassword());
        UserEntity user = findByEmail(dto.getEmail());
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCodes.INVALID_CODE, "验证码错误或已过期");
        }
        emailCodeService.verifyAndConsume(dto.getEmail(), EmailCodeService.Purpose.RESET_PASSWORD, dto.getEmailCode());

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setTokenVersion(user.getTokenVersion() == null ? 1 : user.getTokenVersion() + 1);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        authUserCacheInvalidator.evictAfterCommit(user.getId());
    }

    private void ensureRegistrationEnabled() {
        if (!authProperties.isRegistrationEnabled()) {
            throw new ValidationException("注册功能未开放");
        }
    }

    private void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username.trim()).matches()) {
            throw new ValidationException("用户名需为 3-32 位字母、数字或下划线");
        }
    }

    private void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(ErrorCodes.WEAK_PASSWORD, "密码至少 8 位且包含字母和数字");
        }
    }

    private void ensureEmailAvailable(String email) {
        UserEntity existing = findByEmail(email);
        if (existing != null && existing.getDeletedAt() == null) {
            throw new ConflictException("邮箱已被注册");
        }
    }

    private void ensureUsernameAvailable(String username) {
        UserEntity existing = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username.trim())
                .isNull(UserEntity::getDeletedAt));
        if (existing != null) {
            throw new ConflictException("用户名已被占用");
        }
    }

    private UserEntity findByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getEmail, email.trim().toLowerCase())
                .isNull(UserEntity::getDeletedAt));
    }
}
