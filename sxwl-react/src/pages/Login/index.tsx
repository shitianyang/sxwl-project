// ============================================
// LoginPage — 登录页
// 视觉：全屏浅色渐变背景、横排 Logo+标题、前缀图标输入框
// ============================================

import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import type { FormInstance } from 'antd';
import { loginByPassword } from '@/api/authApi';
import { useAuthStore } from '@/stores/authStore';
import { encryptPassword } from '@/utils/sm2Utils';
import { getCachedPublicKey, invalidatePublicKeyCache } from '@/utils/publicKeyUtils';
import { SxwlButton, SxwlForm, SxwlInput, SxwlMessage, SxwlCaptcha, SxwlIcon } from '@/components';
import logoSrc from '@/assets/images/logo.png';
import useLoginStyles from './index.style';

/** 每次登录都从后端获取最新公钥，防止后端重启后密钥不匹配 */
interface LoginFormValues {
  username: string;
  password: string;
  captchaUuid: string;
  captchaCode: string;
}

/** 验证码行：左侧输入框 + 右侧图片，在 Form.Item 内正确绑定 value/onChange */
const CaptchaInput: React.FC<{
  form: FormInstance;
  value?: string;
  onChange?: (value: string) => void;
  styles: Record<string, string>;
  refreshKey: number;
}> = ({ form, value, onChange, styles, refreshKey }) => (
  <div className={styles.captchaRow}>
    <SxwlInput
      value={value}
      onChange={(e) => onChange?.(e.target.value)}
      placeholder="验证码"
      maxLength={4}
      className={styles.captchaInput}
    />
    <SxwlCaptcha form={form} refreshKey={refreshKey} />
  </div>
);

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  /** 登录失败后递增，触发验证码刷新 */
  const [captchaRefreshKey, setCaptchaRefreshKey] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();
  const setTokens = useAuthStore((s) => s.setTokens);
  const [form] = SxwlForm.useForm<LoginFormValues>();
  const { styles } = useLoginStyles();

  // 保留 search/hash，避免登录后回跳丢失查询参数与锚点
  const fromState = location.state as
    | { from?: { pathname: string; search?: string; hash?: string } }
    | null;
  const from = fromState?.from
    ? fromState.from.pathname + (fromState.from.search || '') + (fromState.from.hash || '')
    : '/';

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

      SxwlMessage.success('登录成功');
      navigate(from, { replace: true });
    } catch (err: unknown) {
      invalidatePublicKeyCache();
      // 登录失败：刷新验证码，避免同一个失效验证码重复提交
      setCaptchaRefreshKey((k) => k + 1);
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
    <div className={styles.container}>
      {/* 极淡网格背景层 */}
      <div className={styles.gridLayer} />
      <div className={styles.content}>
        <div className={styles.loginContainer}>
          {/* 登录卡片（含头部 + 表单 + 底部版权，对齐原型） */}
          <div className={styles.main}>
            {/* 头部：Logo + 标题 + 副标题（卡片内顶部） */}
            <div className={styles.top}>
              <div className={styles.header}>
                <img src={logoSrc} alt="数行未来" className={styles.logo} />
                <div className={styles.logoText}>
                  <strong className={styles.title}>数行未来·御权</strong>
                  <small className={styles.logoEn}>SXWL PERMISSION PLATFORM</small>
                </div>
              </div>
              <p className={styles.desc}>统一权限管控平台 · 请登录你的账号</p>
            </div>

            {/* 表单 */}
            <SxwlForm
              form={form}
              name="login"
              size="large"
              onFinish={onFinish}
              autoComplete="off"
              layout="vertical"
              className={styles.form}
            >
              <SxwlForm.Item
                name="username"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <SxwlInput
                  prefix={<SxwlIcon name="UserOutlined" size={16} />}
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
                <SxwlInput
                  prefix={<SxwlIcon name="LockOutlined" size={16} />}
                  type="password"
                  placeholder="请输入密码"
                  maxLength={64}
                />
              </SxwlForm.Item>

              {/* 验证码：左侧输入框 + 右侧图片 */}
              <SxwlForm.Item
                name="captchaCode"
                rules={[{ required: true, message: '请输入验证码' }]}
              >
                <CaptchaInput form={form} styles={styles} refreshKey={captchaRefreshKey} />
              </SxwlForm.Item>

              {/* captchaUuid 隐藏字段 */}
              <SxwlForm.Item name="captchaUuid" hidden>
                <SxwlInput />
              </SxwlForm.Item>

              <SxwlForm.Item>
                <SxwlButton
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  block
                  className={styles.button}
                >
                  {loading ? null : '登 录'}
                </SxwlButton>
              </SxwlForm.Item>
            </SxwlForm>

            {/* 底部版权（卡片内底部） */}
            <div className={styles.footer}>
              &copy; {new Date().getFullYear()} 河北数行未来科技有限公司
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
