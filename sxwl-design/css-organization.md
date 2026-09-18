# sxwl-frontend CSS/SCSS 文件组织规范

> 📂 统一样式文件管理结构，确保可维护性和团队协作效率

---

## 📋 目录结构

### 1. 全局层（Global）

```
sxwl-react/src/styles/
├── variables.scss    ✅ 设计令牌（颜色、圆角、阴影、间距）
├── mixins.scss       ✅ 混入库（滚动条、通用工具）
├── animations.scss   ✅ 动画定义（@keyframes）
├── global.scss       ✅ 全局基础样式 + Antd 组件覆盖
└── theme.token.ts    ✅ Ant Design ConfigProvider Token 映射
```

**职责**：
- `variables.scss`：定义项目级 Token，所有团队必须使用
- `mixins.scss`：封装可复用的 CSS 片段（如滚动条美化）
- `global.scss`：html/body/#root 重置 + Portal 组件覆盖（Modal/Result）
- `theme.token.ts`：Ant Design 主题定制（与 `variables.scss` 值保持一致）

---

### 2. 公共组件层（Common Components）

```
sxwl-react/src/components/Sxwl*/
├── index.tsx         # 组件逻辑
├── index.scss        # 组件样式（同名 SCSS）
└── types.ts          # TypeScript 类型（可选）
```

**规则**：
- ✅ 每个 `Sxwl*` 组件必须有 `index.scss`
- ✅ SCSS 文件命名必须与组件名一致（`index.scss`）
- ✅ 引用方式：`import './index.scss';`（在 index.tsx 顶部）
- ❌ 禁止在其他位置创建新的 `.scss` 文件

---

### 3. 页面层（Pages）

```
sxwl-react/src/pages/*/index.tsx     # 页面入口
sxwl-react/src/pages/*/index.scss   # 页面专属样式（如有）
```

**规则**：
- 优先复用公共组件（SxwlPage、SxwlCard、SxwlFormModal）
- 仅当页面有**特殊布局需求**时才创建独立 `index.scss`
- 示例：Dashboard（仪表盘）、Login（登录页）

---

### 4. 布局层（Layouts）

```
sxwl-react/src/layouts/SxwlLayout/
├── index.tsx           # 主布局组件
├── index.scss          # 布局样式（侧边栏、顶栏、Content）
└── components/
    ├── HeaderNotice/
    │   ├── index.tsx
    │   └── index.scss  # 通知下拉组件样式
    └── ...
```

---

## 📝 SCSS 编写规范

### 1. 文件头部注释

```scss
// ============================================
// SxwlPage — CRUD 页面骨架样式（sass, 规范 v2.0 · Pro 中性观感）
// 所有业务页都通过 SxwlPage 拼装，改此文件全局受益
// ============================================

@use "../../styles/variables" as *;
@use "../../styles/mixins" as *;
```

**要求**：
- ✅ 必须添加文件头部注释（说明用途、适用场景）
- ✅ 必须引用 `variables` 和 `mixins`

---

### 2. 命名规范（BEM 风格增强版）

#### 根类名（Component Block）

格式：`.sxwl-{component-name}`

```scss
// ✅ 正确
.sxwl-page-wrapper { ... }
.sxwl-form-modal { ... }
.sxwl-button-primary { ... }

// ❌ 错误
.wrapper { ... }                // 缺前缀
.sxwl_page_wrapper { ... }      // 下划线分隔（应使用 -）
.SxwlPageWrapper { ... }        // PascalCase（应使用 kebab-case）
```

#### 子元素（Element）

格式：`.sxwl-{component-name}__{element-name}`

```scss
.sxwl-page-wrapper { ... }      // 根容器
.sxwl-page__breadcrumb { ... }  // 面包屑子元素
.sxwl-page__toolbar { ... }     // 工具栏子元素
```

#### 状态修饰符（Modifier）

格式：`.sxwl-{component-name}-modifier`

```scss
.sxwl-button { ... }                     // 默认态
.sxwl-button-primary { ... }             // 主要按钮变体
.sxwl-button-danger { ... }              // 危险按钮变体
.sxwl-button-disabled { ... }            // 禁用状态
.sxwl-button--loading { ... }            // 加载状态（双连字符）
```

---

### 3. 嵌套语法

```scss
// ✅ 正确：使用 & 嵌套伪类和类
.sxwl-button {
  padding: 8px 16px;
  border-radius: $sxwl-radius-sm;
  transition: all $sxwl-dur-fast;
  
  &:hover {
    background: $sxwl-color-primary-hover;
  }
  
  &:active {
    background: $sxwl-color-primary-active;
  }
  
  &.is-loading {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

// ❌ 错误：多层嵌套过深（> 3 层）
.sxwl-page {
  .sxwl-card {
    .ant-table {
      .ant-spin {               // ❌ 超过 3 层
        .ant-spin-container { ... }
      }
    }
  }
}
```

**规则**：
- ✅ 最多嵌套 3 层（避免选择器冲突）
- ✅ 使用 `&` 引用自身（`:hover`、`:focus`、`.is-*`）

---

### 4. 变量引用规范

```scss
// ✅ 正确：通过 @use 引用
@use "../../styles/variables" as *;

.my-component {
  color: $sxwl-color-primary;
  background: $sxwl-color-bg-container;
  box-shadow: $sxwl-shadow-card;
  border-radius: $sxwl-radius-lg;
  padding: $sxwl-space-4;
}

// ❌ 错误：硬编码色值/圆角/阴影
.my-component {
  color: #DE5F0E;           // ❌ 应使用 $sxwl-color-primary
  border-radius: 12px;      // ❌ 应使用 $sxwl-radius-lg
  box-shadow: 0 1px 2px rgba(...);  // ❌ 应使用 $sxwl-shadow-card
}
```

---

### 5. 混入使用

```scss
// ✅ 正确：调用预定义混入
.scrollable-list {
  height: calc(100vh - 200px);
  overflow-y: auto;
  @include scrollbar-thin();
}

// ❌ 错误：重复实现滚动条
.scrollable-list {
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #cbd5e1; }
  // ... （应直接使用 mixin）
}
```

---

## 🚫 常见错误检查清单

### ❌ 禁止硬编码

```scss
// ❌ 错误
.card {
  color: #ffffff;           // 应使用 $sxwl-color-bg-container
  background: #F0F2F5;      // 应使用 $sxwl-bg-layout
  border-radius: 8px;       // 应使用 $sxwl-radius
  box-shadow: 0 1px 2px...; // 应使用 $sxwl-shadow-card
  font-size: 14px;          // 应使用 $sxwl-font-size
}

// ✅ 正确
.card {
  color: $sxwl-color-bg-container;
  background: $sxwl-bg-layout;
  border-radius: $sxwl-radius;
  box-shadow: $sxwl-shadow-card;
  font-size: $sxwl-font-size;
}
```

### ❌ 禁止全局选择器

```scss
// ❌ 错误（除 html/body/#root 外）
button { color: red; }
div { margin: 0; }
input:focus { outline: none; }

// ✅ 正确
.my-button { color: $sxwl-color-danger; }
.container { margin: 0; }
.my-input:focus { outline: none; box-shadow: $sxwl-focus-ring; }
```

### ❌ 禁止直接修改 Ant Design

```scss
// ❌ 错误
.ant-btn-primary { background: blue !important; }
.ant-table-thead > tr > th { background: green !important; }

// ✅ 正确（通过变量控制）
.ant-table-thead > tr > th {
  background: #fafafa;  // 浅灰底符合品牌规范，不加 !important
}
```

---

## 🔧 代码审查要点

### 审查项 1：Token 一致性

```bash
# 检查是否硬编码了颜色
grep -r "#DE5F0E" src/components/ --exclude="*.token.ts"
# ✅ 应提示："禁止硬编码品牌色，请使用 $sxwl-color-primary"
```

### 审查项 2：命名规范

```
✅ .sxwl-page-wrapper
❌ .page-wrapper
❌ .pageWrapper
❌ .PageWrapper
```

### 审查项 3：嵌套层级

```scss
// ✅ 合格：2 层嵌套
.sxwl-page {
  .sxwl-breadcrumb { ... }
}

// ❌ 不合格：4 层嵌套
.sxwl-page {
  .sxwl-card {
    .ant-table {
      .ant-spin { ... }  // ❌ 超标
    }
  }
}
```

---

## 📦 迁移步骤（CSS-in-JS → SCSS）

如果你正在从 antd-style（CSS-in-JS）迁移回 SCSS，按以下步骤操作：

### Step 1：创建 SCSS 文件

```
src/components/SxwlComponent/
├── index.style.ts       ← 旧文件（保留，后续删除）
└── index.scss           ← 新文件（创建）
```

### Step 2：转换样式规则

```typescript
// ❌ 旧的 style.ts
export const useStyles = createStyles(({ token, css }) => ({
  wrapper: css`
    padding: ${token.padding}px;
    background: ${token.colorBgContainer};
  `,
}));
```

```scss
// ✅ 新的 index.scss
@use "../../styles/variables" as *;

.sxwl-component-wrapper {
  padding: $sxwl-space-4;
  background: $sxwl-color-bg-container;
}
```

### Step 3：更新 TSX 引用

```tsx
// ❌ 旧的引用
import { useStyles } from './index.style';
const styles = useStyles();
return <div className={styles.wrapper}>...</div>;

// ✅ 新的引用
import './index.scss';
return <div className="sxwl-component-wrapper">...</div>;
```

### Step 4：验证编译

```bash
cd sxwl-react
npm run build
```

### Step 5：删除旧文件

```bash
rm src/components/SxwlComponent/index.style.ts
```

---

## 🎯 快速参考卡片

| 类别 | 变量名 | 示例值 | 适用场景 |
|------|--------|--------|---------|
| **品牌色** | `$sxwl-color-primary` | `#DE5F0E` (暖橙色) | 主按钮、链接 |
| **成功色** | `$sxwl-color-success` | `#52C41A` | 成功提示 |
| **警告色** | `$sxwl-color-warning` | `#FAAD14` | 警告提示 |
| **危险色** | `$sxwl-color-danger` | `#FF4D4F` | 删除按钮 |
| **圆角 M** | `$sxwl-radius` | `8px` | 输入框、表格 |
| **圆角 LG** | `$sxwl-radius-lg` | `12px` | 卡片 |
| **阴影 Card** | `$sxwl-shadow-card` | `0 1px 2px...` | 卡片常驻 |
| **间距 4** | `$sxwl-space-4` | `16px` | 标准 padding |

---

## 📚 参考文档

- [sxwl Design System SPECIFICATION.md](./SPECIFICATION.md) - 完整设计规范
- [sxwl Design Tokens 速查表](./tokens.md) - 快速参考
- [Sass 官方文档](https://sass-lang.com/documentation)

---

**本文档由 AI Agent 生成，与代码同步更新。**
**最后更新：2026-09-12**
