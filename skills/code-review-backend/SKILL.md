---
name: code-review-backend
description: This skill should be used when reviewing Java/Spring Boot backend code (especially com.sxwl.* services) for production readiness. It enforces strict checks on authentication/authorization, SQL injection, transaction integrity, null-safety, concurrency, exception handling, security-sensitive logging, and resource leaks. Trigger on requests to "review backend code", "审计后端", "生产前检查", or when auditing any controller/service/mapper under sxwl-boot before release.
---

# Code Review Backend (严格后端审查)

## Overview

对 Spring Boot 后端做**生产级严格审查**。以"能上线吗、会在生产炸吗、会被攻破吗"为标准，逐条核对控制器、服务、Mapper、配置。本技能不追求覆盖率数字，只产出**可复现、有修复建议、带严重级别**的发现。

## When To Use

- 发布/上线前对后端做完整或分模块审查。
- 用户请求"审查后端""找 bug""生产前检查""安全审计"。
- 审查范围为 `sxwl-boot/**/*.java`、`**/resources/**/*.xml`（MyBatis SQL）、`**/resources/sql/*.sql`。

## Severity Definitions（务必在报告中标注）

- **BLOCKER（阻断）**：上线必炸或必被攻破。如 SQL 注入、越权、事务丢失导致脏数据、SSE 连接泄漏。
- **HIGH（高）**：高概率生产事故或严重安全弱点。如 NPE 在主干路径、并发不安全、敏感信息落日志。
- **MEDIUM（中）**：边界/异常场景出错、可维护性/正确性隐患。
- **LOW（低）**：规范/健壮性改进，不影响功能。

## Review Workflow（SOP）

1. **圈定范围**：明确本次审查的模块/包（如 `auth`、`system`、`sse`、`rustfs`）。全量审查时按模块逐个执行本 SOP，避免一次塞满上下文。
2. **读入口**：从 Controller 入手，列出每个端点的方法、HTTP 方法、URL、返回类型、权限注解。
3. **逐条核对清单**：按下方 9 大类 + `references/checklist.md` 详细条目，对每个文件扫描。
4. **核对项目特有机坑位**：加载 `references/sxwl-gotchas.md`，逐条验证本项目已知易错点。
5. **交叉验证**：权限串需在 "Controller 注解 / 前端 perms / 菜单种子 SQL" 三处一致；SQL 需在 `base.sql` 与 `sxwl_project_v1.sql` 一致。
6. **只报确凿项**：每条发现给出 文件:行号、问题描述、复现/触发条件、修复建议。不要臆测。
7. **输出报告**：按下方"Report Format"汇总。

## Checklist（9 大类，完整条目见 references/checklist.md）

1. **鉴权与授权**：每个写/读敏感数据的端点是否有 `@PreAuthorize`/`@SxwlRequiresPermissions`；白名单(`SxwlSecurityConfig`)是否误放行；SSE/ticket 端点是否绕过鉴权（见 gotchas）。
2. **SQL 注入**：MyBatis 中 `${}` 是否拼接外部输入；`ORDER BY`/`LIMIT` 动态参数；字符串拼接 SQL。
3. **事务完整性**：跨表/跨服务的写操作是否 `@Transactional`；自调用(`this.xxx()`)导致代理失效；catch 吞异常导致不回滚；`@Transactional` 标在 private/final/非 public 方法。
4. **空指针与 Optional**：外部输入、Map.get、链式调用、JSON 字段缺失；`Optional.get()` 无守卫。
5. **并发与线程安全**：静态可变字段、共享 `HashMap`/`SimpleDateFormat`、SSE emitter 容器并发、线程池/异步任务。
6. **异常处理**：`catch (Exception)` 吞掉；返回 null 给前端；未统一包装为 `SxwlResult`。
7. **安全敏感**：密码/token/密钥写入日志；路径穿越（文件模块）；IDOR（操作非本人资源）；XSS 存储型。
8. **资源泄漏**：流/Reader/Connection 未关；SSE `SseEmitter` 未设超时/未 `complete`/`onCompletion` 清理；线程池未 shutdown。
9. **API 契约一致性**：返回类型与 `@SxwlNoWrap` 的搭配（见 gotchas）；响应结构前后端一致。

## Project-Specific Gotchas（本项目特有，详见 references/sxwl-gotchas.md）

- **SxwlResult vs String + @SxwlNoWrap**：返回裸 `String` 且被 `SxwlResponseBodyAdvice` 包成 `SxwlResult` 会 `ClassCastException`（`StringHttpMessageConverter` 冲突）。规则：`/sse/connect` 返回 `SseEmitter`+`@SxwlNoWrap`；`/sse/ticket` 返回 `SxwlResult<String>`。禁止混用 `String` 返回类型 + 包装。
- **权限串共享**：`system:log:list` 被登录/操作日志共用，`monitor:job:list` 被定时任务/任务日志共用——检查唯一约束改组合索引 `(id, perms)` 后种子 SQL 是否一致。
- **SQL 漂移**：`base.sql` 与 `sxwl_project_v1.sql` 的 `uk_sys_menu_perms` 定义不同，部署以 v1 为准，但需标注不一致。
- **@PostConstruct 启动同步**：Quartz 定时任务同步建议使用 `@PostConstruct`（而非 `ApplicationRunner`），确保 Spring 容器完全初始化后再执行同步逻辑。示例：`SysJobInfoServiceImpl.syncActiveJobsToQuartz()` 使用幂等设计（Quartz.checkExists 会跳过已存在任务）。
- **批量操作事务性**：批量删除/更新操作的 `@Transactional` 必须标在 Service 层方法（不在 Controller 层）；批量 SQL 需考虑分批提交避免长事务。
- **IP 地址脱敏**：在线用户 IP 输出前必须脱敏（如 `192.168.1.100` → `192.168.*.**`）；DTO 提供 `getIp()`（脱敏）和 `getRawIp()`（仅日志使用）；禁止在日志/响应中直接输出原始 IP。

## Report Format

```
## 后端审查报告 — <模块/范围> — <日期>
范围：<文件/包>
结论：可上线 / 需修复 BLOCKER 后上线

### BLOCKER
- [B1] 文件:行 — 问题 — 触发 — 修复

### HIGH
- [H1] ...

### MEDIUM / LOW
- ...
```

加载 `references/checklist.md` 获取逐条检查细则；加载 `references/sxwl-gotchas.md` 获取本项目已知坑位验证表。
