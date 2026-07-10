import { message as staticMessage } from 'antd';
import type { MessageInstance } from 'antd/es/message/interface';

/**
 * SxwlMessage — 基于 antd message 的二次封装
 *
 * 通过 {@link initMessageInstance} 注入 App.useApp() 的上下文感知实例，
 * 避免 antd v5 静态 message 无法感知动态主题的警告。
 *
 * 用法：
 * ```tsx
 * SxwlMessage.success('操作成功');
 * SxwlMessage.error('操作失败');
 * ```
 */

let messageInstance: MessageInstance | null = null;

/**
 * 在 App 组件内调用此函数，注入上下文感知的 message 实例
 */
export function initMessageInstance(instance: MessageInstance) {
  messageInstance = instance;
}

/**
 * 获取当前 message 实例，兜底到静态 message
 */
function getMessage(): MessageInstance {
  return messageInstance ?? staticMessage;
}

const SxwlMessage = new Proxy({} as MessageInstance, {
  get(_, prop) {
    const msg = getMessage();
    return (msg as any)[prop].bind(msg);
  },
});

export default SxwlMessage;
