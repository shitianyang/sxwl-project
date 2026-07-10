import { useState, useEffect, useCallback } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm,
  SxwlMessage, SxwlPage, SxwlFormModal,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { UserItem } from '@/api/system/userApi';
// import { getUserList, createUser, updateUser, deleteUser } from '@/api/system/userApi';

// ==================== Demo Data

const DEMO_USERS: UserItem[] = [
  { userId: 1, username: 'admin', realName: '系统管理员', phone: '13800000000', email: 'admin@sxwl.com', status: 1, createTime: '2026-01-01 00:00:00' },
  { userId: 2, username: 'zhangsan', realName: '张三', phone: '13800000001', email: 'zhangsan@sxwl.com', status: 1, createTime: '2026-03-15 10:30:00' },
  { userId: 3, username: 'lisi', realName: '李四', phone: '13800000002', email: 'lisi@sxwl.com', status: 0, createTime: '2026-04-20 14:20:00' },
  { userId: 4, username: 'wangwu', realName: '王五', phone: '13800000003', email: 'wangwu@sxwl.com', status: 1, createTime: '2026-05-10 09:00:00' },
  { userId: 5, username: 'zhaoliu', realName: '赵六', phone: '13800000004', email: 'zhaoliu@sxwl.com', status: 1, createTime: '2026-06-01 16:45:00' },
];

export default function UserPage() {
  const [data, setData] = useState<UserItem[]>(DEMO_USERS);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserItem | null>(null);
  const [form] = SxwlForm.useForm();

  const loadData = useCallback(() => {
    setLoading(true);
    // TODO: 接入后端后替换为 API 调用
    // getUserList(params).then((res) => setData(res.data.rows)).finally(() => setLoading(false));
    setTimeout(() => {
      setData(DEMO_USERS);
      setLoading(false);
    }, 300);
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // -------- 搜索 & 重置 --------

  const handleSearch = (_values: Record<string, any>) => {
    // TODO: 传递 values 至 API
    loadData();
  };

  const handleReset = () => {
    loadData();
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

  const handleDelete = (record: UserItem) => {
    // TODO: 接入后端 API
    // deleteUser(record.userId)
    setData((prev) => prev.filter((item) => item.userId !== record.userId));
    SxwlMessage.success('删除成功');
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      if (editingUser) {
        setData((prev) =>
          prev.map((item) =>
            item.userId === editingUser.userId ? { ...item, ...values } : item,
          ),
        );
        SxwlMessage.success('更新成功');
      } else {
        const newUser: UserItem = {
          userId: Date.now(),
          ...values,
          createTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
        };
        setData((prev) => [...prev, newUser]);
        SxwlMessage.success('创建成功');
      }
      setModalOpen(false);
    } catch {
      // 表单校验未通过
    }
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
  ];

  const formFields: FormFieldConfig[] = [
    {
      name: 'username', label: '用户名', type: 'input', required: true,
      rules: [{ min: 2, max: 50, message: '用户名长度为 2-50 个字符' }],
      maxLength: 50,
    },
    { name: 'realName', label: '真实姓名', type: 'input', required: true, maxLength: 50 },
    {
      name: 'phone', label: '手机号', type: 'input',
      rules: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }],
      maxLength: 11,
    },
    {
      name: 'email', label: '邮箱', type: 'input',
      rules: [{ type: 'email', message: '请输入正确的邮箱地址' }],
      maxLength: 100,
    },
    {
      name: 'status', label: '状态', type: 'select', initialValue: 1,
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
  ];

  // -------- 渲染 --------

  return (
    <>
      <SxwlPage
        mode="table"
        paginated
        rowKey="userId"
        columns={columns}
        dataSource={data}
        loading={loading}
        total={data.length}
        pageSize={10}
        breadcrumb={['系统管理', '用户管理']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 1000 }}
        onSearch={handleSearch}
        onReset={handleReset}
      />

      {/* 新增/编辑弹窗 */}
      <SxwlFormModal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        width={520}
      />
    </>
  );
}
