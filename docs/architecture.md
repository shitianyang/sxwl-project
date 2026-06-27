# sxwl-project 架构设计文档

> 版本：v0.1 | 2026-06-27

---

## 1. 系统概述

sxwl-project 是一个前后端分离的权限管理系统（RBAC）。

- **后端**：Spring Boot 3.5.15 + MyBatis 3.0.5 + PostgreSQL 42.7.8 + Redis
- **前端**：React 19.2.6 + TypeScript 6.0.2 + Vite 8.1.0 + Ant Design 6.4.4

---

## 2. 整体架构

```
┌─────────────────────────────────────────────────┐
│                 浏览器 / 客户端                  │
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│        sxwl-react（端口 31001）                  │
│  React 19.2 + TypeScript 6.0 + Vite 8.1         │
└──────────────────────┬──────────────────────────┘
                       │ REST API
┌──────────────────────▼──────────────────────────┐
│      sxwl-boot（端口 30101，context /api）       │
│  Spring Boot 3.5.15 + MyBatis 3.0.5             │
│  PostgreSQL 42.7.8  +  Redis                    │
└─────────────────────────────────────────────────┘
```

---

## 3. 后端架构（sxwl-boot）

### 3.1 Maven 聚合结构

```
sxwl-boot（父 POM，groupId: com.sxwl，version: 0.1.0）
├── pom.xml
├── sxwl-boot-app/                   # 启动模块（jar，入口 SxwlApplication）
│   └── 依赖：sxwl-boot-module-system
├── sxwl-boot-common/                # 公共模块（jar）
├── sxwl-boot-config/                # 配置聚合（pom）
│   ├── config-web/                  # Web 相关配置
│   ├── config-mybatis/              # MyBatis + PageHelper + PostgreSQL
│   ├── config-redis/                # Redis 配置
│   └── config-security/             # 预留认证鉴权配置
└── sxwl-boot-module/                # 业务聚合（pom）
    └── module-system/               # 系统管理模块
```

### 3.2 依赖关系

```
sxwl-boot-app
  └── sxwl-boot-module-system

sxwl-boot-module-system
  ├── sxwl-boot-config-web
  └── sxwl-boot-config-mybatis
```

### 3.3 父 POM 版本管理

| `<properties>` 变量 | 版本 |
|------|------|
| `spring.boot.version` | 3.5.15 |
| `mybatis.version` | 3.0.5 |
| `pagehelper.version` | 2.1.1 |
| `postgre.version` | 42.7.8 |
| `jjwt.version` | 0.13.0 |
| `bcprov.version` | 1.83 |
| `rustfs.version` | 2.42.27 |
| `flowable.version` | 7.2.0 |
| `poi.version` | 5.2.5 |

### 3.4 启动类

```java
// com.sxwl.SxwlApplication
@SpringBootApplication
public class SxwlApplication {
    public static void main(String[] args) {
        SpringApplication.run(SxwlApplication.class, args);
    }
}
```

### 3.5 配置文件

**application.yaml**

| 配置项 | 值 |
|--------|-----|
| `spring.application.name` | `sxwl-boot` |
| `spring.profiles.active` | `dev` |
| `server.port` | `30101` |
| `server.servlet.context-path` | `/api` |
| `mybatis.mapper-locations` | `classpath*:mappers/**/*.xml` |
| `spring.servlet.multipart.max-file-size` | `100MB` |
| `spring.servlet.multipart.max-request-size` | `200MB` |

**application-dev.yaml**

- PostgreSQL 数据源（HikariCP：min-idle 5, max-pool-size 20）
- Redis（Lettuce：max-active 16, max-idle 8, min-idle 2）
- **密码明文，已加入 `.gitignore`，不提交**

**application-prod.yaml**

- 数据源和 Redis 均使用环境变量占位符（`${DB_PASSWORD}`、`${REDIS_PASSWORD}` 等）
- 不落盘任何敏感信息

**logback-spring.xml**

- dev：`com.sxwl` DEBUG 级别，MyBatis SQL DEBUG，控制台彩色输出
- prod：WARN 级别，控制台 + 文件滚动输出（50MB/天，保留 30 天）

---

## 4. 前端架构（sxwl-react）

### 4.1 当前状态

Vite 初始化完成，`App.tsx` 为空组件，等待业务开发。

- `vite.config.ts`：`@` 路径别名 → `src/`，端口 `31001`
- `index.html`：title 为 `sxwl-react-template`

### 4.2 现有文件

```
sxwl-react/
├── src/
│   ├── main.tsx
│   └── App.tsx
├── public/
│   └── favicon.svg
├── index.html
├── vite.config.ts
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.node.json
├── eslint.config.js
└── package.json
```

### 4.3 依赖版本

| 依赖 | 版本 |
|------|------|
| react | 19.2.6 |
| typescript | 6.0.2 |
| vite | 8.1.0 |
| antd | 6.4.4 |
| react-router-dom | 7.17.0 |
| zustand | 5.0.14 |
| axios | 1.17.0 |
| sass | 1.101.0 |
| react-markdown | 10.1.0 |
| highlight.js | 11.11.1 |
| remark-gfm | 4.0.1 |
