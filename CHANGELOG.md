# Changelog

本文件记录 sxwl-project 的所有 notable changes。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

---

## [0.1.0] - 2024-06-27

### Added

**后端骨架（sxwl-boot）**
- Maven 聚合工程：父 POM + 4 个顶层模块（app / common / config / module）
- 父 POM `<properties>` 统一版本管理
- config 按职责拆分：config-web / config-mybatis / config-security
- config-web：依赖 spring-boot-starter-web
- config-mybatis：依赖 mybatis + pagehelper + postgresql
- module-system：依赖 config-web + config-mybatis
- sxwl-boot-app：依赖 module-system
- `SxwlApplication.java`：@SpringBootApplication + main 方法
- 多环境配置：application.yaml + application-dev.yaml + application-prod.yaml
- logback-spring.xml

**前端骨架（sxwl-react）**
- Vite 8.1.0 项目初始化
- React 19.2.6 + TypeScript 6.0.2 + Ant Design 6.4.4
- Zustand 5.0.14、Axios 1.17.0、Sass 1.101.0
- react-markdown 10.1.0、highlight.js 11.11.1、remark-gfm 4.0.1
- @ 路径别名 + 端口 31001

**文档**
- README.md
- docs/architecture.md
- CONTRIBUTING.md
- CHANGELOG.md
- .gitignore
- .github/ISSUE_TEMPLATE
- .github/PULL_REQUEST_TEMPLATE
