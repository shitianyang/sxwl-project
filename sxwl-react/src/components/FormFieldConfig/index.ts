import type { Rule } from 'antd/es/form';

/**
 * 通用表单字段配置
 * SxwlSearchForm / SxwlFormModal 共用
 */
export interface FormFieldConfig {
  /** 字段名 */
  name: string;
  /** 标签文本 */
  label?: string;
  /** 控件类型 */
  type: 'input' | 'select' | 'textarea' | 'dateRange';
  /** 占位符 */
  placeholder?: string;
  /** 是否必填（自动添加必填校验） */
  required?: boolean;
  /** 初始值 */
  initialValue?: any;
  /** 校验规则（required 为 true 时自动追加 required 规则） */
  rules?: Rule[];
  /** Select 选项 */
  options?: { value: any; label: string }[];
  /** 最大输入长度 */
  maxLength?: number;
  /** 是否禁用 */
  disabled?: boolean;
}
