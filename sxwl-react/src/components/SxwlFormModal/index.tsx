import { type JSX, useEffect } from 'react';
import type { FormInstance } from 'antd/es/form';
import { Row, Col } from 'antd';
import {
  SxwlInput, SxwlSelect, SxwlModal, SxwlForm, SxwlMarkdownEditor,
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
  /** 列数（1 或 2），默认 2
   *  - 1 列：每个字段占一行
   *  - 2 列：字段左右两列排列，更紧凑
   */
  columns?: 1 | 2;
  /** 关闭时是否卸载子组件（重新打开时全新创建） */
  destroyOnHidden?: boolean;
  /** 新增时默认值（弹窗打开时自动 resetFields + setFieldsValue） */
  initialValues?: Record<string, any>;
  /** 编辑时初始数据（弹窗打开时自动 resetFields + setFieldsValue，优先级高于 initialValues） */
  editingData?: Record<string, any>;
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
  columns = 2,
  destroyOnHidden,
  initialValues,
  editingData,
}: SxwlFormModalProps): JSX.Element {
  const colSpan = columns === 1 ? 24 : 12;
  const buildRules = (field: FormFieldConfig) => {
    const rules = [...(field.rules ?? [])];
    if (field.required) {
      rules.unshift({ required: true, message: `请输入${field.label ?? field.name}` });
    }
    return rules;
  };

  // 弹窗打开时自动初始化表单，此时 Form 已在 DOM 中（formHooked=true），无警告
  useEffect(() => {
    if (open) {
      form.resetFields();
      if (editingData) {
        form.setFieldsValue(editingData);
      } else if (initialValues) {
        form.setFieldsValue(initialValues);
      }
    }
  }, [open]);

  return (
    <SxwlModal
      title={title}
      open={open}
      onOk={onOk}
      onCancel={onCancel}
      width={width}
      confirmLoading={confirmLoading}
      destroyOnHidden={destroyOnHidden}
    >
      <SxwlForm
        form={form}
        layout={layout}
        className="sxwl-form-modal__form"
        labelCol={layout === 'horizontal' ? { style: { minWidth: 100 } } : undefined}
        preserve={false}
      >
        <Row gutter={16}>
          {fields.map((field) => (
            <Col key={field.name} span={colSpan}>
              <SxwlForm.Item
                name={field.name}
                label={field.label}
                rules={buildRules(field)}
                initialValue={field.initialValue}
              >
                {field.type === 'select' ? (
                  <SxwlSelect
                    placeholder={field.placeholder ?? `请选择${field.label ?? field.name}`}
                    options={field.options}
                    disabled={field.disabled}
                  />
                ) : field.type === 'markdown' ? (
                  <SxwlMarkdownEditor
                    placeholder={field.placeholder ?? '支持 Markdown 格式...'}
                    minRows={6}
                    maxRows={24}
                  />
                ) : (
                  <SxwlInput
                    placeholder={field.placeholder ?? `请输入${field.label ?? field.name}`}
                    maxLength={field.maxLength}
                    disabled={field.disabled}
                  />
                )}
              </SxwlForm.Item>
            </Col>
          ))}
        </Row>
      </SxwlForm>
    </SxwlModal>
  );
}

export default SxwlFormModal;
