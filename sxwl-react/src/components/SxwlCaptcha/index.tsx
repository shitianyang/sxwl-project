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
}

const SxwlCaptcha: React.FC<SxwlCaptchaProps> = ({ form }) => {
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
  }, [loadCaptcha]);

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
            style={{ height: 32, borderRadius: 4, display: 'block' }}
          />
        ) : (
          <div
            style={{
              width: 100,
              height: 32,
              borderRadius: 4,
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
