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
import './index.scss';

export interface SxwlCaptchaProps {
  /** 父表单实例（用于设置 captchaUuid 字段） */
  form: FormInstance;
  /** 变化时强制重新加载验证码（如登录失败后） */
  refreshKey?: number;
  /** 验证码图片高度（默认与登录页输入框等高） */
  height?: number;
}

const SxwlCaptcha: React.FC<SxwlCaptchaProps> = ({ form, refreshKey, height = 40 }) => {
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
    <div className="sxwl-captcha">
      <button
        type="button"
        className="sxwl-captcha__box"
        style={{ height }}
        onClick={loadCaptcha}
        title="点击刷新验证码"
        aria-label="验证码图片，点击刷新"
      >
        {base64Image ? (
          <img className="sxwl-captcha__img" src={base64Image} alt="验证码" />
        ) : (
          <span className="sxwl-captcha__fallback">{loading ? '加载中' : '加载失败'}</span>
        )}
      </button>
      <button
        type="button"
        className="sxwl-captcha__refresh"
        onClick={loadCaptcha}
        title="刷新验证码"
        aria-label="刷新验证码"
      >
        <SxwlIcon name="ReloadOutlined" size={14} />
      </button>
    </div>
  );
};

export default SxwlCaptcha;
