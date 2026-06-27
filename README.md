# sxwl-project

<p align="center">
  <strong>企业级权限管理系统</strong>
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

**sxwl-project** 是一个基于 **Spring Boot 3.5 + React 19** 的权限管理系统。

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
| Bouncy Castle | 1.83 | 加密 |
| AWS S3 SDK | 2.42.27 | 对象存储（RustFS） |
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
├── sxwl-boot/              # 后端（Maven 聚合，Spring Boot 3.5）
│   ├── sxwl-boot-app/      # 启动模块
│   ├── sxwl-boot-common/   # 公共模块
│   ├── sxwl-boot-config/   # 配置模块（web / mybatis / redis / security）
│   └── sxwl-boot-module/   # 业务模块
├── sxwl-react/             # 前端（Vite + React 19 + TypeScript）
├── docs/                   # 文档
├── .github/                # GitHub 模板（Issue / PR）
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

> 端口：`30101`，context-path：`/api`  
> 配置文件：`application.yaml`（激活 `dev` 或 `prod` profile）  
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

Apache License 2.0

## 📮 联系方式

- Issue：[GitHub Issues](https://github.com/shitianyang/sxwl-project/issues)
- 讨论区：[GitHub Discussions](https://github.com/shitianyang/sxwl-project/discussions)
