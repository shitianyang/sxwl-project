// ============================================
// SxwlPage — CRUD 页面骨架样式
// ============================================

import { createStyles } from 'antd-style';

const usePageStyles = createStyles(({ token, css }) => ({
  wrapper: css`
    // 页面根容器：撑满 Content（视口 - Header 64 - Content margin 48），flex 纵向排布
    // 使面包屑/搜索/工具栏固定，仅表格区域滚动
    height: calc(100vh - 64px - 48px);
    display: flex;
    flex-direction: column;
    overflow: hidden;
  `,

  // -------- 面包屑（对齐原型） --------

  breadcrumb: css`
    margin-bottom: 16px;
    flex-shrink: 0;
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
    margin-bottom: 16px;
    flex-shrink: 0;
  `,

  // -------- 表格卡片（对齐原型：白卡片 + 大圆角 + 柔和阴影） --------

  tableCard: css`
    border-radius: ${token.borderRadiusLG}px;
    box-shadow: ${token.boxShadow};
    flex: 1;
    min-height: 0;
    overflow: hidden;

    .ant-card-body {
      height: 100%;
      padding: 16px 20px;
      display: flex;
      flex-direction: column;

      .ant-table-wrapper {
        flex: 1;
        min-height: 0;
        display: flex;
        flex-direction: column;

        .ant-spin-nested-loading {
          flex: 1;
          min-height: 0;
          display: flex;
          flex-direction: column;

          .ant-spin-container {
            flex: 1;
            min-height: 0;
            display: flex;
            flex-direction: column;

            .ant-table {
              flex: 1;
              min-height: 0;
            }

            // 分页固定在底部，上下留白（左右已由卡片内边距 20px 对齐）
            .ant-pagination {
              flex-shrink: 0;
              padding: 16px 0;
              text-align: right;
            }
          }
        }

        .ant-table-container {
          border-radius: 0 0 ${token.borderRadiusLG}px ${token.borderRadiusLG}px;
        }
      }
    }
  `,
}));

export default usePageStyles;
