import { useEffect, useMemo, useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  Layout, Menu, Dropdown, Avatar, Space, Button, Typography, Spin,
} from 'antd';
import type { MenuProps } from 'antd';
import { SxwlIcon } from '@/components';
import { useAuthStore } from '@/stores/authStore';
import { useMenuStore } from '@/stores/menuStore';
import { logout } from '@/api/authApi';
import type { MenuTreeItem } from '@/api/system/menuApi';
import logoSrc from '@/assets/images/logo.png';
import HeaderNotice from './HeaderNotice';
import SxwlClock from '@/components/SxwlClock';
import './index.scss';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;

/** DB 图标名 → SxwlIcon PascalCase 映射 */
const MENU_ICON_MAP: Record<string, string> = {
  setting: 'SettingOutlined',
  dashboard: 'DashboardOutlined',
  user: 'UserOutlined',
  role: 'SafetyCertificateOutlined',
  menu: 'ApartmentOutlined',
  organization: 'IdcardOutlined',
  position: 'ReadOutlined',
  dict: 'OrderedListOutlined',
  config: 'SettingOutlined',
  notice: 'BellOutlined',
  monitor: 'MonitorOutlined',
  server: 'DesktopOutlined',
  'online-user': 'TeamOutlined',
  cache: 'DatabaseOutlined',
  job: 'ScheduleOutlined',
  'job-log': 'FileSearchOutlined',
  backup: 'CloudUploadOutlined',
  log: 'FileSearchOutlined',
  'login-log': 'LoginOutlined',
  tool: 'ToolOutlined',
  codegen: 'SnippetsOutlined',
  file: 'FileOutlined',
};

/**
 * 递归将后端菜单树转换为 Ant Design Menu items
 * 目录节点使用 __dir_{id} 作为 key（不导航），菜单节点使用 /{path}
 */
function toMenuItems(nodes: MenuTreeItem[]): MenuProps['items'] {
  return nodes
    .filter((n) => n.visible === 1 && n.status === 1 && n.menuType <= 2)
    .map((n) => {
      const isDir = n.menuType === 1;
      const isFrame = n.isFrame === 1;
      const item: any = {
        key: isDir ? `__dir_${n.id}` : isFrame ? n.path || `__frame_${n.id}` : `/${n.path}`,
        icon: n.icon ? <SxwlIcon name={MENU_ICON_MAP[n.icon] || 'FileOutlined'} /> : undefined,
        label: n.menuName,
      };
      if (n.children?.length) {
        const children = toMenuItems(n.children);
        if (children?.length) {
          item.children = children;
        }
      }
      return item;
    });
}

/**
 * 收集菜单中的所有叶子节点 key
 */
function collectLeafKeys(items: MenuProps['items']): string[] {
  const keys: string[] = [];
  if (!items) return keys;
  for (const item of items) {
    if (!item) continue;
    if ('children' in item && (item as any).children?.length) {
      keys.push(...collectLeafKeys((item as any).children));
    } else {
      keys.push(item!.key as string);
    }
  }
  return keys;
}

/**
 * 根据当前路径自动展开父目录
 * 从菜单树中找出所有"某子节点 key 与当前路径匹配"的目录 key
 */
function computeParentDirs(items: MenuProps['items'], currentPath: string): string[] {
  const result: string[] = [];
  if (!items) return result;
  for (const item of items) {
    if (!item) continue;
    const key = item.key as string;
    if ('children' in item && (item as any).children?.length) {
      const children = (item as any).children as MenuProps['items'];
      const childMatches = children!.some((c) => {
        if (!c) return false;
        const ck = c.key as string;
        return currentPath === ck || currentPath.startsWith(ck + '/') || (ck.startsWith('__dir_') && computeParentDirs([c], currentPath).length > 0);
      });
      if (childMatches) {
        result.push(key);
      }
      // 递归检查更深层
      result.push(...computeParentDirs(children, currentPath));
    }
  }
  return result;
}

export default function SxwlLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const menuTree = useMenuStore((s) => s.menuTree);
  const loading = useMenuStore((s) => s.loading);
  const [openKeys, setOpenKeys] = useState<string[]>([]);
  const navigate = useNavigate();
  const location = useLocation();
  const username = useAuthStore((s) => s.username);
  const clearAuth = useAuthStore((s) => s.clearAuth);
  const clearMenuTree = useMenuStore((s) => s.clearMenuTree);

  const menuItems = useMemo(() => toMenuItems(menuTree), [menuTree]);

  const leafKeys = useMemo(() => collectLeafKeys(menuItems), [menuItems]);
  const selectedKeys = useMemo(
    () => leafKeys.filter((k) => location.pathname === k || location.pathname.startsWith(k + '/')),
    [leafKeys, location.pathname],
  );

  // 菜单加载完成后，根据当前路径自动展开父目录
  useEffect(() => {
    if (!menuItems?.length) return;
    const dirs = computeParentDirs(menuItems, location.pathname);
    setOpenKeys((prev) => {
      const merged = new Set([...prev, ...dirs]);
      return Array.from(merged);
    });
  }, [menuItems, location.pathname]);

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      // 登出失败也清本地
    }
    clearAuth();
    clearMenuTree();
    navigate('/login', { replace: true });
  };

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'logout',
      icon: <SxwlIcon name="LogoutOutlined" />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  return (
    <Layout className="sxwl-layout">
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        width={220}
        className="sxwl-sider"
      >
        <div className="sxwl-logo">
          <img src={logoSrc} alt="数行未来" className="sxwl-logo-icon" />
          {!collapsed && <span className="sxwl-logo-text">数行未来·御权</span>}
        </div>
        {loading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>
            <Spin />
          </div>
        ) : (
          <Menu
            theme="dark"
            mode="inline"
            selectedKeys={selectedKeys}
            openKeys={openKeys}
            onOpenChange={setOpenKeys}
            items={menuItems}
            onClick={({ key }) => {
              // 外链菜单 → 打开新窗口
              if (key.startsWith('http://') || key.startsWith('https://')) {
                window.open(key, '_blank');
                return;
              }
              const isLeaf = leafKeys.includes(key);
              if (isLeaf) navigate(key);
            }}
          />
        )}
      </Sider>
      <Layout>
        <Header className="sxwl-header">
          <div className="sxwl-header-left">
            <Button
              type="text"
              icon={collapsed ? <SxwlIcon name="MenuUnfoldOutlined" /> : <SxwlIcon name="MenuFoldOutlined" />}
              onClick={() => setCollapsed(!collapsed)}
            />
          </div>
          <div className="sxwl-header-center">
            <SxwlClock />
          </div>
          <div className="sxwl-header-right">
            <HeaderNotice />
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Space className="sxwl-user-dropdown">
                <Avatar size="small" icon={<SxwlIcon name="UserOutlined" />} />
                <Text>{username || '未知用户'}</Text>
              </Space>
            </Dropdown>
          </div>
        </Header>
        <Content className="sxwl-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
