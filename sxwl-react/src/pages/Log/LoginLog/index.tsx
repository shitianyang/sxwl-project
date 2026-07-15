import { useState, useEffect, useCallback, useRef } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlTag,
  SxwlPage,
  type SearchFieldConfig,
} from '@/components';
import type { SysLogItem } from '@/api/system/logApi';
import { getLogPageByParams } from '@/api/system/logApi';

const columns: ColumnsType<SysLogItem> = [
  { title: '登录账号', dataIndex: 'userName', key: 'userName', width: 120 },
  { title: '登录IP', dataIndex: 'operateIp', key: 'operateIp', width: 140 },
  { title: '登录地点', dataIndex: 'operateLocation', key: 'operateLocation', width: 160, ellipsis: true },
  {
    title: '状态', dataIndex: 'status', key: 'status', width: 70,
    render: (status: number) =>
      status === 1 ? <SxwlTag color="green">成功</SxwlTag> : <SxwlTag color="red">失败</SxwlTag>,
  },
  { title: '错误信息', dataIndex: 'errorMsg', key: 'errorMsg', width: 200, ellipsis: true },
  { title: '登录时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
];

const searchFields: SearchFieldConfig[] = [
  { name: 'userName', label: '登录账号', type: 'input', placeholder: '请输入登录账号' },
  {
    name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
    options: [
      { value: 1, label: '成功' },
      { value: 0, label: '失败' },
    ],
  },
  { name: 'dateRange', label: '时间范围', type: 'dateRange', dateRangeStartKey: 'startTime', dateRangeEndKey: 'endTime' },
];

export default function LoginLogPage() {
  const [data, setData] = useState<SysLogItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getLogPageByParams({
        ...searchRef.current,
        logType: 1,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      // SxwlPage handles empty state
    } finally {
      setLoading(false);
    }
  }, [page, pageSize]);

  useEffect(() => { loadData(); }, [loadData]);

  const handleSearch = (values: Record<string, any>) => {
    // dateRange 转换由 SxwlSearchForm 自动处理，无需手动映射
    searchRef.current = values;
    setPage(1);
    loadData(1);
  };

  const handleReset = () => {
    searchRef.current = {};
    setPage(1);
    loadData(1);
  };

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  return (
    <SxwlPage
      mode="table"
      paginated
      rowKey="id"
      columns={columns}
      dataSource={data}
      loading={loading}
      total={total}
      page={page}
      pageSize={pageSize}
      breadcrumb={['日志管理', '登录日志']}
      searchFields={searchFields}
      scroll={{ x: 1000 }}
      onSearch={handleSearch}
      onReset={handleReset}
      onPageChange={handlePageChange}
    />
  );
}