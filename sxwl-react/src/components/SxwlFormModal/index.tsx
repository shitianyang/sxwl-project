import { type JSX, useLayoutEffect } from 'react';
import type { FormInstance } from 'antd/es/form';
import { SxwlInput, SxwlSelect, SxwlModal, SxwlForm, SxwlMarkdownEditor,
  SxwlRichTextEditor, SxwlRow, SxwlCol,
} from '@/components';
import type { FormFieldConfig } from '@/types/FormFieldConfig';
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
  editingData?: Record<string, any> | null;
}

// ==================== RichtextField — 显式受控，绕过 Form.Item 对自定义组件 cloneElement 的兼容性风险

function RichtextField({
  field,
  form,
  colSpan,
  buildRules,
}: {
  field: FormFieldConfig;
  form: FormInstance;
  colSpan: number;
  buildRules: (f: FormFieldConfig) => any[];
}) {
  // 用 useWatch 直接订阅字段值，确保 setFieldsValue 后一定会 re-render
  const watchedValue = SxwlForm.useWatch(field.name, form);

  return (
    <SxwlCol span={colSpan}>
      <SxwlForm.Item
        name={field.name}
        label={field.label}
        rules={buildRules(field)}
        initialValue={field.initialValue}
      >
        <SxwlRichTextEditor
          value={watchedValue ?? ''}
          onChange={(html: string) => form.setFieldValue(field.name, html)}
          placeholder={field.placeholder ?? '请输入内容...'}
          minHeight={300}
        />
      </SxwlForm.Item>
    </SxwlCol>
  );
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

  // 弹窗打开或编辑数据变更时自动初始化表单
  // 用 useLayoutEffect 确保在浏览器绘制前同步设置，富文本编辑器首次渲染即可拿到正确值
  useLayoutEffect(() => {
    if (open) {
      form.resetFields();
      if (editingData) {
        form.setFieldsValue(editingData);
      } else if (initialValues) {
        form.setFieldsValue(initialValues);
      }
    }
  }, [open, editingData, initialValues, form]);

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
        <SxwlRow gutter={16}>
          {fields.map((field) => (
            field.type === 'richtext' ? (
              <RichtextField
                key={field.name}
                field={field}
                form={form}
                colSpan={colSpan}
                buildRules={buildRules}
              />
            ) : (
            <SxwlCol key={field.name} span={colSpan}>
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
            </SxwlCol>
            )
          ))}
        </SxwlRow>
      </SxwlForm>
    </SxwlModal>
  );
}

export default SxwlFormModal;
