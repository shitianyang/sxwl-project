---
name: performance-optimize
description: This skill should be used when analyzing and optimizing full-stack performance, covering backend slow SQL, database indexing, JVM tuning, frontend Bundle optimization, rendering performance, and caching strategies. It encodes project-specific performance patterns discovered in this codebase. Trigger on requests to "performance optimization", "slow query analysis", "page卡顿", "Bundle太大", "内存泄漏排查", or when investigating performance bottlenecks (slow page load, high CPU/memory, database timeout).
---

# Performance Optimize（全栈性能优化）

## Overview

对 sxwl 全栈项目进行**性能分析与优化**。标准：能不能扛住并发？慢查询在哪里？内存是否泄漏？Bundle 是否过大？逐层检查数据库查询、API 响应、前端渲染、缓存策略。本技能不追求覆盖率数字，只产出**可复现、有量化指标、带修复建议**的发现。

## When To Use

- 页面/接口响应慢（≥ 1s 需排查）。
- 用户报告"卡顿""加载慢""超时"。
- 发布前性能压测/瓶颈分析。
- 审查范围：慢SQL日志、MyBatis 执行计划、JVM GC 日志、前端 Bundle 分析、Chrome DevTools 性能面板。

## Severity Definitions（务必在报告中标注）

- **BLOCKER（阻断）**：上线必导致严重卡顿/超时/OOM。如 N+1 查询全表扫描、内存泄漏无上限、Bundle > 5MB 首屏 > 10s。
- **HIGH（高）**：高概率性能瓶颈。如缺少索引导致全表扫描、频繁 Full GC、重复渲染大列表。
- **MEDIUM（中）**：边界场景性能问题、可优化空间大。
- **LOW（低）**：微调优化、锦上添花。

## Review Workflow（SOP）

1. **圈定范围**：明确本次优化的模块（如 auth、system、sse）或层次（数据库层、Service 层、Controller 层、前端渲染）。全量优化时按层次逐个执行本 SOP。
2. **读取入口**：从慢SQL日志（`SxwlSqlMonitorInterceptor`）、Spring Boot Actuator `/metrics`、前端 Bundle Analyzer 入手。
3. **逐条核对清单**：按下方 8 大类 + `references/checklist.md` 详细条目，对每个层次扫描。
4. **核对项目特有性能坑位**：加载 `references/sxwl-gotchas.md`，逐条验证本项目已知性能弱点。
5. **交叉验证**：后端查询耗时 ≠ API 总耗时（含序列化/网络）；前端渲染耗时 ≠ 数据加载耗时（含组件树深度）。
6. **只报确凿项**：每条发现给出 文件:行号、当前性能指标（耗时/CPU/内存）、对比数据（优化前后）、修复建议。
7. **输出报告**：按下方"Report Format"汇总。

## Checklist（8 大类，完整条目见 references/checklist.md）

### 1. **数据库查询性能**
- MyBatis 慢SQL分析：`EXPLAIN` 执行计划是否走索引；是否存在全表扫描。
- N+1 查询优化：循环内单次查询 → 批量查询（`IN` + Map 分组）；`@Select` 用 JOIN 替代多次查询。
- PageHelper 分页优化：是否在所有列表查询使用分页；分页是否加排序避免结果不稳定。
- 联合查询：JOIN 是否过多（> 3 表）；子查询是否可改为 JOIN；临时表/文件排序优化。
- 统计查询：COUNT/SUM/AVG 是否有覆盖索引；预计算聚合表（如报表数据）。

### 2. **索引设计与优化**
- 单列索引：WHERE/JOIN/GROUP BY 字段是否建索引；选择性高（去重多）的字段优先。
- 组合索引：最左前缀原则（如 `(org_id, user_type)` 可加速 `org_id` 单独查询）。
- 覆盖索引：`SELECT id, name FROM user WHERE org_id = ?` → 建 `(org_id, id, name)` 避免回表。
- 唯一索引：业务唯一约束（如 `username + org_id`）→ 防重复数据 + 加速查询。
- 避免过度索引：写入频繁表索引 ≤ 5；更新/删除性能下降。

### 3. **后端架构与 JVM 性能**
- HikariCP 连接池调优：`maximum-pool-size` 是否合理（CPU 核心数 × 2 + 磁盘队列）；`idle-timeout` 是否过短导致频繁重建。
- Spring Boot 启动优化：延迟初始化（`spring.lazy-init=true`）；非核心 Bean 懒加载。
- JVM GC 调优：G1GC vs ParallelGC；Heap 大小（Xms=Xms）；Young/Old Gen 比例；Full GC 频率（≤ 1次/小时）。
- 对象复用：避免循环内 `new` 大量对象（如字符串拼接用 `StringBuilder`）；静态缓存（`ConcurrentHashMap`）。
- 异步处理：`@Async` / 线程池复用（非每次 `Executors.newThreadPool`）；事件驱动（`ApplicationEventPublisher`）。

### 4. **缓存策略与实现**
- Redis 缓存命中率：热点数据（字典、菜单、权限）缓存；过期时间合理（不过短/过长）。
- 缓存穿透：空值缓存（`null` + 短过期）；布隆过滤器（防恶意查询不存在的数据）。
- 缓存击穿：分布式锁（`Redisson`）保护热点 Key 重建；逻辑过期（后台异步刷新）。
- 缓存雪崩：随机过期时间（±10%）；降级方案（本地 Caffeine 缓存 + Redis 双级）。
- 缓存一致性：写操作先删后查（Cache-Aside 模式）；`@TransactionalEventListener` 事务后异步更新缓存。

### 5. **前端 Bundle 与加载性能**
- Bundle 分析：`vite build --mode analyze` 分析包大小；第三方库按需引入（非全量）。
- 路由懒加载：`React.lazy(() => import('./Page'))`；首屏 Bundle ≤ 2MB；二三级路由按需加载。
- Tree-shaking：标记纯函数（`sideEffects: false` in `package.json`）；消除未使用代码。
- 图片/图标优化：小图 SVG sprite（`svg-sprite-loader`）；大图 WebP 格式 + 压缩；懒加载（`loading="lazy"`）。
- CDN 加速：静态资源（JS/CSS/图片）部署 CDN；版本化文件名（`[contenthash]`）长期缓存。

### 6. **前端渲染性能**
- React 渲染优化：`useMemo` 缓存重计算；`useCallback` 稳定引用避免子组件重渲染；`React.memo` 浅比较。
- 大列表虚拟滚动：`react-virtualized` / `rc-virtual-list`；只渲染可视区域（100 条渲染 10 条 DOM）。
- 防抖/节流：搜索框输入（300ms 防抖）；滚动/resize（200ms 节流）；避免同步高频回调。
- 代码分割：动态导入（`import()`）；路由级/模块级分割；减少单 Chunk 大小。
- SSR/SSG：首屏 SEO 需求考虑 Next.js/Nuxt（本项目 Vite SPA 可不适用，但关注 hydration 耗时）。

### 7. **网络传输与协议优化**
- HTTP/2 多路复用：服务端支持 HTTP/2 → 并发请求无需合并；Header 压缩。
- Gzip/Brotli 压缩：文本类（JS/CSS/JSON）压缩率 ≥ 70%；压缩耗时 vs 带宽权衡。
- 接口响应最小化：DTO 只返回必要字段；列表不嵌套深层结构；分页限制 pageSize。
- WebSocket/SSE 优化：推送频率限制（如每秒最多 1 条）；消息批处理（100ms 窗口合并）。
- 连接复用：HTTP Keep-Alive；Redis 连接池（非每次新建连接）；数据库连接池。

### 8. **监控与性能指标**
- 后端监控：Actuator `/metrics`（JVM Heap、线程数、连接池状态）；Slow Query Log（≥ 500ms 告警）。
- 前端监控：LCP（Largest Contentful Paint ≤ 2.5s）、FID（First Input Delay ≤ 100ms）、CLS（Cumulative Layout Shift ≤ 0.1）。
- Chrome DevTools：Performance 面板录制（找 Long Tasks > 50ms）；Memory 面板（Heap Snapshot 查泄漏）。
- APM 工具：SkyWalking/Jaeger 链路追踪（标注慢节点：DB/HTTP/外部服务）。
- 压测基准：K6/JMeter 模拟并发（TPS、平均响应时间、99th 分位数、错误率 ≤ 0.1%）。

## Project-Specific Gotchas（详见 references/sxwl-gotchas.md）

- **PageHelper 分页缺失**：部分列表查询未加 PageHelper → 全表加载 → 内存溢出/前端卡顿；必须分页。
- **MyBatis N+1 查询**：循环内多次 `selectById` → O(1) 变 O(n²)；改用 `IN` + Map 分组。
- **SSE 连接数无上限**：全局最大在线用户数未限制 → 连接堆积 → 内存/CPU 耗尽；设上限 + idle 踢出。
- **Redis 缓存穿透**：查询不存在的数据（如已删除资源 ID）→ 每次都打 DB → 布隆过滤器/空值缓存。
- **前端 Bundle 过大**：全量引入 ECharts/G2 → 按需引入（`import Chart from 'echarts/core/EChart'`）；Tree-shaking。
- **HikariCP 连接泄漏**：`maximum-pool-size` 过小（默认 10）→ 并发请求排队 → 设置监控 + 调整。

## Report Format

```markdown
## 性能优化报告 — <范围> — <日期>
范围：<文件/包/层次>
结论：瓶颈定位 + 预期优化空间

### BLOCKER（立即优化）
- [P-B1] 文件:行 — 当前指标（耗时/内存）— 根因 — 优化方案 — 预期提升

### HIGH（严重）
- [P-H1] ...

### MEDIUM（中等）/ LOW（建议）
- ...

## 性能基准测试（优化前后对比）
| 指标 | 优化前 | 优化后 | 提升幅度 |
|------|--------|--------|----------|
| API 平均响应时间 | 1.2s | 200ms | 83% ↓ |
| 首屏加载时间 | 8s | 2.5s | 69% ↓ |
| Bundle 大小 | 4.5MB | 1.8MB | 60% ↓ |
| CPU 峰值 | 85% | 35% | 59% ↓ |
```

加载 `references/checklist.md` 获取逐条检查细则；加载 `references/sxwl-gotchas.md` 获取本项目已知性能弱点验证表。
