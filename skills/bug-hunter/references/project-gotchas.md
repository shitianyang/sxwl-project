# 本项目 Bug 模式库（bug-hunter）

每条含：现象、根因、搜索特征（用于 `search_content`）、确认方式、修复。

## P1. SSE 响应体 ClassCastException（后端）
- 现象：调用返回裸 `String` 的端点报错/连接断开。
- 根因：返回 `String` 被 `SxwlResponseBodyAdvice` 尝试包成 `SxwlResult` → `StringHttpMessageConverter` 冲突 `ClassCastException`。
- 搜索：`return "` 且方法返回类型非 `SxwlResult` 且未 `@SxwlNoWrap`；或方法签名返回 `String`。
- 确认：`read_file` 看方法返回类型与注解。
- 修复：裸流/文本 → 返回 `SseEmitter`/`void` + `@SxwlNoWrap`；需包装字符串 → 返回 `SxwlResult<String>`。

## P2. SSE 重连风暴（前端）
- 现象：日志每 1–2s 一个 `/sse/ticket`，且 ticket 每次不同。
- 根因：`EventSource` `onerror` 无退避地重连 + 每次申请新一次性 ticket；或 `/sse/connect` 鉴权失败被持续重试。
- 搜索（sxwl-react/src）：`new EventSource`、`/sse/ticket`、`onerror`、`setInterval` 轮询 ticket。
- 确认：`useSSE.ts`/`useMonitorSSE.ts` 重连逻辑、是否 cleanup `close()`、是否有最大重试/退避。
- 修复：指数退避 + 最大重试上限 + 卸载 `close()`；ticket 校验失败立即报错而非无限重试。

## P3. 权限串不一致（跨端）
- 现象：按钮点了无权限 / 能访问不该访问的接口。
- 根因：前端 `perms`、路由守卫、后端 `@SxwlRequiresPermissions`、菜单种子 SQL 四处不一致；共享 perms（`system:log:list`、`monitor:job:list`）映射错。
- 搜索：前端 `perms:"`，后端 `@SxwlRequiresPermissions("`，`menu_seed_full.sql` 的 `perms` 列。
- 确认：逐一比对四处的字符串是否完全相等。
- 修复：以 `menu_seed_full.sql` + 后端注解为准，统一前端。

## P4. MyBatis SQL 注入
- 现象：排序/动态条件被注入。
- 根因：Mapper XML 用 `${}` 拼接外部输入。
- 搜索：`\$\{` 于 `**/*.xml`；关注 `ORDER BY`、`LIMIT`、`IN`。
- 确认：`read_file` 看参数来源是否外部可控。
- 修复：`#{}` 参数化；字段名用白名单枚举；`ORDER BY` 后端校验。

## P5. 事务自调用失效
- 现象：异常后数据未回滚 / 部分写入。
- 根因：`this.xxx()` 调用同 Bean `@Transactional` 方法，绕过 AOP 代理。
- 搜索：`this\.` + 方法名（同文件内调用事务方法）。
- 修复：注入自身 Bean / `AopContext.currentProxy()` / 抽到独立 Bean。

## P6. 资源/连接泄漏
- 现象：句柄数上涨、内存涨、SSE 连接堆积。
- 根因：流/EventSource/监听器/定时器未清理。
- 搜索：后端 `InputStream`/`SseEmitter` 未 try-with-resources/未 `complete`；前端 `new EventSource`/`addEventListener`/`setInterval` 无 cleanup。
- 修复：try-with-resources；`onCompletion/onTimeout` 清理容器；前端 cleanup `close()`。

## P7. SQL 文件漂移
- 现象：用 base 建库与用 v1 建库约束不同，插入报唯一键冲突。
- 根因：`base.sql` 与 `sxwl_project_v1.sql` 的 `uk_sys_menu_perms` 定义不一致（单列 vs 组合 `(id, perms)`）。
- 搜索：`uk_sys_menu_perms` 在两 SQL 文件中的定义。
- 修复：统一为组合唯一索引 `(id, perms)`；部署以 v1 为准并标注。
