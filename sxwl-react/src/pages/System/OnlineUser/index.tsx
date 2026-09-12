// ============================================
// 在线用户管理页面
// ============================================

import { useState, useEffect, useCallback } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlIcon, SxwlMessage, SxwlPage, SxwlPopconfirm, SxwlButton,
} from '@/components';
import type { OnlineUserItem } from '@/api/system/onlineUserApi';
import { getOnlineUserList, getOnlineUserCount, forceLogout } from '@/api/system/onlineUserApi';

export default function OnlineUserPage() {
  const [data, setData] = useState<OnlineUserItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [onlineCount, setOnlineCount] = useState(0);

  /** 加载数据 */
  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getOnlineUserList(queryPage ?? page, 10);
      setData(res.data.data.list);
      setTotal(res.data.data.total);
      
      // 刷新在线人数
      const countRes = await getOnlineUserCount();
      setOnlineCount(countRes.data.data);
    } catch {
      SxwlMessage.error('查询在线用户列表失败');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    loadData();
    
    // 每 30 秒自动刷新一次
    const timer = setInterval(loadData, 30000);
    return () => clearInterval(timer);
  }, [loadData]);

  /** 强制踢人下线 */
  const handleForceLogout = async (record: OnlineUserItem) => {
    try {
      await forceLogout(record.userId);
      SxwlMessage.success('已强制下线');
      loadData();
    } catch {
      SxwlMessage.error('踢人下线失败');
    }
  };

  /** 手动刷新 */
  const handleRefresh = () => {
    loadData();
  };

  // -------- 表格列定义 --------
  const columns: ColumnsType<OnlineUserItem> = [
    {
      title: '用户名',
      dataIndex: 'username',
      width: 150,
    },
    {
      title: '登录 IP',
      dataIndex: 'ip',
      width: 140,
    },
    {
      title: '浏览器',
      dataIndex: 'browser',
      width: 120,
      render: (browser: string) => (
        <span>
          {browser && <SxwlIcon name="ChromeOutlined" size={14} style={{ marginRight: 4 }} />}
          {browser || '-'}
        </span>
      ),
    },
    {
      title: '操作系统',
      dataIndex: 'os',
      width: 150,
    },
    {
      title: '设备 ID',
      dataIndex: 'deviceId',
      ellipsis: true,
      width: 200,
      render: (deviceId: string) => (
        <span style={{ fontFamily: 'monospace', fontSize: 12 }}>
          {deviceId?.slice(0, 8)}***
        </span>
      ),
    },
    {
      title: '登录时间',
      dataIndex: 'loginTime',
      width: 200,
      sorter: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right',
      render: (_: any, record: OnlineUserItem) => (
        <SxwlPopconfirm
          title="确定要强制该用户下线吗？"
          onConfirm={() => handleForceLogout(record)}
          okText="确定"
          cancelText="取消"
        >
          <SxwlButton type="link" danger icon={<SxwlIcon name="PoweroffOutlined" size={14} />}>
            强制下线
          </SxwlButton>
        </SxwlPopconfirm>
      ),
    },
  ];

  return (
    <SxwlPage
      mode="table"
      paginated
      rowKey="deviceid"
      loading={loading}
      dataSource={data}
      columns={columns}
      total={total}
      page={page}
      onPageChange={(newPage) => {
        setPage(newPage);
        loadData(newPage);
      }}
      toolbarButtons={[
        {
          label: `当前在线：${onlineCount} 人`,
          icon: 'UsersOutlined',
          type: 'default',
          onClick: handleRefresh,
        },
        {
          label: '刷新',
          icon: 'ReloadOutlined',
          type: 'primary',
          onClick: handleRefresh,
        },
      ]}
    />
  );
}
