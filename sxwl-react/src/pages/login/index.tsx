import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Form, Input, Button, message, Typography, Space } from 'antd';
import { loginByPassword } from '@/api/auth';
import { LockIcon, UserIcon } from '@/components/icons';
import { useAuthStore } from '@/stores/authStore';
import { encryptPassword } from '@/utils/sm2Utils';
import './index.scss';

const { Title, Text } = Typography;

/** SM2 公钥（裸格式 04||x||y），从环境变量注入，未配置则应用启动即报错 */
const SM2_PUBLIC_KEY = import.meta.env.VITE_SM2_PUBLIC_KEY;
if (!SM2_PUBLIC_KEY) {
  throw new Error('VITE_SM2_PUBLIC_KEY 环境变量未配置，请在 .env 中设置 SM2 裸公钥');
}

interface LoginFormValues {
  username: string;
  password: string;
}

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const setTokens = useAuthStore((s) => s.setTokens);

  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/';

  const onFinish = async (values: LoginFormValues) => {
    setLoading(true);
    try {
      // SM2 加密密码（公钥必须配置，不降级明文）
      const passwordToSend = encryptPassword(values.password, SM2_PUBLIC_KEY);

      const res = await loginByPassword({
        username: values.username,
        password: passwordToSend,
      });

      const { accessToken, refreshToken } = res.data.data;
      setTokens(accessToken, refreshToken, values.username);
      message.success('登录成功');
      navigate(from, { replace: true });
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { msg?: string } } };
      message.error(axiosErr?.response?.data?.msg || '登录失败，请检查用户名和密码');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div className="login-header">
            <Title level={2} style={{ marginBottom: 0 }}>Sxwl Admin</Title>
            <Text type="secondary">欢迎回来，请登录您的账户</Text>
          </div>

          <Form<LoginFormValues>
            name="login"
            size="large"
            onFinish={onFinish}
            autoComplete="off"
            layout="vertical"
          >
            <Form.Item
              name="username"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <Input
                prefix={<UserIcon />}
                placeholder="用户名"
                autoFocus
              />
            </Form.Item>

            <Form.Item
              name="password"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password
                prefix={<LockIcon />}
                placeholder="密码"
              />
            </Form.Item>

            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading} block>
                登 录
              </Button>
            </Form.Item>
          </Form>
        </Space>
      </div>
    </div>
  );
}
