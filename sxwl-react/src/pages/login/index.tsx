import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { loginByPassword } from '@/api/authApi';
import { useAuthStore } from '@/stores/authStore';
import { encryptPassword } from '@/utils/sm2Utils';
import { getItem, setItem, removeItem, STORAGE_KEYS } from '@/utils/storageUtils';
import { SxwlInput, SxwlButton, SxwlCheckbox, SxwlForm, SxwlMessage } from '@/components';
import logoSrc from '@/assets/images/logo.png';
import './index.scss';

/** SM2 公钥（裸格式 04||x||y），从环境变量注入 */
const SM2_PUBLIC_KEY = import.meta.env.VITE_SM2_PUBLIC_KEY;
if (!SM2_PUBLIC_KEY) {
  throw new Error('VITE_SM2_PUBLIC_KEY 环境变量未配置，请在 .env 中设置 SM2 裸公钥');
}

interface LoginFormValues {
  username: string;
  password: string;
  remember?: boolean;
}

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const setTokens = useAuthStore((s) => s.setTokens);
  const [form] = SxwlForm.useForm<LoginFormValues>();

  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/';

  // 页面加载时恢复已记忆的用户名
  useEffect(() => {
    const saved = getItem(STORAGE_KEYS.REMEMBERED_USERNAME);
    if (saved) {
      form.setFieldsValue({ username: saved, remember: true });
    }
  }, [form]);

  const onFinish = async (values: LoginFormValues) => {
    setLoading(true);
    try {
      const passwordToSend = encryptPassword(values.password, SM2_PUBLIC_KEY);

      const res = await loginByPassword({
        username: values.username,
        password: passwordToSend,
      });

      const { accessToken, refreshToken } = res.data.data;
      setTokens(accessToken, refreshToken, values.username);

      if (values.remember) {
        setItem(STORAGE_KEYS.REMEMBERED_USERNAME, values.username);
      } else {
        removeItem(STORAGE_KEYS.REMEMBERED_USERNAME);
      }

      SxwlMessage.success('登录成功');
      navigate(from, { replace: true });
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { msg?: string } } };
      SxwlMessage.error(axiosErr?.response?.data?.msg || '登录失败，请检查用户名和密码');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-body">
          <div className="login-header">
            <img src={logoSrc} alt="数行未来" className="login-logo" />
            <h1 className="login-title">数行未来·御权</h1>
            <p className="login-subtitle">统一权限管控平台</p>
          </div>

          <SxwlForm
            form={form}
            name="login"
            size="large"
            onFinish={onFinish}
            autoComplete="off"
            layout="horizontal"
            labelCol={{ style: { width: 56 } }}
            className="login-form"
            initialValues={{ remember: false }}
          >
            <SxwlForm.Item
              name="username"
              label="账号"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <SxwlInput
                placeholder="请输入用户名"
                autoFocus
                maxLength={50}
              />
            </SxwlForm.Item>

            <SxwlForm.Item
              name="password"
              label="密码"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <SxwlInput
                type="password"
                placeholder="请输入密码"
                maxLength={64}
              />
            </SxwlForm.Item>

            <SxwlForm.Item name="remember" valuePropName="checked">
              <SxwlCheckbox className="login-remember">记住用户名</SxwlCheckbox>
            </SxwlForm.Item>

            <SxwlForm.Item>
              <SxwlButton
                type="primary"
                htmlType="submit"
                loading={loading}
                block
                className="login-btn"
              >
                登 录
              </SxwlButton>
            </SxwlForm.Item>
          </SxwlForm>

          <div className="login-footer">
            &copy; {new Date().getFullYear()} 河北数行未来科技有限公司
          </div>
        </div>
      </div>
    </div>
  );
}
