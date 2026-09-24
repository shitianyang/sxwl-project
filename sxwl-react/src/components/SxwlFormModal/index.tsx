import { type JSX, useLayoutEffect } from 'react';
import type { FormInstance } from 'antd/es/form';
import { SxwlInput, SxwlSelect, SxwlTreeSelect, SxwlModal, SxwlForm, SxwlMarkdownEditor,
  SxwlRichTextEditor, SxwlRow, SxwlCol, SxwlSwitch,
} from '@/components';
import type { SxwlTreeSelectProps } from '@/components';
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
  /** 弹窗宽度，默认 1 列 520 / 2 列 720 / 3 列 960 */
  width?: number | string;
  /** 确定按钮 loading */
  confirmLoading?: boolean;
  /** 布局方式，默认 horizontal（label 与控件同行） */
  layout?: 'vertical' | 'horizontal';
  /** horizontal 布局下的 label 宽度（px），默认 90 */
  labelWidth?: number;
  /** 列数（1 / 2 / 3），默认 2
   *  - 1 列：每个字段占一行
   *  - 2 列：字段左右两列排列，更紧凑
   *  - 3 列：超大表单 / 页面级密集录入
   */
  columns?: 1 | 2 | 3;
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
  buildRules,
}: {
  field: FormFieldConfig;
  form: FormInstance;
  buildRules: (f: FormFieldConfig) => any[];
}) {
  // 用 useWatch 直接订阅字段值，确保 setFieldsValue 后一定会 re-render
  const watchedValue = SxwlForm.useWatch(field.name, form);

  return (
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
  );
}

// ==================== FieldControl — 按 type 分发受控控件

function FieldControl({ field }: { field: FormFieldConfig }) {
  if (field.type === 'select') {
    return (
      <SxwlSelect
        placeholder={field.placeholder}
        options={field.options}
        disabled={field.disabled}
        mode={field.mode}
        showSearch={field.showSearch}
        allowClear={field.allowClear}
        optionFilterProp="label"
        maxTagCount="responsive"
      />
    );
  }
  if (field.type === 'treeSelect') {
    return (
      <SxwlTreeSelect
        placeholder={field.placeholder}
        treeData={(field.treeData ?? []) as SxwlTreeSelectProps['treeData']}
        fieldNames={field.fieldNames ?? { label: 'label', value: 'id', children: 'children' }}
        multiple={field.multiple}
        disabled={field.disabled}
        showSearch
        treeNodeFilterProp={field.fieldNames?.label ?? 'label'}
        allowClear
        maxTagCount="responsive"
        treeDefaultExpandAll
      />
    );
  }
  if (field.type === 'password') {
    return <SxwlInput type="password" placeholder={field.placeholder} maxLength={field.maxLength} disabled={field.disabled} />;
  }
  if (field.type === 'textarea') {
    return <SxwlInput.TextArea placeholder={field.placeholder} maxLength={field.maxLength} disabled={field.disabled} autoSize={{ minRows: 2, maxRows: 6 }} />;
  }
  if (field.type === 'markdown') {
    return <SxwlMarkdownEditor placeholder={field.placeholder ?? '支持 Markdown 格式...'} minRows={6} maxRows={24} />;
  }
  return <SxwlInput placeholder={field.placeholder} maxLength={field.maxLength} disabled={field.disabled} />;
}

// ==================== Component

function SxwlFormModal({
  title,
  open,
  form,
  fields,
  onOk,
  onCancel,
  width,
  confirmLoading,
  layout = 'horizontal',
  labelWidth = 90,
  columns = 2,
  destroyOnHidden,
  initialValues,
  editingData,
}: SxwlFormModalProps): JSX.Element {
  const colSpan = 24 / columns;
  // horizontal 下按最长 label 自动加宽 label 列：CJK 14px/字、其余 8px/字，+16 留白（必填星号等）。
  // 否则「业务名(英文单数)」这类长 label 会被 90px 定宽截断（Codegen 弹窗实测 115px > 90）。
  const autoLabelWidth = layout === 'horizontal'
    ? Math.min(
        180,
        Math.max(
          labelWidth,
          ...fields.map((f) => {
            const label = typeof f.label === 'string' ? f.label : '';
            const w = [...label].reduce((acc, c) => acc + (c.charCodeAt(0) > 0x2e80 ? 14 : 8), 0) + 16;
            return f.required ? w + 12 : w;
          }),
        ),
      )
    : labelWidth;
  // 列数越多台面越宽，保证每个「label + 控件」有足够呼吸感
  const modalWidth = width ?? (columns === 1 ? 520 : columns === 2 ? 720 : 960);
  const spanOf = (field: FormFieldConfig) =>
    field.full || field.type === 'markdown' || field.type === 'richtext' || field.type === 'textarea' ? 24 : colSpan;
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
      width={modalWidth}
      confirmLoading={confirmLoading}
      destroyOnHidden={destroyOnHidden}
      className="sxwl-form-modal"
    >
      <SxwlForm
        form={form}
        layout={layout}
        className="sxwl-form-modal-form"
        colon={false}
        labelAlign="right"
        labelCol={layout === 'horizontal' ? { flex: `${autoLabelWidth}px` } : undefined}
        preserve={false}
      >
        <SxwlRow gutter={[24, 20]}>
          {fields.flatMap((field, idx) => {
            const cells: JSX.Element[] = [];
            // 分组标题：独占整行，标志新段落开始
            if (field.section && field.section !== fields[idx - 1]?.section) {
              cells.push(
                <SxwlCol key={`section-${field.name}`} span={24}>
                  <div className="sxwl-form-modal__section">{field.section}</div>
                </SxwlCol>,
              );
            }
            cells.push(
              <SxwlCol key={field.name} span={spanOf(field)}>
                {field.type === 'richtext' ? (
                  <RichtextField field={field} form={form} buildRules={buildRules} />
                ) : field.type === 'switch' ? (
                  <SxwlForm.Item name={field.name} label={field.label} valuePropName="checked" initialValue={field.initialValue}>
                    <SxwlSwitch />
                  </SxwlForm.Item>
                ) : (
                  <SxwlForm.Item
                    name={field.name}
                    label={field.label}
                    extra={field.extra}
                    rules={buildRules(field)}
                    initialValue={field.initialValue}
                  >
                    <FieldControl field={field} />
                  </SxwlForm.Item>
                )}
              </SxwlCol>,
            );
            return cells;
          })}
        </SxwlRow>
      </SxwlForm>
    </SxwlModal>
  );
}

export default SxwlFormModal;
