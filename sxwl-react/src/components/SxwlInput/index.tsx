import { Input } from 'antd';
import type { InputProps, InputRef } from 'antd';
import { forwardRef } from 'react';

export interface SxwlInputProps extends InputProps {
  /** 'text'（默认）| 'password' */
  type?: 'text' | 'password';
}

/**
 * SxwlInput — 基于 antd Input / Input.Password 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlInput placeholder="用户名" autoFocus />
 * <SxwlInput type="password" placeholder="密码" />
 * <SxwlInput value={name} onChange={setName} allowClear />
 * ```
 */
const SxwlInputImpl = forwardRef<InputRef, SxwlInputProps>(
  ({ type = 'text', allowClear = true, ...rest }, ref) => {
    if (type === 'password') {
      return <Input.Password ref={ref} {...rest} />;
    }
    return <Input ref={ref} allowClear={allowClear} {...rest} />;
  },
);

type SxwlInputComponent = typeof SxwlInputImpl & {
  /** antd Input.TextArea 直通（表单模板 textarea 类型使用） */
  TextArea: typeof Input.TextArea;
};

const SxwlInput = SxwlInputImpl as SxwlInputComponent;

SxwlInput.displayName = 'SxwlInput';
SxwlInput.TextArea = Input.TextArea;

export default SxwlInput;
