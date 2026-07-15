import { TimePicker } from 'antd';
import type { TimePickerProps } from 'antd';

export type SxwlTimePickerProps = TimePickerProps;

/**
 * SxwlTimePicker — 基于 antd TimePicker 的二次封装（时间选择）
 *
 * 用法：
 * ```tsx
 * <SxwlTimePicker onChange={(time) => console.log(time)} />
 * ```
 */
const SxwlTimePicker = (props: SxwlTimePickerProps) => <TimePicker {...props} />;

export default SxwlTimePicker;
