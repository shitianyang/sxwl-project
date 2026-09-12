// ============================================
// 日志管理页面
// ============================================

import { useState, useEffect, useCallback, useRef } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlTag,
  SxwlMessage, SxwlPage,
  type SearchFieldConfig, type ToolbarButtonConfig,
} from '@/components';
import type { LogItem, LogQuery } from '@/api/system/logApi';
import { getLogPageByParams } from '@/api/system/logApi';

/** 日志类型映射 */
const LOG_TYPE_MAP: Record<number, { label: string; color: string }> = {
  1: { label: '登录', color: 'success' },
  2: { label: '操作', color: 'processing' },
  3: { label: '异常', color: 'error' },
  4: { label: '安全', color: 'warning' },
};

export default function LogPage() {
  const [data, setData] = useState<LogItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);

  // 搜索参数（不触发重渲染，仅 loadData 时读取）
  const searchRef = useRef<Partial<LogQuery>>({});

  /** 加载数据 */
  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getLogPageByParams({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize: 10,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询日志列表失败');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  /** 搜索 */
  const handleSearch = (values: Record<string, any>) => {
    searchRef.current = values;
    setPage(1);
    loadData(1);
  };

  /** 重置 */
  const handleReset = () => {
    searchRef.current = {};
    setPage(1);
    loadData(1);
  };

  /** 刷新 */
  const handleRefresh = () => {
    loadData();
  };

  // -------- 搜索表单配置 --------
  const searchFields: SearchFieldConfig[] = [
    {
      name: 'logType',
      label: '日志类型',
      type: 'select',
      placeholder: '请选择日志类型',
      options: Object.entries(LOG_TYPE_MAP).map(([value, { label }]) => ({
        value: Number(value),
        label,
      })),
    },
    {
      name: 'title',
      label: '模块标题',
      type: 'input',
      placeholder: '请输入模块标题',
    },
    {
      name: 'userName',
      label: '操作人账号',
      type: 'input',
      placeholder: '请输入操作人账号',
    },
    {
      name: 'status',
      label: '操作状态',
      type: 'select',
      placeholder: '请选择操作状态',
      options: [
        { value: 0, label: '失败' },
        { value: 1, label: '成功' },
      ],
    },
    {
      name: 'timeRange',
      label: '时间范围',
      type: 'dateRange',
      placeholder: '请选择时间范围',
    },
  ];

  // -------- 工具栏按钮配置 --------
  const toolbarButtons: ToolbarButtonConfig[] = [
    {
      label: '刷新',
      icon: 'ReloadOutlined',
      type: 'link',
      onClick: handleRefresh,
    },
  ];

  // -------- 表格列定义 --------
  const columns: ColumnsType<LogItem> = [
    {
      title: '日志类型',
      dataIndex: 'logType',
      width: 100,
      render: (logType: number) => (
        <SxwlTag color={LOG_TYPE_MAP[logType]?.color}>
          {LOG_TYPE_MAP[logType]?.label || '未知'}
        </SxwlTag>
      ),
    },
    {
      title: '模块标题',
      dataIndex: 'title',
      width: 150,
      ellipsis: true,
    },
    {
      title: '操作描述',
      dataIndex: 'description',
      ellipsis: true,
      width: 250,
    },
    {
      title: '操作人',
      dataIndex: 'userName',
      width: 120,
    },
    {
      title: 'IP地址',
      dataIndex: 'operateIp',
      width: 140,
    },
    {
      title: '耗时(ms)',
      dataIndex: 'executeTime',
      width: 100,
      align: 'right',
      render: (time: number) => <span style={{ color: time > 1000 ? '#faad14' : undefined }}>{time}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (status: number) => (
        <SxwlTag color={status === 1 ? 'success' : 'error'}>
          {status === 1 ? '成功' : '失败'}
        </SxwlTag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      sorter: true,
    },
  ];

  return (
    <SxwlPage
      mode="table"
      paginated
      rowKey="id"
      loading={loading}
      dataSource={data}
      columns={columns}
      total={total}
      page={page}
      onPageChange={(newPage) => {
        setPage(newPage);
        loadData(newPage);
      }}
      searchFields={searchFields}
      onSearch={handleSearch}
      onReset={handleReset}
      toolbarButtons={toolbarButtons}
    />
  );
}
