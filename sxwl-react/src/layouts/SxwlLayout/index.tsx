import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  Layout, Menu, Dropdown, Avatar, Space, Button, Typography,
} from 'antd';
import type { MenuProps } from 'antd';
import { SxwlIcon } from '@/components';
import { useAuthStore } from '@/stores/authStore';
import { logout } from '@/api/authApi';
import logoSrc from '@/assets/images/logo.png';
import './index.scss';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;

export default function SxwlLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const username = useAuthStore((s) => s.username);
  const clearAuth = useAuthStore((s) => s.clearAuth);

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      // 登出失败也清本地
    }
    clearAuth();
    navigate('/login', { replace: true });
  };

  const menuItems: MenuProps['items'] = [
    {
      key: '/dashboard',
      icon: <SxwlIcon name="DashboardOutlined" />,
      label: '工作台',
    },
    {
      key: '/system',
      icon: <SxwlIcon name="SafetyOutlined" />,
      label: '系统管理',
      children: [
        { key: '/system/user', icon: <SxwlIcon name="UserOutlined" />, label: '用户管理' },
        { key: '/system/role', icon: <SxwlIcon name="TeamOutlined" />, label: '角色管理' },
        { key: '/system/menu', icon: <SxwlIcon name="ApartmentOutlined" />, label: '菜单管理' },
        { key: '/system/organization', icon: <SxwlIcon name="IdcardOutlined" />, label: '组织架构' },
        { key: '/system/position', icon: <SxwlIcon name="ReadOutlined" />, label: '岗位管理' },
        { key: '/system/dict', icon: <SxwlIcon name="FileTextOutlined" />, label: '字典管理' },
      ],
    },
    {
      key: '/log',
      icon: <SxwlIcon name="FileSearchOutlined" />,
      label: '日志管理',
      children: [
        { key: '/log/operation', icon: <SxwlIcon name="FileTextOutlined" />, label: '操作日志' },
        { key: '/log/login', icon: <SxwlIcon name="LoginOutlined" />, label: '登录日志' },
      ],
    },
    {
      key: '/file',
      icon: <SxwlIcon name="CloudUploadOutlined" />,
      label: '文件管理',
    },
  ];

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
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          defaultOpenKeys={['/system']}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
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
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Space className="sxwl-user-dropdown">
              <Avatar size="small" icon={<SxwlIcon name="UserOutlined" />} />
              <Text>{username || '未知用户'}</Text>
            </Space>
          </Dropdown>
        </Header>
        <Content className="sxwl-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
