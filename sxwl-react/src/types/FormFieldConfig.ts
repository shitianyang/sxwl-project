import type { ReactNode } from 'react';
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
  type: 'input' | 'password' | 'select' | 'treeSelect' | 'textarea' | 'dateRange' | 'markdown' | 'richtext';
  /** 占位符 */
  placeholder?: string;
  /** 控件下方的一句话说明（比 placeholder 更适合放规则解释） */
  extra?: ReactNode;
  /** 是否必填（自动添加必填校验） */
  required?: boolean;
  /** 初始值 */
  initialValue?: any;
  /** 校验规则（required 为 true 时自动追加 required 规则） */
  rules?: Rule[];
  /** Select 选项 */
  options?: { value: any; label: string }[];
  /** Select 选择模式（多选用于角色/组织分配） */
  mode?: 'multiple' | 'tags';
  /** Select 是否可搜索过滤 */
  showSearch?: boolean;
  /** Select 是否可清空 */
  allowClear?: boolean;
  /** 树形数据（treeSelect 类型时使用） */
  treeData?: unknown[];
  /** treeSelect 是否多选 */
  multiple?: boolean;
  /** 树形字段映射（treeSelect 类型时使用） */
  fieldNames?: { label: string; value: string; children: string };
  /** 最大输入长度 */
  maxLength?: number;
  /** 是否禁用 */
  disabled?: boolean;
}
