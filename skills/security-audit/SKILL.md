---
name: security-audit
description: This skill should be used when conducting full-stack security architecture audits, covering authentication/authorization, cryptographic algorithms (SM2/SM3), data privacy, API security hardening, and penetration testing. It encodes project-specific security patterns discovered in this codebase and runs targeted security checks. Trigger on requests to "security audit", "penetration test", "SM2/SM3 check", "permission vulnerability", "加密审计", "权限漏洞", or when performing security review before release.
---

# Security Audit（全栈安全架构审计）

## Overview

对 sxwl 全栈项目进行**生产级安全架构审计**。标准：能不能扛住常见攻击？国密算法实现是否正确？权限模型是否有漏洞？敏感数据是否泄露？逐层检查认证授权、加密存储、API防护、数据安全。本技能不追求覆盖率数字，只产出**可复现、有修复建议、带严重级别**的发现。

## When To Use

- 发布/上线前进行完整或分模块安全审计。
- 用户请求"安全审计""渗透测试""国密检查""权限漏洞"。
- 安全事件后应急排查（如疑似越权、数据泄露）。
- 审查范围：`sxwl-boot/**/*.java`、`sxwl-react/src/**`、配置文件、数据库 Schema。

## Severity Definitions（务必在报告中标注）

- **BLOCKER（阻断）**：上线必被攻破或导致数据泄露。如 JWT 绕过、SQL 注入、明文密码、IDOR 越权。
- **HIGH（高）**：高概率安全风险或合规问题。如 SM2/SM3 实现错误、密钥硬编码、敏感日志。
- **MEDIUM（中）**：边界场景安全问题、安全头缺失。
- **LOW（低）**：最佳实践改进、安全增强。

## Review Workflow（SOP）

1. **圈定范围**：明确本次审计的模块（如 auth、system、sse、rustfs）或层次（认证层、数据层、API 层）。全量审计时按层次逐个执行本 SOP。
2. **阅读入口**：从安全配置入手（`SxwlSecurityConfig`、JWT Filter、CORS）、加密工具类、认证 Controller。
3. **逐条核对清单**：按下方 10 大类 + `references/checklist.md` 详细条目，对每个层次扫描。
4. **核对项目特有机坑位**：加载 `references/sxwl-gotchas.md`，逐条验证本项目已知安全弱点。
5. **交叉验证**：前端权限隐藏 ≠ 后端安全；加密算法实现 ≠ 合规使用；权限串需在 "Controller / 菜单 SQL / 前端按钮" 三处一致。
6. **只报确凿项**：每条发现给出 文件:行号、问题描述、复现步骤/触发条件、修复建议、合规影响（如等保/国密要求）。
7. **输出报告**：按下方"Report Format"汇总。

## Checklist（10 大类，完整条目见 references/checklist.md）

### 1. **认证安全（Authentication）**
- JWT 令牌安全：过期时间是否合理（不过长）；刷新机制是否防重放；是否支持主动登出/黑名单。
- 密码策略：强度校验（长度、复杂度）；哈希算法（SM3/BCrypt）+ 盐值；是否限制登录失败次数。
- SSE ticket：一次性使用；有效期短；防止重放攻击。
- CORS 跨域配置：白名单是否过度放行（`*`）；是否允许凭证传输（`Allow-Credentials`）。

### 2. **授权与访问控制（Authorization）**
- RBAC 模型完整性：用户-角色-菜单-权限四表关联是否严密；是否有绕过可能。
- IDOR（不安全的直接对象引用）：根据 id 操作资源时，是否校验归属（如操作非本人数据）。
- 权限串一致性：Controller `@SxwlRequiresPermissions` / 菜单种子 SQL `perms` / 前端按钮 `perms` 三处完全一致。
- 共享权限映射：`system:log:list`、`monitor:job:list` 多页面共用，确保权限判断正确。

### 3. **国密算法安全（SM2/SM3/SM4）**
- SM2 公钥/私钥管理：私钥不再配置到 YAML（已废弃）；公钥动态获取；密钥轮换策略。
- SM3 密码哈希：迭代轮数足够；加盐随机；防彩虹表。
- SM4 对称加密：密钥派生安全；加密模式（GCM/CBC）+ IV；避免硬编码密钥。
- 检查：是否有混用 MD5/SHA1 等不合规哈希；国密算法实现是否使用 BouncyCastle 正规库。

### 4. **SQL 注入与 ORM 安全**
- MyBatis `${}` vs `#{}`：`${}` 拼接外部输入 → BLOCKER；动态排序字段需白名单校验。
- 逻辑删除过滤：所有查询是否 `WHERE delete_flag=0`；唯一约束是否考虑删除标记。
- 批量操作：IN 查询参数是否做数量限制；ID 数组是否校验格式。

### 5. **XSS 与输入 sanitization**
- 存储型 XSS：富文本/备注字段是否做白名单过滤（如 TipTap 仅允许可信标签）。
- 反射型 XSS：URL 参数/搜索关键词是否输出到 DOM（需 HTML 转义）。
- 文件上传：文件名/对象 key 是否路径穿越校验；文件类型/大小限制；MIME 类型伪造检测。

### 6. **数据隐私与敏感信息保护**
- 日志脱敏：密码/token/sessionId/身份证/手机号绝不记录日志；密钥不写入任何日志。
- 响应数据最小化：DTO 不返回非必要敏感字段（如 hash 密码、私钥）。
- 传输加密：HTTPS/WSS 在生产环境强制；本地开发环境敏感数据不占位。
- PII（个人身份信息）：存储加密；导出脱敏；传输通道加密。

### 7. **API 安全防护**
- Rate Limiting：登录接口/SSE ticket/注册是否限流（防暴力破解/DDoS）。
- CSRF 防护：如使用 Cookie 认证需 CSRF Token；JWT 无状态可不防但需关注 XSRF。
- 请求体校验：DTO 字段长度/格式/枚举值校验；必填字段非空；数值范围。
- API 版本控制：老版本 API 退役计划；Breaking Change 通知消费者。

### 8. **资源泄漏与拒绝服务**
- SSE 连接数限制：单用户最大连接数；全局最大在线用户数；空闲超时自动断开。
- 文件上传限制：单文件大小/总占用空间；分片并发数；MD5 秒传防重复存储。
- 大列表/导出：分页限制最大 pageSize；导出行数上限；异步生成大文件。
- 线程池/连接池：队列无界 → OOM 风险；异常时未归还连接。

### 9. **配置与密钥管理**
- 环境变量 vs 配置文件：敏感信息不入 Git；模板文件全脱敏（占位符）。
- `.gitignore` 覆盖：`application-*.yaml`（无 template 后缀）；`.env.local`；凭据/日志/产物。
- 多环境差异：生产环境 vs 测试环境安全级别（如 HTTPS、双因素认证）。
- 依赖安全：第三方库 CVE 漏洞；过时版本升级计划；许可证合规。

### 10. **审计与监控**
- 操作日志：关键安全事件记录（登录成功/失败、权限变更、数据导出、删除操作）。
- 登录日志：IP 异常登录；异地登录告警；频繁失败锁定。
- 在线用户监控：长时间 idle 踢出；同账号多端限制（可选）。
- 安全告警：阈值设定（如 5 分钟 10 次失败登录 → 封禁 IP）。

## Project-Specific Gotchas（详见 references/sxwl-gotchas.md）

- **SSE ticket 鉴权脆弱**：浏览器 EventSource 无法带自定义 Header（Authorization），只能靠 query 参数 ticket 鉴权 → ticket 必须一次性 + 短时有效 + 绑定用户。
- **JWT 过滤器默认放行风险**：若 `jwtAuthenticationFilter` 对无 token 请求默认放行 → 所有匿名接口可绕过认证；必须显式白名单控制。
- **权限串共享漏洞**：`system:log:list` 被登录/操作日志共用；若只隐藏按钮未禁用接口 → 可通过 Postman 直接调用 → 后端必须校验。
- **SQL 漂移导致权限失效**：`base.sql` vs `v1.sql` 的 `uk_sys_menu_perms` 不一致 → 部署以 v1 为准但 base 建库可能唯一键冲突 → 权限插入失败 → 菜单为空。
- **RustFS 路径穿越**：文件名为用户可控时 → `../` 遍历目录 → 覆盖系统文件 → 必须白名单校验文件名字符。

## Report Format

```markdown
## 安全审计报告 — <范围> — <日期>
范围：<文件/包/层次>
结论：安全 / 需修复 BLOCKER/HIGH 后上线

### BLOCKER（立即修复）
- [S-B1] 文件:行 — 问题 — 复现步骤 — 修复建议 — 合规影响

### HIGH（严重）
- [S-H1] ...

### MEDIUM（中等）/ LOW（建议）
- ...

## 合规性评估（如适用）
- 等保 2.0：符合/不符合条款
- 国密合规：SM2/SM3/SM4 实现标准
- GDPR/个人信息保护法：PII 处理合规性
```

加载 `references/checklist.md` 获取逐条检查细则；加载 `references/sxwl-gotchas.md` 获取本项目已知安全弱点验证表。
