---
name: code-review-frontend
description: This skill should be used when reviewing React/TypeScript frontend code (especially sxwl-react/src) for production readiness. It enforces strict checks on React Hook rules, memory/resource leaks (EventSource/SSE, listeners, timers), error handling of API calls, key warnings, type safety, and permission-button-to-backend mapping consistency. Trigger on requests to "review frontend code", "审查前端", "生产前前端检查", or when auditing any component/hook/api under sxwl-react/src before release.
---

# Code Review Frontend（严格前端审查）

## Overview

对 React/TypeScript 前端做**生产级严格审查**。标准：会不会内存泄漏、会不会白屏、API 错误会不会吞掉、权限按钮对不对得上后端、类型是否形同虚设。逐条核对组件、hooks、api 层、状态管理。

## When To Use

- 发布/上线前对前端做完整或分目录审查。
- 用户请求"审查前端""前端找 bug""生产前检查"。
- 审查范围限定 `sxwl-react/src/**`（**排除 `node_modules`**，否则噪音爆炸）。

## Severity Definitions

- **BLOCKER**：上线即白屏/崩溃/无限请求/越权可用。如 SSE 未关闭导致重连风暴、未捕获异常使页面崩溃、权限校验缺失。
- **HIGH**：高概率线上问题。如 API 错误未处理导致卡死、内存泄漏（监听器/定时器未清理）、`key` 缺失引发渲染错乱。
- **MEDIUM**：边界/异常 UI 问题、类型不安全。
- **LOW**：规范/健壮性改进。

## Review Workflow（SOP）

1. **圈定范围**：按目录（如 `src/api`、`src/hooks`、`src/layouts`、`src/pages`）或按页面逐个执行，避免一次塞满上下文。
2. **读入口**：从路由/页面组件入手，列出用到的 hooks、api 调用、权限按钮。
3. **逐条核对清单**：按下方 8 大类 + `references/checklist.md` 扫描。
4. **核对项目特有机坑位**：加载 `references/sxwl-gotchas.md`（含 SSE ticket 重连、权限 perms 映射）。
5. **交叉验证权限**：前端按钮 `perms` / 路由 / 后端 `@SxwlRequiresPermissions` / 菜单种子 SQL 四处一致。
6. **只报确凿项**：文件:行号 + 问题 + 触发 + 修复建议。
7. **输出报告**（格式见下）。

## Checklist（8 大类，细则见 references/checklist.md）

1. **React Hook 规则**：deps 数组是否完整/过量；`useState`/`useEffect`/`useMemo`/`useCallback` 误用；条件/循环内调用 hook。
2. **内存与资源泄漏**：`EventSource`/`WebSocket` 是否在卸载时 `close()`；`addEventListener`/`setInterval`/`setTimeout` 是否清理；SSE 重连是否收敛。
3. **API 错误处理**：`try/catch` 或 `.catch` 是否处理错误；loading/disabled 状态是否复位；401/403 是否跳转登录；网络错误是否提示。
4. **key 与列表渲染**：`map` 是否带稳定 `key`；用 index 作 key 的风险。
5. **类型安全**：`any` 滥用、未断言的 `as`、接口与后端响应结构不符。
6. **权限与按钮**：按钮 `perms` 是否匹配后端实际权限串；无权限时是否隐藏/禁用；路由级鉴权。
7. **状态管理**：全局状态误用、闭包陈旧值、竞态（快速切换请求顺序）。
8. **健壮性与 UX**：空/加载/错误三态；大列表虚拟滚动；表单校验；防重复提交。

## Project-Specific Gotchas（详见 references/sxwl-gotchas.md）

- **SSE ticket 重连风暴**：`useSSE`/`useMonitorSSE` 中若 `/sse/connect` 失败，`onerror` 必须指数退避或上限重连；不得每 1–2s 无脑重连并反复申请 ticket（已在日志观察到）。
- **权限串共享**：`system:log:list`（登录/操作日志）、`monitor:job:list`（定时任务/任务日志）前端按钮需与后端一致。
- **请求封装**：统一 `request` 是否已处理 token 注入、错误码、登出；新 api 文件是否复用而非裸 `fetch`/`axios`。
- **G2 图表渲染**：`SxwlChart` 在数据更新时是否正确销毁旧实例；避免内存泄漏。
- **Ant Design 6.5 API 废弃**：`destroyOnClose` 已废弃改用 `destroyOnHidden`；`List` 组件已弃用；生命周期钩子变化。
- **TypeScript 类型严格**：禁止使用 `any`；后端响应结构必须匹配 `SxwlResult<T>`；接口字段名与后端保持一致。
- **Zustand 状态管理**：闭包陈旧值问题；竞态条件（快速切换请求顺序）；全局状态是否污染。
- **useMemo/useCallback 滥用**：不必要的 memo 反而降低性能；小组件无需过度优化；仅在重渲染明显时启用。
- **SxwlResult<T> 类型严格**：禁止使用 `as any` 或 `as unknown as T` 放宽后端响应类型；字段名必须与 Java DTO 一致（驼峰转换）。
- **进度条 UI 实现**：分片上传需可视化进度展示（整体进度 + 分片进度）；使用 `useState` 存储 `progress` 状态；Ant Design `Progress` 组件封装。

## Report Format

```
## 前端审查报告 — <目录/页面> — <日期>
范围：<文件>
结论：可上线 / 需修复 BLOCKER 后上线

### BLOCKER
- [B1] 文件:行 — 问题 — 触发 — 修复
### HIGH / MEDIUM / LOW
- ...
```
