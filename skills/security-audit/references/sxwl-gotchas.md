# 本项目（sxwl）安全审计特有弱点验证表

## S1. SSE ticket 鉴权脆弱性
- **风险**：浏览器 `EventSource` 无法携带自定义 Header（`Authorization`），只能靠 query 参数 `ticket` 鉴权 → ticket 暴露在 URL 日志/Referer 中。
- **正确实现**：
  - ticket 必须一次性使用（生成后立即标记 `consumed=true`，不可重复使用）。
  - ticket 有效期短（建议 ≤ 5 分钟），过期作废。
  - ticket 绑定用户 ID（Redis 存储 `{ticket: userId}`），防止盗用。
  - `/sse/ticket` 接口限流（单 IP ≤ 10 次/分钟）。
- **检查**：`SxwlConnectionTicketService` 是否一次性消费；`SseEmitter` 容器清理是否及时。

## S2. JWT 过滤器默认放行风险
- **现象**：若 `jwtAuthenticationFilter` 对无 token 请求默认放行 → 所有匿名接口可绕过认证。
- **正确约定**：JWT Filter 对无 token 请求必须拒绝（401），仅在白名单端点放行（登录、获取公钥、SSE ticket）。
- **风险**：敏感接口（如 `/user/info`）未加权限注解 + JWT Filter 放行 → 任意未认证访问。
- **检查**：`SxwlSecurityConfig` 白名单 + `jwtAuthenticationFilter` 默认行为一致性。

## S3. 权限串共享漏洞
- **场景**：`system:log:list` 被登录日志和操作日志页面共用；`monitor:job:list` 被定时任务和任务日志共用。
- **风险**：前端只隐藏按钮，但后端接口未校验 → 通过 Postman 直接调用可绕过。
- **正确实现**：后端必须强制校验 `@SxwlRequiresPermissions`；前端仅做 UX 优化（非安全边界）。
- **检查**：Controller 注解与菜单 SQL + 前端按钮三处一致；删除按钮时确认归属权。

## S4. SQL 漂移导致权限失效
- **现象**：`base.sql` vs `sxwl_project_v1.sql` 的 `uk_sys_menu_perms` 定义不一致（单列 vs 组合 `(id, perms)`）。
- **影响**：
  - 用 base 建库 → 重复 perms 插入失败 → 菜单缺失 → 对应功能不可访问。
  - 用 v1 建库 → 组合唯一索引放行重复 perms → 多菜单共用同一权限 → 权限控制变弱。
- **修复**：统一为组合唯一索引 `(id, perms)`；部署脚本以 v1 为准；文档更新标注。

## S5. RustFS 路径穿越攻击
- **风险**：文件名为用户可控输入（如上传重命名、用户填写）→ `../`, `..\\`, `%2e%2e%2f` 遍历目录 → 覆盖系统文件（如 `/etc/passwd`）。
- **正确实现**：
  - 文件名白名单校验：仅允许字母数字下划线短横线点。
  - 拒绝包含 `.` 序列的文件名（除非在白名单内）。
  - 对象 key 拼接后，`PathUtil.normalize()` 去除父目录引用。
- **检查**：`SysFileServiceImpl` 上传/删除操作是否做路径穿越校验。

## S6. SM2 私钥废弃配置残留
- **历史问题**：早期 YAML 配置过 `sm2-private-key` → 已废弃（私钥不应暴露于配置文件）。
- **当前状态**：SM2 私钥由服务端 BouncyCastle 动态生成；公钥客户端动态获取。
- **检查**：所有 `application*.yaml` / `.env` / 代码注释是否残留私钥值；若有立即轮换。
- **迁移方案**：旧数据用旧私钥解密 → 重新用新公私钥加密。

## S7. 日志脱敏不全
- **常见漏网之鱼**：
  - `catch (Exception e) { log.error(e.getMessage(), e); }` → stack trace 含请求体/响应体（可能含密码/token）。
  - DEBUG 模式打开时打印完整 DTO（含用户手机号、身份证号）。
  - Redis 缓存序列化日志输出（含敏感字段）。
- **正确实践**：
  - 业务日志不记录密码/token/密钥；如需记录必须掩码（`sk_****deadbeef`）。
  - 异常日志只记录错误信息 + 堆栈，不打印原始请求体（脱敏后再记）。
  - 生产环境关闭 DEBUG 日志级别。
- **检查**：全量搜索 `log.` / `LOGGER.debug` / `e.printStackTrace()` 输出内容。

## S8. CORS 跨域过度放行
- **风险**：`Access-Control-Allow-Origin: *` + `Allow-Credentials: true` → 任何网站可携带 Cookie 发起请求 → CSRF + 数据窃取。
- **正确实现**：
  - 明确指定白名单域名（如 `http://localhost:31001`, `https://yourdomain.com`）。
  - `Allow-Credentials: true` 时，Origin 必须是具体域名（不能是 `*`）。
  - 预检请求（OPTIONS）校验 `Access-Control-Request-Headers`（限制 HTTP 方法）。
- **检查**：`WebConfig.corsMappings()` 白名单列表；生产环境 CORS 策略收紧。

## S9. 逻辑删除过滤遗漏
- **现象**：部分查询未加 `WHERE delete_flag=0` → 可见已删除数据 → 信息泄露/业务逻辑混乱。
- **典型场景**：
  - 联合查询（JOIN）未同步添加删除标记过滤 → 关联出已删除数据。
  - 统计报表 COUNT/SUM 包含已删除数据 → 统计不准。
- **修复**：MyBatis 拦截器自动注入 `delete_flag=0`；手动查询逐一核对。

## S10. 数据范围权限绕过
- **机制**：自定义拦截器 `SxwlDataScopeInterceptor` 拼装 `WHERE org_id IN (...)` 按角色范围过滤。
- **风险**：
  - 业务 SQL 手写 `SELECT *` 未走拦截器 → 全表可见 → 数据泄露。
  - 拦截器 SQL 拼注入：排序字段/组织树路径拼接 `${}` → 可绕过数据权限。
- **检查**：所有复杂查询是否经过拦截器；手工拼装 SQL 是否有数据范围参数传入。
