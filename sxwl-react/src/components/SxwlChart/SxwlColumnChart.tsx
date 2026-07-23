import SxwlChart from './index';
import type { SxwlChartProps } from './index';

export interface SxwlColumnChartProps extends Omit<SxwlChartProps, 'chartType' | 'coordinate' | 'thetaField' | 'smooth'> {
  /** 柱状间距比例，默认 0.2 */
  padding?: number;
}

/**
 * SxwlColumnChart — 柱形图（垂直）
 *
 * @example
 * ```tsx
 * <SxwlColumnChart
 *   data={[{ product: 'A', sales: 100 }, { product: 'B', sales: 150 }]}
 *   xField="product"
 *   yField="sales"
 *   colorField="region"
 * />
 * ```
 */
const SxwlColumnChart = (props: SxwlColumnChartProps) => {
  const { padding, ...rest } = props;
  const markStyle = { padding, ...rest.markStyle };
  return <SxwlChart {...rest} chartType="interval" markStyle={markStyle} />;
};

export default SxwlColumnChart;
