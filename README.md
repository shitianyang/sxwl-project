# 数行未来·御权（sxwl-project）

<p align="center">
  <strong>统一权限管控平台</strong>
</p>

<p align="center">
  <a href="./LICENSE"><img src="https://img.shields.io/badge/license-Apache--2.0-blue.svg" alt="License"></a>
  <img src="https://img.shields.io/badge/Java-17-orange.svg" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.15-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/React-19.2-61DAFB.svg" alt="React">
  <img src="https://img.shields.io/badge/TypeScript-6.0-blue.svg" alt="TypeScript">
  <img src="https://img.shields.io/badge/Vite-8.1-646CFF.svg" alt="Vite">
</p>

---

## 📖 项目简介

**数行未来·御权**（sxwl-project）是一个基于 **Spring Boot 3.5 + React 19** 的统一权限管控平台。

提供完整的 RBAC 权限管理体系，涵盖系统管理、日志审计、通知公告、定时任务、文件存储、代码生成等核心功能模块，同时具备 C 端平台用户管理能力。

---

## 🛠️ 技术栈

### 后端 `sxwl-boot`

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 运行环境 |
| Spring Boot | 3.5.15 | 应用框架 |
| MyBatis | 3.0.5 | ORM |
| PageHelper | 2.1.1 | 分页插件 |
| PostgreSQL | 42.7.8 | 主数据库 |
| Redis | Lettuce | 缓存 / Token 白名单 |
| JJWT | 0.13.0 | JWT 令牌 |
| Bouncy Castle | 1.83 | 国密（SM2/SM3/SM4） |
| AWS S3 SDK | 2.42.27 | 对象存储（RustFS） |
| Quartz | 5.4.0 | 定时任务调度 |
| Flowable | 7.2.0 | 工作流引擎（预留） |
| Apache POI | 5.2.5 | Excel 导入导出（预留） |
| Maven | 3.9+ | 构建工具 |

### 前端 `sxwl-react`

| 技术 | 版本 | 用途 |
|------|------|------|
| React | 19.2.6 | UI 框架 |
| TypeScript | 6.0.2 | 类型系统 |
| Vite | 8.1.0 | 构建工具 |
| Ant Design | 6.4.4 | UI 组件库 |
| React Router DOM | 7.17.0 | 路由 |
| Zustand | 5.0.14 | 状态管理 |
| Axios | 1.17.0 | HTTP 客户端 |
| Sass | 1.101.0 | CSS 预处理器 |

---

## 📁 项目结构

```
sxwl-project/
├── sxwl-boot/                  # 后端（Maven 聚合，Spring Boot 3.5）
│   ├── sxwl-boot-app/          # 启动模块
│   ├── sxwl-boot-common/       # 公共模块（常量、实体、异常、工具类）
│   ├── sxwl-boot-config/       # 配置模块
│   │   ├── config-web/         # Web 层配置（跨域、Jackson、全局异常、日志 AOP）
│   │   ├── config-mybatis/     # MyBatis 配置（数据权限、自动填充）
│   │   ├── config-security/    # Security 配置（JWT 认证、接口鉴权）
│   │   ├── config-redis/       # Redis 配置（缓存、Token 白名单）
│   │   ├── config-rustfs/      # RustFS 对象存储配置
│   │   ├── config-quartz/      # Quartz 定时任务配置
│   │   ├── config-sse/         # SSE 服务端推送配置
│   │   ├── config-websocket/   # WebSocket 配置
│   │   ├── config-monitor/     # 系统监控配置（服务器/JVM/Redis/DB）
│   │   └── config-freemarker/  # FreeMarker 代码生成模板
│   └── sxwl-boot-module/       # 业务模块
│       ├── module-system/      # 系统管理（用户/角色/菜单/组织/岗位/字典）
│       ├── module-auth/        # 登录认证、注册
│       ├── module-notice/      # 通知公告
│       ├── module-job/         # 定时任务管理
│       ├── module-rustfs/      # RustFS 文件管理
│       ├── module-codegen/     # 代码生成器
│       ├── module-config/      # 系统参数配置
│       └── module-backup/      # 数据备份
├── sxwl-react/                 # 前端（Vite + React 19 + TypeScript）
│   └── src/
│       ├── api/                # 接口层（Axios 封装）
│       ├── components/         # 通用组件
│       ├── layouts/            # 布局组件
│       ├── pages/              # 页面
│       ├── router/             # 路由配置
│       ├── stores/             # 状态管理（Zustand）
│       ├── hooks/              # 自定义 Hook
│       ├── types/              # TypeScript 类型定义
│       ├── utils/              # 工具函数
│       ├── config/             # 前端配置
│       ├── styles/             # 全局样式
│       └── assets/             # 静态资源
├── .github/                    # GitHub 模板（Issue / PR）
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🚀 快速开始

### 后端

```bash
cd sxwl-boot
mvn clean install -DskipTests
cd sxwl-boot-app
mvn spring-boot:run
```

> 端口：`30101`，context-path：`/sxwl-api`  
> 配置文件：`application.yaml`（默认激活 `test` profile，可切换 `dev` / `prod`）  
> **注意**：`application-dev.yaml` 未提交，本地开发需自行创建。

### 前端

```bash
cd sxwl-react
pnpm install
pnpm dev
```

> 端口：`31001`，`@` → `src/` 别名已配置

---

## 📄 开源协议

Copyright © 2026 河北数行未来科技有限公司

本仓库基于 Apache License 2.0 协议开源。

## 📮 联系方式

- Issue：[GitHub Issues](https://github.com/shitianyang/sxwl-project/issues)
- 讨论区：[GitHub Discussions](https://github.com/shitianyang/sxwl-project/discussions)
