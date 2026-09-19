import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm,
  SxwlMessage, SxwlPage, SxwlFormModal, SxwlPermissionButton,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { UserItem } from '@/api/system/userApi';
import { getUserPageByParams, getUserById, createUser, updateUser, deleteUserById, batchDeleteByIds } from '@/api/system/userApi';
import type { RoleItem } from '@/api/system/roleApi';
import { getRolePageByParams } from '@/api/system/roleApi';
import type { OrganizationTreeItem } from '@/api/system/organizationApi';
import { getOrganizationTree } from '@/api/system/organizationApi';
import type { PositionItem } from '@/api/system/positionApi';
import { getPositionPageByParams } from '@/api/system/positionApi';
import { encryptPassword } from '@/utils/sm2Utils';
import { getCachedPublicKey, invalidatePublicKeyCache } from '@/utils/publicKeyUtils';

const SUPER_ADMIN_USERNAME = 'SuperAdmin';

export default function UserPage() {
  const [data, setData] = useState<UserItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  // 编辑回显数据：orgId 是从 orgIds 首个元素提取的表单内部字段（组织单选）
  const [editingUser, setEditingUser] = useState<(UserItem & { orgId?: number }) | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();

  // 角色/组织/岗位 下拉选项
  const [roleOptions, setRoleOptions] = useState<{ value: number; label: string }[]>([]);
  const [orgTreeData, setOrgTreeData] = useState<OrganizationTreeItem[]>([]);
  const [positionOptions, setPositionOptions] = useState<{ value: number; label: string }[]>([]);

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

  // 加载角色/组织/岗位下拉选项（仅首次）
  useEffect(() => {
    (async () => {
      try {
        const [roleRes, orgRes, posRes] = await Promise.all([
          getRolePageByParams({ current: 1, pageSize: 200 }),
          getOrganizationTree(),
          getPositionPageByParams({ current: 1, pageSize: 200 }),
        ]);
        setRoleOptions(roleRes.data.data.list.map((r: RoleItem) => ({ value: r.id, label: r.roleName })));
        setOrgTreeData(orgRes.data.data);
        setPositionOptions(posRes.data.data.list.map((p: PositionItem) => ({ value: p.id, label: p.positionName })));
      } catch {
        SxwlMessage.error('加载角色/组织/岗位选项失败');
      }
    })();
  }, []);

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
    setModalOpen(true);
  };

  const handleEdit = async (record: UserItem) => {
    try {
      const res = await getUserById(record.id);
      const detail = res.data.data;
      // 组织单选：取主组织（orgIds 首个，后端按 is_main DESC 排序）回显到 orgId
      setEditingUser({ ...detail, orgId: detail.orgIds?.[0] });
      setModalOpen(true);
    } catch {
      SxwlMessage.error('获取用户详情失败');
    }
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
    getCheckboxProps: (record: UserItem) => ({ disabled: isProtectedAdmin(record) }),
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setConfirmLoading(true);

      // 组织单选：表单内部用 orgId 标量，提交时包装成后端约定的 orgIds 数组（空表示不变更锚点）
      const { orgId, ...rest } = values;
      const orgIds = orgId != null ? [orgId as number] : [];

      if (editingUser) {
        // 编辑：密码可选，传值则 SM2 加密
        const payload = {
          ...rest,
          id: editingUser.id,
          roleIds: (rest.roleIds ?? []) as number[],
          orgIds,
          positionId: rest.positionId ?? null,
        };
        if (values.password) {
          const publicKey = await getCachedPublicKey();
          payload.password = encryptPassword(values.password, publicKey);
        }
        await updateUser(payload);
        SxwlMessage.success('更新成功');
      } else {
        // 新增：密码必填，SM2 加密后发送
        const publicKey = await getCachedPublicKey();
        const encryptedPassword = encryptPassword(values.password, publicKey);
        await createUser({
          ...rest,
          roleIds: (rest.roleIds ?? []) as number[],
          orgIds,
          positionId: rest.positionId ?? null,
          password: encryptedPassword,
        });
        SxwlMessage.success('创建成功');
      }

      setModalOpen(false);
      loadData();
    } catch (err: unknown) {
      invalidatePublicKeyCache();
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

  const isProtectedAdmin = (record: UserItem) => record.superAdmin || record.username === SUPER_ADMIN_USERNAME;

  const columns: ColumnsType<UserItem> = [
    { title: '用户名', dataIndex: 'username', key: 'username', width: 140 },
    { title: '真实姓名', dataIndex: 'realName', key: 'realName', width: 120 },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      width: 140,
      render: (phone?: string) => (phone ? <span className="sxwl-num">{phone}</span> : '—'),
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      ellipsis: true,
      render: (email?: string) => (email ? <span className="sxwl-num">{email}</span> : '—'),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 88,
      render: (status: number) =>
        status === 1 ? <SxwlTag tone="success">启用</SxwlTag> : <SxwlTag tone="neutral">禁用</SxwlTag>,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 200,
      render: (time?: string) => (time ? <span className="sxwl-num">{time}</span> : '—'),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <SxwlSpace size={0}>
          {!isProtectedAdmin(record) && (
            <SxwlPermissionButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} permission="system:user:edit" onClick={() => handleEdit(record)}>
              编辑
            </SxwlPermissionButton>
          )}
          {!isProtectedAdmin(record) && (
            <SxwlPopconfirm title="确定删除该用户吗？" onConfirm={() => handleDelete(record)}>
              <SxwlPermissionButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />} permission="system:user:delete">
                删除
              </SxwlPermissionButton>
            </SxwlPopconfirm>
          )}
        </SxwlSpace>
      ),
    },
  ];

  // -------- 配置 --------

  const searchFields: SearchFieldConfig[] = [
    { name: 'username', label: '用户名', type: 'input', placeholder: '搜索用户名' },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '全部状态', allowClear: true,
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增用户', type: 'primary', icon: 'PlusOutlined', permission: 'system:user:add', onClick: handleAdd },
    {
      label: '批量删除',
      type: 'default',
      danger: true,
      icon: 'DeleteOutlined',
      permission: 'system:user:delete',
      onClick: handleBatchDelete,
    },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    {
      name: 'username', label: '用户名', type: 'input', required: true,
      disabled: !!editingUser,
      rules: [{ min: 2, max: 50, message: '用户名长度为 2-50 个字符' }],
      maxLength: 50,
      placeholder: '2-50 个字符',
    },
    { name: 'realName', label: '真实姓名', type: 'input', required: true, maxLength: 50 },
    {
      name: 'phone', label: '手机号', type: 'input', required: true,
      rules: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }],
      maxLength: 11,
      placeholder: '11 位手机号',
    },
    {
      name: 'email', label: '邮箱', type: 'input',
      rules: [{ type: 'email', message: '请输入正确的邮箱地址' }],
      maxLength: 100,
      placeholder: 'user@example.com',
    },
    {
      name: 'password', label: '密码', type: 'password',
      required: !editingUser,
      rules: editingUser ? [] : [{ min: 6, max: 32, message: '密码长度为 6-32 个字符' }],
      maxLength: 32,
      extra: editingUser ? '留空则不修改密码' : undefined,
    },
    {
      name: 'status', label: '状态', type: 'select', initialValue: 1,
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
    {
      name: 'roleIds', label: '角色', type: 'select', mode: 'multiple', showSearch: true,
      options: roleOptions, placeholder: '可多选',
    },
    {
      name: 'orgId', label: '组织', type: 'treeSelect',
      treeData: orgTreeData, fieldNames: { label: 'orgName', value: 'id', children: 'children' },
      extra: '决定该用户可见的数据范围',
    },
    {
      name: 'positionId', label: '岗位', type: 'select', showSearch: true, allowClear: true,
      options: positionOptions,
    },
  ], [editingUser, roleOptions, orgTreeData, positionOptions]);

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
        title="用户管理"
        description="维护账号、状态与角色 / 组织 / 岗位归属"
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
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
        layout="vertical"
        columns={2}
        width={640}
        confirmLoading={confirmLoading}
        initialValues={{ status: 1 }}
        editingData={editingUser}
      />
    </>
  );
}
