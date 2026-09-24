import type { ReactNode } from 'react';
import type { Rule } from 'antd/es/form';

/** 下拉选项 */
export interface FormFieldOption {
  value: any;
  label: string;
  disabled?: boolean;
}

/** 树选择节点（配合 fieldNames 映射） */
export interface FormFieldTreeOption {
  label?: string;
  id?: string | number;
  children?: FormFieldTreeOption[];
  [key: string]: unknown;
}

/**
 * 表单字段配置 —— SxwlFormModal / SxwlSearchForm 的驱动数据。
 * 契约由两个组件的实际用法反推（本文件随组件用法演进）。
 */
export interface FormFieldConfig {
  /** 字段名 */
  name: string;
  /** 标签文本 */
  label?: string;
  /** 控件类型 */
  type?:
    | 'input'
    | 'password'
    | 'select'
    | 'treeSelect'
    | 'dateRange'
    | 'markdown'
    | 'richtext'
    | 'textarea'
    | 'switch';
  /** 占位符 */
  placeholder?: string;
  /** 是否必填（自动生成「请输入{label}」规则） */
  required?: boolean;
  /** 附加校验规则 */
  rules?: Rule[];
  /** Form.Item 初始值 */
  initialValue?: unknown;
  /** Form.Item 说明文案 */
  extra?: ReactNode;
  /** 两列布局下独占整行（长文本、备注类字段） */
  full?: boolean;
  /** 分组标题：与前一字段不同时插入整行段落标题（长表单分段用） */
  section?: string;
  /** 禁用 */
  disabled?: boolean;
  /** 最大长度（input / password） */
  maxLength?: number;
  /** select 选项 */
  options?: FormFieldOption[];
  /** select 模式（multiple / tags） */
  mode?: 'multiple' | 'tags';
  /** select 是否可搜索 */
  showSearch?: boolean;
  /** select 是否可清除 */
  allowClear?: boolean;
  /** treeSelect 树数据 */
  treeData?: unknown[];
  /** treeSelect 字段映射，默认 { label:'label', value:'id', children:'children' } */
  fieldNames?: { label?: string; value?: string; children?: string };
  /** treeSelect 是否多选 */
  multiple?: boolean;
  /** dateRange 起始输出参数名，默认 {name}Start */
  dateRangeStartKey?: string;
  /** dateRange 结束输出参数名，默认 {name}End */
  dateRangeEndKey?: string;
}
