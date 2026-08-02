// ============================================
// SxwlPage — CRUD 页面骨架样式
// ============================================

import { createStyles } from 'antd-style';

const usePageStyles = createStyles(({ token, css }) => ({
  wrapper: css`
    // 页面根容器
  `,

  // -------- 面包屑（对齐原型） --------

  breadcrumb: css`
    margin-bottom: 16px;
    font-size: 13px;
    color: ${token.colorTextTertiary};
  `,

  breadcrumbSep: css`
    margin: 0 8px;
    color: ${token.colorTextQuaternary};
  `,

  breadcrumbItem: css`
    &.is-current {
      color: ${token.colorPrimary};
      font-weight: 600;
    }
  `,

  // -------- 工具栏 --------

  toolbar: css`
    display: flex;
    justify-content: flex-end;
    margin-bottom: 12px;
  `,

  // -------- 表格卡片（对齐原型：白卡片 + 大圆角 + 柔和阴影） --------

  tableCard: css`
    border-radius: ${token.borderRadiusLG}px;
    box-shadow: ${token.shadowCard};

    .ant-card-body {
      padding: 18px 20px;

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
