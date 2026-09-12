# 性能优化详细清单（performance-optimize）

## 1. 数据库查询性能

### MyBatis 慢SQL分析
- [ ] 所有 SQL 是否经过 `EXPLAIN` 验证执行计划（走索引、无全表扫描）。
- [ ] 慢SQL日志阈值：建议 ≥ 500ms 告警；`SxwlSqlMonitorInterceptor` 已启用监控。
- [ ] 复杂查询拆分为多次简单查询 + 应用层组装（避免多表 JOIN 性能瓶颈）。
- [ ] 子查询 → JOIN 转换（如 `WHERE id IN (SELECT order_id FROM orders)` → `LEFT JOIN orders`）。

### N+1 查询优化
- [ ] 循环内单次查询（如 `for user in users: selectById(user.deptId)`）→ O(n) 变 O(1)。
- [ ] 批量查询：改用 `IN` + Map 分组（`selectByIds(deptIds)` → 内存关联）。
- [ ] `@Select` 用 JOIN 替代多次查询（如用户列表 JOIN 部门表一次性返回）。
- [ ] Lazy Loading 禁用（MyBatis 延迟加载易导致 N+1；改用 Eager Loading）。

### PageHelper 分页优化
- [ ] **所有列表查询必须分页**（不分页 = BLOCKER）；前端请求必带 page/pageSize。
- [ ] 分页加排序（`ORDER BY create_time DESC`）→ 避免结果不稳定（同页数据跳动）。
- [ ] PageHelper 是否在所有查询前调用（`PageHelper.startPage(page, size);`）。
- [ ] 总条数优化：大数据量（> 100万）不用 `COUNT(*)` → 用近似值（`EXPLAIN` 统计行数）。

### 联合查询优化
- [ ] JOIN 数量：≤ 3 表 JOIN；> 3 表考虑拆分/物化视图/宽表设计。
- [ ] JOIN 字段是否建索引（ON 条件字段必须索引）。
- [ ] 临时表/文件排序：`EXPLAIN Extra` 出现 `Using filesort` / `Using temporary` → 需索引优化。
- [ ] 选择最优 JOIN 顺序（小表驱动大表；MySQL Optimizer 可调整但不可靠）。

### 统计查询优化
- [ ] COUNT/SUM/AVG 是否有覆盖索引（无需回表查主键索引）。
- [ ] 预计算聚合表：报表/仪表盘数据定时刷新（非实时查询）。
- [ ] 分区表：超大数据量按时间/部门分区（减少扫描范围）。

---

## 2. 索引设计与优化

### 单列索引
- [ ] WHERE 字段是否建索引（如 `WHERE org_id = ?` → `INDEX idx_org_id (org_id)`）。
- [ ] JOIN 字段是否建索引（如 `JOIN dept ON user.dept_id = dept.id` → `dept_id` 索引）。
- [ ] GROUP BY / ORDER BY 字段是否建索引（避免 filessort）。
- [ ] 选择性高（去重多）的字段优先建索引（如 username 去重 99% > gender 去重 50%）。

### 组合索引
- [ ] 最左前缀原则：`(org_id, user_type)` 索引可加速 `org_id` 单独查询，但不可加速 `user_type` 单独查询。
- [ ] 等值查询字段放前面（`WHERE org_id = ? AND type = ?` → `(org_id, type)`）。
- [ ] 范围查询字段放后面（`WHERE org_id > ? AND create_time > ?` → `create_time` 放后，否则范围查询后失效）。

### 覆盖索引
- [ ] `SELECT id, name FROM user WHERE org_id = ?` → 建 `(org_id, id, name)` 避免回表（仅从索引取数据）。
- [ ] 覆盖索引显著减少 I/O（InnoDB 聚簇索引，非聚集索引存主键需回表）。
- [ ] 注意覆盖索引空间开销（每多一列 × 记录数存储开销）。

### 唯一索引
- [ ] 业务唯一约束（如 `username + org_id`）→ 防重复数据 + 加速联合查询。
- [ ] 唯一索引 ≠ 主键索引：主键聚簇存储；唯一索引非聚簇（额外开销）。

### 避免过度索引
- [ ] 写入频繁表索引 ≤ 5（每次 INSERT/UPDATE 需更新所有索引 B+树）。
- [ ] 删除无用索引（`SHOW INDEX FROM table` → 监控未使用索引）。
- [ ] 组合索引替代多个单列索引（如 `(org_id, type)` 替代 `idx_org_id` + `idx_type`）。

---

## 3. 后端架构与 JVM 性能

### HikariCP 连接池调优
- [ ] `maximum-pool-size` 是否合理：CPU 核心数 × 2 + 磁盘队列数（默认 10 可能过小）。
- [ ] `idle-timeout` 不宜过短（如 ≤ 10s）→ 频繁重建连接；建议 ≥ 30s。
- [ ] `max-lifetime` ≤ 数据库 `wait_timeout`（如 MySQL 默认 28800s → 设 1800s 防防火墙断连）。
- [ ] 监控活跃连接数（Actuator `/metrics/hikaricp.connections.active`）→ 持续 100% 说明瓶颈。

### Spring Boot 启动优化
- [ ] 延迟初始化（`spring.lazy-init=true`）→ 非核心 Bean 懒加载（缩短启动时间）。
- [ ] 减少自动配置类扫描（`exclude` 不用的 AutoConfiguration）。
- [ ] Profile 隔离（dev 环境禁用 Quartz/邮件发送等非必要模块）。

### JVM GC 调优
- [ ] GC 算法选择：G1GC（低延迟，推荐 Java 17+）vs ParallelGC（高吞吐）。
- [ ] Heap 大小：`-Xms4g -Xmx4g`（相等避免动态扩容）；物理内存 ≤ 50% 给 JVM。
- [ ] Young/Old Gen 比例：G1GC 自动管理；ParallelGC 用 `-XX:NewRatio`（默认 Old = Young × 2）。
- [ ] Full GC 频率：≤ 1次/小时 → 检查内存泄漏/对象生命周期过长/Heap 过小。
- [ ] GC 日志：`-Xlog:gc*:file=/var/log/gc.log:time` → 分析 GC 耗时/暂停时间。

### 对象复用
- [ ] 避免循环内 `new` 大量对象（如字符串拼接用 `StringBuilder` 而非 `+`）。
- [ ] 静态缓存（`ConcurrentHashMap`）热点数据（字典、配置）；注意过期/淘汰策略。
- [ ] BigDecimal/DecimalFormat 非线程安全 → 方法局部创建（共享导致并发错乱）。

### 异步处理
- [ ] `@Async` 任务复用线程池（非每次 `Executors.newFixedThreadPool` → 资源耗尽风险）。
- [ ] 事件驱动（`ApplicationEventPublisher`）解耦耗时操作（如操作日志异步写入）。
- [ ] 线程池异常处理：未捕获异常导致任务静默失败 → 设 `Thread.UncaughtExceptionHandler`。

---

## 4. 缓存策略与实现

### Redis 缓存命中率
- [ ] 热点数据缓存：字典、菜单、权限、用户信息；过期时间合理（不过短/过长）。
- [ ] 缓存 Key 设计规范：`namespace:id:field`（如 `sys:dict:type:100`）；避免模糊匹配（`KEYS *` 阻塞 Redis）。
- [ ] 监控命中率（`INFO stats` → `keyspace_hits` / `keyspace_misses`）→ 目标 ≥ 80%。

### 缓存穿透
- [ ] 查询不存在的数据（如已删除资源 ID）→ 每次都打 DB → 布隆过滤器预检 / 空值缓存（`null` + 短过期 5min）。
- [ ] 参数校验：ID 负数/零值直接拒绝（非查询缓存/DB）。
- [ ] 布隆过滤器：RedisBloom 模块 Guava BloomFilter → 误判率低（0.01%），空间效率高。

### 缓存击穿
- [ ] 热点 Key 过期瞬间大量请求打 DB → 分布式锁（`Redisson RLock`）保护只允许一个线程重建缓存。
- [ ] 逻辑过期：缓存中标记过期时间（非 Redis TTL）；后台异步刷新（不影响读）。

### 缓存雪崩
- [ ] 大量 Key 同时过期 → 缓存大面积失效 → DB 流量突增 → 随机过期时间（±10% 偏移）。
- [ ] 降级方案：本地 Caffeine 缓存（LRU 1000 条）+ Redis 双级（Redis 挂直接用本地）。

### 缓存一致性
- [ ] Cache-Aside 模式：写操作先删缓存 → 改 DB → 成功则 OK（下次读重建缓存）。
- [ ] 先改 DB 后删缓存（更可靠）；`@TransactionalEventListener` 事务后异步删缓存（防事务回滚不一致）。
- [ ] Canal/Trigger 订阅 Binlog 更新缓存（复杂场景；本项目可不适用）。

---

## 5. 前端 Bundle 与加载性能

### Bundle 分析
- [ ] 使用 `vite build --mode analyze` 分析包大小；识别大依赖（> 100KB 标注原因）。
- [ ] 第三方库按需引入（非全量）：
  - ❌ `import { Button, Input, Modal } from 'antd'`（全量引入）
  - ✅ `import { Button } from 'antd'; import 'antd/es/button/style/css'`（按需 + CSS）
- [ ] G2/ECharts 图标按需导入（`import Chart from '@antv/g2'` 非全量组件库）。

### 路由懒加载
- [ ] 首屏路由直接导入；二三级路由 `React.lazy(() => import('./Page'))`。
- [ ] 首屏 Bundle ≤ 2MB（JS 压缩后）；加载时间 ≤ 3s（4G 网络）。
- [ ] Prefetch：不常用路由预加载（`React.lazy` + `prefetch()` HTTP/2 推送）。

### Tree-shaking
- [ ] `package.json` 标记纯函数（`"sideEffects": ["*.css", "*.scss"]`）→ 消除未使用代码。
- [ ] ES Module 语法（`import/export`）支持 tree-shaking；CommonJS（`require/module.exports` 不支持）。
- [ ] 检查打包产物（`dist/assets/index.xxx.js`）确认未使用代码已消除。

### 图片/图标优化
- [ ] 小图标 SVG sprite（`react-icons` / 自构建 sprite）→ 合并为单个 HTTP 请求。
- [ ] 大图 WebP 格式（比 JPEG 小 25%）；压缩工具（ImageOptim/TinyPNG）。
- [ ] 懒加载（`<img loading="lazy">`）→ 可视区域外图片延迟加载（IntersectionObserver）。

### CDN 加速
- [ ] 静态资源（JS/CSS/图片）部署 CDN → 边缘节点分发（降低延迟）。
- [ ] 版本化文件名（`[contenthash]`）→ 内容变更文件名变 → 长期缓存（`Cache-Control: max-age=31536000`）。
- [ ] CDN 回源策略：命中率高设长缓存；频繁更新设短缓存 + 版本号 URL。

---

## 6. 前端渲染性能

### React 渲染优化
- [ ] `useMemo(() => computeExpensiveValue(a, b), [a, b])` → 缓存重计算结果（避免重复计算）。
- [ ] `useCallback(fn, deps)` → 稳定引用避免子组件 `React.memo` 重渲染。
- [ ] `React.memo(Component)` → 浅比较 props 相同不重渲染（注意深比较需用 `useDeepCompareMemo`）。
- [ ] 避免在 render 中定义内联对象/函数（每次渲染新引用 → 子组件重渲染）→ 提至组件外或 `useMemo`。

### 大列表虚拟滚动
- [ ] 1000 条数据渲染 1000 DOM → 卡顿 → 虚拟滚动（只渲染可视区域 10 条 DOM）。
- [ ] 库选择：`react-virtualized` / `rc-virtual-list`（Ant Design Table 已内置虚拟滚动 `scroll={{ y: 500 }}`）。
- [ ] 行高固定（虚拟滚动要求固定高度；动态高度用 `windowing` 库近似）。

### 防抖/节流
- [ ] 搜索框输入（300ms 防抖）→ 避免每次键盘事件发请求 → `useDebouncedCallback` / lodash `debounce`。
- [ ] 滚动/resize（200ms 节流）→ 限制执行频率 → `throttle` / IntersectionObserver。
- [ ] 表单失焦校验（blur 事件）→ 防抖 500ms（用户还在输入时不触发校验）。

### 代码分割
- [ ] 动态导入（`import()`）：路由级/模级分割 → 减少单 Chunk 大小。
- [ ] 条件加载：不常用功能（如导出 Excel）按需 `import('xlsx')` → 避免首屏打包。
- [ ] Vendor Chunk：第三方库分离（`splitChunks: { chunks: 'all' }`）→ 长期缓存（vendor 变化频率低）。

### Hydration 优化
- [ ] SSR/SSG 项目关注 hydration 耗时（React `hydrateRoot`）；本项目 Vite SPA 可不适用。
- [ ] 客户端首次渲染优化：预渲染 Skeleton（加载中占位）→ 数据到达后替换。

---

## 7. 网络传输与协议优化

### HTTP/2 多路复用
- [ ] 服务端支持 HTTP/2 → 并发请求无需合并（HTTP/1.1 需合并请求避并发限制）。
- [ ] Header 压缩（HPACK 算法）→ 减少 Admin 请求 Header 传输开销。
- [ ] 服务端推送（Push Promise）：关键资源（CSS/Font）提前推送（需谨慎，误用反而拖慢）。

### Gzip/Brotli 压缩
- [ ] 文本类（JS/CSS/JSON/SVG）压缩率 ≥ 70% → 带宽节省明显。
- [ ] Brotli 压缩率 > Gzip（10~15%）但耗时更长 → 权衡 CPU vs 带宽（推荐 Brotli if CPU 充足）。
- [ ] Nginx/Apache 配置压缩（`nginx gzip on;` / `brotli on;`）；二进制文件（图片/PDF）不压缩。

### 接口响应最小化
- [ ] DTO 只返回必要字段（不返回整个 Entity → 序列化开销大/响应体臃肿）。
- [ ] 列表不嵌套深层结构（如用户列表含角色 → 角色列表 → 权限列表）→ 分页/扁平化。
- [ ] 分页限制 pageSize（如 ≤ 1000）→ 防止一次性返回万级数据。

### WebSocket/SSE 优化
- [ ] 推送频率限制（如每秒最多 1 条消息）→ 前端攒批（100ms 窗口合并）。
- [ ] 消息批处理（`[id:1, data:A], [id:2, data:B]` → `[{1:A}, {2:B}]`）→ 减少序列化开销。
- [ ] 离线攒消息 →  reconnect 后补发（防丢失）；消息编号/去重。

### 连接复用
- [ ] HTTP Keep-Alive（默认开启）→ TCP 连接复用（避免每次三次握手）。
- [ ] Redis 连接池（HikariCP 类似）→ 非每次新建连接 → 延迟降低（TCP 握手 + TLS 握手）。
- [ ] 数据库连接池：`maximum-pool-size` 合理设置（监控活跃连接数）。

---

## 8. 监控与性能指标

### 后端监控
- [ ] Actuator `/metrics`（JVM Heap Used、Thread Count、Connection Pool Active）→ 设定告警阈值。
- [ ] Slow Query Log（≥ 500ms 告警）；`SxwlSqlMonitorInterceptor` 已启用 → 定期分析 Top 10 慢SQL。
- [ ] GC 监控（`jstat` / Actuator `/metrics/jvm.gc.pause`）→ Full GC 频率、Stop-the-world 时长。
- [ ] 线程 Dump（`jstack`）→ 死锁/阻塞线程分析（CPU 100% 时排查）。

### 前端监控（Web Vitals）
- [ ] LCP（Largest Contentful Paint）≤ 2.5s → 首屏内容加载完成（ Largest 图片/文字）。
- [ ] FID（First Input Delay）≤ 100ms → 用户首次交互响应时间（JS 解析完成前排队延迟）。
- [ ] CLS（Cumulative Layout Shift）≤ 0.1 → 布局偏移分数（图片无宽高 → 加载后页面跳动）。
- [ ] 其他：FCP（First Contentful Paint ≤ 2s）、TTFB（Time to First Byte ≤ 800ms）。

### Chrome DevTools
- [ ] Performance 面板录制（15s 时长）→ 找 Long Tasks（> 50ms 阻塞主线程）→ 优化。
- [ ] Memory 面板 Heap Snapshot → 查对象泄漏（对比 Snapshot 1 和 2 → 新增对象数量）。
- [ ] Network 面板 Waterfall → 分析请求串行化（HTTP/1.1 限制 6 请求并行）→ 升级 HTTP/2。

### APM 工具
- [ ] SkyWalking/Jaeger 链路追踪（标注慢节点：DB/HTTP/外部服务）。
- [ ] Span 粒度：精确到 SQL 语句（MyBatis Plugin）/ HTTP 调用（Feign Interceptor）。
- [ ] 采样率：生产环境 10~20%（平衡存储成本与覆盖率）。

### 压测基准
- [ ] K6/JMeter 模拟并发（用户数 = 在线峰值 × 3；如 1000 在线 → 3000 并发）。
- [ ] 指标：TPS（Transactions Per Second）、平均响应时间、99th 分位数（P99）、错误率（≤ 0.1%）。
- [ ] 瓶颈定位：CPU 100% → 优化算法/缓存；Memory OOM → 堆内存调整/查泄漏；DB 超时 → 索引优化。
- [ ] 回归测试：优化后重新压测对比（同一环境/同一数据量）。
