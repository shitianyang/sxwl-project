import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm,
  SxwlMessage, SxwlPage, SxwlFormModal,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { PositionItem } from '@/api/system/positionApi';
import { getPositionPageByParams, createPosition, updatePosition, deletePositionById, batchDeletePositionByIds } from '@/api/system/positionApi';

export default function PositionPage() {
  const [data, setData] = useState<PositionItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingPosition, setEditingPosition] = useState<PositionItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();

  // 搜索参数（不触发重渲染，仅 loadData 时读取）
  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getPositionPageByParams({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询岗位列表失败');
    } finally {
      setLoading(false);
    }
  }, [page, pageSize]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // -------- 搜索 & 重置 --------

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

  // -------- 新增 & 编辑 --------

  const handleAdd = () => {
    setEditingPosition(null);
    form.resetFields();
    form.setFieldsValue({ status: 1, sort: 0 });
    setModalOpen(true);
  };

  const handleEdit = (record: PositionItem) => {
    setEditingPosition(record);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleDelete = async (record: PositionItem) => {
    try {
      await deletePositionById(record.id);
      SxwlMessage.success('删除成功');
      setSelectedRowKeys([]);
      loadData();
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  // -------- 批量删除 --------

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      SxwlMessage.warning('请至少选择一条记录');
      return;
    }
    try {
      await batchDeletePositionByIds(selectedRowKeys);
      SxwlMessage.success('批量删除成功');
      setSelectedRowKeys([]);
      loadData();
    } catch {
      SxwlMessage.error('批量删除失败');
    }
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[]) => setSelectedRowKeys(keys as number[]),
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setConfirmLoading(true);

      if (editingPosition) {
        await updatePosition({ ...values, id: editingPosition.id });
        SxwlMessage.success('更新成功');
      } else {
        await createPosition(values);
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

  // -------- 分页 --------

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  // -------- 列定义 --------

  const columns: ColumnsType<PositionItem> = [
    { title: '岗位编码', dataIndex: 'positionCode', key: 'positionCode', width: 120 },
    { title: '岗位名称', dataIndex: 'positionName', key: 'positionName', width: 150 },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 80 },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">启用</SxwlTag> : <SxwlTag color="red">禁用</SxwlTag>,
    },
    { title: '描述', dataIndex: 'description', key: 'description', width: 200, ellipsis: true },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} onClick={() => handleEdit(record)}>
            编辑
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该岗位吗？" onConfirm={() => handleDelete(record)}>
            <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />}>
              删除
            </SxwlButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  // -------- 配置 --------

  const searchFields: SearchFieldConfig[] = [
    { name: 'positionCode', label: '岗位编码', type: 'input', placeholder: '请输入岗位编码' },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增岗位', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
    {
      label: '批量删除',
      type: 'default',
      danger: true,
      icon: 'DeleteOutlined',
      onClick: handleBatchDelete,
    },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    { name: 'positionCode', label: '岗位编码', type: 'input', required: true, maxLength: 32, placeholder: '请输入岗位编码，如：dev_leader' },
    { name: 'positionName', label: '岗位名称', type: 'input', required: true, maxLength: 64, placeholder: '请输入岗位名称' },
    { name: 'sort', label: '排序', type: 'input', required: true, initialValue: 0, placeholder: '排序号，越小越靠前' },
    {
      name: 'status', label: '状态', type: 'select', required: true, initialValue: 1,
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
      placeholder: '请选择状态',
    },
    { name: 'description', label: '描述', type: 'input', maxLength: 200, placeholder: '请输入描述' },
  ], []);

  // -------- 渲染 --------

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
        breadcrumb={['系统管理', '岗位管理']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 1000 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <SxwlFormModal
        title={editingPosition ? '编辑岗位' : '新增岗位'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        layout="horizontal"
        columns={1}
        width={560}
        confirmLoading={confirmLoading}
      />
    </>
  );
}
