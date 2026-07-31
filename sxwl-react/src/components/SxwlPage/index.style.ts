// ============================================
// SxwlPage — CRUD 页面骨架样式
// ============================================

import { createStyles } from 'antd-style';

const usePageStyles = createStyles(({ token, css }) => ({
  wrapper: css`
    // 页面根容器
  `,

  // -------- 面包屑 --------

  breadcrumb: css`
    margin-bottom: 16px;
    font-size: ${token.fontSizeSM}px;
    color: ${token.colorTextTertiary};
  `,

  breadcrumbSep: css`
    margin: 0 6px;
    color: ${token.colorTextQuaternary};
  `,

  breadcrumbItem: css`
    &.is-current {
      color: ${token.colorText};
      font-weight: 500;
    }
  `,

  // -------- 工具栏 --------

  toolbar: css`
    display: flex;
    justify-content: flex-end;
    margin-bottom: 12px;
  `,

  // -------- 表格卡片 --------

  tableCard: css`
    border-radius: ${token.borderRadiusLG}px;

    .ant-card-body {
      padding: 20px 24px;

      .ant-table-wrapper {
        .ant-table {
          .ant-table-container {
            border-radius: 0 0 ${token.borderRadiusLG}px ${token.borderRadiusLG}px;
          }
        }
      }
    }
  `,
}));

export default usePageStyles;
