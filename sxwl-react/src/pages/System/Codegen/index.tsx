import { useState, useEffect, useCallback, useRef } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm, SxwlMessage,
  SxwlPage,
  type SearchFieldConfig, type ToolbarButtonConfig,
} from '@/components';
import type { CodegenTableItem } from '@/api/codegen/codegenApi';
import {
  getCodegenTablePage, createCodegenTable, updateCodegenTable,
  deleteCodegenTable, downloadCodegen,
} from '@/api/codegen/codegenApi';
import CodegenFormModal from './CodegenFormModal';
import FieldConfigModal from './FieldConfigModal';
import PreviewModal from './PreviewModal';

export default function CodegenPage() {
  // ==================== 表配置 CRUD ====================
  const [data, setData] = useState<CodegenTableItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingTable, setEditingTable] = useState<CodegenTableItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();
  const searchRef = useRef<Record<string, any>>({});

  // ==================== 字段配置弹窗 ====================
  const [fieldModalOpen, setFieldModalOpen] = useState(false);
  const [fieldTableId, setFieldTableId] = useState<number>(0);
  const [fieldTableName, setFieldTableName] = useState('');

  // ==================== 预览弹窗 ====================
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewTableId, setPreviewTableId] = useState<number>(0);
  const [previewTableName, setPreviewTableName] = useState('');

  // -------- 数据加载 --------

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getCodegenTablePage({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询代码生成配置列表失败');
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

  // -------- CRUD --------

  const handleAdd = () => {
    setEditingTable(null);
    setModalOpen(true);
  };

  const handleEdit = (record: CodegenTableItem) => {
    setEditingTable(record);
    setModalOpen(true);
  };

  const handleDelete = async (record: CodegenTableItem) => {
    try {
      await deleteCodegenTable(record.id);
      SxwlMessage.success('删除成功');
      loadData();
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setConfirmLoading(true);

      if (editingTable) {
        await updateCodegenTable(editingTable.id, values);
        SxwlMessage.success('更新成功');
      } else {
        await createCodegenTable(values);
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

  // -------- 字段配置 --------

  const handleFieldConfig = (record: CodegenTableItem) => {
    setFieldTableId(record.id);
    setFieldTableName(record.bizNameCn || record.tableName);
    setFieldModalOpen(true);
  };

  // -------- 预览 --------

  const handlePreview = (record: CodegenTableItem) => {
    setPreviewTableId(record.id);
    setPreviewTableName(record.bizNameCn || record.tableName);
    setPreviewOpen(true);
  };

  // -------- 生成本地下载 --------

  const handleGenerate = async (record: CodegenTableItem) => {
    try {
      await downloadCodegen(record.id);
    } catch {
      SxwlMessage.error('代码生成失败');
    }
  };

  // -------- 列定义 --------

  const columns: ColumnsType<CodegenTableItem> = [
    { title: '数据库表名', dataIndex: 'tableName', key: 'tableName', width: 160 },
    { title: '业务名', dataIndex: 'bizName', key: 'bizName', width: 100 },
    { title: '中文名', dataIndex: 'bizNameCn', key: 'bizNameCn', width: 100 },
    { title: '模块前缀', dataIndex: 'modulePrefix', key: 'modulePrefix', width: 100 },
    { title: '包名', dataIndex: 'packageName', key: 'packageName', width: 180, ellipsis: true },
    {
      title: '生成类型', dataIndex: 'genType', key: 'genType', width: 90,
      render: (type: string) =>
        type === 'crud' ? <SxwlTag color="blue">CRUD</SxwlTag> : <SxwlTag color="green">树形</SxwlTag>,
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 70,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">启用</SxwlTag> : <SxwlTag color="red">禁用</SxwlTag>,
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 360,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} onClick={() => handleEdit(record)}>
            编辑
          </SxwlButton>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="SettingOutlined" />} onClick={() => handleFieldConfig(record)}>
            字段
          </SxwlButton>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EyeOutlined" />} onClick={() => handlePreview(record)}>
            预览
          </SxwlButton>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="DownloadOutlined" />} onClick={() => handleGenerate(record)}>
            生成
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

  // -------- 搜索/工具栏配置 --------

  const searchFields: SearchFieldConfig[] = [
    { name: 'tableName', label: '数据库表名', type: 'input', placeholder: '请输入表名' },
    { name: 'bizNameCn', label: '中文名', type: 'input', placeholder: '请输入中文业务名' },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增配置', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
  ];

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
        breadcrumb={['系统管理', '代码生成']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 1400 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <CodegenFormModal
        title={editingTable ? '编辑表配置' : '新增表配置'}
        open={modalOpen}
        form={form}
        initialValues={editingTable}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        confirmLoading={confirmLoading}
      />

      {fieldModalOpen && (
        <FieldConfigModal
          tableId={fieldTableId}
          tableName={fieldTableName}
          open={fieldModalOpen}
          onCancel={() => setFieldModalOpen(false)}
        />
      )}

      {previewOpen && (
        <PreviewModal
          tableId={previewTableId}
          tableName={previewTableName}
          open={previewOpen}
          onCancel={() => setPreviewOpen(false)}
        />
      )}
    </>
  );
}
