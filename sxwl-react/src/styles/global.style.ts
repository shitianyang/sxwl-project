// ============================================
// sxwl-react 全局样式（antd-style）
// 用于 #root 容器内样式覆盖 + 全局重置
// 滚动条等全局效果通过 App.tsx 注入
// ============================================

import { createStyles } from 'antd-style';

const useGlobalStyles = createStyles(({ token, css }) => ({
  /**
   * 应用到 #root 容器的全局样式
   * 包含字体、背景色、滚动条等
   */
  root: css`
    height: 100%;

    // HTML 文本渲染优化
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;

    // 全局重置
    *, *::before, *::after {
      margin: 0;
      box-sizing: border-box;
    }

    a {
      color: ${token.colorPrimary};
      text-decoration: none;
      transition: color 0.2s;
    }

    a:hover {
      color: ${token.colorPrimaryHover};
    }

    // 滚动条美化
    ::-webkit-scrollbar {
      width: 6px;
      height: 6px;
    }
    ::-webkit-scrollbar-track {
      background: transparent;
    }
    ::-webkit-scrollbar-thumb {
      background: #e2e8f0;
      border-radius: 3px;
    }
    ::-webkit-scrollbar-thumb:hover {
      background: #94a3b8;
    }
  `,
}));

export default useGlobalStyles;
