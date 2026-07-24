import { useState, useEffect, useCallback, useRef } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag, SxwlPopconfirm,
  SxwlSpace, SxwlMessage,
  SxwlPage, SxwlModal,
  type SearchFieldConfig,
} from '@/components';
import type { SysJobLogItem } from '@/api/system/jobLogApi';
import { getLogPageByParams, deleteLogById, cleanLogBefore } from '@/api/system/jobLogApi';

export default function JobLogPage() {
  const [data, setData] = useState<SysJobLogItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [detailOpen, setDetailOpen] = useState(false);
  const [detailRecord, setDetailRecord] = useState<SysJobLogItem | null>(null);

  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getLogPageByParams({
        ...searchRef.current,
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
    searchRef.current = values;
    setPage(1);
    loadData(1);
  };

  const handleReset = () => {
    searchRef.current = {};
    setPage(1);
    loadData(1);
  };

  const handleDelete = async (record: SysJobLogItem) => {
    try {
      await deleteLogById(record.id);
      SxwlMessage.success('删除成功');
      loadData();
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  const handleClean = async () => {
    try {
      await cleanLogBefore(30);
      SxwlMessage.success('清理完成');
      loadData();
    } catch {
      SxwlMessage.error('清理失败');
    }
  };

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  const handleShowDetail = (record: SysJobLogItem) => {
    setDetailRecord(record);
    setDetailOpen(true);
  };

  const columns: ColumnsType<SysJobLogItem> = [
    { title: '任务名称', dataIndex: 'jobName', key: 'jobName', width: 130 },
    { title: '任务组', dataIndex: 'jobGroup', key: 'jobGroup', width: 90 },
    { title: '类名', dataIndex: 'className', key: 'className', width: 180, ellipsis: true },
    { title: '方法名', dataIndex: 'methodName', key: 'methodName', width: 110 },
    { title: 'Cron 表达式', dataIndex: 'cronExpression', key: 'cronExpression', width: 140 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 70,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">成功</SxwlTag> : <SxwlTag color="red">失败</SxwlTag>,
    },
    {
      title: '耗时', dataIndex: 'executeTime', key: 'executeTime', width: 80,
      render: (val: number) => (val != null ? `${val}ms` : '-'),
    },
    { title: '执行时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 180,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="InfoCircleOutlined" />} onClick={() => handleShowDetail(record)}>
            详情
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该日志吗？" onConfirm={() => handleDelete(record)}>
            <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />}>
              删除
            </SxwlButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  const searchFields: SearchFieldConfig[] = [
    { name: 'jobName', label: '任务名称', type: 'input', placeholder: '请输入任务名称' },
    { name: 'jobGroup', label: '任务组', type: 'input', placeholder: '请输入任务组' },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
      options: [{ value: 1, label: '成功' }, { value: 0, label: '失败' }],
    },
  ];

  return (
    <>
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
        breadcrumb={['监控管理', '任务日志']}
        searchFields={searchFields}
        scroll={{ x: 1200 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
        toolbarButtons={[
          { label: '清理日志', type: 'default', icon: 'DeleteOutlined', onClick: handleClean },
        ]}
      />

      <SxwlModal
        title="日志详情"
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={null}
        width={640}
        destroyOnClose
      >
        {detailRecord && (
          <div style={{ lineHeight: 2 }}>
            <div><strong>任务名称：</strong>{detailRecord.jobName}</div>
            <div><strong>任务组：</strong>{detailRecord.jobGroup}</div>
            <div><strong>类名：</strong>{detailRecord.className}</div>
            <div><strong>方法名：</strong>{detailRecord.methodName}</div>
            <div><strong>方法参数：</strong>{detailRecord.methodParams || '-'}</div>
            <div><strong>Cron 表达式：</strong>{detailRecord.cronExpression}</div>
            <div><strong>状态：</strong>{detailRecord.status === 1 ? '成功' : '失败'}</div>
            <div><strong>耗时：</strong>{detailRecord.executeTime != null ? `${detailRecord.executeTime}ms` : '-'}</div>
            <div><strong>执行时间：</strong>{detailRecord.fireTime || '-'}</div>
            <div><strong>创建时间：</strong>{detailRecord.createTime}</div>
            {detailRecord.errorMsg && (
              <div>
                <strong>错误信息：</strong>
                <pre style={{ background: '#f5f5f5', padding: 8, borderRadius: 4, marginTop: 4, whiteSpace: 'pre-wrap', wordBreak: 'break-all' }}>
                  {detailRecord.errorMsg}
                </pre>
              </div>
            )}
          </div>
        )}
      </SxwlModal>
    </>
  );
}
