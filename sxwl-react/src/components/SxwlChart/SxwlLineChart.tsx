import SxwlChart from './index';
import type { SxwlChartProps } from './index';

export interface SxwlLineChartProps extends Omit<SxwlChartProps, 'chartType' | 'coordinate' | 'thetaField' | 'smooth'> {
  /** 是否平滑曲线 */
  smooth?: boolean;
}

/**
 * SxwlLineChart — 折线图
 *
 * @example
 * ```tsx
 * <SxwlLineChart
 *   data={[{ year: '2024', value: 100 }, { year: '2025', value: 150 }]}
 *   xField="year"
 *   yField="value"
 *   colorField="category"
 *   smooth
 * />
 * ```
 */
const SxwlLineChart = (props: SxwlLineChartProps) => {
  const { smooth, ...rest } = props;
  return <SxwlChart {...rest} chartType="line" smooth={smooth} />;
};

export default SxwlLineChart;
