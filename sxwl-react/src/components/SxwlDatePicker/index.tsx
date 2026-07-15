import { DatePicker } from 'antd';
import type { DatePickerProps } from 'antd';

export type SxwlDatePickerProps = DatePickerProps;

/**
 * SxwlDatePicker — 基于 antd DatePicker 的二次封装（日期选择）
 *
 * 用法：
 * ```tsx
 * <SxwlDatePicker onChange={(date) => console.log(date)} />
 * ```
 */
const SxwlDatePicker = (props: SxwlDatePickerProps) => <DatePicker {...props} />;

export default SxwlDatePicker;
