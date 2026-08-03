// ============================================
// sxwl-react 自定义品牌 Token
// antd Design Token 未覆盖的自定义值集中管理
// 换产品时只需改此文件 + ConfigProvider
// ============================================

export const SXWL_TOKENS = {
  /** 品牌色渐变 */
  gradientBrand: 'linear-gradient(135deg, #DE5F0E 0%, #f0972d 100%)',
  /** 文字色：标题 */
  colorTextHeading: '#1a1a2e',
  /** 文字色：正文（antd 默认覆盖） */
  colorText: '#333333',
  /** 文字色：说明 */
  colorTextSecondary: '#64748b',
  /** 文字色：置灰 */
  colorTextTertiary: '#94a3b8',
  /** 文字色：禁用 */
  colorTextDisabled: '#cbd5e1',

  /** 背景色：页面（浅色渐变 + 淡橙光晕，对齐 login 原型） */
  colorBgLayout:
    'radial-gradient(50% 45% at 12% 8%, rgba(222,95,14,.08) 0%, transparent 60%),' +
    'radial-gradient(45% 45% at 90% 85%, rgba(240,151,45,.07) 0%, transparent 60%),' +
    'linear-gradient(160deg, #f7f9fc 0%, #eef2f8 100%)',
  /** 背景色：容器 */
  colorBgContainer: '#ffffff',

  /** 边框色：次级 */
  colorBorderSecondary: '#e2e8f0',
  /** 边框色：强调（卡片描边，对齐原型 #e2e8f0） */
  colorBorder: '#e2e8f0',

  /** 圆角：小元素（标签、inline-code） */
  borderRadiusSM: 8,
  /** 圆角：容器（Card、Table、Pre）对齐原型 16px */
  borderRadius: 12,
  /** 圆角：大容器（卡片、弹窗）对齐原型 16px */
  borderRadiusLG: 16,
  /** 圆角：超大（弹窗 Modal） */
  borderRadiusXL: 20,

  /** 阴影：卡片（对齐原型 0 6px 24px rgba(26,26,46,.06)） */
  shadowCard: '0 6px 24px rgba(26,26,46,.06)',
  /** 阴影：下拉/弹窗 */
  shadowPopup: '0 8px 24px rgba(26,26,46,.10)',
  /** 阴影：模态框 */
  shadowModal: '0 18px 50px rgba(26,26,46,.22)',

  /** 动画时长：hover/过渡 */
  animationDurationFast: '0.2s',
  /** 动画时长：结构变化 */
  animationDurationNormal: '0.3s',

  /** 字重：标题 */
  fontWeightHeading: 500,
  /** 字重：强调 */
  fontWeightStrong: 600,
  /** 字重：正文 */
  fontWeightRegular: 400,

  /** 品牌色 hover */
  colorPrimaryHover: '#f57020',
  /** 品牌色 active */
  colorPrimaryActive: '#c05008',
} as const;
