# 安全审计详细清单（security-audit）

## 1. 认证安全（Authentication）

### JWT 令牌安全
- [ ] JWT `exp`（过期时间）是否合理（建议 access token 不超过 2 小时）；refresh token 是否有使用次数限制。
- [ ] Token 刷新机制：是否支持主动登出/黑名单（Redis 存储黑名单）；刷新后旧 token 是否立即失效。
- [ ] Token 传输方式： Authorization Header 优先；Cookie 方式需 `HttpOnly` + `Secure` + `SameSite`。
- [ ] 签名算法：HS256/RS256 强度足够；密钥长度 ≥ 256 位（或 RSA 2048+）。

### 密码与登录
- [ ] 密码强度校验：最小长度（≥8）；复杂度要求（大小写+数字+特殊字符至少 3 项）；常见弱口令黑名单。
- [ ] 哈希算法：SM3/BCrypt 迭代轮数足够（BCrypt cost factor ≥ 10）；加盐随机（每用户独立 salt）。
- [ ] 暴力破解防护：连续失败 N 次锁定账号/验证码/CAPTCHA；IP 频率限制（Rate Limiting）。
- [ ] 密码重置：token 一次性 + 短时有效；链接只能用一次；重置后旧 token 失效。

### SSE ticket 安全
- [ ] ticket 是否一次性使用（生成后立即标记已消费，不可重复使用）。
- [ ] ticket 有效期是否短（建议 ≤ 5 分钟）。
- [ ] ticket 是否与用户绑定（防止盗用 ticket 建立连接）。
- [ ] ticket 接口（`/sse/ticket`）是否限流（防批量申请 ticket DoS）。

### CORS 跨域配置
- [ ] `Access-Control-Allow-Origin` 是否过度放行（`*` 禁止用于凭证接口）。
- [ ] 允许的来源白名单是否严格（指定域名而非 `*`）。
- [ ] `Allow-Credentials: true` 时，Origin 必须是明确域名（不能是 `*`）。

---

## 2. 授权与访问控制（Authorization）

### RBAC 模型完整性
- [ ] 用户-角色-菜单-权限四表关联查询是否严密；是否存在绕过可能（如直接访问无菜单端点）。
- [ ] 角色分配/回收是否实时生效（缓存/会话更新）。
- [ ] 超级管理员权限是否隔离（能否操作所有资源 / 审计日志可否自删）。

### IDOR（不安全的直接对象引用）
- [ ] 根据 id 查询/更新/删除资源时，是否校验当前用户归属（如只允许操作自己创建的数据）。
- [ ] 批量操作接口（如 `/batch-delete`）是否校验每个 id 的归属权。
- [ ] URL 参数是否可篡改（如 id=1 → id=2 访问他人数据）。

### 权限串一致性
- [ ] Controller `@SxwlRequiresPermissions("x:y:z")` / 菜单种子 SQL `perms` 列 / 前端按钮 `perms` 三处字符串完全一致。
- [ ] 新增权限后：Controller 注解 → 菜单 SQL 插入 → 前端按钮渲染 → 四处同步更新（缺一则功能不完整）。
- [ ] 共享权限映射正确（`system:log:list` 登录日志和操作日志页面共用同一权限；判断逻辑一致）。

### 数据范围权限（Data Scope）
- [ ] 自定义拦截器 `SxwlDataScopeInterceptor` 是否正确拼装 `WHERE` 子句（按角色权限范围：全部/本部门/本部门及下级/仅本人）。
- [ ] 数据权限是否被业务层绕过（如直接 `SELECT *` 未走拦截器）。

---

## 3. 国密算法安全（SM2/SM3/SM4）

### SM2 公钥/私钥管理
- [ ] 私钥 **不再配置到 YAML**（已废弃）；公钥动态获取（从服务端/BouncyCastle 库）。
- [ ] 密钥轮换策略：新公私钥对生成流程；旧数据解密重加密方案（如双密钥过渡期）。
- [ ] 私钥存储：如需持久化，必须加密存储（如 KMS/HSM）；不在源码/配置文件中明文出现。

### SM3 密码哈希
- [ ] 迭代轮数：建议 ≥ 10000 次（防暴力破解）。
- [ ] Salt 随机性：每用户独立 salt（≥ 16 字节随机数）；相同密码不同 hash。
- [ ] 彩虹表防护：Salt 加入哈希计算，非单独存储。
- [ ] 检查：是否有混用 MD5/SHA1/SHA256 等不合规哈希（中国等保要求 SM3）。

### SM4 对称加密
- [ ] 密钥派生：PBKDF2/Argon2 等强密钥派生；避免硬编码密钥字符串。
- [ ] 加密模式：GCM（认证加密）优于 CBC（仅加密）；IV/Nonce 随机且不可预测。
- [ ] 密文存储：base64 编码后可接受；但不可在前端 JS 中硬编码密钥。

### 合规检查
- [ ] 国密算法实现是否使用 BouncyCastle 正规库（`org.bouncycastle:bcprov-jdk18on`）。
- [ ] 禁止使用非国密算法替代（如 AES/DES 需替换为 SM4；RSA 替换为 SM2）。
- [ ] 第三方依赖审查：是否有间接引入不安全加密库（如 `javax.crypto` 弱Cipher）。

---

## 4. SQL 注入与 ORM 安全

### MyBatis `${}` vs `#{}`
- [ ] Mapper XML 中所有 `${}`：参数来源是否外部可控（请求参数、排序字段、动态表名）。若是 → BLOCKER。
- [ ] `ORDER BY ${sortField}`：是否在后端做白名单校验（仅限允许的字段列表）。
- [ ] 动态 `IN` / 分页偏移是否用 `#{}`（参数化查询，预编译）。
- [ ] `@Select("<script>...")` 注解 SQL：是否有字符串拼接外部变量。

### 逻辑删除过滤
- [ ] 所有查询是否 `WHERE delete_flag=0`；遗漏则可见已删除数据 → 信息泄露/逻辑混乱。
- [ ] 唯一约束是否考虑删除标记：部分索引 `UNIQUE WHERE delete_flag=0`（否则重复逻辑删除数据可能触发唯一键冲突）。

### 批量操作安全
- [ ] IN 查询参数是否做数量限制（如 `LIMIT 1000`）；恶意传入超大数组导致数据库压力/SQL 超时。
- [ ] ID 数组是否校验格式（非负整数）；防类型注入。

---

## 5. XSS 与输入 Sanitization

### 存储型 XSS
- [ ] 富文本字段（如 TipTap 内容、备注、公告正文）是否做白名单过滤（仅允许可信标签：<p>, <strong>, <img> 等）。
- [ ] 黑名单过滤危险标签：<script>, <iframe>, <object>, <embed>, <form>, <input type="text">。
- [ ] HTML 属性过滤：`onerror`, `onclick`, `onload` 等事件处理器移除。

### 反射型 XSS
- [ ] URL 参数/搜索关键词输出到 DOM 时，是否做 HTML 转义（如 `<` → `&lt;`）。
- [ ] `document.write()` / `innerHTML` 是否拼接用户输入。
- [ ] Content-Security-Policy (CSP) Header 是否设置（限制脚本来源）。

### 文件上传安全
- [ ] 文件名/对象 key 是否路径穿越校验：拒绝包含 `../`, `..\\`, `%2e%2e` 等序列的文件名。
- [ ] 文件类型校验：MIME 类型白名单（图片：image/jpeg, image/png；禁止 .exe, .jsp, .sh 等可执行文件）。
- [ ] 文件大小限制：单文件上限（如 100MB）；分片上传总大小限制。
- [ ] MIME 类型伪造检测：检查文件头（Magic Number），非仅依赖后缀/MIME。

---

## 6. 数据隐私与敏感信息保护

### 日志脱敏
- [ ] **绝对禁止**记录以下信息到日志：密码、Token、Secret-Key、JWT、身份证号、手机号、银行卡号。
- [ ] 若需打印调试信息，敏感字段必须掩码处理（如 `138****1234`, `sk_****deadbeef`）。
- [ ] 检查 `catch (Exception e)` 中 `e.printStackTrace()` 或日志输出是否包含请求体/响应体（可能含敏感数据）。

### 响应数据最小化
- [ ] DTO/VO 不返回非必要敏感字段（如 `hashPassword`, `privateKey`, `secretKey`）。
- [ ] 列表页 vs 详情页：列表可不返回完整描述（详情再查）。
- [ ] `@JsonIgnore` / `@JSONField(serialize=false)` 标注敏感字段不被 JSON 序列化。

### 传输加密
- [ ] 生产环境强制 HTTPS/WSS；HTTP 301 重定向到 HTTPS。
- [ ] 本地开发环境：敏感数据占位符（非真实凭据）；私有配置管理。
- [ ] PII（个人身份信息）传输通道加密（TLS 1.2+）。

### PII 存储与导出
- [ ] 敏感字段存储加密（如 SM4 加密手机号/邮箱，密钥由 KMS 管理）。
- [ ] 数据导出（Excel/CSV）：脱敏处理（如 `138****1234`）。
- [ ] 数据备份：备份文件是否加密；备份传输通道安全。

---

## 7. API 安全防护

### Rate Limiting（限流）
- [ ] 登录接口：连续失败 N 次（如 5 次/5 分钟）→ 验证码/IP 临时封禁。
- [ ] SSE ticket 接口：单 IP 每分钟不超过 M 次（防批量申请 ticket DoS）。
- [ ] 注册/忘记密码/短信验证码：高频限制（防刷短信费/注册滥用）。
- [ ] 实现方式：Redis + 计数器/滑动窗口；分布式场景共享限流状态。

### CSRF 防护
- [ ] 如使用 Cookie 认证：必须 CSRF Token 机制（Double Submit Cookie 或 SameSite Cookie）。
- [ ] JWT 无状态认证：可不防 CSRF 但需关注 XSRF（恶意网站诱导浏览器发送请求）。
- [ ] 关键操作（修改密码、删除数据、转账）需二次验证（密码/验证码）。

### 请求体校验
- [ ] DTO 字段长度：`@Size`, `@Length` 注解校验；超长字符串导致数据库字段溢出/DoS。
- [ ] 数值范围：`@Min`, `@Max` 校验；负数价格/库存等异常值。
- [ ] 枚举值白名单：非法枚举值拒绝（如 `roleName` 非预定义角色名）。
- [ ] 必填字段：`@NotNull`, `@NotBlank` 校验；null 导致业务逻辑漏洞。

### API 版本控制
- [ ] 老版本 API 退役计划（EOL 日期）； Breaking Change 提前通知消费者。
- [ ] API 文档同步更新（Swagger/OpenAPI）；废弃接口标注 `@Deprecated`。

---

## 8. 资源泄漏与拒绝服务

### SSE 连接数限制
- [ ] 单用户最大 SSE 连接数（如 ≤ 3）；超过新连接踢出旧连接。
- [ ] 全局最大在线用户数（如 10000）；达到上限排队/拒绝/降级。
- [ ] 空闲超时自动断开（如 5 分钟无消息推送 → 关闭连接）。
- [ ] `SseEmitter` 必须设 `setTimeout()`；`onCompletion/onTimeout/onError` 清理容器。

### 文件上传限制
- [ ] 单文件大小：图片 ≤ 10MB；文档 ≤ 100MB；视频需分段上传。
- [ ] 总占用空间：单用户配额（如 10GB）；超额需升级/清理。
- [ ] 分片并发数：限制同时上传分片数（如 ≤ 10）；避免拖垮存储。
- [ ] MD5 秒传：防重复存储；校验秒传文件完整性（非仅靠 MD5 碰撞）。

### 大列表/导出
- [ ] 分页限制最大 `pageSize`（如 ≤ 1000）；恶意请求全表扫描。
- [ ] 导出行数上限（如 ≤ 50000）；超量改为异步生成 + 邮件通知下载。
- [ ] 导出文件格式：禁止远程实体解析（防 XXE）。

### 线程池/连接池
- [ ] 队列无界（如 `LinkedBlockingQueue` 无容量）→ OOM 风险 → 设最大队列 + 拒绝策略（CallerRunsPolicy）。
- [ ] 异常时未归还连接 → 连接池耗尽 → `try-finally` 或 `useClose` 确保归还。
- [ ] `@Async` 任务异常是否被吞；未捕获异常导致任务静默失败。

---

## 9. 配置与密钥管理

### 环境变量 vs 配置文件
- [ ] 敏感信息不入 Git：`application-dev.yaml`, `application-prod.yaml`, `.env.local` 等。
- [ ] 模板文件全脱敏（占位符）：`application-test.yaml.template`（`your_db_password`, `your-secret-key`）。
- [ ] 检查 `.gitignore` 覆盖：所有 `**/application-*.yaml`（除 `application.yaml` 和 `*.template`）。

### 多环境差异
- [ ] 生产环境 vs 测试环境安全级别差异记录（如 HTTPS 强制、双因素认证、IP 白名单）。
- [ ] 生产环境不暴露 Debug 信息 / Actuator 端点（需鉴权/内网访问）。
- [ ] 测试数据库 ≠ 生产数据库；误操作不影响真实数据。

### 依赖安全
- [ ] 第三方库 CVE 漏洞扫描（`mvn dependency:tree` + NVD/CVE 数据库）。
- [ ] 过时版本升级计划（如 Spring Boot v2 → v3；Ant Design 5 → 6）。
- [ ] 许可证合规：GPL/Affero GPL 传染性问题（商业软件需 MIT/BSD/Apache）。

---

## 10. 审计与监控

### 操作日志
- [ ] 关键安全事件记录：登录成功/失败、权限变更、数据导出、删除操作、密码修改。
- [ ] 操作日志不可被操作者删除（防篡改）；管理员也只可读。
- [ ] 操作日志保留期限（如 ≥ 6 个月）；归档/冷存储方案。

### 登录日志
- [ ] 记录 IP、User-Agent、登录地点（IP 地理位置解析）、登录设备指纹。
- [ ] IP 异常登录告警（异地/非常用城市）；频繁失败锁定（N 次失败 → 封禁 30 分钟）。
- [ ] 登录日志与业务解耦（`@TransactionalEventListener` 异步写入，不影响主流程）。

### 在线用户监控
- [ ] 长时间 idle 踢出（如 30 分钟无活动 → 强制登出）。
- [ ] 同账号多端限制（可选：单账号同时在线 ≤ 3；新登录踢旧会话）。
- [ ] Redis Key 管理：在线用户 Session 过期时间 = JWT exp；Redis 过期自动清理。

### 安全告警
- [ ] 阈值设定：5 分钟 10 次失败登录 → 封禁 IP 24 小时。
- [ ] 异常行为检测：单用户 1 分钟内查询 > 1000 次 → 临时限流。
- [ ] 告警通知：钉钉/企业微信/邮件；分级告警（BLOCKER 即时，HIGH 1 小时内）。
