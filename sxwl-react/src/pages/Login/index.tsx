// ============================================
// LoginPage — 登录页
// 视觉：满幅两栏（左 40% 实心品牌栏 + 右 60% 表单列），无卡片、无阴影、无渐变
// ============================================

import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import type { FormInstance } from 'antd';
import { loginByPassword, loginBySms, getSmsCaptcha } from '@/api/authApi';
import { useAuthStore } from '@/stores/authStore';
import { encryptPassword } from '@/utils/sm2Utils';
import { getCachedPublicKey, invalidatePublicKeyCache } from '@/utils/publicKeyUtils';
import { SxwlButton, SxwlForm, SxwlInput, SxwlMessage, SxwlCaptcha, SxwlIcon, SxwlSegmented } from '@/components';
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

/** 与后端 CaptchaController.SMS_SEND_INTERVAL 对齐 */
const SMS_RESEND_SECONDS = 60;

/** 短信验证码行：左侧输入框 + 右侧发送按钮，在 Form.Item 内正确绑定 value/onChange */
const SmsCodeInput: React.FC<{
  value?: string;
  onChange?: (value: string) => void;
  onSend: () => void;
  sending: boolean;
  countdown: number;
}> = ({ value, onChange, onSend, sending, countdown }) => (
  <div className="sxwl-login-sms-row">
    <SxwlInput
      value={value}
      onChange={(e) => onChange?.(e.target.value)}
      prefix={<SxwlIcon name="SafetyOutlined" size={16} />}
      placeholder="请输入短信验证码"
      maxLength={6}
      className="sxwl-login-sms-input"
    />
    <SxwlButton
      className="sxwl-login-sms-send"
      disabled={countdown > 0}
      loading={sending}
      onClick={onSend}
    >
      {countdown > 0 ? `${countdown} 秒后重发` : '获取验证码'}
    </SxwlButton>
  </div>
);

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  /** 登录失败后递增，触发验证码刷新 */
  const [captchaRefreshKey, setCaptchaRefreshKey] = useState(0);
  /** 登录模式：password | sms */
  const [loginMode, setLoginMode] = useState<'password' | 'sms'>('password');
  /** 短信验证码重发倒计时（秒），0 表示可发送 */
  const [smsCountdown, setSmsCountdown] = useState(0);
  const [smsSending, setSmsSending] = useState(false);
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

  // 倒计时：一秒一跳，归零或卸载即停表
  useEffect(() => {
    if (smsCountdown <= 0) return;
    const timer = setTimeout(() => setSmsCountdown((s) => s - 1), 1000);
    return () => clearTimeout(timer);
  }, [smsCountdown]);

  const sendSmsCode = async () => {
    if (smsCountdown > 0 || smsSending) return;

    let phone: string;
    try {
      const values = await form.validateFields(['phone']);
      if (!values.phone) return;
      phone = values.phone;
    } catch {
      return; // 手机号格式错误，提示由 Form.Item 呈现
    }

    setSmsSending(true);
    try {
      await getSmsCaptcha(phone);
      SxwlMessage.success('验证码已发送，请注意查收');
      setSmsCountdown(SMS_RESEND_SECONDS);
    } catch (err: unknown) {
      const e = err as { response?: { data?: { message?: string } } };
      const message = e?.response?.data?.message || '验证码发送失败，请稍后重试';
      SxwlMessage.error(message);
      // 服务端对同一手机号 60 秒内拒发并回剩余秒数，以它为准，避免两端倒计时不一致
      const wait = message.match(/等待\s*(\d+)\s*秒/);
      if (wait) setSmsCountdown(Number(wait[1]));
    } finally {
      setSmsSending(false);
    }
  };

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
      {/* 左侧实心品牌栏（<1024px 隐藏） */}
      <aside className="sxwl-login-brand">
        <svg className="sxwl-login-brand-bg" viewBox="0 0 400 560" preserveAspectRatio="xMidYMid slice" aria-hidden="true">
          <circle cx="360" cy="60" r="180" fill="none" stroke="rgba(255,255,255,.10)" strokeWidth="1.5" />
          <circle cx="360" cy="60" r="130" fill="none" stroke="rgba(255,255,255,.14)" strokeWidth="1.5" />
          <circle cx="40" cy="520" r="150" fill="none" stroke="rgba(255,255,255,.08)" strokeWidth="1.5" />
          <circle cx="300" cy="470" r="4" fill="rgba(255,255,255,.22)" />
          <circle cx="120" cy="80" r="3" fill="rgba(255,255,255,.18)" />
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
          <div className="sxwl-login-brand-slogan">集中管控 · 精细授权<br />安全合规 一体化</div>
          <div className="sxwl-login-brand-tagline">统一身份权限 · 多租户单点接入 · 全链路审计 · 风险可视</div>
        </div>

        <div className="sxwl-login-brand-foot">© 2026 数行未来 · 御权平台　保留所有权利</div>
      </aside>

      {/* 右侧表单列（字段与登录逻辑保持不变） */}
      <main className="sxwl-login-form-side">
        <div className="sxwl-login-form">
          <div className="sxwl-login-form-head">
            <div className="sxwl-login-form-title">欢迎登录</div>
            <div className="sxwl-login-form-sub">
              {loginMode === 'password'
                ? '请输入账号信息以进入管控台'
                : '请输入手机号并获取验证码'}
            </div>
          </div>

          <SxwlSegmented
            className="sxwl-login-mode-switch"
            block
            value={loginMode}
            onChange={(val) => setLoginMode(val as 'password' | 'sms')}
            options={[
              { label: '密码登录', value: 'password' },
              { label: '短信登录', value: 'sms' },
            ]}
          />

          <SxwlForm
            form={form}
            name="login"
            size="large"
            onFinish={onFinish}
            autoComplete="off"
            layout="vertical"
            requiredMark={false}
          >
            {/* 密码登录：显示用户名 */}
            {loginMode === 'password' && (
              <SxwlForm.Item
                label="用户名"
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
                label="手机号"
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
                label="密码"
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
                label="短信验证码"
                name="smsCode"
                rules={[{ required: true, message: '请输入短信验证码' }]}
              >
                <SmsCodeInput
                  onSend={sendSmsCode}
                  sending={smsSending}
                  countdown={smsCountdown}
                />
              </SxwlForm.Item>
            )}

            {/* 图形验证码（两种模式都需要） */}
            <SxwlForm.Item
              label="图形验证码"
              name="captchaCode"
              rules={[{ required: true, message: '请输入验证码' }]}
            >
              <CaptchaInput form={form} refreshKey={captchaRefreshKey} />
            </SxwlForm.Item>

            {/* captchaUuid 隐藏字段 */}
            <SxwlForm.Item name="captchaUuid" hidden>
              <SxwlInput />
            </SxwlForm.Item>

            <SxwlForm.Item className="sxwl-login-submit">
              <SxwlButton
                type="primary"
                htmlType="submit"
                loading={loading}
                block
                size="large"
              >
                {loading ? null : '登 录'}
              </SxwlButton>
            </SxwlForm.Item>
          </SxwlForm>

          <p className="sxwl-login-help">忘记密码或尚未开通账号？请联系系统管理员</p>
        </div>
      </main>
    </div>
  );
}
