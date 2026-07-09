import { Button, Typography, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import { logout } from '@/api/authApi';
import { LogoutIcon } from '@/components/icons';
import { useAuthStore } from '@/stores/authStore';

const { Title, Text } = Typography;

export default function DashboardPage() {
  const navigate = useNavigate();
  const username = useAuthStore((s) => s.username);
  const clearAuth = useAuthStore((s) => s.clearAuth);

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      // 登出失败也清本地状态
    }
    clearAuth();
    navigate('/login', { replace: true });
  };

  return (
    <div style={{ padding: 48, textAlign: 'center' }}>
      <Space direction="vertical" size="large">
        <Title level={3}>登录成功 🎉</Title>
        <Text>当前用户：{username}</Text>
        <Button
          type="primary"
          danger
          icon={<LogoutIcon />}
          onClick={handleLogout}
        >
          退出登录
        </Button>
      </Space>
    </div>
  );
}
