// ============================================
// LoginPage — 登录页
// 视觉：左右分栏布局（左侧暖橙品牌玻璃侧栏 + 右侧表单卡片），玻璃拟态 · 暖橙 #DE5F0E
// 注意：仅调整布局与样式，登录逻辑（SM2 / onFinish / 验证码 / token）保持不变
// ============================================

import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import type { FormInstance } from 'antd';
import { loginByPassword, loginBySms } from '@/api/authApi';
import { useAuthStore } from '@/stores/authStore';
import { encryptPassword } from '@/utils/sm2Utils';
import { getCachedPublicKey, invalidatePublicKeyCache } from '@/utils/publicKeyUtils';
import { SxwlButton, SxwlForm, SxwlInput, SxwlMessage, SxwlCaptcha, SxwlIcon } from '@/components';
import logoSrc from '@/assets/images/logo.png';
import './index.scss';

/** 每次登录都从后端获取最新公钥，防止后端重启后密钥不匹配 */
interface LoginFormValues {
  username: string;
  password: string;     // SM2 加密后的 Base64 密文（密码登录时使用）
  phone?: string;       // 手机号（短信登录时使用）
  smsCode?: string;     // 短信验证码（短信登录时使用）
  captchaUuid: string;
  captchaCode: string;
}

/** 验证码行：左侧输入框 + 右侧图片，在 Form.Item 内正确绑定 value/onChange */
const CaptchaInput: React.FC<{
  form: FormInstance;
  value?: string;
  onChange?: (value: string) => void;
  refreshKey: number;
}> = ({ form, value, onChange, refreshKey }) => (
  <div className="sxwl-login-captcha-row">
    <SxwlInput
      value={value}
      onChange={(e) => onChange?.(e.target.value)}
      placeholder="验证码"
      maxLength={4}
      className="sxwl-login-captcha-input"
    />
    <SxwlCaptcha form={form} refreshKey={refreshKey} />
  </div>
);

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  /** 登录失败后递增，触发验证码刷新 */
  const [captchaRefreshKey, setCaptchaRefreshKey] = useState(0);
  /** 登录模式：password | sms */
  const [loginMode, setLoginMode] = useState<'password' | 'sms'>('password');
  const navigate = useNavigate();
  const location = useLocation();
  const setTokens = useAuthStore((s) => s.setTokens);
  const [form] = SxwlForm.useForm<LoginFormValues>();

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
      if (loginMode === 'password') {
        // 密码登录：SM2 加密
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
      } else {
        // 短信登录：无需 SM2 加密
        const res = await loginBySms({
          phone: values.phone,
          smsCode: values.smsCode,
          captchaUuid: values.captchaUuid,
          captchaCode: values.captchaCode,
        });

        const { accessToken, refreshToken } = res.data.data;
        setTokens(accessToken, refreshToken, values.phone || 'user');

        SxwlMessage.success('登录成功');
        navigate(from, { replace: true });
      }
    } catch (err: unknown) {
      if (loginMode === 'password') {
        invalidatePublicKeyCache();
      }
      // 登录失败：刷新验证码，避免同一个失效验证码重复提交
      setCaptchaRefreshKey((k) => k + 1);
      const e = err as { response?: { data?: { message?: string } } };
      if (!e?.response) {
        SxwlMessage.error('网络连接异常，请检查网络');
      } else {
        SxwlMessage.error(e.response.data?.message || '登录失败，请检查输入');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="sxwl-login-container">
      <div className="sxwl-login-wrap">
        {/* 左侧蓝色品牌玻璃侧栏（文案沿用项目品牌：数行未来·御权） */}
        <aside className="sxwl-login-brand">
          <svg className="sxwl-login-brand-bg" viewBox="0 0 400 560" preserveAspectRatio="xMidYMid slice" aria-hidden="true">
            <circle cx="320" cy="120" r="150" fill="none" stroke="rgba(255,255,255,.18)" strokeWidth="1" />
            <circle cx="60" cy="470" r="90" fill="none" stroke="rgba(255,255,255,.22)" strokeWidth="1" />
            <circle cx="350" cy="500" r="3" fill="rgba(255,255,255,.35)" />
            <circle cx="90" cy="90" r="3" fill="rgba(255,255,255,.30)" />
            <circle cx="300" cy="60" r="2.4" fill="rgba(255,255,255,.5)" />
            <circle cx="40" cy="300" r="2.4" fill="rgba(255,255,255,.25)" />
            <circle cx="370" cy="320" r="2.4" fill="rgba(255,255,255,.25)" />
          </svg>

          <div className="sxwl-login-brand-top">
            <span className="sxwl-login-brand-mark">
              <img src={logoSrc} alt="数行未来" />
            </span>
            <div className="sxwl-login-brand-name">
              数行未来·御权
              <small>UNIFIED ACCESS CONTROL</small>
            </div>
          </div>

          <div className="sxwl-login-brand-mid">
            <svg className="sxwl-login-brand-art" viewBox="0 0 96 96" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <path d="M48 12 L78 24 V52 C78 70 64 82 48 88 C32 82 18 70 18 52 V24 Z"
                    fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinejoin="round" />
              <rect x="40" y="52" width="16" height="14" rx="2.5" fill="none" stroke="currentColor" strokeWidth="2" />
              <path d="M43 52 V46 a5 5 0 0 1 10 0 v6" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
            </svg>
            <div className="sxwl-login-brand-slogan">集中管控 · 精细授权<br /><em>安全合规</em> 一体化</div>
            <div className="sxwl-login-brand-tagline">统一身份权限 · 多租户单点接入 · 全链路审计 · 风险可视</div>
          </div>

          <div className="sxwl-login-brand-foot">© 2026 数行未来 · 御权平台　保留所有权利</div>
        </aside>

        {/* 右侧表单区（字段与登录逻辑保持不变） */}
        <main className="sxwl-login-form-side">
          <div className="sxwl-login-form-head">
            <div className="sxwl-login-form-title">登录</div>
            <div className="sxwl-login-form-sub">
              {loginMode === 'password' 
                ? '请输入账号信息以进入管控台' 
                : '请输入手机号获取验证码'}
            </div>
            {/* 登录模式切换 */}
            <div className="sxwl-login-mode-switch" style={{ marginTop: 12 }}>
              <span
                onClick={() => setLoginMode('password')}
                style={{
                  fontWeight: loginMode === 'password' ? 'bold' : 'normal',
                  color: loginMode === 'password' ? '#DE5F0E' : '#8C8C8C',
                  cursor: 'pointer',
                  marginRight: 16,
                }}
              >
                密码登录
              </span>
              <span
                onClick={() => setLoginMode('sms')}
                style={{
                  fontWeight: loginMode === 'sms' ? 'bold' : 'normal',
                  color: loginMode === 'sms' ? '#DE5F0E' : '#8C8C8C',
                  cursor: 'pointer',
                }}
              >
                短信登录
              </span>
            </div>
          </div>

          <SxwlForm
            form={form}
            name="login"
            size="large"
            onFinish={onFinish}
            autoComplete="off"
            layout="vertical"
            className="sxwl-login-form"
          >
            {/* 密码登录：显示用户名 */}
            {loginMode === 'password' && (
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
            )}

            {/* 短信登录：显示手机号 */}
            {loginMode === 'sms' && (
              <SxwlForm.Item
                name="phone"
                rules={[
                  { required: true, message: '请输入手机号' },
                  { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
                ]}
              >
                <SxwlInput
                  prefix={<SxwlIcon name="PhoneOutlined" size={16} />}
                  placeholder="请输入手机号"
                  autoFocus
                  maxLength={11}
                />
              </SxwlForm.Item>
            )}

            {/* 密码登录：显示密码框 */}
            {loginMode === 'password' && (
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
            )}

            {/* 短信登录：显示短信验证码 */}
            {loginMode === 'sms' && (
              <SxwlForm.Item
                name="smsCode"
                rules={[{ required: true, message: '请输入短信验证码' }]}
              >
                <div style={{ display: 'flex', gap: 8 }}>
                  <SxwlInput
                    prefix={<SxwlIcon name="SafetyOutlined" size={16} />}
                    placeholder="请输入短信验证码"
                    maxLength={6}
                    style={{ flex: 1 }}
                  />
                </div>
              </SxwlForm.Item>
            )}

            {/* 图形验证码（两种模式都需要） */}
            <SxwlForm.Item
              name="captchaCode"
              rules={[{ required: true, message: '请输入验证码' }]}
            >
              <CaptchaInput form={form} refreshKey={captchaRefreshKey} />
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
                className="sxwl-login-button"
              >
                {loading ? null : '登 录'}
              </SxwlButton>
            </SxwlForm.Item>
          </SxwlForm>
        </main>
      </div>
    </div>
  );
}
