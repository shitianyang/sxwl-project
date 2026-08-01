// ============================================
// SxwlCaptcha — 图形验证码组件
//
// 显示验证码图片 + 刷新按钮，点击图片可刷新。
// captchaUuid 通过 form 的 hidden 字段传递给登录请求。
// ============================================

import { useState, useEffect, useCallback } from 'react';
import type { FormInstance } from 'antd';
import { getCaptchaImage } from '@/api/authApi';
import SxwlIcon from '../SxwlIcon';

export interface SxwlCaptchaProps {
  /** 父表单实例（用于设置 captchaUuid 字段） */
  form: FormInstance;
  /** 变化时强制重新加载验证码（如登录失败后） */
  refreshKey?: number;
  /** 验证码图片高度（默认 48，与登录页输入框等高） */
  height?: number;
}

const SxwlCaptcha: React.FC<SxwlCaptchaProps> = ({ form, refreshKey, height = 48 }) => {
  const [base64Image, setBase64Image] = useState<string>('');
  const [loading, setLoading] = useState(true);

  const loadCaptcha = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getCaptchaImage();
      const { uuid, base64Image: image } = res.data.data;
      setBase64Image(image);
      form.setFieldsValue({ captchaUuid: uuid });
    } catch {
      setBase64Image('');
    } finally {
      setLoading(false);
    }
  }, [form]);

  useEffect(() => {
    loadCaptcha();
  }, [loadCaptcha, refreshKey]);

  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
      <div
        onClick={loadCaptcha}
        style={{
          cursor: 'pointer',
          opacity: loading ? 0.5 : 1,
          transition: 'opacity 0.2s',
          flexShrink: 0,
        }}
        title="点击刷新验证码"
      >
        {base64Image ? (
          <img
            src={base64Image}
            alt="验证码"
            style={{ height, width: 104, borderRadius: 12, display: 'block' }}
          />
        ) : (
          <div
            style={{
              width: 104,
              height,
              borderRadius: 12,
              background: '#f5f5f5',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 12,
              color: '#999',
            }}
          >
            {loading ? '加载中...' : '加载失败'}
          </div>
        )}
      </div>
      <SxwlIcon
        name="ReloadOutlined"
        onClick={loadCaptcha}
        style={{ cursor: 'pointer', color: '#999', fontSize: 14, flexShrink: 0 }}
        size={14}
      />
    </div>
  );
};

export default SxwlCaptcha;
