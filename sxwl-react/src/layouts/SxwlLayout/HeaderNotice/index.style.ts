// ============================================
// HeaderNotice — 通知铃铛样式
// ============================================

import { createStyles } from 'antd-style';

const useNoticeStyles = createStyles(({ css }) => ({
  // ===== Header 通知铃铛 =====

  badge: css`
    margin-right: 16px;
    cursor: pointer;
    line-height: 64px;
  `,

  trigger: css`
    display: inline-flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
  `,

  btn: css`
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    height: 64px;
    width: 40px;
    border-radius: 0;

    &:hover {
      background: rgba(0, 0, 0, 0.04);
    }
  `,

  // ===== Popover 弹窗 =====

  overlay: css`
    .ant-popover-inner {
      padding: 0;
    }
  `,

  popover: css`
    width: 360px;
    max-height: 480px;
    display: flex;
    flex-direction: column;
  `,

  popoverHeader: css`
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    border-bottom: 1px solid #f0f0f0;
  `,

  popoverLoading: css`
    display: flex;
    justify-content: center;
    padding: 32px 0;
  `,

  // ===== 公告列表 =====

  list: css`
    overflow-y: auto;
    max-height: 380px;
  `,

  listItem: css`
    padding: 10px 16px;
    cursor: pointer;
    transition: background 0.2s;

    &.unread {
      background: #f6ffed;
    }

    &:hover {
      background: #fafafa;
    }
  `,

  listItemTitle: css`
    display: flex;
    align-items: center;
    gap: 4px;
    margin-bottom: 2px;
    line-height: 22px;
  `,

  listItemDesc: css`
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 2px;
  `,

  dot: css`
    display: inline-block;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #ff4d4f;
    vertical-align: middle;
    margin-right: 4px;
    flex-shrink: 0;
  `,

  // ===== SSE 连接状态点 =====

  sseWrapper: css`
    display: inline-flex;
    align-items: center;
    line-height: 64px;
    margin-right: 4px;
    cursor: default;
  `,

  sseDot: css`
    display: inline-block;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    vertical-align: middle;
    flex-shrink: 0;
    transition: background 0.3s;

    &.connected {
      background: #52c41a;
      box-shadow: 0 0 4px rgba(82, 196, 26, 0.6);
    }

    &.connecting {
      background: #faad14;
      animation: sxwl-sse-pulse 1s ease-in-out infinite;
    }

    &.disconnected {
      background: #ff4d4f;
    }
  `,

  // ===== 关键帧动画 =====

  '@keyframes sxwl-sse-pulse': {
    '0%, 100%': { opacity: 1 },
    '50%': { opacity: 0.4 },
  },
}));

export default useNoticeStyles;
