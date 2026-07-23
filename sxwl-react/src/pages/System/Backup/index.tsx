import { useState, useEffect, useCallback, useRef } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlMessage,
  SxwlPage,
  type ToolbarButtonConfig,
} from '@/components';
import type { SysBackupItem } from '@/api/system/backupApi';
import { backup, getBackupList, restoreBackup, deleteBackup } from '@/api/system/backupApi';

export default function BackupPage() {
  const [data, setData] = useState<SysBackupItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getBackupList(queryPage ?? page, pageSize);
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

  const handleBackup = async () => {
    try {
      await backup();
      SxwlMessage.success('备份任务已提交，请稍后刷新列表查看');
    } catch {
      SxwlMessage.error('备份执行失败');
    }
  };

  const handleRestore = async (record: SysBackupItem) => {
    try {
      await restoreBackup(record.id);
      SxwlMessage.info('恢复请求已提交');
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { message?: string } } };
      SxwlMessage.error(axiosErr?.response?.data?.message || '恢复失败');
    }
  };

  const handleDelete = async (record: SysBackupItem) => {
    try {
      await deleteBackup(record.id);
      SxwlMessage.success('删除成功');
      loadData();
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  const columns: ColumnsType<SysBackupItem> = [
    { title: '文件名', dataIndex: 'fileName', key: 'fileName', width: 240, ellipsis: true },
    { title: '文件大小', dataIndex: 'fileSizeDisplay', key: 'fileSizeDisplay', width: 100 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">正常</SxwlTag> : <SxwlTag color="red">异常</SxwlTag>,
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 200,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlPopconfirm
            title="确定从该备份恢复数据库吗？此操作不可逆！"
            onConfirm={() => handleRestore(record)}
          >
            <SxwlButton type="link" size="small" icon={<SxwlIcon name="RedoOutlined" />}>
              恢复
            </SxwlButton>
          </SxwlPopconfirm>
          <SxwlPopconfirm title="确定删除该备份记录吗？" onConfirm={() => handleDelete(record)}>
            <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />}>
              删除
            </SxwlButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '执行备份', type: 'primary', icon: 'CloudUploadOutlined', onClick: handleBackup },
  ];

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
      breadcrumb={['系统管理', '数据备份']}
      scroll={{ x: 900 }}
      toolbarButtons={toolbarButtons}
      onSearch={handleSearch}
      onReset={handleReset}
      onPageChange={handlePageChange}
    />
  );
}
