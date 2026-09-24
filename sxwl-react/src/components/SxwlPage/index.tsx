import { type JSX, useLayoutEffect, useRef, useState } from 'react';
import {
  SxwlButton, SxwlIcon, SxwlPermissionButton,
  SxwlTable, SxwlSearchForm,
} from '@/components';
import { SXWL_LAYOUT } from '@/styles/theme.token';
import './index.scss';

// ==================== Types

export type SxwlPageMode = 'table' | 'tree' | 'list';

export interface SearchFieldConfig {
  /** 字段名 */
  name: string;
  /** 标签文本：作为无障碍标签与占位符兜底 */
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
  /** 权限标识，如 'system:user:add'；有值时自动使用 SxwlPermissionButton */
  permission?: string | string[];
  /** 权限逻辑：and=同时拥有 all，or=任一即可。默认 or */
  permissionMode?: 'and' | 'or';
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
  /** 工具栏按钮配置：渲染在面板内、筛选条与表格之间；type='link' 归右组（次操作），其余左组 */
  toolbarButtons?: ToolbarButtonConfig[];
  /** Table 横向滚动（纵向由骨架测量，不要传 y） */
  scroll?: { x?: number | string };
  /** 点击查询 */
  onSearch?: (values: Record<string, any>) => void;
  /** 点击重置 */
  onReset?: () => void;
  /** 分页切换 */
  onPageChange?: (page: number, pageSize: number) => void;
  /** 错误态：传入后展示加载失败提示（由页面在请求失败时 setError） */
  error?: string | null;
}

// ==================== Hooks

/**
 * 表体高度实测。
 * 旧实现把 64+48+40+40+60 这类预留值写死相加，窗口或头部一改就算错，
 * 所以改成观察真实容器，只再减去表头一行。
 */
function useTableScrollY(hasSearch: boolean) {
  const ref = useRef<HTMLDivElement>(null);
  const [y, setY] = useState(0);
  useLayoutEffect(() => {
    const el = ref.current;
    if (!el) return;
    const measure = () => setY(Math.max(el.clientHeight - SXWL_LAYOUT.tableRow, 120));
    measure();
    const ro = new ResizeObserver(measure);
    ro.observe(el);
    return () => ro.disconnect();
  }, [hasSearch]);
  return [ref, y] as const;
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
    error,
  } = props;

  const hasSearch = !!searchFields?.length;
  const [bodyRef, scrollY] = useTableScrollY(hasSearch);

  // -------- 页头：单行面包屑（侧边栏已回答「我在哪」，不再放大标题造成三遍重复） --------

  const crumb = breadcrumb ?? [];

  const renderButton = (btn: ToolbarButtonConfig, index: number) => {
    const btnKey = btn.permission ?? btn.label ?? index;
    const key = Array.isArray(btnKey) ? btnKey[0] ?? index : btnKey;
    const shared = {
      type: btn.type,
      danger: btn.danger,
      icon: btn.icon ? <SxwlIcon name={btn.icon} /> : undefined,
      onClick: btn.onClick,
    };
    if (btn.permission) {
      return (
        <SxwlPermissionButton key={key} {...shared} permission={btn.permission} mode={btn.permissionMode}>
          {btn.label}
        </SxwlPermissionButton>
      );
    }
    return <SxwlButton key={key} {...shared}>{btn.label}</SxwlButton>;
  };

  // 邻近性原则：按钮影响表格数据，就长在表格旁边（筛选条与表格之间）
  const renderToolbar = () => {
    if (!toolbarButtons?.length) return null;
    const main = toolbarButtons.filter((b) => b.type !== 'link');
    const aux = toolbarButtons.filter((b) => b.type === 'link');
    return (
      <div className="sxwl-panel__toolbar">
        <div className="sxwl-panel__toolbar-main">{main.map(renderButton)}</div>
        {aux.length > 0 && <div className="sxwl-panel__toolbar-aux">{aux.map(renderButton)}</div>}
      </div>
    );
  };

  // -------- 空态：区分"没有数据"和"筛选没命中" --------

  const emptyText = hasSearch ? (
    <div className="sxwl-page__empty">
      <SxwlIcon name="InboxOutlined" className="sxwl-page__empty-icon" />
      <p className="sxwl-page__empty-title">没有匹配的记录</p>
      <p className="sxwl-page__empty-hint">调整筛选条件后再试一次</p>
      <SxwlButton onClick={onReset}>清除筛选</SxwlButton>
    </div>
  ) : (
    <div className="sxwl-page__empty">
      <SxwlIcon name="InboxOutlined" className="sxwl-page__empty-icon" />
      <p className="sxwl-page__empty-title">暂无数据</p>
    </div>
  );

  const renderContent = () => {
    if (error) {
      return (
        <div className="sxwl-page__error">
          <SxwlIcon name="ExclamationCircleOutlined" />
          <span>数据加载失败：{error}</span>
        </div>
      );
    }
    return (
      <div className="sxwl-page__body" ref={bodyRef}>
        <SxwlTable
          rowKey={rowKey}
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          rowSelection={rowSelection}
          locale={{ emptyText }}
          scroll={{ ...scroll, y: scrollY }}
          pagination={
            paginated && mode !== 'tree'
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
        />
      </div>
    );
  };

  return (
    <div className="sxwl-page">
      {crumb.length > 0 && (
        <div className="sxwl-page__head">
          <div className="sxwl-page__crumb">
            {crumb.map((item, i) => (
              <span key={item} className="sxwl-page__crumb-part">
                {i > 0 && <span className="sxwl-page__crumb-sep">/</span>}
                <span className={i === crumb.length - 1 ? 'is-current' : undefined}>{item}</span>
              </span>
            ))}
          </div>
        </div>
      )}

      <section className="sxwl-panel">
        {hasSearch && (
          <SxwlSearchForm
            className="sxwl-panel__filters"
            fields={searchFields}
            onSearch={onSearch}
            onReset={onReset}
          />
        )}
        {renderToolbar()}
        {renderContent()}
      </section>
    </div>
  );
}

export default SxwlPage;
