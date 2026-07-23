import { useState, useEffect, useCallback, useRef } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon,
  SxwlSpace, SxwlPopconfirm, SxwlMessage,
  SxwlPage,
  type ToolbarButtonConfig,
} from '@/components';
import type { SysOnlineUserItem } from '@/api/monitor/onlineUserApi';
import { getOnlineUserList, forceLogout } from '@/api/monitor/onlineUserApi';

export default function OnlineUserPage() {
  const [data, setData] = useState<SysOnlineUserItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getOnlineUserList(queryPage ?? page, pageSize);
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
    searchRef.current = values;
    setPage(1);
    loadData(1);
  };

  const handleReset = () => {
    searchRef.current = {};
    setPage(1);
    loadData(1);
  };

  const handleForceLogout = async (record: SysOnlineUserItem) => {
    try {
      await forceLogout(record.userId);
      SxwlMessage.success('已强制下线');
      loadData();
    } catch {
      SxwlMessage.error('操作失败');
    }
  };

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  const columns: ColumnsType<SysOnlineUserItem> = [
    { title: '用户名', dataIndex: 'username', key: 'username', width: 120 },
    { title: 'IP 地址', dataIndex: 'ip', key: 'ip', width: 140 },
    { title: '浏览器', dataIndex: 'browser', key: 'browser', width: 160, ellipsis: true },
    { title: '操作系统', dataIndex: 'os', key: 'os', width: 140 },
    { title: '设备 ID', dataIndex: 'deviceId', key: 'deviceId', width: 200, ellipsis: true },
    { title: '登录时间', dataIndex: 'loginTime', key: 'loginTime', width: 180 },
    {
      title: '操作', key: 'action', width: 120,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlPopconfirm
            title="确定强制该用户下线吗？"
            onConfirm={() => handleForceLogout(record)}
          >
            <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="LogoutOutlined" />}>
              踢下线
            </SxwlButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [];

  return (
    <SxwlPage
      mode="table"
      paginated
      rowKey="userId"
      columns={columns}
      dataSource={data}
      loading={loading}
      total={total}
      page={page}
      pageSize={pageSize}
      breadcrumb={['监控运维', '在线用户']}
      scroll={{ x: 1000 }}
      toolbarButtons={toolbarButtons}
      onSearch={handleSearch}
      onReset={handleReset}
      onPageChange={handlePageChange}
    />
  );
}
