import SxwlChart from './index';
import type { SxwlChartProps } from './index';

export interface SxwlBarChartProps extends Omit<SxwlChartProps, 'chartType' | 'coordinate' | 'thetaField' | 'smooth'> {
  /** 条间距比例，默认 0.2 */
  padding?: number;
}

/**
 * SxwlBarChart — 条形图（水平）
 *
 * 内部使用 interval + transpose 坐标系实现水平条形图。
 *
 * @example
 * ```tsx
 * <SxwlBarChart
 *   data={[{ product: 'A', sales: 100 }, { product: 'B', sales: 150 }]}
 *   xField="sales"
 *   yField="product"
 *   colorField="region"
 * />
 * ```
 */
const SxwlBarChart = (props: SxwlBarChartProps) => {
  const { padding, ...rest } = props;
  const markStyle = { padding, ...rest.markStyle };
  return (
    <SxwlChart
      {...rest}
      chartType="interval"
      coordinate={{ type: 'transpose' }}
      markStyle={markStyle}
    />
  );
};

export default SxwlBarChart;
