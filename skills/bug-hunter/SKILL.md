---
name: bug-hunter
description: This skill should be used when hunting cross-cutting bugs across the full-stack sxwl project (backend sxwl-boot + frontend sxwl-react). It encodes project-specific failure patterns discovered in this codebase and runs targeted searches to confirm/find them, producing a prioritized bug list. Trigger on requests to "找 bug", "排查问题", "全栈 bug 审查", "生产前排雷", or when investigating recurring anomalies (e.g. request storms, connection leaks, duplicate-key errors).
---

# Bug Hunter（跨端 Bug 猎人）

## Overview

针对本项目（sxwl 全栈）做**跨前后端的定向 bug 排查**。不泛泛扫描，而是基于"本项目已知会出问题的模式"做精准搜索与验证，确认是否仍存在问题、是否扩散到别处。

## When To Use

- 用户报告某个异常现象（如请求风暴、连接泄漏、唯一键报错、白屏），需要定位根因并排查同类。
- 用户请求"找 bug""排雷""全栈审查"。
- 作为 `code-review-backend` / `code-review-frontend` 的补充：把发现的项目特有坑位落成可复用的搜索模式。

## Workflow（SOP）

1. **确定嫌疑模式**：从 `references/project-gotchas.md` 选取相关模式，或根据用户描述的现象归纳。
2. **定向搜索**：用 `search_content`/`search_file` 在 `sxwl-boot` 与 `sxwl-react/src` 中检索该模式的代码特征（给出具体正则/关键字）。
3. **读取确认**：对有嫌疑的文件用 `read_file` 确认是否真有问题（避免误报）。
4. **扩散排查**：同一模式是否在其他模块重复出现（如多个端点都误用 `String` 返回 + 包装）。
5. **交叉验证**：前后端对该 bug 的处理是否一致（如后端 SSE 修好但前端仍无退避）。
6. **输出报告**：按严重级别列出，每条含 文件:行号、现象、根因、影响范围、修复建议。

## Search Patterns（关键正则，详见 references/project-gotchas.md）

- SSE 包体冲突：`return ".*"` 且方法返回类型非 `SxwlResult` 又非 `@SxwlNoWrap`。
- 一次性 ticket 重连：前端 `onerror` 无退避 + 每轮申请 ticket；`setInterval` 轮询 `/sse/ticket`。
- 唯一约束漂移：`uk_sys_menu_perms` 在 `base.sql` 与 `sxwl_project_v1.sql` 是否一致。
- 权限不一致：前端 `perms:"..."` 与后端 `@SxwlRequiresPermissions("...")` 及 `menu_seed_full.sql` 是否三/四处对齐。
- SQL 注入：`\$\{` 在 Mapper XML 中是否拼接外部输入。
- 资源泄漏：`new EventSource`/`new WebSocket`/`addEventListener`/`setInterval` 是否缺 cleanup。
- 事务自调用：`this\.` 调用同 Bean 的 `@Transactional` 方法。

## Report Format

```
## Bug 排查报告 — <现象/范围> — <日期>
根因：<一句话>
影响范围：<模块/文件>

### BLOCKER
- [B] 文件:行 — 根因 — 修复
### HIGH / MEDIUM / LOW
- ...
```

加载 `references/project-gotchas.md` 获取完整模式库与对应搜索正则。
