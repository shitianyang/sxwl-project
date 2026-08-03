// ============================================
// SxwlLayout — 全局布局样式（方案 C：浅色 Sider + 品牌橙激活）
// ============================================

import { createStyles } from 'antd-style';

const useLayoutStyles = createStyles(({ token, css }) => ({
  layout: css`
    min-height: 100vh;
    // 完整页面背景：浅色渐变 + 淡橙光晕 + 极淡网格（对齐 login 原型）
    background:
      radial-gradient(50% 45% at 12% 8%, rgba(222, 95, 14, 0.08) 0%, transparent 60%),
      radial-gradient(45% 45% at 90% 85%, rgba(240, 151, 45, 0.07) 0%, transparent 60%),
      linear-gradient(rgba(26, 26, 46, 0.02) 1px, transparent 1px),
      linear-gradient(90deg, rgba(26, 26, 46, 0.02) 1px, transparent 1px),
      linear-gradient(160deg, #f7f9fc 0%, #eef2f8 100%);
    background-size: auto, auto, 48px 48px, 48px 48px, auto;
  `,

  // -------- Sider：浅色底 --------

  sider: css`
    overflow-x: hidden;
    overflow-y: auto;
    // 滚动条悬浮不占位，避免挤压菜单内容宽度（overlay 滚动条）
    scrollbar-gutter: stable;
    height: 100vh;
    position: sticky;
    top: 0;
    left: 0;
    bottom: 0;
    background: #fbf9f7 !important;
    border-right: 1px solid #f0ebe5;
    // 折叠/展开宽度过渡（配合 antd Sider 内置动画）
    transition: width 0.2s cubic-bezier(0.645, 0.045, 0.355, 1), background 0.2s;

    // 悬浮滚动条美化：极细 + 悬浮于内容之上，不占布局宽度
    &::-webkit-scrollbar {
      width: 4px;
    }
    &::-webkit-scrollbar-track {
      background: transparent;
    }
    &::-webkit-scrollbar-thumb {
      background: rgba(148, 163, 184, 0.4);
      border-radius: 2px;
    }
    &::-webkit-scrollbar-thumb:hover {
      background: rgba(148, 163, 184, 0.7);
    }

    // 侧边栏 antd Menu 整体覆盖
    .ant-menu {
      background: transparent;
      border-inline-end: none !important;
      padding: 4px 10px 16px;

      // 一级菜单项
      .ant-menu-item {
        height: 40px;
        line-height: 40px;
        margin: 4px 0;
        border-radius: 8px;
        color: #5f5e5a;
        font-size: 14px;

        .ant-menu-item-icon {
          font-size: 16px;
          color: #b0aca5;
          transition: color 0.2s;
        }

        &:hover {
          background: #f3ece4;
          color: #DE5F0E;
          .ant-menu-item-icon {
            color: #DE5F0E;
          }
        }

        // 激活项：品牌橙渐变底 + 白字
        &.ant-menu-item-selected {
          background: linear-gradient(135deg, #DE5F0E 0%, #f0972d 100%);
          color: #fff;
          font-weight: 500;
          box-shadow: 0 4px 12px rgba(222, 95, 14, 0.28);

          .ant-menu-item-icon {
            color: #fff;
          }
        }
      }

      // 子菜单标题（目录）
      .ant-menu-submenu-title {
        height: 40px;
        line-height: 40px;
        margin: 4px 0;
        border-radius: 8px;
        color: #5f5e5a;
        font-size: 14px;

        .ant-menu-item-icon {
          font-size: 16px;
          color: #b0aca5;
        }

        &:hover {
          color: #DE5F0E;
          background: #f3ece4;
          .ant-menu-item-icon {
            color: #DE5F0E;
          }
        }
      }

      // 子菜单展开容器
      .ant-menu-sub {
        background: transparent !important;
        .ant-menu-item {
          padding-left: 40px !important;
        }
      }

      // 目录展开态高亮
      .ant-menu-submenu-open > .ant-menu-submenu-title {
        color: #DE5F0E;
        .ant-menu-item-icon {
          color: #DE5F0E;
        }
      }
    }
  `,

  // -------- Logo 区：品牌橙渐变图标 + 深色文字 --------

  logo: css`
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    gap: 10px;
    white-space: nowrap;
    overflow: hidden;
    border-bottom: 1px solid #f0ebe5;
    margin-bottom: 8px;
  `,

  logoIcon: css`
    width: 34px;
    height: 34px;
    flex-shrink: 0;
    object-fit: contain;
    border-radius: 8px;
  `,

  logoText: css`
    font-size: 18px;
    font-weight: 700;
    color: #1a1a2e;
    letter-spacing: 2px;
  `,

  // -------- Header：透明背景，与 Content 统一 --------

  header: css`
    padding: 0 24px 0 0 !important;
    display: flex;
    align-items: center;
    justify-content: space-between;
    // 与 Sider 保持一致：浅暖色，形成统一的导航框架
    background: #fbf9f7;
    border-bottom: 1px solid #f0ebe5;
    position: sticky;
    top: 0;
    z-index: 9;
    height: 64px;
    line-height: 64px;
  `,

  headerLeft: css`
    display: flex;
    align-items: center;

    // 折叠按钮：hover 品牌橙
    .ant-btn {
      color: #5f5e5a;
      transition: all 0.2s;

      &:hover {
        color: #DE5F0E;
        background: #f3ece4;
      }
    }
  `,

  headerCenter: css`
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #5f5e5a;
    letter-spacing: 0.3px;
    user-select: none;

    // SxwlClock（全局类，此处覆盖）
    :global(.sxwl-header-clock) {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      color: #5f5e5a;

      // 时间前加品牌橙圆点点缀
      &::before {
        content: '';
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: linear-gradient(135deg, #DE5F0E 0%, #f0972d 100%);
        box-shadow: 0 0 6px rgba(222, 95, 14, 0.35);
      }
    }
  `,

  headerRight: css`
    display: flex;
    align-items: center;
    gap: 4px;
  `,

  userDropdown: css`
    cursor: pointer;
    padding: 4px 12px;
    border-radius: 8px;
    transition: all 0.2s;

    .anticon {
      color: #5f5e5a;
      transition: color 0.2s;
    }

    &:hover {
      background: #f3ece4;

      .anticon {
        color: #DE5F0E;
      }
    }
  `,

  content: css`
    margin: 24px;
    min-height: calc(100vh - 64px - 48px);
  `,
}));

export default useLayoutStyles;
