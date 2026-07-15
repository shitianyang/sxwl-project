// ============================================
// LoginPage — 登录页
//
// 竖向无标签布局，所有提示通过 placeholder 展示。
// 验证码输入框 + 图片平排显示。
// ============================================

import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Input } from 'antd';
import type { FormInstance } from 'antd';
import { loginByPassword } from '@/api/authApi';
import { useAuthStore } from '@/stores/authStore';
import { encryptPassword } from '@/utils/sm2Utils';
import { getCachedPublicKey, invalidatePublicKeyCache } from '@/utils/publicKeyUtils';
import { getItem, setItem, removeItem, STORAGE_KEYS } from '@/utils/storageUtils';
import { SxwlButton, SxwlCheckbox, SxwlForm, SxwlInput, SxwlMessage, SxwlCaptcha } from '@/components';
import logoSrc from '@/assets/images/logo.png';
import './index.scss';

/** 每次登录都从后端获取最新公钥，防止后端重启后密钥不匹配 */
interface LoginFormValues {
  username: string;
  password: string;
  captchaUuid: string;
  captchaCode: string;
  remember?: boolean;
}

/** 验证码行：左侧输入框 + 右侧图片，在 Form.Item 内正确绑定 value/onChange */
const CaptchaInput: React.FC<{
  form: FormInstance;
  value?: string;
  onChange?: (value: string) => void;
}> = ({ form, value, onChange }) => (
  <div className="login-captcha-row">
    <Input
      value={value}
      onChange={(e) => onChange?.(e.target.value)}
      placeholder="验证码"
      maxLength={4}
      className="login-captcha-input"
    />
    <SxwlCaptcha form={form} />
  </div>
);

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
      let publicKey: string;
      try {
        publicKey = await getCachedPublicKey();
      } catch {
        SxwlMessage.error('密钥服务异常，请稍后重试');
        return;
      }

      const passwordToSend = encryptPassword(values.password, publicKey);

      const res = await loginByPassword({
        username: values.username,
        password: passwordToSend,
        captchaUuid: values.captchaUuid,
        captchaCode: values.captchaCode,
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
      invalidatePublicKeyCache();
      const e = err as { response?: { data?: { message?: string } } };
      if (!e?.response) {
        SxwlMessage.error('网络连接异常，请检查网络');
      } else {
        SxwlMessage.error(e.response.data?.message || '登录失败，请检查用户名和密码');
      }
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
            layout="vertical"
            className="login-form"
            initialValues={{ remember: false }}
          >
            <SxwlForm.Item
              name="username"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <SxwlInput
                    placeholder="请输入用户名"
                    autoFocus
                    maxLength={50}
                    onBlur={(e) => {
                      const v = e.target.value.trim();
                      if (v !== e.target.value) form.setFieldsValue({ username: v });
                    }}
                  />
            </SxwlForm.Item>

            <SxwlForm.Item
              name="password"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <SxwlInput type="password" placeholder="请输入密码" maxLength={64} />
            </SxwlForm.Item>

            {/* 验证码：左侧输入框 + 右侧图片 */}
            <SxwlForm.Item
              name="captchaCode"
              rules={[{ required: true, message: '请输入验证码' }]}
            >
              <CaptchaInput form={form} />
            </SxwlForm.Item>

            {/* captchaUuid 隐藏字段 */}
            <SxwlForm.Item name="captchaUuid" hidden>
              <SxwlInput />
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

