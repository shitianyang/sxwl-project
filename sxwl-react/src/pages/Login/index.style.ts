// ============================================
// Login — 登录页样式
// 仿 ant-design-pro 视觉设计：全屏背景、横排 Logo+标题、328px 表单
// ============================================

import { createStyles } from 'antd-style';

const useLoginStyles = createStyles(({ token, css }) => ({
  /**
   * 页面容器：全屏背景
   */
  container: css`
    display: flex;
    flex-direction: column;
    height: 100vh;
    overflow: auto;
    background:
      radial-gradient(ellipse at 80% 20%, rgba(222, 95, 14, 0.04) 0%, transparent 50%),
      linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
    background-size: 100% 100%;
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
   * 登录容器（模仿 .ant-pro-form-login-container）
   */
  loginContainer: css`
    display: flex;
    flex-direction: column;
    align-items: center;
  `,

  /**
   * 头部区：Logo + 标题 + 副标题
   */
  top: css`
    text-align: center;
    margin-bottom: 32px;
  `,

  /**
   * Logo + 标题横排
   */
  header: css`
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12px;
  `,

  /**
   * Logo 图片
   */
  logo: css`
    width: 44px;
    height: 44px;
    margin-right: 12px;
  `,

  /**
   * 标题
   */
  title: css`
    font-size: 26px;
    font-weight: 600;
    color: ${token.colorText};
    letter-spacing: 1px;
    margin: 0;
    line-height: 1.2;
  `,

  /**
   * 副标题
   */
  desc: css`
    font-size: 14px;
    color: ${token.colorTextSecondary};
    letter-spacing: 0.5px;
    margin: 0;
  `,

  /**
   * 表单区域（328px 定宽，类似 .ant-pro-form-login-main）
   */
  main: css`
    width: 328px;
    min-width: 280px;
    max-width: 75vw;
    background: #fff;
    border-radius: ${token.borderRadiusLG}px;
    padding: 28px 24px 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
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
      margin-bottom: 20px;
    }

    .ant-input-affix-wrapper {
      padding: 8px 12px;
      font-size: ${token.fontSize}px;
      border-radius: ${token.borderRadius}px;
      border-color: ${token.colorBorderSecondary};
      transition: all 0.2s;
    }

    .ant-input-affix-wrapper:hover {
      border-color: #cbd5e1;
    }

    .ant-input-affix-wrapper-focused {
      border-color: ${token.colorPrimary};
      box-shadow: 0 0 0 2px rgba(222, 95, 14, 0.1);
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
   * 登录按钮
   */
  button: css`
    height: 44px;
    font-size: 14px;
    font-weight: 600;
    border-radius: ${token.borderRadius}px;
    letter-spacing: 4px;
    background: ${token.colorPrimary};
    border-color: ${token.colorPrimary};
    transition: all 0.2s;

    &:hover:not(:disabled) {
      background: #d0550c;
      border-color: #d0550c;
      box-shadow: none;
      transform: none;
    }

    &:active:not(:disabled) {
      background: #c04a08;
      border-color: #c04a08;
    }
  `,

  /**
   * 底部版权
   */
  footer: css`
    text-align: center;
    padding: 24px 0;
    font-size: 12px;
    color: ${token.colorTextTertiary};
    letter-spacing: 0.3px;
  `,
}));

export default useLoginStyles;
