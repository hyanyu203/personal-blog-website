package com.jiangou.auth.controller;

import com.jiangou.auth.dto.LoginDTO;
import com.jiangou.auth.dto.LogoutDTO;
import com.jiangou.auth.dto.OAuthExchangeDTO;
import com.jiangou.auth.dto.RefreshDTO;
import com.jiangou.auth.dto.RegisterDTO;
import com.jiangou.auth.dto.ResetPasswordDTO;
import com.jiangou.auth.dto.SendRegisterCodeDTO;
import com.jiangou.auth.dto.SendResetCodeDTO;
import com.jiangou.auth.service.AuthService;
import com.jiangou.auth.service.CaptchaService;
import com.jiangou.auth.service.GitHubOAuthService;
import com.jiangou.auth.service.UserAuthService;
import com.jiangou.auth.vo.AuthVO;
import com.jiangou.auth.vo.CaptchaVO;
import com.jiangou.common.exception.UnauthorizedException;
import com.jiangou.common.result.ApiResult;
import com.jiangou.security.AuthCookieService;
import com.jiangou.security.ClientIpResolver;
import com.jiangou.security.CsrfCookieService;
import com.jiangou.security.AuthTokenResolver;
import com.jiangou.security.JwtTokenBlacklistService;
import com.jiangou.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserAuthService userAuthService;
    private final CaptchaService captchaService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GitHubOAuthService gitHubOAuthService;
    private final JwtTokenBlacklistService tokenBlacklistService;
    private final AuthCookieService authCookieService;
    private final CsrfCookieService csrfCookieService;
    private final AuthTokenResolver authTokenResolver;
    private final ClientIpResolver clientIpResolver;

    public AuthController(AuthService authService, UserAuthService userAuthService,
                          CaptchaService captchaService, JwtTokenProvider jwtTokenProvider,
                          GitHubOAuthService gitHubOAuthService,
                          JwtTokenBlacklistService tokenBlacklistService,
                          AuthCookieService authCookieService,
                          CsrfCookieService csrfCookieService,
                          AuthTokenResolver authTokenResolver,
                          ClientIpResolver clientIpResolver) {
        this.authService = authService;
        this.userAuthService = userAuthService;
        this.captchaService = captchaService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.gitHubOAuthService = gitHubOAuthService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.authCookieService = authCookieService;
        this.csrfCookieService = csrfCookieService;
        this.authTokenResolver = authTokenResolver;
        this.clientIpResolver = clientIpResolver;
    }

    private void writeSessionCookies(HttpServletResponse response, AuthVO auth) {
        authCookieService.writeTokens(response, auth.getAccessToken(), auth.getRefreshToken(), auth.getExpiresIn());
        csrfCookieService.issueFreshToken(response);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public ApiResult<AuthVO> login(@Valid @RequestBody LoginDTO dto, HttpServletResponse response) {
        captchaService.verifyAndConsume(dto.getCaptchaId(), dto.getCaptchaCode());
        AuthVO auth = authService.login(dto);
        writeSessionCookies(response, auth);
        return ApiResult.ok(auth.withoutTokens());
    }

    @Operation(summary = "图形验证码")
    @GetMapping("/captcha")
    public ApiResult<CaptchaVO> captcha() {
        return ApiResult.ok(captchaService.generate());
    }

    @Operation(summary = "发送注册邮箱验证码")
    @PostMapping("/register/send-code")
    public ApiResult<Void> sendRegisterCode(@Valid @RequestBody SendRegisterCodeDTO dto,
                                            HttpServletRequest request) {
        userAuthService.sendRegisterCode(dto, clientIp(request));
        return ApiResult.ok();
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResult<AuthVO> register(@Valid @RequestBody RegisterDTO dto, HttpServletResponse response) {
        AuthVO auth = userAuthService.register(dto);
        writeSessionCookies(response, auth);
        return ApiResult.ok(auth.withoutTokens());
    }

    @Operation(summary = "发送重置密码邮箱验证码")
    @PostMapping("/forgot-password/send-code")
    public ApiResult<Void> sendResetCode(@Valid @RequestBody SendResetCodeDTO dto,
                                         HttpServletRequest request) {
        userAuthService.sendResetCode(dto, clientIp(request));
        return ApiResult.ok();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    public ApiResult<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        userAuthService.resetPassword(dto);
        return ApiResult.ok();
    }

    @Operation(summary = "刷新 access token")
    @PostMapping("/refresh")
    public ApiResult<AuthVO> refresh(@RequestBody(required = false) RefreshDTO dto,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        String refreshToken = dto != null ? dto.getRefreshToken() : null;
        if (!StringUtils.hasText(refreshToken)) {
            refreshToken = authCookieService.readRefreshToken(request);
        }
        if (!StringUtils.hasText(refreshToken)) {
            throw new UnauthorizedException("缺少 refresh token");
        }
        RefreshDTO refreshDTO = new RefreshDTO();
        refreshDTO.setRefreshToken(refreshToken);
        AuthVO auth = authService.refresh(refreshDTO);
        writeSessionCookies(response, auth);
        return ApiResult.ok(auth.withoutTokens());
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public ApiResult<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization,
                                  @RequestBody(required = false) LogoutDTO dto,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        blacklistIfPresent(authTokenResolver.resolveAccessToken(request, authorization), true);
        String refreshToken = dto != null ? dto.getRefreshToken() : null;
        if (!StringUtils.hasText(refreshToken)) {
            refreshToken = authCookieService.readRefreshToken(request);
        }
        blacklistIfPresent(refreshToken, false);
        authCookieService.clearTokens(response);
        return ApiResult.ok();
    }

    @Operation(summary = "GitHub OAuth 一次性 code 换取 token")
    @PostMapping("/oauth/exchange")
    public ApiResult<AuthVO> oauthExchange(@Valid @RequestBody OAuthExchangeDTO dto,
                                           HttpServletResponse response) {
        AuthVO auth = gitHubOAuthService.exchangeCode(dto.getCode());
        writeSessionCookies(response, auth);
        return ApiResult.ok(auth.withoutTokens());
    }

    @Operation(summary = "GitHub OAuth 跳转")
    @GetMapping("/github")
    public void githubRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(gitHubOAuthService.buildAuthorizeUrl());
    }

    @Operation(summary = "GitHub OAuth 回调")
    @GetMapping("/github/callback")
    public void githubCallback(@RequestParam String code, @RequestParam String state,
                               HttpServletResponse response) throws IOException {
        response.sendRedirect(gitHubOAuthService.buildCallbackRedirect(code, state));
    }

    @Operation(summary = "OAuth 配置状态")
    @GetMapping("/github/enabled")
    public ApiResult<Map<String, Boolean>> githubEnabled() {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        result.put("enabled", gitHubOAuthService.isEnabled());
        return ApiResult.ok(result);
    }

    @Operation(summary = "当前用户")
    @GetMapping("/me")
    public ApiResult<AuthVO.UserBriefVO> me(HttpServletRequest request,
                                            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String accessToken = authTokenResolver.resolveAccessToken(request, authorization);
        if (!StringUtils.hasText(accessToken)) {
            return ApiResult.fail(40101, "未登录");
        }
        Long userId = Long.valueOf(jwtTokenProvider.validateAccessToken(accessToken).getSubject());
        return ApiResult.ok(authService.me(userId));
    }

    private void blacklistIfPresent(String token, boolean accessType) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        try {
            if (accessType) {
                tokenBlacklistService.blacklist(token,
                        jwtTokenProvider.validateAccessToken(token).getExpiration());
            } else {
                tokenBlacklistService.blacklist(token,
                        jwtTokenProvider.validateRefreshToken(token).getExpiration());
            }
        } catch (Exception ignored) {
            // ignore invalid token on logout
        }
    }

    private String clientIp(HttpServletRequest request) {
        return clientIpResolver.resolve(request);
    }
}
