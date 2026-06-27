# Contributing to sxwl-project

感谢你考虑为 sxwl-project 贡献代码！🎉

## 行为准则

请尊重每一位贡献者，保持友善、包容的沟通氛围。

---

## 如何贡献

### 报告 Bug

- 使用 [GitHub Issues](https://github.com/shitianyang/sxwl-project/issues) 提交
- 请描述：**复现步骤 / 期望行为 / 实际行为 / 环境信息**
- 贴出相关日志或截图

### 提议新功能

- 先开 Issue 讨论，确认方向后再动手写代码
- 说明**使用场景**和**预期效果**

### 提交代码

#### 1. Fork & 克隆

```bash
git clone https://github.com/shitianyang/sxwl-project.git
cd sxwl-project
```

#### 2. 创建分支

```bash
git checkout -b feature/你的功能描述
# 或
git checkout -b fix/你要修的bug
```

#### 3. 代码规范

**后端（Java）**
- 遵循阿里巴巴 Java 开发手册规范
- 类名使用大驼峰，常量全大写
- 提交前确保无编译错误

**前端（TypeScript/React）**
- 使用项目配置的 ESLint + Prettier 格式化
- 组件名使用大驼峰
- 优先使用 TypeScript 类型，避免 `any`

#### 4. Commit 规范

请使用 [Conventional Commits](https://www.conventionalcommits.org/) 格式：

```
feat: 新增角色管理模块
fix: 修复登录页重定向循环问题
docs: 更新 README 快速开始章节
refactor: 重构权限校验中间件
test: 补充用户服务的单元测试
chore: 升级 spring-boot 到 3.2.0
```

#### 5. 提交 PR

```bash
git add .
git commit -m "feat: 描述你的改动"
git push origin feature/你的分支名
```

然后在 GitHub 上提交 Pull Request，并：
- 填写 PR 模板（如有）
- 描述改动内容和测试情况
- 关联相关 Issue（如 `Closes #12`）

---

## 开发环境搭建

见 [README.md → 快速开始](./README.md#快速开始)

---

## 分支说明

| 分支 | 说明 |
|------|------|
| `main` | 稳定版本，可直接部署 |
| `dev` | 开发分支，功能合并到此 |
| `feature/*` | 功能开发分支 |
| `fix/*` | Bug 修复分支 |

---

再次感谢你的贡献！🚀
