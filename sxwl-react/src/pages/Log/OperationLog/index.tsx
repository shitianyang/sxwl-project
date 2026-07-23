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
  { title: '日志标题', dataIndex: 'title', key: 'title', width: 140, ellipsis: true },
  { title: '操作描述', dataIndex: 'description', key: 'description', width: 220, ellipsis: true },
  { title: '操作人', dataIndex: 'userName', key: 'userName', width: 100 },
  { title: '请求URL', dataIndex: 'requestUrl', key: 'requestUrl', width: 220, ellipsis: true },
  { title: '请求方式', dataIndex: 'requestMethod', key: 'requestMethod', width: 90 },
  { title: 'IP', dataIndex: 'operateIp', key: 'operateIp', width: 130 },
  {
    title: '耗时', dataIndex: 'executeTime', key: 'executeTime', width: 90,
    render: (val: number) => (val != null ? `${val}ms` : '-'),
  },
  {
    title: '状态', dataIndex: 'status', key: 'status', width: 70,
    render: (status: number) =>
      status === 1 ? <SxwlTag color="green">成功</SxwlTag> : <SxwlTag color="red">失败</SxwlTag>,
  },
  {
    title: '错误信息', dataIndex: 'errorMsg', key: 'errorMsg', width: 180, ellipsis: true,
    render: (val: string) => val || '-',
  },
  { title: '请求参数', dataIndex: 'requestParam', key: 'requestParam', width: 200, ellipsis: true,
    render: (val: string) => val || '-',
  },
  { title: '响应结果', dataIndex: 'responseResult', key: 'responseResult', width: 200, ellipsis: true,
    render: (val: string) => val || '-',
  },
  { title: '浏览器', dataIndex: 'browser', key: 'browser', width: 90,
    render: (val: string) => val || '-',
  },
  { title: '操作系统', dataIndex: 'os', key: 'os', width: 100,
    render: (val: string) => val || '-',
  },
  { title: '操作时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
];

const searchFields: SearchFieldConfig[] = [
  { name: 'title', label: '日志标题', type: 'input', placeholder: '请输入日志标题' },
  { name: 'userName', label: '操作人', type: 'input', placeholder: '请输入操作人' },
  {
    name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
    options: [
      { value: 1, label: '成功' },
      { value: 0, label: '失败' },
    ],
  },
  { name: 'dateRange', label: '时间范围', type: 'dateRange', dateRangeStartKey: 'startTime', dateRangeEndKey: 'endTime' },
];

export default function OperationLogPage() {
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
        logType: 2,
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
      breadcrumb={['日志管理', '操作日志']}
      searchFields={searchFields}
      scroll={{ x: 1400 }}
      onSearch={handleSearch}
      onReset={handleReset}
      onPageChange={handlePageChange}
    />
  );
}