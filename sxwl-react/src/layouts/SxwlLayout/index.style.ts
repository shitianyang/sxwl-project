// ============================================
// SxwlLayout — 全局布局样式
// ============================================

import { createStyles } from 'antd-style';

const useLayoutStyles = createStyles(({ token, css }) => ({
  layout: css`
    min-height: 100vh;
  `,

  sider: css`
    overflow: auto;
    height: 100vh;
    position: sticky;
    top: 0;
    left: 0;
    bottom: 0;
  `,

  logo: css`
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    gap: 12px;
    color: #fff;
    font-size: 18px;
    font-weight: 700;
    white-space: nowrap;
    overflow: hidden;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  `,

  logoIcon: css`
    width: 36px;
    height: 36px;
    flex-shrink: 0;
    object-fit: contain;
  `,

  logoText: css`
    font-size: 18px;
    letter-spacing: 2px;
  `,

  header: css`
    padding: 0 24px 0 0 !important;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    position: sticky;
    top: 0;
    z-index: 9;
    height: 64px;
    line-height: 64px;
  `,

  headerLeft: css`
    display: flex;
    align-items: center;
  `,

  headerCenter: css`
    display: flex;
    align-items: center;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    user-select: none;
  `,

  headerRight: css`
    display: flex;
    align-items: center;
    gap: 4px;
  `,

  userDropdown: css`
    cursor: pointer;
    padding: 4px 12px;
    border-radius: 6px;
    transition: background 0.2s;

    &:hover {
      background: rgba(0, 0, 0, 0.04);
    }
  `,

  content: css`
    margin: 24px;
    min-height: calc(100vh - 64px - 48px);
    background: transparent;
  `,
}));

export default useLayoutStyles;
