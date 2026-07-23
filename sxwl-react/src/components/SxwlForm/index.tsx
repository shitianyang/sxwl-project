import { type JSX } from 'react';
import { Form } from 'antd';
import type { FormInstance, FormProps } from 'antd/es/form';

export type SxwlFormProps<Values = any> = FormProps<Values>;
export type SxwlFormInstance = FormInstance;

/**
 * SxwlForm — 基于 antd Form 的二次封装
 *
 * 用法：
 * ```tsx
 * const [form] = SxwlForm.useForm();
 * <SxwlForm form={form} layout="vertical">
 *   <SxwlForm.Item name="username" label="用户名">
 *     <SxwlInput />
 *   </SxwlForm.Item>
 * </SxwlForm>
 * ```
 */
function SxwlForm<Values = any>(props: FormProps<Values>): JSX.Element {
  return <Form<Values> {...(props as any)} />;
}

SxwlForm.Item = Form.Item;
SxwlForm.useForm = Form.useForm;

export default SxwlForm;
