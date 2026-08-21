// ============================================
// sxwl-react 自定义品牌 Token
// 对齐 docs/UI设计规范.md v2.0（Web 端 · Pro 骨架 + 品牌橙点缀）
// 仅保留被实际引用的字段（App.tsx ConfigProvider 使用），已按"无冗余"原则清理
// 自定义样式层变量见 variables.scss（编译期），两处值必须保持一致
// ============================================

export const SXWL_TOKENS = {
  /** 文字色：正文（规范中性阶 1） */
  colorText: '#1F2329',
  /** 文字色：次级（规范中性阶 2） */
  colorTextSecondary: '#4E5969',
  /** 文字色：弱化（规范中性阶 3） */
  colorTextTertiary: '#86909C',
  /** 文字色：禁用/占位（规范中性阶 4） */
  colorTextDisabled: '#C9CDD4',

  /** 背景色：页面（Pro 浅灰底 #F0F2F5） */
  colorBgLayout: '#F0F2F5',
  /** 背景色：容器 */
  colorBgContainer: '#ffffff',

  /** 边框色：次级（表格内分割） */
  colorBorderSecondary: '#F0F0F0',
  /** 边框色：常规控件 */
  colorBorder: '#E5E6EB',

  /** 圆角：按钮/小元素 */
  borderRadiusSM: 6,
  /** 圆角：输入框/表格 */
  borderRadius: 8,
  /** 圆角：卡片/弹窗 */
  borderRadiusLG: 12,
  /** 圆角：超大容器（Modal） */
  borderRadiusXL: 16,

  /** 阴影：L1 卡片常驻 */
  shadowCard: '0 1px 2px rgba(16,24,40,.04), 0 1px 3px rgba(16,24,40,.06)',
  /** 阴影：L2 悬浮 */
  shadowPopup: '0 4px 12px rgba(16,24,40,.08), 0 2px 6px rgba(16,24,40,.05)',

  /** 品牌色 hover（橙阶 400 亮） */
  colorPrimaryHover: '#F57020',
  /** 品牌色 active（橙阶 600 深） */
  colorPrimaryActive: '#C74E08',
} as const;
