package com.jiangou.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.auth.vo.AuthVO;
import com.jiangou.common.exception.ConflictException;
import com.jiangou.common.exception.UnauthorizedException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.config.GitHubOAuthProperties;
import com.jiangou.security.JwtTokenProvider;
import com.jiangou.user.entity.UserEntity;
import com.jiangou.user.mapper.UserMapper;
import com.jiangou.user.service.RoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GitHubOAuthService {

    private static final String STATE_PREFIX = "oauth:github:state:";
    private static final String CODE_PREFIX = "oauth:github:code:";

    private final GitHubOAuthProperties oauthProperties;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RoleService roleService;

    @Value("${jiangou.admin-url:http://localhost:5173/admin}")
    private String adminUrl;

    public GitHubOAuthService(GitHubOAuthProperties oauthProperties, UserMapper userMapper,
                              JwtTokenProvider jwtTokenProvider, StringRedisTemplate redisTemplate,
                              RestTemplate restTemplate, ObjectMapper objectMapper,
                              RoleService roleService) {
        this.oauthProperties = oauthProperties;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.roleService = roleService;
    }

    public boolean isEnabled() {
        return oauthProperties.isEnabled();
    }

    public String buildAuthorizeUrl() {
        if (!isEnabled()) {
            throw new ValidationException("GitHub OAuth 未配置");
        }
        String state = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(STATE_PREFIX + state, "1", 10, TimeUnit.MINUTES);
        String redirect = encode(oauthProperties.getRedirectUri());
        return "https://github.com/login/oauth/authorize?client_id=" + oauthProperties.getClientId()
                + "&redirect_uri=" + redirect
                + "&scope=read:user user:email"
                + "&state=" + state;
    }

    public String buildCallbackRedirect(String code, String state) {
        AuthVO auth = handleCallback(code, state);
        String exchangeCode = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(CODE_PREFIX + exchangeCode, String.valueOf(auth.getUser().getId()),
                2, TimeUnit.MINUTES);
        return adminUrl + "/oauth/callback?code=" + encode(exchangeCode);
    }

    public AuthVO exchangeCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ValidationException("缺少 OAuth code");
        }
        String key = CODE_PREFIX + code;
        String payload = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(payload)) {
            throw new UnauthorizedException("OAuth code 无效或已过期");
        }
        redisTemplate.delete(key);
        Long userId;
        try {
            userId = Long.valueOf(payload.trim());
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("OAuth code 无效");
        }
        UserEntity user = userMapper.selectById(userId);
        if (user == null || user.getDeletedAt() != null) {
            throw new UnauthorizedException("用户不存在");
        }
        if (!roleService.hasRole(user.getId(), "ADMIN")) {
            throw new UnauthorizedException("该账号无后台访问权限");
        }
        if (!"active".equals(user.getStatus())) {
            throw new UnauthorizedException("账号已禁用");
        }
        return toAuthVO(user);
    }

    public AuthVO handleCallback(String code, String state) {
        if (!isEnabled()) {
            throw new ValidationException("GitHub OAuth 未配置");
        }
        if (!StringUtils.hasText(code) || !StringUtils.hasText(state)) {
            throw new ValidationException("缺少 OAuth 参数");
        }
        String stateKey = STATE_PREFIX + state;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(stateKey))) {
            throw new UnauthorizedException("无效的 OAuth state");
        }
        redisTemplate.delete(stateKey);

        JsonNode tokenJson = exchangeToken(code);
        String accessToken = textOrNull(tokenJson, "access_token");
        if (!StringUtils.hasText(accessToken)) {
            throw new UnauthorizedException("GitHub 授权失败");
        }

        JsonNode profile = fetchProfile(accessToken);
        String githubId = profile.path("id").asText();
        String login = profile.path("login").asText();
        String name = profile.path("name").asText(login);
        String avatar = textOrNull(profile, "avatar_url");
        String email = textOrNull(profile, "email");
        if (!StringUtils.hasText(email)) {
            email = fetchPrimaryEmail(accessToken);
        }

        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getProvider, "github")
                .eq(UserEntity::getProviderId, githubId)
                .isNull(UserEntity::getDeletedAt));
        if (user == null) {
            if (!oauthProperties.isUsernameAllowed(login)) {
                throw new UnauthorizedException("该 GitHub 账号未授权登录");
            }
            UserEntity usernameTaken = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getUsername, login)
                    .isNull(UserEntity::getDeletedAt));
            if (usernameTaken != null) {
                throw new ConflictException("GitHub 用户名与已有本地账号冲突");
            }
            user = new UserEntity();
            user.setUsername(login);
            user.setDisplayName(name);
            user.setEmail(email);
            user.setAvatarUrl(avatar);
            user.setProvider("github");
            user.setProviderId(githubId);
            user.setStatus("active");
            user.setMetadata("{}");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
            roleService.assignRole(user.getId(), "ADMIN");
        } else {
            user.setDisplayName(name);
            user.setEmail(email);
            user.setAvatarUrl(avatar);
            user.setProvider("github");
            user.setProviderId(githubId);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
        if (!roleService.hasRole(user.getId(), "ADMIN")) {
            throw new UnauthorizedException("该账号无后台访问权限");
        }
        if (!"active".equals(user.getStatus())) {
            throw new UnauthorizedException("账号已禁用");
        }
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);
        return toAuthVO(user);
    }

    private JsonNode exchangeToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
            body.add("client_id", oauthProperties.getClientId());
            body.add("client_secret", oauthProperties.getClientSecret());
            body.add("code", code);
            body.add("redirect_uri", oauthProperties.getRedirectUri());
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://github.com/login/oauth/access_token",
                    new HttpEntity<MultiValueMap<String, String>>(body, headers), String.class);
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new UnauthorizedException("GitHub token 交换失败");
        }
    }

    private JsonNode fetchProfile(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.github.com/user", org.springframework.http.HttpMethod.GET,
                    new HttpEntity<Void>(headers), String.class);
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new UnauthorizedException("获取 GitHub 用户信息失败");
        }
    }

    private String fetchPrimaryEmail(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.github.com/user/emails", org.springframework.http.HttpMethod.GET,
                    new HttpEntity<Void>(headers), String.class);
            JsonNode emails = objectMapper.readTree(response.getBody());
            if (emails.isArray()) {
                for (JsonNode email : emails) {
                    if (email.path("primary").asBoolean(false)) {
                        return textOrNull(email, "email");
                    }
                }
            }
        } catch (Exception ignored) {
            // optional
        }
        return null;
    }

    private AuthVO toAuthVO(UserEntity user) {
        int tokenVersion = user.getTokenVersion() == null ? 0 : user.getTokenVersion();
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUsername(), tokenVersion);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getUsername(), tokenVersion);
        return AuthVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .user(AuthVO.UserBriefVO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .displayName(user.getDisplayName())
                        .roles(roleService.getRoleCodes(user.getId()))
                        .build())
                .build();
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode child = node.get(field);
        if (child == null || child.isNull()) {
            return null;
        }
        String text = child.asText();
        return text.isEmpty() ? null : text;
    }
}
