import { type JSX } from 'react';
import type { FormInstance } from 'antd/es/form';
import {
  SxwlInput, SxwlSelect, SxwlModal, SxwlForm,
} from '@/components';
import type { FormFieldConfig } from '@/components/FormFieldConfig';
import './index.scss';

// ==================== Types

export interface SxwlFormModalProps {
  /** 弹窗标题 */
  title: string;
  /** 是否打开 */
  open: boolean;
  /** Form 实例（通过 SxwlForm.useForm() 获取） */
  form: FormInstance;
  /** 表单字段配置 */
  fields: FormFieldConfig[];
  /** 点击确定 */
  onOk: () => void | Promise<void>;
  /** 点击取消 / 关闭 */
  onCancel: () => void;
  /** 弹窗宽度 */
  width?: number | string;
  /** 确定按钮 loading */
  confirmLoading?: boolean;
  /** 布局方式 */
  layout?: 'vertical' | 'horizontal';
}

// ==================== Component

function SxwlFormModal({
  title,
  open,
  form,
  fields,
  onOk,
  onCancel,
  width = 520,
  confirmLoading,
  layout = 'vertical',
}: SxwlFormModalProps): JSX.Element {
  const buildRules = (field: FormFieldConfig) => {
    const rules = [...(field.rules ?? [])];
    if (field.required) {
      rules.unshift({ required: true, message: `请输入${field.label ?? field.name}` });
    }
    return rules;
  };

  return (
    <SxwlModal
      title={title}
      open={open}
      onOk={onOk}
      onCancel={onCancel}
      width={width}
      confirmLoading={confirmLoading}
    >
      <SxwlForm form={form} layout={layout} className="sxwl-form-modal__form">
        {fields.map((field) => (
          <SxwlForm.Item
            key={field.name}
            name={field.name}
            label={field.label}
            rules={buildRules(field)}
            initialValue={field.initialValue}
          >
            {field.type === 'select' ? (
              <SxwlSelect
                placeholder={field.placeholder ?? '请选择'}
                options={field.options}
              />
            ) : (
              <SxwlInput
                placeholder={field.placeholder ?? '请输入'}
                maxLength={field.maxLength}
              />
            )}
          </SxwlForm.Item>
        ))}
      </SxwlForm>
    </SxwlModal>
  );
}

export default SxwlFormModal;
