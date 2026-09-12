# 本项目（sxwl-react）特有坑位验证表

## F1. SSE / EventSource 重连风暴
- 现象：日志中 `/sse/ticket` 每 1–2s 一次、每次 ticket 不同 → 前端 `onerror` 无退避地重连并反复申请一次性 ticket。
- 正确约定（`useSSE.ts` / `useMonitorSSE.ts`）：
  - `EventSource` 在 `useEffect` cleanup 中 `close()`。
  - `/sse/connect` 失败时 `onerror` 必须**指数退避或设置最大重试次数**，不得固定 1s 轮询。
  - ticket 为一次性，连接失败不应无限申请新 ticket（或限制重试上限并报错提示）。
- 检查：`useSSE`/`useMonitorSSE` 的重连逻辑；是否对 `EventSource` 做了关闭；重连间隔是否写死。

## F2. 权限串与按钮/路由映射
- 共享权限：`system:log:list`（登录日志、操作日志页面）、`monitor:job:list`（定时任务、任务日志页面）。
- 检查：前端按钮/页面 `perms` 是否等于后端控制器注解；菜单种子 SQL 中对应按钮 perms 是否与前端一致；缺失权限的按钮是否会被误放行。

## F3. 统一请求封装
- 检查：所有 api 是否走统一 `request`（处理 token、错误码、登出）；是否存在裸 `fetch`/`axios.get` 绕过拦截（会导致 401 不跳转、错误不提示）。
- SSE 的 ticket 获取接口（`/sse/ticket`）是否走统一封装以获取 Authorization。

## F4. 路由与鉴权守卫
- 检查：路由级权限（如 `Access/Authorized` 组件）是否使用正确的 perms 字段；与 `menu_seed_full.sql` 的 `perms` 是否对应。
- 动态菜单：前端路由是否来自后端菜单接口；菜单接口返回结构与前端解析是否一致。

## F5. 表单/表格与后端分页契约
- 检查：前端分页参数（page/pageSize 或 current/size）、排序字段是否与后端 Controller/SQL 一致；`SxwlPage`/分页响应字段名是否匹配。
- 批量操作（删除/导出）是否把 id 数组正确传给后端，且后端做了归属校验（防 IDOR）。

## F6. G2 图表渲染内存泄漏
- `SxwlChart` 在组件卸载时是否正确销毁图表实例（`chart.destroy()`）。
- 数据更新时是否重复创建 chart 实例而不清理旧实例 → 内存泄漏。
- 检查：`useEffect` cleanup 是否调用 `destroy()`；动态数据刷新是否先清除后重建。

## F7. Ant Design 6.5 API 废弃
- `destroyOnClose` 已废弃 → 改用 `destroyOnHidden`（Modal、Drawer）。
- `List` 组件已弃用 → 改用 `Table` 或自定义列表。
- 生命周期钩子变化（如 `componentWillReceiveProps` 移除）→ 改用 `useEffect`。
- 检查：全量搜索废弃 API 使用情况；逐个替换。

## F8. TypeScript 类型严格
- 禁止使用 `any`；确需动态类型时使用 `unknown` + 类型守卫。
- 后端响应结构必须匹配 `SxwlResult<T>`；不得擅自放宽类型。
- 接口字段名与后端保持一致（驼峰 vs 下划线）；不一致时明确转换层。
- 危险的 `as` 断言（掩盖真实类型）→ 改用 `interface`/`type` 定义。

## F9. Zustand 状态管理陷阱
- 闭包陈旧值：`useEffect` 中依赖的 state 未放入 deps →  stale state。
- 竞态条件：快速切换请求（如快速翻页）→ 后发先至覆盖正确数据。
- 全局状态污染：store 方法未做不可变更新 → 不触发重渲染或状态错乱。
- 检查：所有 `useState`/`useEffect` 的 state 是否放入 deps；异步回调是否用 ref 或序号。
