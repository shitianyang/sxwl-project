import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm,
  SxwlMessage, SxwlPage, SxwlFormModal,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { UserItem } from '@/api/system/userApi';
import { getUserPageByParams, createUser, updateUser, deleteUserById, batchDeleteByIds } from '@/api/system/userApi';
import { getPublicKey } from '@/api/authApi';
import { encryptPassword } from '@/utils/sm2Utils';

export default function UserPage() {
  const [data, setData] = useState<UserItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();

  // 搜索参数（不触发重渲染，仅 loadData 时读取）
  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getUserPageByParams({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询用户列表失败');
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
    setEditingUser(null);
    form.resetFields();
    form.setFieldsValue({ status: 1 });
    setModalOpen(true);
  };

  const handleEdit = (record: UserItem) => {
    setEditingUser(record);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleDelete = async (record: UserItem) => {
    try {
      await deleteUserById(record.id);
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
      await batchDeleteByIds(selectedRowKeys);
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

      if (editingUser) {
        // 编辑：密码可选，传值则 SM2 加密
        const payload = { ...values, id: editingUser.id };
        if (values.password) {
          const publicKey = (await getPublicKey()).data.data.publicKey;
          payload.password = encryptPassword(values.password, publicKey);
        }
        await updateUser(payload);
        SxwlMessage.success('更新成功');
      } else {
        // 新增：密码必填，SM2 加密后发送
        const publicKey = (await getPublicKey()).data.data.publicKey;
        const encryptedPassword = encryptPassword(values.password, publicKey);
        await createUser({ ...values, password: encryptedPassword });
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

  const columns: ColumnsType<UserItem> = [
    { title: '用户名', dataIndex: 'username', key: 'username', width: 120 },
    { title: '真实姓名', dataIndex: 'realName', key: 'realName', width: 130 },
    { title: '手机号', dataIndex: 'phone', key: 'phone', width: 140 },
    { title: '邮箱', dataIndex: 'email', key: 'email', width: 200, ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">启用</SxwlTag> : <SxwlTag color="red">禁用</SxwlTag>,
    },
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
          <SxwlPopconfirm title="确定删除该用户吗？" onConfirm={() => handleDelete(record)}>
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
    { name: 'username', label: '用户名', type: 'input', placeholder: '请输入用户名' },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '请选择',
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增用户', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
    {
      label: '批量删除',
      type: 'default',
      danger: true,
      icon: 'DeleteOutlined',
      onClick: handleBatchDelete,
    },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    {
      name: 'username', label: '用户名', type: 'input', required: true,
      disabled: !!editingUser,
      rules: [{ min: 2, max: 50, message: '用户名长度为 2-50 个字符' }],
      maxLength: 50,
    },
    { name: 'realName', label: '真实姓名', type: 'input', required: true, maxLength: 50 },
    {
      name: 'phone', label: '手机号', type: 'input', required: true,
      rules: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }],
      maxLength: 11,
    },
    {
      name: 'email', label: '邮箱', type: 'input',
      rules: [{ type: 'email', message: '请输入正确的邮箱地址' }],
      maxLength: 100,
    },
    {
      name: 'password', label: '密码', type: 'input',
      required: !editingUser,
      rules: editingUser ? [] : [{ min: 6, max: 32, message: '密码长度为 6-32 个字符' }],
      maxLength: 32,
    },
    {
      name: 'status', label: '状态', type: 'select', initialValue: 1,
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
  ], [editingUser]);

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
        breadcrumb={['系统管理', '用户管理']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 1000 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <SxwlFormModal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        width={520}
        confirmLoading={confirmLoading}
      />
    </>
  );
}
