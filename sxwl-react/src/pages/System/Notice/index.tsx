import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm, SxwlMessage,
  SxwlPage, SxwlFormModal, SxwlPermissionButton,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { SysNoticeItem } from '@/api/system/noticeApi';
import {
  getNoticePageByParams, createNotice, updateNotice, deleteNoticeById,
  publishNotice, revokeNotice, getNoticeById,
} from '@/api/system/noticeApi';

export default function NoticePage() {
  const [data, setData] = useState<SysNoticeItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState<SysNoticeItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();
  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getNoticePageByParams({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询公告列表失败');
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

  const handleEdit = async (record: SysNoticeItem) => {
    try {
      // 列表接口不返回 content，需调详情接口拿完整数据
      const res = await getNoticeById(record.id);
      setEditingRecord(res.data.data);
    } catch {
      // 兜底：用列表数据（无 content）
      setEditingRecord(record);
    }
    setModalOpen(true);
  };

  const handleDelete = async (record: SysNoticeItem) => {
    try {
      await deleteNoticeById(record.id);
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
        await updateNotice({ ...values, id: editingRecord.id });
        SxwlMessage.success('更新成功');
      } else {
        await createNotice(values);
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

  const handlePublish = async (id: number) => {
    try {
      await publishNotice(id);
      SxwlMessage.success('发布成功');
      loadData();
    } catch {
      SxwlMessage.error('发布失败');
    }
  };

  const handleRevoke = async (id: number) => {
    try {
      await revokeNotice(id);
      SxwlMessage.success('撤回成功');
      loadData();
    } catch {
      SxwlMessage.error('撤回失败');
    }
  };

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  const columns: ColumnsType<SysNoticeItem> = [
    { title: '标题', dataIndex: 'title', key: 'title', width: 200, ellipsis: true },
    {
      title: '类型', dataIndex: 'noticeType', key: 'noticeType', width: 100,
      render: (val: string) => {
        const typeMap: Record<string, string> = { ANNOUNCEMENT: '公告', NOTICE: '通知', WARNING: '警告' };
        return typeMap[val] || val;
      },
    },
    {
      title: '级别', dataIndex: 'level', key: 'level', width: 80,
      render: (val: string) => {
        if (val === 'URGENT') return <SxwlTag color="red">紧急</SxwlTag>;
        if (val === 'IMPORTANT') return <SxwlTag color="orange">重要</SxwlTag>;
        return <SxwlTag color="blue">普通</SxwlTag>;
      },
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status: number) => {
        if (status === 1) return <SxwlTag color="green">已发布</SxwlTag>;
        if (status === 0) return <SxwlTag color="default">草稿</SxwlTag>;
        return <SxwlTag color="red">已撤回</SxwlTag>;
      },
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 280,
      render: (_, record) => (
        <SxwlSpace>
          {record.status === 0 && (
            <SxwlPermissionButton type="link" size="small" icon={<SxwlIcon name="CheckCircleOutlined" />} permission="system:notice:publish" onClick={() => handlePublish(record.id)}>
              发布
            </SxwlPermissionButton>
          )}
          {record.status === 1 && (
            <SxwlPermissionButton type="link" size="small" icon={<SxwlIcon name="MinusCircleOutlined" />} permission="system:notice:revoke" onClick={() => handleRevoke(record.id)}>
              撤回
            </SxwlPermissionButton>
          )}
          <SxwlPermissionButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} permission="system:notice:edit" onClick={() => handleEdit(record)}>
            编辑
          </SxwlPermissionButton>
          <SxwlPopconfirm title="确定删除该公告吗？" onConfirm={() => handleDelete(record)}>
            <SxwlPermissionButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />} permission="system:notice:delete">
              删除
            </SxwlPermissionButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  const searchFields: SearchFieldConfig[] = [
    { name: 'title', label: '标题', type: 'input', placeholder: '请输入标题' },
    {
      name: 'noticeType', label: '类型', type: 'select', placeholder: '请选择类型',
      options: [
        { value: 'ANNOUNCEMENT', label: '公告' },
        { value: 'NOTICE', label: '通知' },
        { value: 'WARNING', label: '警告' },
      ],
    },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
      options: [
        { value: 0, label: '草稿' },
        { value: 1, label: '已发布' },
        { value: 2, label: '已撤回' },
      ],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增公告', type: 'primary', icon: 'PlusOutlined', permission: 'system:notice:add', onClick: handleAdd },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    { name: 'title', label: '标题', type: 'input', required: true, maxLength: 128, placeholder: '请输入公告标题' },
    {
      name: 'noticeType', label: '类型', type: 'select', required: true, initialValue: 'ANNOUNCEMENT',
      options: [
        { value: 'ANNOUNCEMENT', label: '公告' },
        { value: 'NOTICE', label: '通知' },
        { value: 'WARNING', label: '警告' },
      ],
      placeholder: '请选择类型',
    },
    {
      name: 'level', label: '级别', type: 'select', required: true, initialValue: 'NORMAL',
      options: [
        { value: 'NORMAL', label: '普通' },
        { value: 'IMPORTANT', label: '重要' },
        { value: 'URGENT', label: '紧急' },
      ],
      placeholder: '请选择级别',
    },
    { name: 'content', label: '内容', type: 'richtext', required: true, placeholder: '请输入公告内容...' },
    {
      name: 'status', label: '状态', type: 'select', required: true, initialValue: 0,
      options: [{ value: 0, label: '草稿' }, { value: 1, label: '已发布' }],
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
        breadcrumb={['系统管理', '通知公告']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 1000 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <SxwlFormModal
        title={editingRecord ? '编辑公告' : '新增公告'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        layout="horizontal"
        columns={1}
        width={820}
        confirmLoading={confirmLoading}
        initialValues={{ status: 1, noticeType: 'ANNOUNCEMENT', level: 'NORMAL' }}
        editingData={editingRecord}
        destroyOnHidden
      />
    </>
  );
}
