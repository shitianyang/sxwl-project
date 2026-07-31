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

  /** 背景色：页面 */
  colorBgLayout: '#f8fafc',
  /** 背景色：容器 */
  colorBgContainer: '#ffffff',

  /** 边框色：次级 */
  colorBorderSecondary: '#e2e8f0',

  /** 圆角：小元素（标签、inline-code） */
  borderRadiusSM: 4,
  /** 圆角：容器（Card、Table、Pre） */
  borderRadius: 8,

  /** 阴影：卡片 */
  shadowCard: '0 1px 3px rgba(0, 0, 0, 0.04), 0 8px 24px rgba(0, 0, 0, 0.06)',
  /** 阴影：下拉/弹窗 */
  shadowPopup: '0 4px 12px rgba(0, 0, 0, 0.08)',
  /** 阴影：模态框 */
  shadowModal: '0 8px 24px rgba(0, 0, 0, 0.12)',

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
