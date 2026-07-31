// ============================================
// SxwlSearchForm — 搜索表单样式
// ============================================

import { createStyles } from 'antd-style';

const useSearchFormStyles = createStyles(({ token, css }) => ({
  wrapper: css`
    margin-bottom: 16px;
    padding: 16px 24px;
    background: ${token.colorBgContainer};
    border-radius: ${token.borderRadiusLG}px;
  `,

  inner: css`
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;

    .ant-form-item {
      margin-bottom: 0;
    }

    .ant-select {
      min-width: 120px;
    }
  `,
}));

export default useSearchFormStyles;
