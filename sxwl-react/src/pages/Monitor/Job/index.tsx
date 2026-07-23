import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm, SxwlMessage,
  SxwlPage, SxwlFormModal,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { SysJobItem } from '@/api/system/jobApi';
import {
  getJobPageByParams, createJob, updateJob, deleteJobById,
  pauseJob, resumeJob, runOnceJob,
} from '@/api/system/jobApi';

export default function JobPage() {
  const [data, setData] = useState<SysJobItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState<SysJobItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();
  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getJobPageByParams({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询任务列表失败');
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

  const handleAdd = () => {
    setEditingRecord(null);
    setModalOpen(true);
  };

  const handleEdit = (record: SysJobItem) => {
    setEditingRecord(record);
    setModalOpen(true);
  };

  const handleDelete = async (record: SysJobItem) => {
    try {
      await deleteJobById(record.id);
      SxwlMessage.success('删除成功');
      setSelectedRowKeys([]);
      loadData();
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setConfirmLoading(true);
      if (editingRecord) {
        await updateJob({ ...values, id: editingRecord.id });
        SxwlMessage.success('更新成功');
      } else {
        await createJob(values);
        SxwlMessage.success('创建成功');
      }
      setModalOpen(false);
      loadData();
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { message?: string } } };
      SxwlMessage.error(axiosErr?.response?.data?.message || '操作失败');
    } finally {
      setConfirmLoading(false);
    }
  };

  const handlePause = async (id: number) => {
    try {
      await pauseJob(id);
      SxwlMessage.success('已暂停');
      loadData();
    } catch {
      SxwlMessage.error('暂停失败');
    }
  };

  const handleResume = async (id: number) => {
    try {
      await resumeJob(id);
      SxwlMessage.success('已恢复');
      loadData();
    } catch {
      SxwlMessage.error('恢复失败');
    }
  };

  const handleRunOnce = async (id: number) => {
    try {
      await runOnceJob(id);
      SxwlMessage.success('执行命令已发送');
    } catch {
      SxwlMessage.error('执行失败');
    }
  };

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  const columns: ColumnsType<SysJobItem> = [
    { title: '任务名称', dataIndex: 'jobName', key: 'jobName', width: 140 },
    { title: '任务组', dataIndex: 'jobGroup', key: 'jobGroup', width: 100 },
    { title: '类名', dataIndex: 'className', key: 'className', width: 180, ellipsis: true },
    { title: '方法名', dataIndex: 'methodName', key: 'methodName', width: 120 },
    { title: 'Cron 表达式', dataIndex: 'cronExpression', key: 'cronExpression', width: 150 },
    { title: '描述', dataIndex: 'description', key: 'description', width: 160, ellipsis: true },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">正常</SxwlTag> : <SxwlTag color="red">暂停</SxwlTag>,
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 300,
      render: (_, record) => (
        <SxwlSpace>
          {record.status === 1 ? (
            <SxwlButton type="link" size="small" icon={<SxwlIcon name="PauseCircleOutlined" />} onClick={() => handlePause(record.id)}>
              暂停
            </SxwlButton>
          ) : (
            <SxwlButton type="link" size="small" icon={<SxwlIcon name="PlayCircleOutlined" />} onClick={() => handleResume(record.id)}>
              恢复
            </SxwlButton>
          )}
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="CaretRightOutlined" />} onClick={() => handleRunOnce(record.id)}>
            执行
          </SxwlButton>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} onClick={() => handleEdit(record)}>
            编辑
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该任务吗？" onConfirm={() => handleDelete(record)}>
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
      options: [{ value: 1, label: '正常' }, { value: 0, label: '暂停' }],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增任务', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    { name: 'jobName', label: '任务名称', type: 'input', required: true, maxLength: 64, placeholder: '请输入任务名称' },
    { name: 'jobGroup', label: '任务组', type: 'input', required: true, maxLength: 32, placeholder: '请输入任务分组' },
    { name: 'className', label: '类名', type: 'input', required: true, maxLength: 256, placeholder: '请输入完整类名' },
    { name: 'methodName', label: '方法名', type: 'input', required: true, maxLength: 64, placeholder: '请输入方法名' },
    { name: 'methodParams', label: '方法参数', type: 'input', maxLength: 200, placeholder: '可选，方法参数' },
    { name: 'cronExpression', label: 'Cron 表达式', type: 'input', required: true, maxLength: 64, placeholder: '请输入 Cron 表达式' },
    { name: 'description', label: '描述', type: 'input', maxLength: 200, placeholder: '请输入描述' },
    {
      name: 'status', label: '状态', type: 'select', required: true, initialValue: 1,
      options: [{ value: 1, label: '正常' }, { value: 0, label: '暂停' }],
      placeholder: '请选择状态',
    },
  ], []);

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[]) => setSelectedRowKeys(keys as number[]),
  };

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
        rowSelection={rowSelection}
        breadcrumb={['监控管理', '定时任务']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 1300 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <SxwlFormModal
        title={editingRecord ? '编辑任务' : '新增任务'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        layout="horizontal"
        columns={1}
        width={560}
        confirmLoading={confirmLoading}
        initialValues={{ status: 1 }}
        editingData={editingRecord}
      />
    </>
  );
}
