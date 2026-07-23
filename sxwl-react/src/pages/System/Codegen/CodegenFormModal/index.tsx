import { type JSX } from 'react';
import type { FormInstance } from 'antd/es/form';
import {
  SxwlFormModal,
  type FormFieldConfig,
} from '@/components';
import type { CodegenConfigForm } from '@/api/codegen/codegenApi';

// ==================== Types

export interface CodegenFormModalProps {
  /** 弹窗标题 */
  title: string;
  /** 是否打开 */
  open: boolean;
  /** Form 实例 */
  form: FormInstance;
  /** 编辑时的初始值 */
  initialValues?: CodegenConfigForm | null;
  /** 点击确定 */
  onOk: () => void | Promise<void>;
  /** 点击取消 */
  onCancel: () => void;
  /** 确定按钮 loading */
  confirmLoading?: boolean;
}

// ==================== Form Fields

const formFields: FormFieldConfig[] = [
  { name: 'tableName', label: '数据库表名', type: 'input', required: true, maxLength: 128, placeholder: '请输入数据库表名' },
  { name: 'modulePrefix', label: '模块前缀', type: 'input', required: true, maxLength: 64, placeholder: '如 system、log' },
  { name: 'bizName', label: '业务名(英文单数)', type: 'input', required: true, maxLength: 64, placeholder: '如 User' },
  { name: 'bizNameCn', label: '业务中文名', type: 'input', required: true, maxLength: 64, placeholder: '如 用户' },
  { name: 'bizNamePlural', label: '业务名(英文复数)', type: 'input', maxLength: 64, placeholder: '不填自动+s' },
  { name: 'packageName', label: '包名', type: 'input', required: true, maxLength: 128, placeholder: 'com.sxwl.system' },
  { name: 'author', label: '作者', type: 'input', required: true, maxLength: 32 },
  {
    name: 'genType', label: '生成类型', type: 'select', required: true,
    options: [
      { value: 'crud', label: 'CRUD' },
      { value: 'tree', label: '树形' },
    ],
  },
  { name: 'tableComment', label: '表注释', type: 'input', maxLength: 256 },
];

// ==================== Component

function CodegenFormModal({
  title,
  open,
  form,
  initialValues,
  onOk,
  onCancel,
  confirmLoading,
}: CodegenFormModalProps): JSX.Element {
  return (
    <SxwlFormModal
      title={title}
      open={open}
      form={form}
      fields={formFields}
      onOk={onOk}
      onCancel={onCancel}
      layout="horizontal"
      columns={1}
      width={640}
      confirmLoading={confirmLoading}
      initialValues={{ genType: 'crud' }}
      editingData={initialValues ?? undefined}
    />
  );
}

export default CodegenFormModal;
