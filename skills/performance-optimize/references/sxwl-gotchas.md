# 本项目（sxwl）性能优化特有弱点验证表

## P1. PageHelper 分页缺失导致全表加载
- **现象**：部分列表查询未加 PageHelper → 一次性加载全表数据 → 前端卡顿/内存溢出。
- **正确实现**：
  - 所有列表查询必须在 Controller 层调用 `PageHelper.startPage(page, size)`。
  - 分页必须加排序（`ORDER BY create_time DESC`）→ 避免同页数据不稳定（翻页时数据跳动）。
  - 检查：全量搜索 Service 层的查询方法，确认是否有对应分页参数传入。
- **风险等级**：BLOCKER（大数据量必现）

---

## P2. MyBatis N+1 查询问题
- **现象**：循环内多次调用 `selectById()` 或关联查询 → O(1) 变 O(n²) → 列表数据越多越卡。
- **典型场景**：用户列表 JOIN 部门表 → 每个用户查一次部门 → 100 用户 = 100 次查询。
- **正确实现**：
  - 批量查询：改用 `IN` + Map 分组（`selectByIds(userIds)` → 内存组装）。
  - SQL JOIN：用单个 SQL 关联查询（`SELECT u.*, d.dept_name FROM sys_user u LEFT JOIN sys_dept d ON u.dept_id = d.id`）。
  - 禁用 Lazy Loading（MyBatis 延迟加载易导致 N+1；改用 Eager Loading）。
- **检查**：循环内查询操作；Service 层多次调用 Mapper 方法。
- **风险等级**：HIGH（数据量 ≥ 100 条明显）

---

## P3. SSE 连接数无上限
- **现象**：全局最大在线用户数未限制 → 大量 SSE 连接堆积 → 内存/CPU 耗尽 → 服务不可用。
- **正确实现**：
  - 单用户最大连接数 ≤ 3（防多端重连风暴）。
  - 全局最大连接数 ≤ 10000（按服务器配置调整）；达到上限新连接排队/拒绝。
  - 空闲超时自动断开（5 分钟无消息推送 → 关闭连接）。
  - `SseEmitter` 必须设 `setTimeout()`；`onCompletion/onTimeout/onError` 清理容器。
- **检查**：`SxwlSseEmitterManager` 是否做连接数统计与限制；Redis Key 过期时间设置。
- **风险等级**：BLOCKER（多用户并发必现 OOM）

---

## P4. Redis 缓存穿透/击穿/雪崩
- **缓存穿透**：查询不存在的数据（如已删除资源 ID）→ 每次都打 DB → 布隆过滤器预检 / 空值缓存（`null` + 5min 过期）。
- **缓存击穿**：热点 Key 过期瞬间大量请求打 DB → 分布式锁保护只允许一个线程重建缓存。
- **缓存雪崩**：大量 Key 同时过期 → 随机过期时间（±10% 偏移）；本地 Caffeine 降级。
- **检查**：字典/菜单/权限等热点数据是否设过期时间；空值是否特殊处理。
- **风险等级**：HIGH（促销/活动场景必现）

---

## P5. HikariCP 连接池瓶颈
- **现象**：`maximum-pool-size` 过小（默认 10）→ 高并发请求排队 → 响应超时；连接泄漏（未归还）→ 连接耗尽。
- **正确调优**：
  - `maximum-pool-size` = CPU 核心数 × 2 + 磁盘队列数（如 8 核 CPU → 18~20）。
  - `idle-timeout` ≥ 30s（过短频繁重建连接）；`max-lifetime` ≤ 数据库 `wait_timeout`。
  - 监控活跃连接数（Actuator `/metrics/hikaricp.connections.active`）→ 持续 100% 需扩容。
  - 确保所有 DB 操作在 `try-with-resources` 或 `@Transactional` 内 → 异常时自动归还。
- **检查**：`application.yaml` 中 HikariCP 配置；Service 层手动管理 Connection 处。
- **风险等级**：HIGH（并发 ≥ 50 QPS 明显）

---

## P6. 前端 Bundle 过大
- **现象**：全量引入第三方库（G2/ECharts/Ant Design 全套）→ Bundle > 5MB → 首屏加载 > 10s。
- **优化方案**：
  - 按需引入：`import { Button } from 'antd'` + CSS；非 `import * as antd from 'antd'`。
  - G2/ECharts 按需导入组件（`import Chart from '@antv/g2'` 非全量组件库）。
  - 路由懒加载：二三级页面 `React.lazy(() => import('./Page'))`。
  - Tree-shaking：`package.json` 设 `"sideEffects": ["*.css"]` → 消除未使用代码。
  - Bundle 分析：`vite build --mode analyze` 定位大依赖。
- **目标**：首屏 Bundle ≤ 2MB；首屏加载时间 ≤ 3s（4G 网络）。
- **检查**：`vite.config.ts` 打包配置；各页面 import 语句。
- **风险等级**：HIGH（海外/低带宽用户体验差）

---

## P7. Spring Boot 启动慢
- **现象**：模块过多/Bean 数量大 → 启动时间 > 30s → 开发效率低/部署慢。
- **优化方案**：
  - 延迟初始化（`spring.lazy-init=true`）→ 非核心 Bean 懒加载。
  - 减少 AutoConfiguration 扫描（`exclude` 不用的模块如 Quartz/SSE）。
  - Profile 隔离（dev 环境禁用非必要功能）。
  - JVM Cold Start → 启用 AOT/GraalVM（本项目的可不适用，但关注）。
- **检查**：启动日志中各模块耗时标注；`--debug` 查看 Bean 依赖树。
- **风险等级**：LOW（影响开发体验，不影响生产性能）

---

## P8. 序列化性能损耗
- **现象**：Entity 直接 JSON 序列化 → 字段冗余 → 响应体臃肿 + 序列化耗时。
- **正确实现**：
  - DTO 模式：Controller 返回 VO/DTO（只含必要字段），非 Entity。
  - Jackson 配置：`@JsonIgnore` 敏感字段；`@JsonInclude(Include.NON_NULL)` 省略 null。
  - 大型对象列表序列化：`ObjectMapper.writeArray()` 流式输出（非 `writeValueAsString()`）。
- **检查**：Controller 返回值类型；Jackson 全局配置。
- **风险等级**：MEDIUM（影响带宽与响应延迟）

---

## P9. 索引失效常见陷阱
- **隐式类型转换**：`WHERE phone = 13800138000`（phone 是 VARCHAR → 字符串应加引号）。
- **函数/表达式**：`WHERE YEAR(create_time) = 2024` → 索引失效 → 改范围查询 `create_time >= '2024-01-01' AND create_time < '2025-01-01'`。
- **模糊查询前缀通配符**：`WHERE name LIKE '%abc'` → 失效；`LIKE 'abc%'` 可用索引。
- **OR 条件字段无索引**：`WHERE indexed_col OR non_indexed_col` → 全表扫描 → 两字段都建索引或用 UNION。
- **检查**：慢SQL `EXPLAIN` 看 `Extra` 列出现 `Using where; Using temporary; Using filesort`。
- **风险等级**：HIGH（隐性性能杀手）

---

## P10. JVM 内存泄漏模式
- **静态集合无限增长**：`static List<Data> cache = new ArrayList<>();` → 只加不删 → OOM。
- **未关闭资源**：`InputStream`/`Connection` 未 try-with-resources → 句柄数上涨 → 操作系统限制。
- **线程局部变量泄露**：`ThreadLocal` 未 `remove()` → 线程池复用线程 → 旧数据累积。
- **监听器/回调未注销**：`addMouseListener` 未 `remove` → 对象无法 GC。
- **检查**：Heap Dump（`jmap -dump:format=b,file=heap.hprof <pid>`）→ MAT/Eclipse Analyzer 查 Dominator Tree。
- **风险等级**：HIGH（生产环境渐进恶化，重启临时缓解）
