// ============================================
// SxwlFormModal — 表单弹窗样式
// ============================================

import { createStyles } from 'antd-style';

const useFormModalStyles = createStyles(({ css }) => ({
  form: css`
    margin-top: 16px;

    // vertical 布局下减小表单项间距，更紧凑
    .ant-form-item {
      margin-bottom: 16px;
    }
  `,
}));

export default useFormModalStyles;
