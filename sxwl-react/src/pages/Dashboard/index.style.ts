// ============================================
// Dashboard — 工作台样式
// ============================================

import { createStyles } from 'antd-style';

const useDashboardStyles = createStyles(({ token, css }) => ({
  // ===== 页面容器 =====

  page: css`
    // 占满整个内容区域，不设置 max-width
  `,

  // ===== 欢迎横幅 =====

  banner: css`
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
    border-radius: ${token.borderRadiusLG}px;
    padding: 28px 32px;
    margin-bottom: 16px;
  `,

  bannerContent: css`
    display: flex;
    align-items: center;
    justify-content: space-between;
  `,

  bannerTitle: css`
    color: #fff !important;
    margin-bottom: 4px !important;
  `,

  bannerDesc: css`
    color: rgba(255, 255, 255, 0.55);
    font-size: ${token.fontSizeSM}px;
  `,

  bannerTip: css`
    display: flex;
    align-items: center;
    gap: 6px;
    color: #22c55e;
    font-size: ${token.fontSizeSM}px;
    padding: 6px 14px;
    background: rgba(34, 197, 94, 0.1);
    border-radius: 20px;

    &.banner-tip--error {
      color: #ef4444;
      background: rgba(239, 68, 68, 0.1);
    }
  `,

  // ===== 统计卡片 =====

  stats: css`
    margin-bottom: 16px;
  `,

  statCard: css`
    border-radius: ${token.borderRadiusLG}px;
    transition: box-shadow 0.2s, transform 0.2s;

    &:hover {
      box-shadow: ${token.boxShadowSecondary};
      transform: translateY(-2px);
    }
  `,

  statCardBody: css`
    display: flex;
    align-items: center;
    gap: 16px;
  `,

  statCardIcon: css`
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    flex-shrink: 0;
  `,

  statCardContent: css`
    flex: 1;
    min-width: 0;
  `,

  statCardValue: css`
    font-size: ${token.fontSizeHeading2}px;
    font-weight: 700;
    color: ${token.colorText};
    line-height: 1.2;
  `,

  statCardLabel: css`
    font-size: ${token.fontSizeSM}px;
    color: ${token.colorTextTertiary};
    margin-top: 2px;
  `,

  // ===== 快捷入口 =====

  quickLinks: css`
    border-radius: ${token.borderRadiusLG}px;
  `,

  sectionTitle: css`
    font-size: ${token.fontSize}px;
    font-weight: 600;
    color: ${token.colorText};
  `,

  quickLinkItem: css`
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 16px 8px;
    border-radius: ${token.borderRadius}px;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: ${token.colorBgElevated};
    }
  `,

  quickLinkIcon: css`
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
  `,

  quickLinkLabel: css`
    font-size: ${token.fontSizeSM}px;
    color: ${token.colorTextSecondary};
    text-align: center;
  `,
}));

export default useDashboardStyles;
