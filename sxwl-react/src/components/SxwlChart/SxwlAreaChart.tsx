import SxwlChart from './index';
import type { SxwlChartProps } from './index';

export interface SxwlAreaChartProps extends Omit<SxwlChartProps, 'chartType' | 'thetaField' | 'smooth'> {
  /** 是否平滑面积图 */
  smooth?: boolean;
}

/**
 * SxwlAreaChart — 面积图
 *
 * @example
 * ```tsx
 * <SxwlAreaChart
 *   data={[{ month: '1月', value: 100 }, { month: '2月', value: 150 }]}
 *   xField="month"
 *   yField="value"
 *   colorField="category"
 *   smooth
 * />
 * ```
 */
const SxwlAreaChart = (props: SxwlAreaChartProps) => {
  const { smooth, ...rest } = props;
  return <SxwlChart {...rest} chartType="area" smooth={smooth} />;
};

export default SxwlAreaChart;
