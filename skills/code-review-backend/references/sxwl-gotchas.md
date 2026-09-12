# 本项目（sxwl-boot）特有坑位验证表

审查时逐条核对以下已知易错点，命中即报（含当前状态判断）。

## G1. SxwlResult 与 String + @SxwlNoWrap 的包体冲突
- 现象：端点声明返回 `String` 并期望被统一响应体包裹时，因 Spring 选 `StringHttpMessageConverter`，`SxwlResponseBodyAdvice.beforeBodyWrite` 把 `String` 包成 `SxwlResult` 会 `ClassCastException`。
- 正确约定：
  - 返回**裸流/文本/二进制**且不需包装的端点（如 `/sse/connect` 返回 `SseEmitter`）→ 加 `@SxwlNoWrap`，返回类型声明为具体类型（如 `SseEmitter`），**不要**返回 `String`。
  - 需要返回字符串且要包成统一响应 → 返回类型声明为 `SxwlResult<String>`（如 `/sse/ticket`）。
- 检查：搜索 `return "..."` 且方法返回类型非 `SxwlResult` 又非 `@SxwlNoWrap` 的端点。

## G2. SSE 鉴权与重连风暴
- `/sse/connect` 由浏览器 `EventSource` 发起的 GET，**无法带自定义请求头**（Authorization），只能靠 query 参数里的 ticket 鉴权。
- 风险：`jwtAuthenticationFilter` 对无 token 请求若默认放行，或 whitelist 漏配 `/sse/connect`，会造成连接失败→前端 `onerror`→1s 重连→再申请新一次性 ticket 的**无限重连风暴**（已在日志中观察到每 1–2s 一次 `/sse/ticket`）。
- 检查：`SxwlSecurityConfig` 白名单是否含 `/sse/connect` 与 `/sse/ticket`；ticket 是否一次性消费（`SxwlConnectionTicketService`）；`/sse/connect` 是否用 ticket 校验而非 header token。

## G3. 权限串共享与唯一约束
- `system:log:list` 被「登录日志」「操作日志」页面共用；`monitor:job:list` 被「定时任务」「任务日志」页面共用。
- `uk_sys_menu_perms` 已改为**组合唯一索引 `(id, perms)`**（id 主键唯一，放开 perms 跨行重复）。
- 检查：`menu_seed_full.sql` 中共享 perms 是否各出现两次且一致；生成的种子脚本是否仍对重复 perms 报错退出（若改回组合索引则不应拦截）。

## G4. SQL 文件漂移（base.sql vs sxwl_project_v1.sql）
- 两文件对 `uk_sys_menu_perms` 的定义不同：`base.sql` 仍可能是单列唯一索引，`v1` 已改组合。
- 检查：部署以哪个为准；若以后用 base 建库需同步改；审查时标注不一致，避免上线后因约束不同而插入失败。

## G5. MyBatis Mapper 与表结构一致性
- 检查 XML 中 `resultMap`/`column` 是否与 `sxwl_project_v1.sql` 实际表字段一致（尤其新增字段、改名、删字段后未同步 Mapper）。
- 检查逻辑删除 `delete_flag` 是否在所有查询中正确过滤；唯一约束的 `WHERE delete_flag=0` 部分索引是否被查询命中。

## G6. 文件模块（rustfs）路径与分片
- `SysFileServiceImpl` 多处 `@Transactional`；分片上传合并、秒传、删除是否幂等；并发上传同一文件是否安全。
- 检查：文件名/对象 key 是否做路径穿越校验；大文件流是否 try-with-resources；分片元数据清理是否完整。
