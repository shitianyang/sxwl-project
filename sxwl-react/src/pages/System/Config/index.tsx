import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm, SxwlMessage,
  SxwlPage, SxwlFormModal,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { SysConfigItem } from '@/api/system/configApi';
import {
  getConfigPageByParams, createConfig, updateConfig, deleteConfigById,
} from '@/api/system/configApi';

export default function ConfigPage() {
  const [data, setData] = useState<SysConfigItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState<SysConfigItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();
  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getConfigPageByParams({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询配置列表失败');
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

  const handleEdit = (record: SysConfigItem) => {
    setEditingRecord(record);
    setModalOpen(true);
  };

  const handleDelete = async (record: SysConfigItem) => {
    try {
      await deleteConfigById(record.id);
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
        await updateConfig({ ...values, id: editingRecord.id });
        SxwlMessage.success('更新成功');
      } else {
        await createConfig(values);
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

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  const columns: ColumnsType<SysConfigItem> = [
    { title: '参数键名', dataIndex: 'configKey', key: 'configKey', width: 160 },
    { title: '参数名称', dataIndex: 'configName', key: 'configName', width: 150 },
    { title: '参数值', dataIndex: 'configValue', key: 'configValue', width: 200, ellipsis: true },
    {
      title: '内置', dataIndex: 'configType', key: 'configType', width: 70,
      render: (val: string) =>
        val === 'Y' ? <SxwlTag color="blue">是</SxwlTag> : <SxwlTag>否</SxwlTag>,
    },
    { title: '描述', dataIndex: 'description', key: 'description', width: 200, ellipsis: true },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">启用</SxwlTag> : <SxwlTag color="red">禁用</SxwlTag>,
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 160,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} onClick={() => handleEdit(record)}>
            编辑
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该配置吗？" onConfirm={() => handleDelete(record)}>
            <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />}>
              删除
            </SxwlButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  const searchFields: SearchFieldConfig[] = [
    { name: 'configKey', label: '参数键名', type: 'input', placeholder: '请输入参数键名' },
    { name: 'configName', label: '参数名称', type: 'input', placeholder: '请输入参数名称' },
    {
      name: 'configType', label: '内置', type: 'select', placeholder: '请选择',
      options: [{ value: 'Y', label: '是' }, { value: 'N', label: '否' }],
    },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
      options: [{ value: 1, label: '启用' }, { value: 0, label: '禁用' }],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增配置', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    { name: 'configKey', label: '参数键名', type: 'input', required: true, maxLength: 64, placeholder: '请输入参数键名' },
    { name: 'configName', label: '参数名称', type: 'input', required: true, maxLength: 128, placeholder: '请输入参数名称' },
    { name: 'configValue', label: '参数值', type: 'input', required: true, maxLength: 512, placeholder: '请输入参数值' },
    {
      name: 'configType', label: '是否内置', type: 'select', required: true, initialValue: 'N',
      options: [{ value: 'Y', label: '是' }, { value: 'N', label: '否' }],
      placeholder: '请选择',
    },
    {
      name: 'status', label: '状态', type: 'select', required: true, initialValue: 1,
      options: [{ value: 1, label: '启用' }, { value: 0, label: '禁用' }],
      placeholder: '请选择状态',
    },
    { name: 'description', label: '描述', type: 'input', maxLength: 200, placeholder: '请输入描述' },
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
        breadcrumb={['系统管理', '参数配置']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 1100 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <SxwlFormModal
        title={editingRecord ? '编辑配置' : '新增配置'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        layout="horizontal"
        columns={1}
        width={560}
        confirmLoading={confirmLoading}
        initialValues={{ status: 1, configType: 'N' }}
        editingData={editingRecord}
      />
    </>
  );
}
