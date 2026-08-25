# 项目协作指南

## 项目概览

本仓库是统一 RBAC 权限管理平台，采用 Java 后端和 React 前端分离架构。

- `sxwl-boot/`：Java 17、Spring Boot 3.5、Maven 聚合工程。
- `sxwl-react/`：React 19、TypeScript、Vite、Ant Design、Zustand、Sass。
- API 前缀为 `/sxwl-api`；本地后端端口为 `30101`，Vite 前端端口为 `31001`。

修改任意模块前，先阅读根目录 `README.md`、对应的 `pom.xml` 或 `package.json`，以及更近层级的 `AGENTS.md`（如存在）。保留与当前任务无关的未提交改动。

## 目录结构

### 后端：`sxwl-boot`

- `sxwl-boot-app`：应用启动和环境配置。
- `sxwl-boot-common`：通用 DTO、实体、异常、常量与工具类。
- `sxwl-boot-config`：Web、安全、MyBatis、Redis、RustFS、Quartz、SSE、WebSocket、监控和代码生成等基础设施配置。
- `sxwl-boot-module`：认证、系统管理、公告、定时任务、RustFS、系统配置、备份、代码生成等业务模块。

业务模块内沿用既有的分层与命名：`controller`、`service`、`service/impl`、`mapper`，Mapper XML 放在 `src/main/resources/mappers`。

### 前端：`sxwl-react/src`

- `api/`：带类型的接口模块；全部请求经 `api/http.ts` 发出。
- `pages/`：路由级页面。
- `components/`、`layouts/`：通用组件和布局组件。
- `router/`：静态/动态路由与 `AuthGuard`。
- `stores/`：Zustand 的认证、菜单、权限状态。
- `styles/`：全局 Sass Token、变量与混入。
- `config/`、`hooks/`、`types/`、`utils/`：前端公共能力。

前端引用使用 `@/` 指向 `src/`。遵循当前组件和 Sass 写法；除非任务明确要求，不要在当前 Sass 迁移过程中重新引入 CSS-in-JS。

## 常用命令

在对应目录执行命令：

```powershell
# 后端：构建全部 Maven 模块并启动应用
cd sxwl-boot
mvn clean install -DskipTests
cd sxwl-boot-app
mvn spring-boot:run

# 前端：安装、开发与校验
cd sxwl-react
npm install
npm run dev
npm run build
npm run lint
npm test
```

优先执行最小相关验证。前端改动至少执行 `npm run build`，有相关测试时一并运行；涉及 ESLint 覆盖范围时运行 `npm run lint`。后端改动应编译受影响的 Maven 模块及其依赖方；跨模块改动时运行聚合构建。

不要为了清理而更新依赖、重写锁文件、全量格式化或执行完整构建。不要提交本地运行文件，例如 `application-dev.yaml`、凭据、日志和生成产物。

## 后端改动规则

- 功能放入所属的 `sxwl-boot-module`，不要把业务逻辑重复放在 `sxwl-boot-app`。
- 保持 `controller -> service -> mapper/XML` 分层，复用既有的统一响应、分页、异常与安全工具。
- 所有外部输入均应校验；除非任务明确要求变更，否则保持请求和响应契约兼容。
- 非公开接口使用既有 `@PreAuthorize` 权限表达式保护。新增权限时，同步权限/菜单数据以及前端的可见性或路由处理。
- 在 Service 与 Mapper 查询中落实当前用户和组织/数据范围隔离。不得以客户端传入的用户、组织或角色标识作为鉴权依据。
- 修改 Mapper 方法时，同时更新对应 Mapper XML 和所有调用方。
- 数据库或表结构变更属于联动改动：提供迁移脚本或项目现有风格的 SQL，考虑存量数据与回滚，并同步受影响的 API 消费端。
- 不得在源码、日志、测试或文档中暴露 JWT 信息、加密密钥、数据库凭据、对象存储凭据或个人数据。

## 前端改动规则

- 在对应的 `src/api` 模块定义接口函数和请求/响应类型。复用 `src/api/http.ts` 的 `http`，不要创建临时 Axios 实例。
- 保持统一的 `SxwlResult<T>` 响应结构和 `api/http.ts` 中既有的 Token 刷新逻辑。
- 优先复用现有组件、Ant Design 基础组件、Sass 变量和混入，再新增组件或样式抽象。
- 新增受保护页面时，视需求联动页面组件、路由/菜单解析、接口模块、后端接口和权限处理。
- 不得硬编码 Token、密钥、API 主机地址或用户身份。除非前后端明确同步调整，否则保持 `/sxwl-api` 的 Vite 代理契约不变。
- API 边界保持明确 TypeScript 类型，避免 `any`，不要悄然放宽后端响应类型。

## 前后端联动清单

功能涉及前后端时，在同一改动中检查并更新以下内容：

1. 后端 Controller、请求/响应 DTO、输入校验、授权、Service、Mapper/XML 与数据库变更。
2. 权限/菜单元数据，以及前端权限或动态路由行为。
3. 前端带类型的接口函数、页面/组件状态、加载/错误/空状态与用户可见的权限控制。
4. 改动两端各自相关的测试或构建校验。

如有已知消费者未同步更新，必须在交付说明中明确列出。

## 工作要求

- 新增依赖、抽象、表格模式、接口或权限命名之前，先搜索相似实现。
- 改动应限于任务范围；不得还原、覆盖或格式化并非本次任务产生的变更。
- 编辑源码和配置时使用 UTF-8；除非任务要求，不修改原有换行风格和本地化文本。
- 交付前检查差异，执行与风险匹配的验证，并说明改动文件、运行命令、验证结果和剩余风险。
