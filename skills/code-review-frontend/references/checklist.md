# 前端审查详细清单（code-review-frontend）

## 1. React Hook 规则
- [ ] `useEffect/useMemo/useCallback` 的 deps 是否包含全部用到的外部变量（否则陈旧值）；是否包含不稳定引用导致无限循环。
- [ ] 是否在条件分支/`if`/循环内调用 hook（必须顶层调用）。
- [ ] `useState` 初始值是否为重计算（应 `useState(() => heavy())`）。
- [ ] 自定义 hook 是否返回稳定引用、是否正确处理清理。

## 2. 内存与资源泄漏
- [ ] `new EventSource(url)` / `new WebSocket(url)`：是否在 `useEffect` cleanup 中 `close()`/`removeEventListener`。
- [ ] `window/document` 的 `addEventListener` 是否在卸载时 `removeEventListener`（同引用）。
- [ ] `setInterval`/`setTimeout` 是否 `clear`；`setTimeout` 引用是否在卸载后取消。
- [ ] SSE `onmessage/onerror/onopen` 是否用 `addEventListener` 以便精准移除。
- [ ] 订阅（如状态库 `subscribe`、事件总线）是否退订。

## 3. API 错误处理
- [ ] 每个 api 调用是否有 `catch`/错误分支；是否至少 reset `loading=false`。
- [ ] 401 是否触发登出/刷新 token；403 是否提示无权限；5xx 是否提示服务异常。
- [ ] 是否吞掉错误（`catch(() => {})`）导致界面卡在 loading。
- [ ] 列表请求是否处理空数组与分页末尾。

## 4. key 与列表渲染
- [ ] `.map(...)` 是否提供稳定唯一 `key`（非数组 index，除非静态列表）。
- [ ] 动态列表用 index 作 key 是否导致输入框/状态错乱。

## 5. 类型安全
- [ ] 是否大量 `any`；接口（TS interface/type）是否与后端响应结构一致。
- [ ] 危险的 `as` 断言（掩盖真实类型）。
- [ ] `JSON.parse` 结果是否断言为具体类型。

## 6. 权限与按钮
- [ ] 按钮 `perms` 字符串是否与后端 `@SxwlRequiresPermissions` 完全一致（含共享 perms 场景）。
- [ ] 无权限时是否 `hidden`/`disabled`；路由是否有权限守卫。
- [ ] 前端权限判断是否可被篡改绕过（仅后端为准，前端隐藏只是 UX）。

## 7. 状态管理
- [ ] 是否在 `useEffect` 中依赖过期闭包（需把状态放入 deps 或用 ref）。
- [ ] 并发请求竞态：先发后到的响应是否覆盖正确数据（用请求序号/AbortController）。
- [ ] 全局状态是否污染、是否不必要地触发重渲染。

## 8. 健壮性与 UX
- [ ] 数据加载三态：loading / empty / error 是否都覆盖。
- [ ] 大列表是否虚拟滚动或分页。
- [ ] 表单提交是否防重复（提交中 disabled）；校验是否完整。
- [ ] 用户输入是否做长度/格式校验，避免打爆后端。

## 9. 性能与 Bundle 优化
- [ ] 路由是否使用 `React.lazy()` 懒加载；首屏 Bundle 是否过大（>2MB 需关注）。
- [ ] 第三方库（如 G2、ECharts）是否按需引入而非全量引入。
- [ ] 图片/图标是否优化（小图用 SVG sprite；大图是否压缩）。
- [ ] `useMemo`/`useCallback` 是否用在重计算/重渲染场景；是否滥用导致反优化。

## 10. 可访问性 (A11y)
- [ ] 关键交互元素是否有 `aria-label` 或 `title`。
- [ ] 表单控件是否与标签关联（`htmlFor`/`id`）。
- [ ] 颜色对比度是否满足 WCAG AA（尤其是按钮文字、链接）。

## 11. 浏览器兼容性
- [ ] 是否使用了浏览器不支持的 API（查 CanIUse 表）。
- [ ] Vite `esbuild` 目标浏览器（`build.target`）是否合理（现代浏览器 vs 兼容旧版）。
- [ ] Polyfill 是否缺失（如 `Promise`、`fetch` 在低版本浏览器）
