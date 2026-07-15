import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import type { DataNode } from 'antd/es/tree';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm, SxwlForm, SxwlMessage, SxwlModal,
  SxwlPage, SxwlFormModal, SxwlTree,
  type SearchFieldConfig, type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { RoleItem } from '@/api/system/roleApi';
import {
  getRolePageByParams, createRole, updateRole, deleteRoleById,
  saveRoleMenus, getMenuIdListByRoleId,
  saveRoleDataScope, getDataScopeOrgIdListByRoleId,
} from '@/api/system/roleApi';
import { getMenuTree } from '@/api/system/menuApi';
import { getOrganizationTree } from '@/api/system/organizationApi';

/** 菜单树转为 DataNode */
function toMenuTreeData(list: any[]): DataNode[] {
  return list.map((item: any) => ({
    key: item.id,
    title: `${item.menuName}${item.perms ? ` (${item.perms})` : ''}`,
    children: item.children ? toMenuTreeData(item.children) : undefined,
  }));
}

/** 组织树转为 DataNode */
function toOrgTreeData(list: any[]): DataNode[] {
  return list.map((item: any) => ({
    key: item.id,
    title: `${item.orgName} (${item.orgCode})`,
    children: item.children ? toOrgTreeData(item.children) : undefined,
  }));
}

export default function RolePage() {
  // ==================== 角色 CRUD ====================
  const [data, setData] = useState<RoleItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingRole, setEditingRole] = useState<RoleItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = SxwlForm.useForm();
  const searchRef = useRef<Record<string, any>>({});

  // ==================== 菜单分配 ====================
  const [menuAssignOpen, setMenuAssignOpen] = useState(false);
  const [assignRoleId, setAssignRoleId] = useState<number>(0);
  const [assignRoleName, setAssignRoleName] = useState('');
  const [menuTree, setMenuTree] = useState<DataNode[]>([]);
  const [checkedKeys, setCheckedKeys] = useState<number[]>([]);

  // ==================== 数据权限 ====================
  const [dataScopeOpen, setDataScopeOpen] = useState(false);
  const [dataScopeRoleId, setDataScopeRoleId] = useState<number>(0);
  const [dataScopeRoleName, setDataScopeRoleName] = useState('');
  const [orgTree, setOrgTree] = useState<DataNode[]>([]);
  const [checkedOrgKeys, setCheckedOrgKeys] = useState<number[]>([]);

  // -------- 角色 CRUD --------

  const loadData = useCallback(async (queryPage?: number) => {
    setLoading(true);
    try {
      const res = await getRolePageByParams({
        ...searchRef.current,
        current: queryPage ?? page,
        pageSize,
      });
      setData(res.data.data.list);
      setTotal(res.data.data.total);
    } catch {
      SxwlMessage.error('查询角色列表失败');
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
    setEditingRole(null);
    form.resetFields();
    form.setFieldsValue({ dataScope: 4, sort: 0, status: 1 });
    setModalOpen(true);
  };

  const handleEdit = (record: RoleItem) => {
    setEditingRole(record);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleDelete = async (record: RoleItem) => {
    try {
      await deleteRoleById(record.id);
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

      if (editingRole) {
        await updateRole({ ...values, id: editingRole.id });
        SxwlMessage.success('更新成功');
      } else {
        await createRole(values);
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

  // -------- 菜单分配 --------

  const handleAssignMenu = async (record: RoleItem) => {
    setAssignRoleId(record.id);
    setAssignRoleName(record.roleName);
    setMenuAssignOpen(true);
    try {
      const [treeRes, menuIdsRes] = await Promise.all([
        getMenuTree(),
        getMenuIdListByRoleId(record.id),
      ]);
      setMenuTree(toMenuTreeData(treeRes.data.data));
      setCheckedKeys(menuIdsRes.data.data || []);
    } catch {
      SxwlMessage.error('获取菜单数据失败');
    }
  };

  const handleSaveMenuAssign = async () => {
    try {
      await saveRoleMenus(assignRoleId, checkedKeys);
      SxwlMessage.success('菜单分配成功');
      setMenuAssignOpen(false);
    } catch {
      SxwlMessage.error('菜单分配失败');
    }
  };

  const handleCheck = (checked: any) => {
    setCheckedKeys(checked as number[]);
  };

  // -------- 数据权限 --------

  const handleConfigDataScope = async (record: RoleItem) => {
    setDataScopeRoleId(record.id);
    setDataScopeRoleName(record.roleName);
    setDataScopeOpen(true);
    try {
      const [treeRes, orgIdsRes] = await Promise.all([
        getOrganizationTree(),
        getDataScopeOrgIdListByRoleId(record.id),
      ]);
      setOrgTree(toOrgTreeData(treeRes.data.data));
      setCheckedOrgKeys(orgIdsRes.data.data || []);
    } catch {
      SxwlMessage.error('获取组织数据失败');
    }
  };

  const handleSaveDataScope = async () => {
    try {
      await saveRoleDataScope(dataScopeRoleId, checkedOrgKeys);
      SxwlMessage.success('数据权限配置成功');
      setDataScopeOpen(false);
    } catch {
      SxwlMessage.error('数据权限配置失败');
    }
  };

  const handleOrgCheck = (checked: any) => {
    setCheckedOrgKeys(checked as number[]);
  };

  // -------- 数据范围名称 --------

  const dataScopeLabel = (scope: number) => {
    const map: Record<number, string> = {
      1: '全部', 2: '本组织', 3: '本组织及下级', 4: '仅本人', 5: '自定义',
    };
    return map[scope] || '未知';
  };

  // -------- 列定义 --------

  const columns: ColumnsType<RoleItem> = [
    { title: '角色编码', dataIndex: 'roleCode', key: 'roleCode', width: 120 },
    { title: '角色名称', dataIndex: 'roleName', key: 'roleName', width: 150 },
    {
      title: '数据范围', dataIndex: 'dataScope', key: 'dataScope', width: 120,
      render: (scope: number) => dataScopeLabel(scope),
    },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 60 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">启用</SxwlTag> : <SxwlTag color="red">禁用</SxwlTag>,
    },
    { title: '描述', dataIndex: 'description', key: 'description', width: 200, ellipsis: true },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 300,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="SafetyCertificateOutlined" />} onClick={() => handleAssignMenu(record)}>
            菜单
          </SxwlButton>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="TeamOutlined" />} onClick={() => handleConfigDataScope(record)}>
            数据权限
          </SxwlButton>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} onClick={() => handleEdit(record)}>
            编辑
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该角色吗？" onConfirm={() => handleDelete(record)}>
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
    { name: 'roleCode', label: '角色编码', type: 'input', placeholder: '请输入角色编码' },
    { name: 'roleName', label: '角色名称', type: 'input', placeholder: '请输入角色名称' },
    {
      name: 'status', label: '状态', type: 'select', placeholder: '请选择状态',
      options: [
        { value: 1, label: '启用' },
        { value: 0, label: '禁用' },
      ],
    },
  ];

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增角色', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    { name: 'roleCode', label: '角色编码', type: 'input', required: true, maxLength: 32, placeholder: '请输入角色编码，如：admin' },
    { name: 'roleName', label: '角色名称', type: 'input', required: true, maxLength: 64, placeholder: '请输入角色名称' },
    {
      name: 'dataScope', label: '数据权限', type: 'select', required: true, initialValue: 4,
      options: [
        { value: 1, label: '全部' },
        { value: 2, label: '本组织' },
        { value: 3, label: '本组织及下级' },
        { value: 4, label: '仅本人' },
        { value: 5, label: '自定义' },
      ],
      placeholder: '请选择数据权限',
    },
    { name: 'sort', label: '排序', type: 'input', required: true, initialValue: 0, placeholder: '排序号，越小越靠前' },
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
        breadcrumb={['系统管理', '角色管理']}
        searchFields={searchFields}
        toolbarButtons={toolbarButtons}
        scroll={{ x: 1200 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <SxwlFormModal
        title={editingRole ? '编辑角色' : '新增角色'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        layout="horizontal"
        columns={1}
        width={600}
        confirmLoading={confirmLoading}
      />

      {/* 菜单分配弹窗 */}
      <SxwlModal
        title={`分配菜单 - ${assignRoleName}`}
        open={menuAssignOpen}
        onOk={handleSaveMenuAssign}
        onCancel={() => setMenuAssignOpen(false)}
        width={500}
        destroyOnHidden
      >
        <SxwlTree
          treeData={menuTree}
          checkable
          checkedKeys={checkedKeys}
          onCheck={handleCheck}
          defaultExpandAll
        />
      </SxwlModal>

      {/* 数据权限弹窗 */}
      <SxwlModal
        title={`数据权限 - ${dataScopeRoleName}`}
        open={dataScopeOpen}
        onOk={handleSaveDataScope}
        onCancel={() => setDataScopeOpen(false)}
        width={500}
        destroyOnHidden
      >
        <SxwlTree
          treeData={orgTree}
          checkable
          checkedKeys={checkedOrgKeys}
          onCheck={handleOrgCheck}
          defaultExpandAll
        />
      </SxwlModal>
    </>
  );
}
