package com.jiangou.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 启动守卫（审计项 C1）：在任何非开发环境下，若仍使用打包内置的公开默认 JWT 密钥，则 fail-fast 拒绝启动。
 *
 * <p>与 {@link ProdEnvValidator}（仅 {@code @Profile("prod")} 生效）不同，本守卫在所有 profile 下都会运行，
 * 因此即便误以 dev 以外的方式启动打包 jar、却忘记设置 {@code SPRING_PROFILES_ACTIVE=prod}，
 * 也无法以一个公开可见的签名密钥静默启动（否则攻击者可凭该密钥伪造任意管理员令牌）。</p>
 *
 * <p>开发便利保留：dev / test / e2e / integration-test 这些本地与 CI profile 仍允许使用默认密钥。</p>
 */
@Component
public class StartupSecretGuard {

    /** 允许使用内置默认密钥的开发/测试 profile。 */
    private static final Set<String> DEV_PROFILES = new HashSet<String>(Arrays.asList(
            "dev", "test", "e2e", "integration-test"));

    /** 与 application.yml 中 jiangou.jwt.secret 的默认值保持一致。 */
    private static final String DEFAULT_SECRET = "jiangou-dev-secret-change-in-production-min-32-chars";

    private final Environment environment;

    @Value("${jiangou.jwt.secret:}")
    private String jwtSecret;

    public StartupSecretGuard(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validate() {
        if (!DEFAULT_SECRET.equals(jwtSecret)) {
            return;
        }
        for (String profile : environment.getActiveProfiles()) {
            if (DEV_PROFILES.contains(profile)) {
                return;
            }
        }
        throw new IllegalStateException(
                "检测到内置默认 JWT 密钥，但当前不是开发环境 (active profiles="
                        + Arrays.toString(environment.getActiveProfiles())
                        + ")。请设置安全的 JWT_SECRET 环境变量，或显式以 dev profile 启动。");
    }
}
