# 技术栈升级评估

> 评估日期：2026-06-27  
> 当前约束：Java 8、Spring Boot 2.7.18、Node 14.21.3

## 现状与风险

| 组件 | 当前版本 | EOL / 状态 | 风险 |
|------|----------|------------|------|
| Java | 8 | 公共更新已结束（需商业支持） | 安全补丁、新语言特性不可用 |
| Spring Boot | 2.7.18 | OSS 支持已结束 | 依赖 CVE 需自行跟进 |
| Node.js | 14.21.3 | 2023-04 EOL | 前端工具链逐渐不兼容 |
| Nuxt | 3.5.3 | 活跃 | 长期需 Node 18+ |

## 建议升级路径

### 阶段 1：Node 18 LTS（优先，影响面小）

1. 本地与 CI 切换到 Node 18.20.x
2. 升级 `frontend` / `admin` / `e2e` 的 `engines` 与 lockfile
3. 移除 `frontend/scripts/patch-nuxi-node14.cjs` 等兼容补丁
4. 验证 `npm run build` 与 Playwright E2E

**预估工作量：** 1–2 天

### 阶段 2：Java 17 + Spring Boot 3.2 LTS

1. 升级 `pom.xml`：`java.version=17`，Spring Boot 3.2.x
2. 迁移 `javax.*` → `jakarta.*`（Servlet、Validation）
3. 更新 Spring Security 6 配置 DSL（`authorizeHttpRequests` 等）
4. 跑通 `mvnw test` 与 Docker 生产栈

**预估工作量：** 3–5 天（含回归）

### 阶段 3：微服务拆分（可选）

仅在流量/团队规模需要时，按 `backend/microservices/README.md` 路线图拆分；当前单体已满足个人博客场景。

## 暂不升级的情况

- 仅内网部署、可接受自行打补丁
- 无合规要求强制 JDK 17+
- 团队暂无前端升级窗口

## 决策记录

| 日期 | 决策 |
|------|------|
| 2026-06-27 | 记录评估；短期继续 Java 8 + Node 14，优先完成 P0/P1 功能与安全项 |
