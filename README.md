# 数行未来·御权

<p align="center">
  <strong>基于 Spring Boot 与 React 的统一 RBAC 权限管理平台</strong>
</p>

<p align="center">
  <a href="./LICENSE"><img src="https://img.shields.io/badge/license-Apache--2.0-blue.svg" alt="Apache-2.0 License"></a>
  <img src="https://img.shields.io/badge/Java-17-orange.svg" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.15-brightgreen.svg" alt="Spring Boot 3.5.15">
  <img src="https://img.shields.io/badge/React-19.2-61DAFB.svg" alt="React 19.2">
  <img src="https://img.shields.io/badge/TypeScript-6.0-3178C6.svg" alt="TypeScript 6.0">
  <img src="https://img.shields.io/badge/Vite-8.1-646CFF.svg" alt="Vite 8.1">
</p>

## 项目简介

数行未来·御权（`sxwl-project`）是一个前后端分离的统一权限管理平台，提供基于角色的访问控制（RBAC）、动态菜单、接口鉴权和常用后台管理能力。

项目由 Spring Boot 后端与 React 管理端组成：后端统一提供 `/sxwl-api` 接口，前端通过 Vite 开发服务器代理访问后端服务。

## 功能模块

- 认证与安全：账号登录、验证码、JWT Token 刷新、接口权限控制。
- 系统管理：用户、角色、菜单、组织、岗位、字典和系统参数。
- 运维监控：服务监控、缓存管理、在线用户、定时任务及任务日志。
- 审计与通知：操作日志、登录日志、通知公告。
- 文件与工具：RustFS/S3 文件管理、分片上传、代码生成、数据备份。
- 前端能力：动态路由、菜单和权限状态管理、SSE 监控推送、富文本与 Markdown 编辑。

## 技术栈

### 后端 `sxwl-boot`

| 技术 | 版本 | 用途 |
| --- | --- | --- |
| Java | 17 | 运行环境 |
| Spring Boot | 3.5.15 | 应用框架 |
| MyBatis | 3.0.5 | 数据访问 |
| PageHelper | 2.1.1 | 分页 |
| PostgreSQL | 42.7.8 | 主数据库驱动 |
| Redis / Lettuce | Spring Boot 管理 | 缓存与 Token 管理 |
| JJWT | 0.13.0 | JWT |
| Bouncy Castle | 1.84 | SM2/SM3/SM4 国密能力 |
| AWS SDK S3 | 2.42.27 | RustFS 对象存储 |
| Quartz | Spring Boot 管理 | 定时任务 |
| Flowable | 7.2.0 | 工作流能力（预留） |
| Apache POI | 5.2.5 | Excel 导入导出（预留） |
| Maven | 3.9+ | 构建工具 |

### 前端 `sxwl-react`

| 技术 | 版本 | 用途 |
| --- | --- | --- |
| React | 19.2.8 | UI 框架 |
| TypeScript | 6.0.2 | 类型系统 |
| Vite | 8.1.0 | 开发与构建工具 |
| Ant Design | 6.5.4 | UI 组件库 |
| React Router | 8.3.0 | 路由 |
| Zustand | 5.0.14 | 状态管理 |
| Axios | 1.18.1 | HTTP 客户端 |
| TipTap | 3.29.2 | 富文本编辑器 |
| Sass | 1.103.1 | 样式预处理 |
| Vitest | 4.1.11 | 单元测试 |

## 项目结构

```text
sxwl-project/
├── sxwl-boot/                         # 后端 Maven 聚合工程
│   ├── sxwl-boot-app/                 # 应用启动、环境配置、初始化 SQL 与集成测试
│   ├── sxwl-boot-common/              # 公共实体、DTO、异常、常量、工具类
│   ├── sxwl-boot-config/              # 基础设施配置模块
│   │   ├── sxwl-boot-config-web/      # Web、跨域、Jackson、异常与日志 AOP
│   │   ├── sxwl-boot-config-security/ # JWT 认证与接口鉴权
│   │   ├── sxwl-boot-config-mybatis/  # MyBatis、数据权限与自动填充
│   │   ├── sxwl-boot-config-redis/    # Redis
│   │   ├── sxwl-boot-config-rustfs/   # RustFS / S3
│   │   ├── sxwl-boot-config-quartz/   # Quartz
│   │   ├── sxwl-boot-config-sse/      # SSE
│   │   ├── sxwl-boot-config-websocket/# WebSocket
│   │   ├── sxwl-boot-config-monitor/  # 服务器、JVM、Redis、数据库监控
│   │   └── sxwl-boot-config-freemarker/# 代码生成模板
│   └── sxwl-boot-module/              # 业务模块
│       ├── sxwl-boot-module-auth/     # 登录认证与验证码
│       ├── sxwl-boot-module-system/   # 用户、角色、菜单、组织、岗位、字典等
│       ├── sxwl-boot-module-notice/   # 通知公告
│       ├── sxwl-boot-module-job/      # 定时任务
│       ├── sxwl-boot-module-rustfs/   # 文件管理
│       ├── sxwl-boot-module-codegen/  # 代码生成
│       ├── sxwl-boot-module-config/   # 系统参数
│       └── sxwl-boot-module-backup/   # 数据备份
├── sxwl-react/                        # React 管理端
│   ├── src/
│   │   ├── api/                       # 接口定义与 HTTP 封装
│   │   ├── components/                # 通用组件
│   │   ├── layouts/                   # 布局组件
│   │   ├── pages/                     # 页面
│   │   ├── router/                    # 路由与鉴权守卫
│   │   ├── stores/                    # 认证、菜单、权限状态
│   │   └── styles/                    # Sass 全局样式、变量和混入
├── AGENTS.md                          # 项目协作约定
├── LICENSE
└── README.md
```

## 环境要求

- JDK 17
- Maven 3.9 或更高版本
- Node.js 20 或更高版本
- npm（仓库使用 `package-lock.json`）
- PostgreSQL 与 Redis
- 可选：兼容 S3 的 RustFS 服务，用于文件管理功能

## 快速开始

### 1. 初始化后端配置与数据

初始化 PostgreSQL 数据库后，执行 [base.sql](./sxwl-boot/sxwl-boot-app/src/main/resources/sql/base.sql) 创建基础表和数据。

后端配置位于 `sxwl-boot/sxwl-boot-app/src/main/resources/`：

- `application.yaml`：公共配置，默认激活 `test` Profile。
- `application-test.yaml`、`application-dev.yaml`、`application-prod.yaml`：不同环境的连接与服务配置。

根据本地 PostgreSQL、Redis 和对象存储环境填写对应 Profile 的配置。请使用本地私有配置管理真实密码、Token 密钥和对象存储凭据，不要提交到仓库。

### 2. 启动后端

```bash
cd sxwl-boot
mvn clean install -DskipTests

cd sxwl-boot-app
mvn spring-boot:run
```

默认后端地址为 `http://127.0.0.1:30101/sxwl-api`。

如需指定环境，可通过 Spring Profile 启动，例如：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. 启动前端

```bash
cd sxwl-react
npm install
npm run dev
```

默认前端地址为 `http://127.0.0.1:31001`。开发服务器将 `/sxwl-api` 代理到 `http://127.0.0.1:30101`，并已配置 `@` 指向 `src/`。

## 构建与测试

```bash
# 后端：在 sxwl-boot 目录执行
mvn test
mvn clean install

# 前端：在 sxwl-react 目录执行
npm run build
npm run lint
npm test
```

前端构建会执行 TypeScript 类型检查。后端集成测试依赖 PostgreSQL、Redis 等运行环境；执行前请确保相应配置可用。

## 开发说明

- 后端接口统一使用 `/sxwl-api` 前缀，受保护接口按既有 `@PreAuthorize` 权限规则授权。
- 前端请求统一通过 `src/api/http.ts`，该模块负责响应格式、Token 注入和 Token 刷新；新增接口请复用它。
- 新增受保护功能时，应同步后端授权、菜单/权限数据、前端 API、动态路由或可见性控制。
- 后端 Mapper 改动需同步更新对应 XML；数据库结构变更请提供可追踪的 SQL 或迁移脚本。
- 完整协作规范见 [AGENTS.md](./AGENTS.md)。

## 开源协议

Copyright © 2026 河北数行未来科技有限公司。

本项目基于 [Apache License 2.0](./LICENSE) 协议开源。

## 联系方式

- Issue：[GitHub Issues](https://github.com/shitianyang/sxwl-project/issues)
- 讨论区：[GitHub Discussions](https://github.com/shitianyang/sxwl-project/discussions)
- QQ 群：`726069355`
