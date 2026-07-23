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
import type { MenuTreeItem } from '@/api/system/menuApi';
import { getMenuTree, getMenuById, createMenu, updateMenu, deleteMenuById, getAllMenuList } from '@/api/system/menuApi';

/** 平铺列表转为 antd TreeDataNode（用于 TreeSelect） */
function toFlatTreeData(list: MenuTreeItem[]): DataNode[] {
  return list.map(item => ({
    key: item.id,
    title: `${item.menuName} (${item.menuType === 1 ? '目录' : item.menuType === 2 ? '菜单' : '按钮'})`,
    value: item.id,
    children: item.children ? toFlatTreeData(item.children) : undefined,
  }));
}

export default function MenuPage() {
  const [treeData, setTreeData] = useState<MenuTreeItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingMenu, setEditingMenu] = useState<MenuTreeItem | null>(null);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [flatTree, setFlatTree] = useState<DataNode[]>([]);
  const [menuType, setMenuType] = useState<number>(1);
  const [parentId, setParentId] = useState<number | undefined>(undefined);
  const [isRoot, setIsRoot] = useState(false);
  const [form] = SxwlForm.useForm();

  // 新增弹窗的默认值（编辑时为 undefined，由 editingData 控制）
  const formInitialValues = useMemo(() => {
    if (editingMenu) return undefined;
    const values: Record<string, any> = { menuType, sort: 0, visible: 1, status: 1, isFrame: 0, isCache: 0 };
    if (parentId !== undefined) values.parentId = parentId;
    return values;
  }, [editingMenu, parentId, menuType]);

  const loadTree = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getMenuTree();
      setTreeData(res.data.data);
    } catch {
      SxwlMessage.error('查询菜单树失败');
    } finally {
      setLoading(false);
    }
  }, []);

  const loadAllMenus = useCallback(async () => {
    try {
      const res = await getAllMenuList();
      const rootNode: MenuTreeItem = {
        id: 0, menuName: '根菜单', parentId: -1, ancestors: '0',
        menuType: 1, isFrame: 0, isCache: 0, sort: 0, visible: 1, status: 1,
      };
      const all = [rootNode, ...res.data.data];
      setFlatTree(toFlatTreeData(await buildTreeFromList(all)));
    } catch {
      // silent
    }
  }, []);

  useEffect(() => {
    loadTree();
    loadAllMenus();
  }, [loadTree, loadAllMenus]);

  // -------- 新增 & 编辑 --------

  const handleAdd = () => {
    setEditingMenu(null);
    setParentId(undefined);
    setMenuType(1);
    setIsRoot(true);
    setModalOpen(true);
  };

  const handleAddChild = (record: MenuTreeItem) => {
    setEditingMenu(null);
    setParentId(record.id);
    setMenuType(2);
    setIsRoot(false);
    setModalOpen(true);
  };

  const handleEdit = async (record: MenuTreeItem) => {
    try {
      const res = await getMenuById(record.id);
      const menu = res.data.data;
      setEditingMenu(menu);
      setParentId(menu.parentId);
      setMenuType(menu.menuType);
      setIsRoot(menu.parentId === 0);
      setModalOpen(true);
    } catch {
      SxwlMessage.error('获取菜单详情失败');
    }
  };

  const handleDelete = async (record: MenuTreeItem) => {
    try {
      await deleteMenuById(record.id);
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

      if (editingMenu) {
        await updateMenu({ ...values, id: editingMenu.id });
        SxwlMessage.success('更新成功');
      } else {
        await createMenu(values);
        SxwlMessage.success('创建成功');
      }

      setModalOpen(false);
      loadTree();
      loadAllMenus();
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { message?: string } } };
      SxwlMessage.error(axiosErr?.response?.data?.message || '操作失败');
    } finally {
      setConfirmLoading(false);
    }
  };

  const handleMenuTypeChange = (value: number) => {
    setMenuType(value);
  };

  const handleParentIdChange = (value: number) => {
    setParentId(value);
  };

  // -------- 列定义 --------

  const columns: ColumnsType<MenuTreeItem> = [
    { title: '菜单名称', dataIndex: 'menuName', key: 'menuName', width: 200 },
    {
      title: '类型', dataIndex: 'menuType', key: 'menuType', width: 80,
      render: (type: number) =>
        type === 1 ? <SxwlTag color="blue">目录</SxwlTag> :
        type === 2 ? <SxwlTag color="green">菜单</SxwlTag> :
        <SxwlTag color="orange">按钮</SxwlTag>,
    },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 60 },
    { title: '权限标识', dataIndex: 'perms', key: 'perms', width: 180 },
    { title: '路径', dataIndex: 'path', key: 'path', width: 150 },
    { title: '组件', dataIndex: 'component', key: 'component', width: 200 },
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
          <SxwlPopconfirm title="确定删除该菜单吗？" onConfirm={() => handleDelete(record)}>
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
    { label: '新增菜单', type: 'primary', icon: 'PlusOutlined', onClick: handleAdd },
  ];

  const formFields: FormFieldConfig[] = useMemo(() => [
    {
      name: 'menuType', label: '菜单类型', type: 'select', required: true, initialValue: 1,
      options: [
        { value: 1, label: '目录' },
        { value: 2, label: '菜单' },
        { value: 3, label: '按钮' },
      ],
      onChange: handleMenuTypeChange,
      placeholder: '请选择菜单类型',
    },
    { name: 'menuName', label: '菜单名称', type: 'input', required: true, maxLength: 64, placeholder: '请输入菜单名称' },
    {
      name: 'parentId', label: '上级菜单', type: 'select', placeholder: '请选择上级菜单',
      initialValue: isRoot ? 0 : parentId,
      options: flatTree.map(n => ({ value: n.key as number, label: n.title as string })),
      onChange: handleParentIdChange,
    },
    { name: 'path', label: '路由路径', type: 'input', maxLength: 128, placeholder: '路由路径，如：/sys/user' },
    { name: 'component', label: '组件路径', type: 'input', maxLength: 128, placeholder: '组件路径，如：system/user/index' },
    { name: 'perms', label: '权限标识', type: 'input', maxLength: 64, placeholder: '权限标识，如：system:user:list' },
    { name: 'icon', label: '菜单图标', type: 'input', maxLength: 64, placeholder: '图标名，如：UserOutlined' },
    { name: 'sort', label: '排序', type: 'input', required: true, initialValue: 0, placeholder: '排序号，越小越靠前' },
    {
      name: 'visible', label: '是否可见', type: 'select', initialValue: 1,
      options: [{ value: 1, label: '显示' }, { value: 0, label: '隐藏' }],
      placeholder: '请选择是否可见',
    },
    {
      name: 'status', label: '状态', type: 'select', required: true, initialValue: 1,
      options: [{ value: 1, label: '启用' }, { value: 0, label: '禁用' }],
      placeholder: '请选择状态',
    },
    { name: 'isFrame', label: '是否外链', type: 'select', initialValue: 0,
      options: [{ value: 0, label: '内嵌' }, { value: 1, label: '外链' }],
      placeholder: '请选择是否外链',
    },
    { name: 'isCache', label: '是否缓存', type: 'select', initialValue: 0,
      options: [{ value: 0, label: '不缓存' }, { value: 1, label: '缓存' }],
      placeholder: '请选择是否缓存',
    },
    { name: 'description', label: '描述', type: 'input', maxLength: 200, placeholder: '请输入描述' },
  ], [flatTree, menuType, isRoot, parentId]);

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
        breadcrumb={['系统管理', '菜单管理']}
        toolbarButtons={toolbarButtons}
      />

      <SxwlFormModal
        title={editingMenu ? '编辑菜单' : '新增菜单'}
        open={modalOpen}
        form={form}
        fields={formFields}
        onOk={handleSave}
        onCancel={() => setModalOpen(false)}
        width={640}
        confirmLoading={confirmLoading}
        initialValues={formInitialValues}
        editingData={editingMenu}
      />
    </>
  );
}

/** 从平铺列表构建树结构（递归） */
async function buildTreeFromList(list: MenuTreeItem[]): Promise<MenuTreeItem[]> {
  const map = new Map<number, MenuTreeItem>();
  const roots: MenuTreeItem[] = [];
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
