import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm, SxwlMessage, SxwlModal,
  SxwlPage, SxwlFormModal, SxwlTable,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { DictItem, DictDetailItem } from '@/api/system/dictApi';
import {
  getDictPageByParams, createDict, updateDict, deleteDictById,
  getDetailListByDictId, createDetail, updateDetail, deleteDetailById,
} from '@/api/system/dictApi';

export default function DictPage() {
  // ==================== 字典主表 ====================
  const [data, setData] = useState<DictItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingDict, setEditingDict] = useState<DictItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();
  const searchRef = useRef<Record<string, any>>({});

  // ==================== 字典明细 ====================
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [currentDictId, setCurrentDictId] = useState<number>(0);
  const [currentDictName, setCurrentDictName] = useState('');
  const [detailData, setDetailData] = useState<DictDetailItem[]>([]);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailFormOpen, setDetailFormOpen] = useState(false);
  const [editingDetail, setEditingDetail] = useState<DictDetailItem | null>(null);
  const [detailConfirmLoading, setDetailConfirmLoading] = useState(false);
  const [detailForm] = SxwlForm.useForm();

  // -------- 字典主表 CRUD --------

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getDictPageByParams({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询字典列表失败');
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
    setEditingDict(null);
    setModalOpen(true);
  };

  const handleEdit = (record: DictItem) => {
    setEditingDict(record);
    setModalOpen(true);
  };

  const handleDelete = async (record: DictItem) => {
    try {
      await deleteDictById(record.id);
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
      if (editingDict) {
        await updateDict({ ...values, id: editingDict.id });
        SxwlMessage.success('更新成功');
      } else {
        await createDict(values);
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

  // -------- 字典明细 --------

  const handleShowDetails = async (record: DictItem) => {
    setCurrentDictId(record.id);
    setCurrentDictName(record.dictName);
    setDetailModalOpen(true);
    setDetailLoading(true);
    try {
      const res = await getDetailListByDictId(record.id);
      setDetailData(res.data.data);
    } catch {
      SxwlMessage.error('查询字典明细失败');
    } finally {
      setDetailLoading(false);
    }
  };

  const handleAddDetail = () => {
    setEditingDetail(null);
    setDetailFormOpen(true);
  };

  const handleEditDetail = (record: DictDetailItem) => {
    setEditingDetail(record);
    setDetailFormOpen(true);
  };

  const handleDeleteDetail = async (record: DictDetailItem) => {
    try {
      await deleteDetailById(record.id);
      SxwlMessage.success('删除成功');
      // 重新加载明细
      const res = await getDetailListByDictId(currentDictId);
      setDetailData(res.data.data);
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  const handleSaveDetail = async () => {
    try {
      const values = await detailForm.validateFields();
      setDetailConfirmLoading(true);
      if (editingDetail) {
        await updateDetail({ ...values, id: editingDetail.id });
        SxwlMessage.success('更新成功');
      } else {
        await createDetail(values);
        SxwlMessage.success('创建成功');
      }
      setDetailFormOpen(false);
      // 重新加载明细
      const res = await getDetailListByDictId(currentDictId);
      setDetailData(res.data.data);
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { message?: string } } };
      SxwlMessage.error(axiosErr?.response?.data?.message || '操作失败');
    } finally {
      setDetailConfirmLoading(false);
    }
  };

  // -------- 列定义 --------

  const columns: ColumnsType<DictItem> = [
    { title: '字典编码', dataIndex: 'dictCode', key: 'dictCode', width: 100 },
    { title: '字典名称', dataIndex: 'dictName', key: 'dictName', width: 150 },
    { title: '描述', dataIndex: 'description', key: 'description', width: 200, ellipsis: true },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">启用</SxwlTag> : <SxwlTag color="red">禁用</SxwlTag>,
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 240,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="OrderedListOutlined" />} onClick={() => handleShowDetails(record)}>
            明细
          </SxwlButton>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} onClick={() => handleEdit(record)}>
            编辑
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该字典吗？" onConfirm={() => handleDelete(record)}>
            <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />}>
              删除
            </SxwlButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  // -------- 明细列定义 --------

  const detailColumns: ColumnsType<DictDetailItem> = [
    { title: '字典值', dataIndex: 'detailValue', key: 'detailValue', width: 100 },
    { title: '字典标签', dataIndex: 'detailLabel', key: 'detailLabel', width: 150 },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 60 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 70,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">启用</SxwlTag> : <SxwlTag color="red">禁用</SxwlTag>,
    },
    {
      title: '默认', dataIndex: 'isDefault', key: 'isDefault', width: 60,
      render: (isDefault: number) =>
        isDefault === 1 ? <SxwlTag color="blue">是</SxwlTag> : null,
    },
    { title: '描述', dataIndex: 'description', key: 'description', width: 150, ellipsis: true },
    {
      title: '操作', key: 'action', width: 160,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} onClick={() => handleEditDetail(record)}>
            编辑
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该明细吗？" onConfirm={() => handleDeleteDetail(record)}>
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
    { name: 'dictCode', label: '字典编码', type: 'input', placeholder: '请输入字典编码' },
    { name: 'dictName', label: '字典名称', type: 'input', placeholder: '请输入字典名称' },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增字典', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    { name: 'dictCode', label: '字典编码', type: 'input', required: true, maxLength: 2, placeholder: '请输入字典编码，如：01' },
    { name: 'dictName', label: '字典名称', type: 'input', required: true, maxLength: 64, placeholder: '请输入字典名称' },
    {
      name: 'status', label: '状态', type: 'select', required: true, initialValue: 1,
      options: [{ value: 1, label: '启用' }, { value: 0, label: '禁用' }],
      placeholder: '请选择状态',
    },
    { name: 'description', label: '描述', type: 'input', maxLength: 200, placeholder: '请输入描述' },
  ], []);

  const detailFormFields: FormFieldConfig[] = useMemo(() => [
    { name: 'detailValue', label: '字典值', type: 'input', required: true, maxLength: 4, placeholder: '请输入字典值，如：0101' },
    { name: 'detailLabel', label: '字典标签', type: 'input', required: true, maxLength: 128, placeholder: '请输入字典标签' },
    { name: 'sort', label: '排序', type: 'input', required: true, initialValue: 0, placeholder: '排序号，越小越靠前' },
    {
      name: 'status', label: '状态', type: 'select', required: true, initialValue: 1,
      options: [{ value: 1, label: '启用' }, { value: 0, label: '禁用' }],
      placeholder: '请选择状态',
    },
    {
      name: 'isDefault', label: '是否默认', type: 'select', required: true, initialValue: 0,
      options: [{ value: 0, label: '否' }, { value: 1, label: '是' }],
      placeholder: '请选择是否默认',
    },
    { name: 'description', label: '描述', type: 'input', maxLength: 200, placeholder: '请输入描述' },
  ], []);

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[]) => setSelectedRowKeys(keys as number[]),
  };

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
        breadcrumb={['系统管理', '字典管理']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 900 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <SxwlFormModal
        title={editingDict ? '编辑字典' : '新增字典'}
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
        editingData={editingDict}
      />

      {/* 字典明细弹窗 */}
      <SxwlModal
        title={`字典明细 - ${currentDictName}`}
        open={detailModalOpen}
        onCancel={() => { setDetailModalOpen(false); setDetailFormOpen(false); }}
        footer={null}
        width={800}
        destroyOnHidden
      >
        <SxwlSpace style={{ marginBottom: 16 }}>
          <SxwlButton type="primary" icon={<SxwlIcon name="PlusOutlined" />} onClick={handleAddDetail}>
            新增明细
          </SxwlButton>
        </SxwlSpace>
        <SxwlTable
          rowKey="id"
          columns={detailColumns}
          dataSource={detailData}
          loading={detailLoading}
          pagination={false}
          scroll={{ x: 800 }}
        />

        {/* 明细编辑弹窗 */}
        <SxwlFormModal
          title={editingDetail ? '编辑字典明细' : '新增字典明细'}
          open={detailFormOpen}
          form={detailForm}
          fields={detailFormFields}
          onOk={handleSaveDetail}
          onCancel={() => setDetailFormOpen(false)}
          layout="horizontal"
          columns={1}
          width={560}
          confirmLoading={detailConfirmLoading}
          initialValues={{ dictId: currentDictId, sort: 0, status: 1, isDefault: 0 }}
          editingData={editingDetail}
        />
      </SxwlModal>
    </>
  );
}
