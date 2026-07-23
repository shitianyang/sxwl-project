import { Select } from 'antd';
import type { SelectProps } from 'antd';

export type SxwlSelectProps = SelectProps;

/**
 * SxwlSelect — 基于 antd Select 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlSelect
 *   placeholder="请选择"
 *   allowClear
 *   options={[
 *     { value: 1, label: '启用' },
 *     { value: 0, label: '禁用' },
 *   ]}
 * />
 * ```
 */
const SxwlSelect = (props: SxwlSelectProps) => <Select {...props} />;

export default SxwlSelect;
