// ============================================
// SxwlSearchForm — 搜索表单样式
// ============================================

import { createStyles } from 'antd-style';

const useSearchFormStyles = createStyles(({ token, css }) => ({
  wrapper: css`
    margin-bottom: 16px;
    padding: 16px 20px;
    background: ${token.colorBgContainer};
    border-radius: ${token.borderRadiusLG}px;
    border: 1px solid ${token.colorBorderSecondary};
    box-shadow: ${token.shadowCard};
  `,

  inner: css`
    display: flex;
    flex-wrap: wrap;
    gap: 14px 22px;
    align-items: center;

    .ant-form-item {
      margin-bottom: 0;
    }

    .ant-form-item-label {
      padding-right: 8px;
    }

    .ant-form-item-label > label {
      color: ${token.colorTextSecondary};
      font-size: 13px;
    }

    .ant-select {
      min-width: 140px;
    }
  `,
}));

export default useSearchFormStyles;
