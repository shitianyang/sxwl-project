// ============================================
// Login — 登录页样式
// 浅色系视觉：浅灰渐变背景 + 品牌橙淡光晕 + 白色卡片
// 视觉对齐 login-redesign 原型
// ============================================

import { createStyles } from 'antd-style';

const useLoginStyles = createStyles(({ token, css }) => ({
  /**
   * 页面容器：全屏浅色渐变背景 + 品牌橙光晕 + Plus Jakarta Sans 字体
   */
  container: css`
    position: relative;
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    overflow: auto;
    font-family: 'Plus Jakarta Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
    background:
      radial-gradient(50% 45% at 12% 8%, rgba(222, 95, 14, 0.07) 0%, transparent 60%),
      radial-gradient(45% 45% at 90% 85%, rgba(240, 151, 45, 0.06) 0%, transparent 60%),
      radial-gradient(50% 40% at 85% 10%, rgba(222, 95, 14, 0.04) 0%, transparent 60%),
      linear-gradient(160deg, #f7f9fc 0%, #eef2f8 100%);
  `,

  /**
   * 极淡网格背景层（对齐原型 .grid）
   */
  gridLayer: css`
    position: absolute;
    inset: 0;
    pointer-events: none;
    background-image:
      linear-gradient(rgba(26, 26, 46, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(26, 26, 46, 0.03) 1px, transparent 1px);
    background-size: 48px 48px;
    -webkit-mask-image: radial-gradient(75% 75% at 50% 50%, #000 30%, transparent 100%);
    mask-image: radial-gradient(75% 75% at 50% 50%, #000 30%, transparent 100%);
  `,

  /**
   * 中间内容区
   */
  content: css`
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 32px 0;
  `,

  /**
   * 登录容器（单栏居中）
   */
  loginContainer: css`
    display: flex;
    flex-direction: column;
    align-items: center;
  `,

  /**
   * 头部区：Logo + 标题 + 副标题（卡片内顶部居中）
   */
  top: css`
    text-align: center;
    margin-bottom: 30px;
  `,

  /**
   * Logo + 标题横排（居中对齐）
   */
  header: css`
    display: inline-flex;
    align-items: center;
    gap: 14px;
  `,

  /**
   * Logo 图片（透明底，无背景色）
   */
  logo: css`
    width: 62px;
    height: 62px;
    object-fit: contain;
    flex-shrink: 0;
  `,

  /**
   * 标题 + 英文小字（左对齐竖排）
   */
  logoText: css`
    text-align: left;
    display: flex;
    flex-direction: column;
  `,

  /**
   * 标题
   */
  title: css`
    font-size: 25px;
    font-weight: 700;
    color: ${token.colorText};
    letter-spacing: 1.5px;
    margin: 0;
    line-height: 1.2;
  `,

  /**
   * 品牌英文小字
   */
  logoEn: css`
    font-size: 12px;
    color: ${token.colorTextTertiary};
    letter-spacing: 2px;
    margin-top: 3px;
  `,

  /**
   * 副标题
   */
  desc: css`
    margin-top: 16px;
    font-size: 13px;
    color: ${token.colorTextSecondary};
    letter-spacing: 0.3px;
  `,

  /**
   * 表单卡片（白色圆角卡片 + 柔和阴影）
   */
  main: css`
    position: relative;
    z-index: 1;
    width: 460px;
    min-width: 280px;
    max-width: 92vw;
    background: #fff;
    border-radius: 20px;
    padding: 40px 40px 32px;
    box-shadow: 0 8px 32px rgba(26, 26, 46, 0.08);
    border: 1px solid ${token.colorBorderSecondary};

    @media (max-width: 480px) {
      width: calc(100% - 32px);
      padding: 28px 20px 24px;
    }
  `,

  /**
   * 表单内部样式覆盖
   */
  form: css`
    .ant-form-item {
      margin-bottom: 18px;
    }

    .ant-input-affix-wrapper,
    .ant-input {
      border-radius: 12px;
      border-color: ${token.colorBorderSecondary};
      transition: all 0.2s;
    }

    .ant-input-affix-wrapper {
      height: 48px;
      padding: 0 12px;
      font-size: ${token.fontSize}px;
      background: #fbfcfe;
    }

    .ant-input-affix-wrapper:hover,
    .ant-input:hover {
      border-color: #cbd5e1;
    }

    .ant-input-affix-wrapper-focused {
      background: #fff;
      border-color: ${token.colorPrimary};
      box-shadow: 0 0 0 3px rgba(222, 95, 14, 0.1);
    }

    .ant-input-prefix {
      margin-right: 8px;
      color: ${token.colorTextTertiary};
      font-size: 16px;
    }
  `,

  /**
   * 验证码行：输入框 + 图片平排
   */
  captchaRow: css`
    display: flex;
    align-items: center;
    gap: 12px;
  `,

  /**
   * 验证码输入框：flex 自适应
   */
  captchaInput: css`
    flex: 1;
    min-width: 0;
  `,

  /**
   * 登录按钮（品牌橙渐变 + 投影）
   */
  button: css`
    height: 48px;
    font-size: 15px;
    font-weight: 700;
    letter-spacing: 3px;
    border-radius: 12px;
    background: linear-gradient(135deg, #de5f0e 0%, #f0972d 100%);
    border-color: transparent;
    box-shadow: 0 6px 18px rgba(222, 95, 14, 0.3);
    transition: all 0.2s;
    margin-top: 6px;

    &:hover:not(:disabled) {
      background: linear-gradient(135deg, #de5f0e 0%, #f0972d 100%);
      filter: brightness(1.05);
      box-shadow: 0 8px 22px rgba(222, 95, 14, 0.38);
      transform: translateY(-1px);
    }

    &:active:not(:disabled) {
      background: linear-gradient(135deg, #c05008 0%, #e07f1a 100%);
      transform: translateY(0);
    }
  `,

  /**
   * 底部版权（卡片内底部居中）
   */
  footer: css`
    text-align: center;
    margin-top: 26px;
    font-size: 12px;
    color: ${token.colorTextTertiary};
    letter-spacing: 0.3px;
  `,
}));

export default useLoginStyles;
