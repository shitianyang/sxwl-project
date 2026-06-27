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

| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.5.15 |
| MyBatis | 3.0.5 |
| PageHelper | 2.1.1 |
| PostgreSQL | 42.7.8 |
| JJWT | 0.13.0 |
| Bouncy Castle | 1.83 |
| AWS S3 SDK | 2.42.27 |
| Flowable | 7.2.0 |
| Apache POI | 5.2.5 |
| Maven | 3.9+ |

### 前端 `sxwl-web`

| 技术 | 版本 |
|------|------|
| React | 19.2.6 |
| TypeScript | 6.0.2 |
| Vite | 8.1.0 |
| Ant Design | 6.4.4 |
| React Router DOM | 7.17.0 |
| Zustand | 5.0.14 |
| Axios | 1.17.0 |
| Sass | 1.101.0 |
| react-markdown | 10.1.0 |
| highlight.js | 11.11.1 |
| remark-gfm | 4.0.1 |

---

## 📁 项目结构

```
sxwl-project/
├── sxwl-boot/                          # 后端（Maven 聚合项目）
│   ├── pom.xml                        # 父 POM
│   ├── sxwl-boot-app/                 # 启动模块
│   │   ├── pom.xml
│   │   └── src/main/
│   │       ├── java/com/sxwl/SxwlApplication.java
│   │       └── resources/
│   │           ├── application.yaml
│   │           ├── application-dev.yaml
│   │           ├── application-prod.yaml
│   │           └── logback-spring.xml
│   ├── sxwl-boot-common/              # 公共模块
│   │   └── pom.xml
│   ├── sxwl-boot-config/              # 配置模块（聚合）
│   │   ├── pom.xml
│   │   ├── sxwl-boot-config-web/
│   │   │   └── pom.xml
│   │   ├── sxwl-boot-config-mybatis/
│   │   │   └── pom.xml
│   │   └── sxwl-boot-config-security/
│   │       └── pom.xml
│   └── sxwl-boot-module/              # 业务模块（聚合）
│       ├── pom.xml
│       └── sxwl-boot-module-system/
│           └── pom.xml
├── sxwl-react/                         # 前端（Vite + React 19）
│   ├── src/
│   │   ├── main.tsx
│   │   └── App.tsx
│   ├── public/
│   │   └── favicon.svg
│   ├── index.html
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── tsconfig.app.json
│   ├── tsconfig.node.json
│   ├── eslint.config.js
│   └── package.json
├── docs/
│   └── architecture.md
├── .github/
│   ├── ISSUE_TEMPLATE/
│   └── PULL_REQUEST_TEMPLATE/
├── .gitignore
├── .gitattributes
├── LICENSE
├── CHANGELOG.md
├── CONTRIBUTING.md
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
> 配置文件：`application.yaml` / `application-dev.yaml` / `application-prod.yaml`

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
