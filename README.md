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

### 核心能力

- **认证与安全**：密码/短信双模式登录、SM2 加密传输、JWT 双令牌刷新、图形验证码（Kaptcha）、IP 黑白名单拦截、BCrypt 密码哈希。
- **系统管理**：用户、角色、菜单、组织、岗位、字典、参数配置（含 Redis 缓存 + SpEL 操作日志）。
- **运维监控**：Quartz 定时任务（Cron 校验 + @PostConstruct 启动同步）、SSE 实时服务器监控（CPU/内存/磁盘/JVM）、在线用户（Redis + IP 脱敏）。
- **审计与通知**：操作日志/登录日志（六层完整）、TipTap 富文本公告、SSE 实时推送（铃铛提醒 + 未读角标）。
- **文件管理**：RustFS/S3 分片上传（5MB 分片 + 3 并发）、SparkMD5 秒传、断点续传、批量删除、软删除 + S3 孤儿清理。
- **工具扩展**：代码生成器（129 行强类型定义 + Freemarker 模板渲染）、数据备份（@SxwlRepeatSubmit 防重复 + SecurityContext 同步）。

### 统计概览

| 模块 | API 数量 | 前端对接 | 六层完整度 | 评分 |
|------|---------|---------|-----------|------|
| 认证与安全 | 8 | 100% | 100% | 97/100 ⭐ |
| 系统管理 | 48 | 100% | 100% | 100/100 ⭐⭐ |
| 运维监控 | 25 | 100% | 90% | 97.5/100 ⭐ |
| 审计与通知 | 11 | 100% | 100% | 100/100 ⭐⭐ |
| 文件管理 | 10 | 100% | 70%* | 98.25/100 ⭐ |
| 工具与扩展 | 18 | 100% | 100% | 99.7/100 ⭐⭐ |
| **总计** | **120+** | **98%** | **96%** | **98.8/100** ⭐⭐ |

> *RustFS 使用 S3 + Redis 会话管理，非传统 DB CRUD

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
| TipTap | 3.30.2 | 富文本编辑器 |
| React Markdown | 10.1.0 | Markdown 渲染 |
| Sass | 1.103.1 | 样式预处理 |
| G2 | 5.4.8 | 图表可视化 |

## 项目结构

```text
sxwl-project/
├── .github/                         # GitHub Issue 和 PR 模板
│   ├── ISSUE_TEMPLATE/              # Issue 模板
│   └── PULL_REQUEST_TEMPLATE/       # PR 模板
├── skills/                          # AI 助手技能插件
│   ├── bug-hunter/                  # Bug 扫描技能
│   ├── code-review-backend/         # 后端代码审查技能
│   └── code-review-frontend/        # 前端代码审查技能
├── sxwl-boot/                       # 后端 Maven 聚合工程
│   ├── sxwl-boot-app/               # 应用启动、环境配置与初始化 SQL
│   ├── sxwl-boot-common/            # 公共实体、DTO、异常、常量、工具类
│   ├── sxwl-boot-config/            # 基础设施配置模块
│   │   ├── sxwl-boot-config-web/    # Web、跨域、Jackson、异常与日志 AOP
│   │   ├── sxwl-boot-config-security/ # JWT 认证与接口鉴权
│   │   ├── sxwl-boot-config-mybatis/  # MyBatis、数据权限与自动填充
│   │   ├── sxwl-boot-config-redis/  # Redis
│   │   ├── sxwl-boot-config-rustfs/ # RustFS / S3
│   │   ├── sxwl-boot-config-quartz/ # Quartz
│   │   ├── sxwl-boot-config-sse/    # SSE
│   │   ├── sxwl-boot-config-websocket/ # WebSocket
│   │   ├── sxwl-boot-config-monitor/ # 服务器、JVM、Redis、数据库监控
│   │   └── sxwl-boot-config-freemarker/ # 代码生成模板
│   └── sxwl-boot-module/            # 业务模块
│       ├── sxwl-boot-module-auth/   # 登录认证与验证码
│       ├── sxwl-boot-module-system/ # 用户、角色、菜单、组织、岗位、字典等
│       ├── sxwl-boot-module-notice/ # 通知公告
│       ├── sxwl-boot-module-job/    # 定时任务
│       ├── sxwl-boot-module-rustfs/ # 文件管理
│       ├── sxwl-boot-module-codegen/ # 代码生成
│       ├── sxwl-boot-module-config/ # 系统参数
│       └── sxwl-boot-module-backup/ # 数据备份
├── sxwl-react/                      # React 管理端
│   ├── src/
│   │   ├── api/                     # 接口定义与 HTTP 封装
│   │   ├── assets/                  # 静态资源（图标、图片）
│   │   ├── components/              # 通用组件
│   │   ├── config/                  # 前端配置
│   │   ├── hooks/                   # 自定义 Hooks
│   │   ├── layouts/                 # 布局组件
│   │   ├── pages/                   # 页面
│   │   ├── router/                  # 路由与鉴权守卫
│   │   ├── stores/                  # 认证、菜单、权限状态
│   │   ├── styles/                  # Sass 全局样式、变量和混入
│   │   ├── types/                   # TypeScript 类型定义
│   │   └── utils/                   # 工具函数
├── AGENTS.md                        # AI 助手协作约定
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

执行 [sxwl_project_v1.sql](./sxwl-boot/sxwl-boot-app/src/main/resources/sql/sxwl_project_v1.sql) 创建全部数据库表和数据（约 41+ 张表 + 种子数据）。

#### 后端环境配置

后端配置位于 `sxwl-boot/sxwl-boot-app/src/main/resources/`：

- `application.yaml`：公共配置，默认激活 `test` Profile。（✅ 已提交）
- `application-test.yaml.template`：测试环境配置模板。（✅ 已提交，提供完整结构参考）
- `application-test.yaml`、`application-dev.yaml`、`application-prod.yaml`：不同环境的连接与服务配置。（❌ 需本地创建）

**操作步骤：**

```bash
# 方式1：从模板复制（推荐）
cp application-test.yaml.template application-test.yaml

# 方式2：从零创建或从其他环境复制
# 参考 application.yaml 的结构，自行创建各环境的配置文件
```

**配置内容说明：**

打开 `application-test.yaml`，修改以下关键配置项：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sxwl_db
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: # Redis密码（如有）
rustfs:
  access-key: # S3访问密钥
  secret-key: # S3秘密密钥
jwt:
  secret: # JWT签名密钥（至少32字符）
```

> **⚠️ 安全警告**：
> - `application-dev.yaml` 和 `application-prod.yaml` **绝不要提交到 Git**
> - `.gitignore` 已配置忽略所有 `application-*.yaml` 文件
> - 请使用本地私有配置管理真实密码、Token 密钥和对象存储凭据
> - 如需团队共享，建议使用加密的 Vault 工具或环境变量

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
```

#### 前端环境配置

前端环境配置文件：

- `.env`：全局环境变量。（✅ 可提交基础配置）
- `.env.example`：环境变量模板。（✅ 已提交，供团队成员参考）
- `.env.local`、`.env.development.local`、`.env.production.local`：本地开发环境变量。（❌ 需本地创建）

**操作步骤：**

```bash
# 复制模板并编辑
cp .env.example .env.local
```

**配置内容示例：**

```bash
# API 基础路径（通常使用默认值，无需修改）
# VITE_API_BASE_URL=http://127.0.0.1:30101/sxwl-api
```

> **⚠️ 安全警告**：
> - `.env.local` 等 `.env.*.local` 文件 **绝不要提交到 Git**
> - `.gitignore` 已配置忽略所有 `*.env.local` 文件
> - 仅在 `.env.example` 中提交非敏感的模板配置

## 构建

```bash
# 后端：在 sxwl-boot 目录执行
mvn clean install

# 前端：在 sxwl-react 目录执行
npm run build
npm run lint
```

前端构建会执行 TypeScript 类型检查。当前前端未保留自动化测试脚本。

## 开发说明

### 架构特点

- **六层全链路验证**：Controller → Service → Mapper → XML → 前端 API → 前端页面，覆盖率 96%。
- **DB 驱动动态路由**：前端路由完全由后端 `sys_menu_info` 表控制，`pageResolver.ts` 基于 `import.meta.glob` 自动发现组件，零手动映射。
- **前后端 100% RESTful 规范一致**：URL 路径、HTTP 方法（GET/POST/PUT/DELETE）、响应类型（Java DTO ↔ TypeScript 接口）严格匹配。
- **100% 代码注释率**：Controller/Service/ServiceImpl/Mapper 全部 JavaDoc，前端 API/页面全部 JSDoc，SQL 清晰注释。

### 开发规范

- 后端接口统一使用 `/sxwl-api` 前缀，受保护接口按既有 `@PreAuthorize` 权限规则授权。
- 前端请求统一通过 `src/api/http.ts`，该模块负责响应格式、Token 注入和 Token 刷新；新增接口请复用它。
- 新增受保护功能时，应同步后端授权、菜单/权限数据、前端 API、动态路由或可见性控制。
- 后端 Mapper 改动需同步更新对应 XML；数据库结构变更请提供可追踪的 SQL 或迁移脚本。
- 完整协作规范见 [AGENTS.md](./AGENTS.md)。

### 代码审查报告

项目已完成全栈代码审查，详见以下专项报告：

1. [认证与安全模块审查报告](./docs/认证与安全模块专项审查报告.md) - 8 API，97/100
2. [系统管理模块审查报告](./docs/系统管理模块专项审查报告.md) - 48 API，100/100
3. [运维监控模块审查报告](./docs/运维监控模块专项审查报告.md) - 25 API，97.5/100
4. [审计与通知模块审查报告](./docs/审计与通知模块专项审查报告.md) - 11 API，100/100
5. [文件管理模块审查报告](./docs/文件管理RustFS模块专项审查报告.md) - 10 API，98.25/100
6. [工具与扩展模块审查报告](./docs/工具与扩展模块专项审查报告.md) - 18 API，99.7/100
7. [前端实现验证审查报告](./docs/前端实现验证专项审查报告.md) - 57+ 文件，100/100
8. [功能实现检查报告](./docs/功能实现检查报告.md) - 120+ API 总览

## 开源协议

Copyright © 2026 河北数行未来科技有限公司。

本项目基于 [Apache License 2.0](./LICENSE) 协议开源。

## 联系方式

- Issue：[GitHub Issues](https://github.com/shitianyang/sxwl-project/issues)
- 讨论区：[GitHub Discussions](https://github.com/shitianyang/sxwl-project/discussions)
- QQ 群：`726069355`
