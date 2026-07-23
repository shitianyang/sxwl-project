import { DatePicker } from 'antd';
import type { DatePickerProps } from 'antd';

export type SxwlDateTimePickerProps = DatePickerProps;

/**
 * SxwlDateTimePicker — 基于 antd DatePicker 的二次封装（日期+时间选择）
 *
 * 默认开启 showTime，用于需要精确到时分秒的场景。
 *
 * 用法：
 * ```tsx
 * <SxwlDateTimePicker onChange={(date) => console.log(date)} />
 * <SxwlDateTimePicker.RangePicker showTime onChange={(dates) => console.log(dates)} />
 * ```
 */
const SxwlDateTimePicker = ((props: SxwlDateTimePickerProps) => (
  <DatePicker showTime {...props} />
)) as typeof DatePicker;

SxwlDateTimePicker.RangePicker = DatePicker.RangePicker;
SxwlDateTimePicker.WeekPicker = DatePicker.WeekPicker;
SxwlDateTimePicker.MonthPicker = DatePicker.MonthPicker;
SxwlDateTimePicker.QuarterPicker = DatePicker.QuarterPicker;
SxwlDateTimePicker.YearPicker = DatePicker.YearPicker;

export default SxwlDateTimePicker;
