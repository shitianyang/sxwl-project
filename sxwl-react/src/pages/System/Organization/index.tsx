import { useState, useEffect, useCallback, useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import type { DataNode } from 'antd/es/tree';
import {
  SxwlButton, SxwlIcon, SxwlTag,
  SxwlSpace, SxwlPopconfirm,
  SxwlForm, SxwlMessage, SxwlPage, SxwlFormModal,
  type ToolbarButtonConfig,
  type FormFieldConfig,
} from '@/components';
import type { OrganizationTreeItem } from '@/api/system/organizationApi';
import { getOrganizationTree, getOrganizationById, createOrganization, updateOrganization, deleteOrganizationById, getAllOrganizationList } from '@/api/system/organizationApi';

/** 转为 TreeSelect 可用的平铺树 */
function toFlatTreeData(list: OrganizationTreeItem[]): DataNode[] {
  return list.map(item => ({
    key: item.id,
    title: `${item.orgName} (${item.orgCode})`,
    value: item.id,
    children: item.children ? toFlatTreeData(item.children) : undefined,
  }));
}

export default function OrganizationPage() {
  const [treeData, setTreeData] = useState<OrganizationTreeItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingOrg, setEditingOrg] = useState<OrganizationTreeItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [flatTree, setFlatTree] = useState<DataNode[]>([]);
  const [form] = SxwlForm.useForm();

  // 新增时的上级组织 ID（handleAddChild 设置）
  const [addParentId, setAddParentId] = useState<number | undefined>(undefined);

  // 新增弹窗默认值（编辑时为 undefined，由 editingData 控制）
  const formInitialValues = useMemo(() => {
    if (editingOrg) return undefined;
    const values: Record<string, any> = { orgLevel: 2, sort: 0, status: 1 };
    if (addParentId !== undefined) values.parentId = addParentId;
    return values;
  }, [editingOrg, addParentId]);

  const loadTree = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getOrganizationTree();
      setTreeData(res.data.data);
    } catch {
      SxwlMessage.error('查询组织树失败');
    } finally {
      setLoading(false);
    }
  }, []);

  const loadAllOrgs = useCallback(async () => {
    try {
      const res = await getAllOrganizationList();
      const rootNode: OrganizationTreeItem = {
        id: 0, orgCode: '', orgName: '根组织', parentId: -1, ancestors: '0',
        orgLevel: 1, sort: 0, status: 1,
      };
      const all = [rootNode, ...res.data.data];
      setFlatTree(toFlatTreeData(await buildTreeFromList(all)));
    } catch {
      // silent
    }
  }, []);

  useEffect(() => {
    loadTree();
    loadAllOrgs();
  }, [loadTree, loadAllOrgs]);

  // -------- 新增 & 编辑 --------

  const handleAdd = () => {
    setEditingOrg(null);
    setAddParentId(undefined);
    setModalOpen(true);
  };

  const handleAddChild = (record: OrganizationTreeItem) => {
    setEditingOrg(null);
    setAddParentId(record.id);
    setModalOpen(true);
  };

  const handleEdit = async (record: OrganizationTreeItem) => {
    try {
      const res = await getOrganizationById(record.id);
      const org = res.data.data;
      setEditingOrg(org);
      setAddParentId(undefined);
      setModalOpen(true);
    } catch {
      SxwlMessage.error('获取组织详情失败');
    }
  };

  const handleDelete = async (record: OrganizationTreeItem) => {
    try {
      await deleteOrganizationById(record.id);
      SxwlMessage.success('删除成功');
      loadTree();
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setConfirmLoading(true);

      if (editingOrg) {
        await updateOrganization({ ...values, id: editingOrg.id });
        SxwlMessage.success('更新成功');
      } else {
        await createOrganization(values);
        SxwlMessage.success('创建成功');
      }

      setModalOpen(false);
      loadTree();
      loadAllOrgs();
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { message?: string } } };
      SxwlMessage.error(axiosErr?.response?.data?.message || '操作失败');
    } finally {
      setConfirmLoading(false);
    }
  };

  // -------- 列定义 --------

  const columns: ColumnsType<OrganizationTreeItem> = [
    { title: '组织名称', dataIndex: 'orgName', key: 'orgName', width: 200 },
    { title: '组织编码', dataIndex: 'orgCode', key: 'orgCode', width: 100 },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 60 },
    {
      title: '层级', dataIndex: 'orgLevel', key: 'orgLevel', width: 80,
      render: (level: number) =>
        level === 1 ? '公司' : level === 2 ? '部门' : level === 3 ? '小组' : '',
    },
    { title: '负责人', dataIndex: 'leaderId', key: 'leaderId', width: 100 },
    { title: '电话', dataIndex: 'phone', key: 'phone', width: 130 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 70,
      render: (status: number) =>
        status === 1 ? <SxwlTag color="green">启用</SxwlTag> : <SxwlTag color="red">禁用</SxwlTag>,
    },
    {
      title: '操作', key: 'action', width: 220,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="PlusOutlined" />} onClick={() => handleAddChild(record)}>
            新增
          </SxwlButton>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EditOutlined" />} onClick={() => handleEdit(record)}>
            编辑
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该组织吗？" onConfirm={() => handleDelete(record)}>
            <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />}>
              删除
            </SxwlButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  // -------- 配置 --------

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '新增组织', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    { name: 'orgName', label: '组织名称', type: 'input', required: true, maxLength: 64, placeholder: '请输入组织名称' },
    { name: 'orgCode', label: '组织编码', type: 'input', required: true, maxLength: 4, placeholder: '请输入组织编码，如：DEPT' },
    {
      name: 'parentId', label: '上级组织', type: 'select', placeholder: '请选择上级组织',
      options: flatTree.map(n => ({ value: n.key as number, label: n.title as string })),
    },
    {
      name: 'orgLevel', label: '组织层级', type: 'select', required: true, initialValue: 2,
      options: [
        { value: 1, label: '公司' },
        { value: 2, label: '部门' },
        { value: 3, label: '小组' },
      ],
      placeholder: '请选择组织层级',
    },
    { name: 'orgType', label: '组织类型', type: 'input', maxLength: 4, placeholder: '请输入组织类型，如：01（关联字典）' },
    { name: 'leaderId', label: '负责人ID', type: 'input', placeholder: '请输入负责人ID' },
    { name: 'phone', label: '联系电话', type: 'input', maxLength: 20, placeholder: '请输入联系电话' },
    { name: 'sort', label: '排序', type: 'input', required: true, initialValue: 0, placeholder: '排序号，越小越靠前' },
    {
      name: 'status', label: '状态', type: 'select', required: true, initialValue: 1,
      options: [{ value: 1, label: '启用' }, { value: 0, label: '禁用' }],
      placeholder: '请选择状态',
    },
    { name: 'description', label: '描述', type: 'input', maxLength: 200, placeholder: '请输入描述' },
  ], [flatTree]);

  // -------- 渲染 --------

  return (
    <>
      <SxwlPage
        mode="tree"
        paginated={false}
        rowKey="id"
        columns={columns}
        dataSource={treeData}
        loading={loading}
        breadcrumb={['系统管理', '组织管理']}
        toolbarButtons={toolbarButtons}
      />

      <SxwlFormModal
        title={editingOrg ? '编辑组织' : '新增组织'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        width={600}
        confirmLoading={confirmLoading}
        initialValues={formInitialValues}
        editingData={editingOrg}
      />
    </>
  );
}

/** 从平铺列表构建树结构 */
async function buildTreeFromList(list: OrganizationTreeItem[]): Promise<OrganizationTreeItem[]> {
  const map = new Map<number, OrganizationTreeItem>();
  const roots: OrganizationTreeItem[] = [];
  list.forEach(item => {
    map.set(item.id, { ...item, children: [] });
  });
  list.forEach(item => {
    const node = map.get(item.id)!;
    if (item.parentId === 0 || !map.has(item.parentId)) {
      roots.push(node);
    } else {
      const parent = map.get(item.parentId);
      if (parent) {
        parent.children = parent.children || [];
        parent.children.push(node);
      }
    }
  });
  return roots;
}
