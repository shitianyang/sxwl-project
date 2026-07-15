import { DatePicker } from 'antd';
import type { RangePickerProps } from 'antd/es/date-picker';

export type SxwlRangePickerProps = RangePickerProps;

/**
 * SxwlRangePicker — 基于 antd DatePicker.RangePicker 的二次封装（日期+时间范围）
 *
 * 默认开启 showTime，用于选择带时分秒的时间段。
 *
 * 用法：
 * ```tsx
 * <SxwlRangePicker onChange={(dates) => console.log(dates)} />
 * ```
 */
const SxwlRangePicker = (props: SxwlRangePickerProps) => (
  <DatePicker.RangePicker showTime {...props} />
);

export default SxwlRangePicker;
