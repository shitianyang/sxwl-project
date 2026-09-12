# sxwl Design System
## 御权 RBAC 权限管理平台 UI 设计规范

> 📅 版本：v2.0 | 👤 更新日期：2026-09-12 | 🎨 设计语言：Pro 极简科技风

---

## 一、设计哲学

### 核心理念
- **中性 Pro 观感**：页面以中性色为主（灰/白），品牌色仅用于点缀（链接、按钮高亮）
- **极简克制**：减少装饰性元素，用间距和阴影营造层次
- **零原生引用**：所有业务组件使用 `Sxwl*` 封装组件，禁止直接引用 Ant Design 基础组件
- **Token 驱动**：颜色、圆角、阴影、间距全部通过变量管理，禁止硬编码

---

## 二、设计令牌（Design Tokens）

### 2.1 颜色系统

#### 品牌色（Primary Palette）

| 级别 | 变量名 | 色值 | 用途 |
|------|--------|------|------|
| Base | `$sxwl-color-primary` | `#1677FF` | 主按钮、链接、选中态 |
| Hover | `$sxwl-color-primary-hover` | `#3A8BFF` | 悬停态 |
| Active | `$sxwl-color-primary-active` | `#0E5FD8` | 点击态 |
| Light 1 | `$sxwl-color-primary-1` | `#E8F1FF` | 选中背景、图标底 |
| Gradient | `$sxwl-gradient-brand` | `linear-gradient(135deg, #1677FF 0%, #0E5FD8 100%)` | 登录页背景、渐变按钮 |

#### 成功色（Success Palette）

| 级别 | 变量名 | 色值 | 用途 |
|------|--------|------|------|
| Base | `$sxwl-color-success` | `#52C41A` | 成功提示、通过状态 |
| Light | `$sxwl-color-success-1` | `#F6FFED` | 成功背景 |

#### 警告色（Warning Palette）

| 级别 | 变量名 | 色值 | 用途 |
|------|--------|------|------|
| Base | `$sxwl-color-warning` | `#FAAD14` | 警告提示、未完成 |
| Light | `$sxwl-color-warning-1` | `#FFFBE6` | 警告背景 |

#### 危险色（Danger Palette）

| 级别 | 变量名 | 色值 | 用途 |
|------|--------|------|------|
| Base | `$sxwl-color-danger` | `#FF4D4F` | 删除按钮、错误提示 |
| Light | `$sxwl-color-danger-1` | `#FFF2F0` | 危险背景 |

#### 中性色阶（Neutral Tone）

| 级别 | 变量名 | 色值 | 用途 |
|------|--------|------|------|
| Heading | `$sxwl-color-text-heading` | `#1F2329` | 标题、正文标题 |
| Primary | `$sxwl-color-text` | `#1F2329` | 正文文本 |
| Secondary | `$sxwl-color-text-secondary` | `#4E5969` | 次要文本（描述、标签） |
| Tertiary | `$sxwl-color-text-tertiary` | `#86909C` | 提示文本（时间、占位） |
| Quaternary | `$sxwl-color-text-quaternary` | `#C9CDD4` | 禁用文本、分割线 |

#### 背景色（Background）

| 级别 | 变量名 | 色值 | 用途 |
|------|--------|------|------|
| Container | `$sxwl-color-bg-container` | `#FFFFFF` | 卡片、弹窗背景 |
| Layout | `$sxwl-bg-layout` | `#F0F2F5` | 页面底色 |

#### 边框色（Border）

| 级别 | 变量名 | 色值 | 用途 |
|------|--------|------|------|
| Primary | `$sxwl-color-border` | `#E5E6EB` | 输入框、常规控件边框 |
| Secondary | `$sxwl-color-border-secondary` | `#F0F0F0` | 内部分割、表格行线 |

---

### 2.2 圆角系统（Radius）

| 级别 | 变量名 | 值 | 适用场景 |
|------|--------|-----|---------|
| SM | `$sxwl-radius-sm` | `6px` | 小按钮、标签（Tag）、复选框 |
| M | `$sxwl-radius` | `8px` | 输入框、下拉选择、表格 |
| LG | `$sxwl-radius-lg` | `12px` | 卡片（Card）、搜索表单 |
| XL | `$sxwl-radius-xl` | `16px` | Modal 弹窗、大容器 |

```scss
// 使用示例
.my-button {
  border-radius: $sxwl-radius-sm; // 6px
}

.my-card {
  border-radius: $sxwl-radius-lg; // 12px
}

.my-modal {
  border-radius: $sxwl-radius-xl; // 16px
}
```

---

### 2.3 阴影系统（Shadow）

| 级别 | 变量名 | 值 | 适用场景 |
|------|--------|-----|---------|
| L1 (Card) | `$sxwl-shadow-card` | `0 1px 2px rgba(16,24,40,.04), 0 1px 3px rgba(16,24,40,.06)` | 卡片常驻 |
| L2 (Popup) | `$sxwl-shadow-popup` | `0 4px 12px rgba(16,24,40,.08), 0 2px 6px rgba(16,24,40,.05)` | 悬浮态 |
| L3 (Modal) | `$sxwl-shadow-modal` | `0 18px 44px rgba(16,24,40,.14), 0 6px 16px rgba(16,24,40,.10)` | 弹窗层 |
| Float (Glass) | `$sxwl-shadow-float` | `0 24px 60px -20px rgba(22,60,130,.35)` | 浮层玻璃卡片 |

```scss
// 使用示例
.card-static {
  box-shadow: $sxwl-shadow-card; // 常驻阴影
}

.card-hover:hover {
  box-shadow: $sxwl-shadow-popup; // 悬浮增强
}

.modal-overlay {
  box-shadow: $sxwl-shadow-modal; // 弹窗层
}

.glass-panel {
  box-shadow: $sxwl-shadow-float; // 玻璃质感
}
```

---

### 2.4 间距系统（Spacing）

| 级别 | 变量名 | 值 | 适用场景 |
|------|--------|-----|---------|
| 1 | `$sxwl-space-1` | `4px` | 紧凑排列（图标间距） |
| 2 | `$sxwl-space-2` | `8px` | 小元素（输入框与标签） |
| 3 | `$sxwl-space-3` | `12px` | 中等（表单字段间距） |
| 4 | `$sxwl-space-4` | `16px` | 标准（卡片 padding） |
| 5 | `$sxwl-space-5` | `20px` | 稍大（区块间距） |
| 6 | `$sxwl-space-6` | `24px` | 大（Section 间距） |
| 8 | `$sxwl-space-8` | `32px` | 超大（页面留白） |
| 10 | `$sxwl-space-10` | `40px` | 极端（布局分隔） |
| 12 | `$sxwl-space-12` | `48px` | 极限（Hero 区域） |

```scss
// 使用示例
.form-item {
  margin-bottom: $sxwl-space-4; // 16px
}

.section {
  padding: $sxwl-space-6; // 24px
}

.hero {
  padding: $sxwl-space-12 $sxwl-space-10; // 48px 40px
}
```

---

### 2.5 字号系统（Typography）

| 级别 | 变量名 | 值 | 适用场景 |
|------|--------|-----|---------|
| Default | `$sxwl-font-size` | `14px` | 正文、输入框 |
| Small | `$sxwl-font-size-sm` | `13px` | 次要文本、面包屑 |
| H2 | `$sxwl-font-size-heading-2` | `24px` | 二级标题 |
| H1 (Manual) | - | `28px` | 一级标题 |
| Large (Manual) | - | `16px` | 强调文本 |

```scss
// 使用示例
.title-h1 { font-size: 28px; font-weight: 700; }
.title-h2 { font-size: $sxwl-font-size-heading-2; font-weight: 600; }
.body-normal { font-size: $sxwl-font-size; color: $sxwl-color-text; }
.text-secondary { font-size: $sxwl-font-size-sm; color: $sxwl-color-text-secondary; }
```

---

### 2.6 动画时长（Duration）

| 级别 | 变量名 | 值 | 适用场景 |
|------|--------|-----|---------|
| Fast | `$sxwl-dur-fast` | `0.15s` | 悬停反馈、颜色变化 |
| Normal | `$sxwl-dur` | `0.3s` | 展开/收起、淡入淡出 |

```scss
// 使用示例
.button {
  transition: color $sxwl-dur-fast; // 快速响应
}

.drawer {
  transition: transform $sxwl-dur cubic-bezier(0.22, 1, 0.36, 1); // 柔和动画
}
```

---

### 2.7 特殊效果

#### 聚焦光晕（Focus Ring）

```scss
$sxwl-focus-ring: 0 0 0 3px rgba(22, 119, 255, 0.12);
```

```scss
// 使用示例
input:focus {
  outline: none;
  box-shadow: $sxwl-focus-ring; // 蓝色光晕
}
```

#### 玻璃质感（Glass Effect）

```scss
$sxwl-glass-light: rgba(255, 255, 255, 0.55);         // 半透明白
$sxwl-glass-light-strong: rgba(255, 255, 255, 0.72);  // 更强不透明度
$sxwl-glass-blue: rgba(22, 119, 255, 0.16);           // 品牌色半透明
$sxwl-glass-blue-strong: rgba(22, 119, 255, 0.32);    // 更强品牌色
$sxwl-glass-border: rgba(255, 255, 255, 0.65);        // 白色半透明边框
$sxwl-glass-blur: 22px;                                // 毛玻璃模糊
```

```scss
// 使用示例（登录页背景）
.glass-card {
  background: $sxwl-glass-light-strong;
  backdrop-filter: blur($sxwl-glass-blur);
  border: 1px solid $sxwl-glass-border;
  box-shadow: $sxwl-shadow-float;
}
```

---

## 三、混入库（Mixins）

### 3.1 极细滚动条

```scss
@mixin scrollbar-thin($thumb: #cbd5e1, $hover: #94a3b8) {
  &::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  &::-webkit-scrollbar-thumb {
    background: $thumb;
    border-radius: 3px;
  }
  &::-webkit-scrollbar-thumb:hover {
    background: $hover;
  }
}

// 使用示例
.scrollable-list {
  @include scrollbar-thin(); // 默认灰色
}

.custom-scrollbar {
  @include scrollbar-thin(#1677FF, #3A8BFF); // 品牌色
}
```

---

## 四、动画定义（Animations）

### 4.1 柔和升起（Rise Soft）

```scss
@keyframes sxwl-rise-soft {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 使用示例
.fade-in-up {
  animation: sxwl-rise-soft 0.3s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.delay-1 {
  animation-delay: 0.05s;
}

.delay-2 {
  animation-delay: 0.1s;
}
```

---

## 五、组件样式规范

### 5.1 组件目录结构

```
src/components/SxwlComponentName/
├── index.tsx          // 组件逻辑
├── index.scss         // 组件样式（同名 SCSS 文件）
└── types.ts           // TypeScript 类型定义（可选）
```

### 5.2 命名规范（BEM 风格）

#### 规则 1：根类名使用 `sxwl-{component-name}`

```scss
.sxwl-user-page { ... }      // ✅ 正确
.sxwl-form-modal { ... }     // ✅ 正确
.user-wrapper { ... }        // ❌ 错误（缺统一前缀）
```

#### 规则 2：子元素用 `__` 分隔

```scss
.sxwl-page-wrapper { ... }    // 根容器
.sxwl-page-breadcrumb { ... } // 面包屑子元素
.sxwl-page-toolbar { ... }    // 工具栏子元素
```

#### 规则 3：状态修饰符用 `-` 前缀

```scss
.sxwl-button-primary { ... }  // 主要按钮变体
.sxwl-button-danger { ... }   // 危险按钮变体
.sxwl-button-disabled { ... } // 禁用状态
```

#### 规则 4：伪类选择器缩进

```scss
.sxwl-button {
  color: $sxwl-color-text;
  
  &:hover {
    color: $sxwl-color-primary;
  }
  
  &:active {
    color: $sxwl-color-primary-active;
  }
  
  &.is-loading {
    opacity: 0.6;
  }
}
```

---

### 5.3 全局组件覆盖规范

#### Ant Design 组件覆盖

```scss
// ============================================
// antd Modal 覆盖（Portal 渲染到 body）
// ============================================
.ant-modal {
  &-content {
    overflow: hidden;
  }

  &-header {
    margin-bottom: 0;
    padding: 16px 24px;
    border-bottom: 1px solid $sxwl-color-border-secondary;
    background: $sxwl-color-bg-container;
  }

  &-title {
    color: $sxwl-color-text-heading;
    font-size: 16px;
    font-weight: 600;
  }

  &-close {
    color: $sxwl-color-text-tertiary;
    transition: color $sxwl-dur-fast;

    &:hover {
      color: $sxwl-color-primary !important;
    }
  }

  &-body {
    padding: 20px 24px;
  }

  &-footer {
    margin-top: 0;
    padding: 12px 24px;
    border-top: 1px solid $sxwl-color-border-secondary;
  }
}
```

#### 通用规则

- ✅ Portal 组件（Modal、Drawer、Dropdown）必须在 `global.scss` 中覆盖
- ✅ 使用 `&-子元素` 嵌套语法，避免全局污染
- ✅ 自定义属性必须加 `!important`（如 `background: #fafafa !important`）

---

## 六、页面骨架规范

### 6.1 SxwlPage 组件（CRUD 页面标准模板）

```scss
// src/components/SxwlPage/index.scss

// 页面根容器：撑满 Content
.sxwl-page-wrapper {
  height: calc(100vh - 64px - 48px); // Header + Content margin
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: sxwl-rise-soft 0.3s cubic-bezier(0.22, 1, 0.36, 1) both;
}

// 面包屑
.sxwl-page-breadcrumb {
  margin-bottom: 16px;
  flex-shrink: 0;
  font-size: 13px;
  color: $sxwl-color-text-tertiary;
  animation: sxwl-rise-soft 0.3s cubic-bezier(0.22, 1, 0.36, 1) 0.05s both;
}

// 工具栏
.sxwl-page-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
  flex-shrink: 0;
  animation: sxwl-rise-soft 0.3s cubic-bezier(0.22, 1, 0.36, 1) 0.1s both;
}

// 表格卡片（白底 + 细边框 + L1 阴影）
.sxwl-page-table-card {
  border-radius: $sxwl-radius-lg;
  background: $sxwl-color-bg-container;
  border: 1px solid $sxwl-color-border-secondary;
  box-shadow: $sxwl-shadow-card;
  flex: 1;
  min-height: 0;
  overflow: hidden;
  animation: sxwl-rise-soft 0.3s cubic-bezier(0.22, 1, 0.36, 1) 0.15s both;

  // 表头浅灰底 + 加粗
  .ant-table-thead > tr > th {
    background: #fafafa;
    font-weight: 600;
  }

  // 行 hover
  .ant-table-tbody > tr:hover > td {
    background: #fafafa !important;
  }

  // 选中行
  .ant-table-row-selected > td {
    background: $sxwl-color-primary-1 !important;
  }
}
```

### 6.2 纵向滚动策略

```scss
// 计算表格纵向滚动高度
.table-scroll-y {
  y: calc(100vh - #{Header + Margin + Breadcrumb + Card + Pagination});
}

// 预留高度计算
reserve = 64 (Header) + 48 (Margin) + 40 (Breadcrumb) + 40 (Card Padding) + 60 (Pagination)
reserve = 252px (无搜索表单)
reserve += 88px (有搜索表单)
reserve += 52px (有工具栏)
```

---

## 七、布局规范

### 7.1 响应式断点

| 设备类型 | 断点 | 适用场景 |
|---------|------|---------|
| Mobile | `< 576px` | 暂不支持（后台管理系统） |
| Tablet | `576px - 992px` | 适配小窗口（可考虑） |
| Desktop | `≥ 992px` | 主要支持（推荐 1440px+） |

### 7.2 Flexbox 布局

```scss
.container {
  display: flex;
  flex-direction: row; // 或 column
  justify-content: space-between; // flex-start | center | flex-end
  align-items: center; // stretch | flex-start | flex-end
  gap: $sxwl-space-4;
}
```

### 7.3 Grid 布局

```scss
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); // 两列等分
  gap: $sxwl-space-4;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr; // 移动端单列
  }
}
```

---

## 八、最佳实践

### ✅ 应该做的

1. **使用 Token 变量**
   ```scss
   // ✅ 正确
   .my-button {
     color: $sxwl-color-primary;
     border-radius: $sxwl-radius-sm;
   }
   
   // ❌ 错误
   .my-button {
     color: #1677FF;
     border-radius: 6px;
   }
   ```

2. **就近维护样式**
   ```
   ✅ src/components/SxwlUser/
       ├── index.tsx
       └── index.scss
   ```

3. **分层组织 SCSS**
   ```scss
   // ✅ 正确：按功能块注释分组
   // ============================================
   // 页面根容器
   // ============================================
   .page-wrapper { ... }
   
   // ============================================
   // 搜索表单区
   // ============================================
   .search-section { ... }
   
   // ============================================
   // 工具栏
   // ============================================
   .toolbar { ... }
   ```

4. **使用 Mixin 复用**
   ```scss
   .scrollable-area {
     @include scrollbar-thin();
   }
   ```

### ❌ 不应该做的

1. **禁止硬编码颜色/圆角/阴影**
   ```scss
   // ❌ 错误
   .card {
     background: #ffffff;
     box-shadow: 0 1px 2px rgba(16,24,40,.04);
   }
   
   // ✅ 正确
   .card {
     background: $sxwl-color-bg-container;
     box-shadow: $sxwl-shadow-card;
   }
   ```

2. **禁止全局选择器（除 html/body）**
   ```scss
   // ❌ 错误
   button { color: red; }
   div { margin: 0; }
   
   // ✅ 正确
   .my-button { color: $sxwl-color-danger; }
   .container { margin: 0; }
   ```

3. **禁止直接修改 Ant Design 源码**
   ```scss
   // ❌ 错误
   .ant-btn-primary { background: blue !important; }
   
   // ✅ 正确（通过 ConfigProvider 主题注入）
   :root { --ant-primary-color: blue; }
   ```

---

## 九、迁移指南（CSS-in-JS → SCSS）

### 当前过渡期说明

项目目前处于 **antd-style（CSS-in-JS）→ SCSS** 回迁阶段：

- ✅ **已完成**：`theme.token.ts`（Ant Design Token）、`variables.scss`（设计令牌）
- ⚠️ **待迁移**：以下组件仍需从 `.style.ts` 迁移回 `.scss`：
  - `SxwlFormModal`
  - `SxwlPage`
  - `SxwlSearchForm`
  - `Dashboard`
  - `SxwlLayout`
  - `HeaderNotice`
  - `Login`
  - `ServerMonitor`

### 迁移步骤

#### 第 1 步：创建 SCSS 文件

```typescript
// src/components/SxwlComponent/index.style.ts（旧的 CSS-in-JS 文件）

// → 删除此文件，改为：
// src/components/SxwlComponent/index.scss（新的 SCSS 文件）
```

#### 第 2 步：转换样式规则

```typescript
// ❌ 旧的 CSS-in-JS
import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ token, css }) => ({
  wrapper: css`
    padding: ${token.padding}px;
    background: ${token.colorBgContainer};
  `,
}));
```

```scss
// ✅ 新的 SCSS
@use "../../styles/variables" as *;

.sxwl-component-wrapper {
  padding: $sxwl-space-4;
  background: $sxwl-color-bg-container;
}
```

#### 第 3 步：更新 TSX 引用

```tsx
// ❌ 旧的导入
import { useStyles } from './index.style';

export function SxwlComponent() {
  const styles = useStyles();
  return <div className={styles.wrapper}>...</div>;
}
```

```tsx
// ✅ 新的导入
import './index.scss';

export function SxwlComponent() {
  return <div className="sxwl-component-wrapper">...</div>;
}
```

#### 第 4 步：验证编译

```bash
cd sxwl-react
npm run build
```

---

## 十、常见问题（FAQ）

### Q1：为什么不用 Tailwind CSS？

**答**：Tailwind 的原子类会导致 HTML 冗长，不利于代码审查和维护。SCSS 的语义化类名更清晰（如 `sxwl-page-breadcrumb` 比 `flex items-center gap-2` 更易理解）。

### Q2：为什么不继续使用 antd-style？

**答**：
1. 运行时 CSS-in-JS 有性能开销（每次渲染需解析 JS 对象）
2. 无法享受 Vite 预编译优势
3. SCSS 生态成熟（IDE 自动补全、Linter 检查完善）

### Q3：如何确保 Token 一致性？

**答**：
- `variables.scss`（编译期）和 `theme.token.ts`（运行期）两处值必须一致
- 修改时需同时更新两处
- 定期运行 `npm run lint:style` 检查硬编码颜色

### Q4：复杂组件（如富文本编辑器）如何处理？

**答**：
- 第三方库自带样式（TipTap、ECharts）保留原样
- 仅在需要覆盖时使用 `global.scss`
- 不要强行引入设计 Token

---

## 十一、附录

### A. 颜色对比度检查

遵循 WCAG 2.1 AA 标准：
- 正文字号 ≥ 16px：对比度 ≥ 4.5:1
- 正文字号 < 16px：对比度 ≥ 7:1

### B. 无障碍建议

- 焦点可见：`outline` 始终保留（除非 `:focus-visible` 判断）
- 色盲友好：不仅用颜色传达信息（如 `success` 用 `✓` 图标辅助）
- 字体缩放：支持浏览器缩放至 200% 仍可用

### C. 参考文档

- [Ant Design v5 主题定制](https://ant.design/docs/spec/inject-cn)
- [Sass 官方文档](https://sass-lang.com/documentation)
- [MDN 色彩指南](https://developer.mozilla.org/en-US/docs/Web/CSS/color_value)

---

**本文档由 AI Agent 生成，与代码同步更新。**
**最后更新：2026-09-12**
