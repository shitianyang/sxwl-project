import { type JSX } from 'react';
import {
  SxwlButton, SxwlIcon,
  SxwlCard, SxwlTable, SxwlSpace, SxwlSearchForm,
} from '@/components';
import './index.scss';

// ==================== Types

export type SxwlPageMode = 'table' | 'tree' | 'list';

export interface SearchFieldConfig {
  /** 字段名 */
  name: string;
  /** 标签文本 */
  label?: string;
  /** 控件类型 */
  type: 'input' | 'select' | 'dateRange';
  /** 占位符 */
  placeholder?: string;
  /** 是否允许清除 */
  allowClear?: boolean;
  /** Select 选项 */
  options?: { value: string | number; label: string }[];
  /** 控件宽度（仅 select 生效） */
  width?: number;
  /** dateRange 字段的起始时间输出参数名，默认 {name}Start */
  dateRangeStartKey?: string;
  /** dateRange 字段的结束时间输出参数名，默认 {name}End */
  dateRangeEndKey?: string;
}

export interface ToolbarButtonConfig {
  /** 按钮文字 */
  label: string;
  /** 按钮类型 */
  type?: 'primary' | 'default' | 'link' | 'dashed';
  /** 图标名（SxwlIcon name） */
  icon?: string;
  /** 危险按钮 */
  danger?: boolean;
  /** 点击事件 */
  onClick: () => void;
}

export interface SxwlPageProps {
  /** 列表模式 */
  mode?: SxwlPageMode;
  /** 是否分页 */
  paginated?: boolean;
  /** 表格行 key */
  rowKey?: string;
  /** 表格列定义 */
  columns?: any[];
  /** 表格数据源 */
  dataSource?: any[];
  /** 加载中 */
  loading?: boolean;
  /** 总条数 */
  total?: number;
  /** 当前页码 */
  page?: number;
  /** 每页条数 */
  pageSize?: number;
  /** 表格行选择配置 */
  rowSelection?: object;
  /** 面包屑（如 ['系统管理', '用户管理']） */
  breadcrumb?: string[];
  /** 搜索字段配置 */
  searchFields?: SearchFieldConfig[];
  /** 工具栏按钮配置 */
  toolbarButtons?: ToolbarButtonConfig[];
  /** Table 横向/纵向滚动 */
  scroll?: { x?: number | string; y?: number | string };
  /** 点击查询 */
  onSearch?: (values: Record<string, any>) => void;
  /** 点击重置 */
  onReset?: () => void;
  /** 分页切换 */
  onPageChange?: (page: number, pageSize: number) => void;
}

// ==================== Component

function SxwlPage(props: SxwlPageProps): JSX.Element {
  const {
    mode = 'table',
    paginated = true,
    rowKey = 'id',
    columns,
    dataSource,
    loading,
    total,
    page,
    pageSize = 10,
    rowSelection,
    breadcrumb,
    searchFields,
    toolbarButtons,
    scroll,
    onSearch,
    onReset,
    onPageChange,
  } = props;

  // -------- 面包屑 --------

  const renderBreadcrumb = () => {
    if (!breadcrumb?.length) return null;
    return (
      <div className="sxwl-page-breadcrumb">
        {breadcrumb.map((item, i) => (
          <span
            key={item}
            className={`sxwl-page-breadcrumb-item${i === breadcrumb.length - 1 ? ' is-current' : ''}`}
          >
            {i > 0 && <span className="sxwl-page-breadcrumb-sep">/</span>}
            {item}
          </span>
        ))}
      </div>
    );
  };

  // -------- 搜索 --------

  const renderSearch = () => {
    if (!searchFields?.length) return null;
    return <SxwlSearchForm fields={searchFields} onSearch={onSearch} onReset={onReset} />;
  };

  // -------- 工具栏 --------

  const renderToolbar = () => {
    if (!toolbarButtons?.length) return null;
    return (
      <div className="sxwl-page-toolbar">
        <SxwlSpace>
          {toolbarButtons.map((btn, index) => (
            <SxwlButton
              key={index}
              type={btn.type}
              danger={btn.danger}
              icon={btn.icon ? <SxwlIcon name={btn.icon} /> : undefined}
              onClick={btn.onClick}
            >
              {btn.label}
            </SxwlButton>
          ))}
        </SxwlSpace>
      </div>
    );
  };

  // -------- 主体内容 --------

  const renderContent = () => {
    if (mode === 'tree') {
      return (
        <SxwlTable
          rowKey={rowKey}
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          rowSelection={rowSelection}
          pagination={false}
          expandable={{ defaultExpandAllRows: true }}
          scroll={scroll}
        />
      );
    }

    // table | list
    return (
      <SxwlTable
        rowKey={rowKey}
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        rowSelection={rowSelection}
        pagination={
          paginated
            ? {
                current: page,
                pageSize,
                total,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (t: number) => `共 ${t} 条`,
                onChange: onPageChange,
              }
            : false
        }
        scroll={scroll}
      />
    );
  };

  return (
    <div className="sxwl-page">
      {renderBreadcrumb()}
      {renderSearch()}
      <SxwlCard className="sxwl-page-table-card">
        {renderToolbar()}
        {renderContent()}
      </SxwlCard>
    </div>
  );
}

export default SxwlPage;
