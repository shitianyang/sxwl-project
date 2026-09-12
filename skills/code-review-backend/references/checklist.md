# 后端审查详细清单（code-review-backend）

逐条扫描，命中即记录。条目按 SKILL.md 的 9 大类展开。

## 1. 鉴权与授权
- [ ] Controller 方法（尤其写操作：add/update/delete/import/export）是否声明权限注解（`@PreAuthorize("hasPermission(...)")` 或本项目 `@SxwlRequiresPermissions("x:y:z")`）。
- [ ] 缺失权限注解的端点是否真的应是匿名/白名单（登录、获取公钥、SSE ticket 除外）。
- [ ] `SxwlSecurityConfig` 白名单（`/sse/connect`、`/sse/ticket` 等）是否过度放行，导致敏感接口绕过 JWT。
- [ ] JWT 过滤器对"无 token"请求的处理：是否默认放行（应为拒绝），仅在白名单放行。
- [ ] 权限串是否与 `menu_seed_full.sql`、前端按钮 `perms` 三处一致。

## 2. SQL 注入
- [ ] MyBatis Mapper XML 中所有 `${}`：参数是否来自外部输入（请求参数、排序字段、动态表名）。若是 → BLOCKER，改用 `#{}` 或白名单枚举。
- [ ] `ORDER BY ${sortField}`：是否校验字段在允许集合内。
- [ ] 动态 `IN` / 分页偏移是否用 `#{}`（参数化）。
- [ ] 拼接 SQL 的 `@Select("<script>...")` 注解：是否有字符串拼接外部变量。

## 3. 事务完整性
- [ ] 跨多表写、或"先写后发事件/消息"是否在 `@Transactional` 内；异常时数据是否一致。
- [ ] 自调用：`this.serviceMethod()` 是否绕过事务代理（应注入自身 Bean 或用 `AopContext`）。
- [ ] `try/catch` 中是否吞异常导致事务不回滚（catch 后未 `throw` 新异常 / 未 `TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()`）。
- [ ] `@Transactional` 是否标在 public 方法；是否因类是 final / 方法是 private 而失效。
- [ ] 事务传播行为是否正确（REQUIRES_NEW 用于日志/通知等旁路）。

## 4. 空指针与 Optional
- [ ] 请求体/DTO 字段、外部 API 返回、`Map.get()`、`list.get(0)`、JSON 反序列化缺失字段是否做非空判断。
- [ ] `Optional.get()` 是否有 `isPresent()`/`orElse` 守卫。
- [ ] 基础类型与包装类型混用（远程/JSON 返回 null 赋给 int）。

## 5. 并发与线程安全
- [ ] 是否存在 `static` 可变集合/计数器被多线程共享且无同步。
- [ ] `HashMap`/`ArrayList` 是否用作跨请求共享状态（应 `ConcurrentHashMap`）。
- [ ] `SimpleDateFormat`/`DecimalFormat` 是否定义为静态共享（非线程安全）。
- [ ] SSE emitter 容器（`SxwlSseEmitterManager`）的增删是否线程安全；连接断开是否移除。
- [ ] `@Async` / 自定义线程池：是否复用、是否队列无界（OOM 风险）、异常是否被吞。

## 6. 异常处理
- [ ] 是否存在空 `catch (Exception e) {}` 或仅 `e.printStackTrace()` 吞掉异常。
- [ ] 业务异常是否统一翻译为 `SxwlResult` 错误码，而非抛原生异常到前端。
- [ ] 是否返回 `null` 而非空对象/空集合导致前端 NPE。

## 7. 安全敏感
- [ ] 日志是否打印 密码、token、sessionId、身份证/手机号等 PII。
- [ ] 文件模块（rustfs）：文件名为用户可控时是否校验路径穿越（`../`）、是否限制存储桶/前缀。
- [ ] IDOR：根据 id 操作资源时，是否校验资源归属当前用户/租户。
- [ ] 存储型 XSS：富文本/备注是否转义或白名单过滤。

## 8. 资源泄漏
- [ ] `InputStream/OutputStream/Reader/Writer/Connection` 是否 try-with-resources。
- [ ] `SseEmitter`：是否设置 `setTimeout`；`onCompletion`/`onTimeout`/`onError` 是否清理容器。
- [ ] 定时任务/线程池是否注册 shutdown hook。

## 9. API 契约一致性
- [ ] 返回裸 `String` 的端点是否 `@SxwlNoWrap`；返回对象的是否被统一包装（见 gotchas）。
- [ ] 分页/列表响应结构（字段名、嵌套）是否前后端一致。
- [ ] 错误码约定是否被前端正确处理。
