# sxwl Design Tokens 速查表

> 🎨 御权 RBAC 权限管理平台 - 设计令牌快速参考

---

## 🔴 品牌色（Primary）

```scss
$sxwl-color-primary:       #1677FF;  // 主按钮、链接
$sxwl-color-primary-hover: #3A8BFF;  // 悬停态
$sxwl-color-primary-active:#0E5FD8;  // 点击态
$sxwl-color-primary-1:     #E8F1FF;  // 选中背景
```

### 成功色（Success）

```scss
$sxwl-color-success:   #52C41A;  // 成功提示
$sxwl-color-success-1: #F6FFED;  // 成功背景
```

### 警告色（Warning）

```scss
$sxwl-color-warning:   #FAAD14;  // 警告提示
$sxwl-color-warning-1: #FFFBE6;  // 警告背景
```

### 危险色（Danger）

```scss
$sxwl-color-danger:   #FF4D4F;  // 删除按钮
$sxwl-color-danger-1: #FFF2F0;  // 危险背景
```

---

## ⚪ 中性色（Neutral Tone）

```scss
$sxwl-color-text-heading:      #1F2329;  // 标题
$sxwl-color-text:              #1F2329;  // 正文
$sxwl-color-text-secondary:    #4E5969;  // 次要文本
$sxwl-color-text-tertiary:     #86909C;  // 提示文本
$sxwl-color-text-quaternary:   #C9CDD4;  // 禁用文本
```

### 背景色

```scss
$sxwl-color-bg-container: #FFFFFF;  // 卡片、弹窗
$sxwl-bg-layout:          #F0F2F5;  // 页面底色
```

### 边框色

```scss
$sxwl-color-border:            #E5E6EB;  // 输入框
$sxwl-color-border-secondary: #F0F0F0;  // 表格行线
```

---

## ◯ 圆角（Radius）

| 级别 | 变量名 | 值 |
|------|--------|-----|
| SM | `$sxwl-radius-sm` | `6px` |
| M | `$sxwl-radius` | `8px` |
| LG | `$sxwl-radius-lg` | `12px` |
| XL | `$sxwl-radius-xl` | `16px` |

---

## ▢ 阴影（Shadow）

| 级别 | 变量名 | 适用场景 |
|------|--------|---------|
| L1 | `$sxwl-shadow-card` | 卡片常驻 |
| L2 | `$sxwl-shadow-popup` | 悬浮增强 |
| L3 | `$sxwl-shadow-modal` | 弹窗层 |
| Float | `$sxwl-shadow-float` | 玻璃质感 |

```scss
// 复制完整值
$shadow-card: 0 1px 2px rgba(16,24,40,.04), 0 1px 3px rgba(16,24,40,.06);
$shadow-popup: 0 4px 12px rgba(16,24,40,.08), 0 2px 6px rgba(16,24,40,.05);
$shadow-modal: 0 18px 44px rgba(16,24,40,.14), 0 6px 16px rgba(16,24,40,.10);
$shadow-float: 0 24px 60px -20px rgba(22,60,130,.35);
```

---

## ⊞ 间距（Spacing）

| 级别 | 变量名 | 值 |
|------|--------|-----|
| 1 | `$sxwl-space-1` | `4px` |
| 2 | `$sxwl-space-2` | `8px` |
| 3 | `$sxwl-space-3` | `12px` |
| 4 | `$sxwl-space-4` | `16px` |
| 5 | `$sxwl-space-5` | `20px` |
| 6 | `$sxwl-space-6` | `24px` |
| 8 | `$sxwl-space-8` | `32px` |
| 10 | `$sxwl-space-10` | `40px` |
| 12 | `$sxwl-space-12` | `48px` |

---

## A 字号（Typography）

```scss
$sxwl-font-size:         14px;  // 正文字号
$sxwl-font-size-sm:      13px;  // 次要文本
$sxwl-font-size-h2:      24px;  // 二级标题
```

---

## ↻ 动画时长（Duration）

```scss
$sxwl-dur-fast:  0.15s;  // 悬停反馈
$sxwl-dur:       0.3s;   // 展开/收起
```

---

## ✦ 特殊效果

### 聚焦光晕

```scss
$sxwl-focus-ring: 0 0 0 3px rgba(22, 119, 255, 0.12);
```

### 玻璃质感

```scss
$sxwl-glass-light:        rgba(255, 255, 255, 0.55);
$sxwl-glass-light-strong: rgba(255, 255, 255, 0.72);
$sxwl-glass-blue:         rgba(22, 119, 255, 0.16);
$sxwl-glass-blue-strong:  rgba(22, 119, 255, 0.32);
$sxwl-glass-border:       rgba(255, 255, 255, 0.65);
$sxwl-glass-blur:         22px;
```

---

## 📝 引用方式

```scss
@use "../../styles/variables" as *;

.my-component {
  color: $sxwl-color-primary;
  border-radius: $sxwl-radius-sm;
  box-shadow: $sxwl-shadow-card;
  padding: $sxwl-space-4;
  transition: color $sxwl-dur-fast;
}
```

---

**📖 完整规范详见 [SPECIFICATION.md](./SPECIFICATION.md)**
