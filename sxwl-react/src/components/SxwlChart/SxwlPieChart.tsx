import SxwlChart from './index';
import type { SxwlChartProps } from './index';

export interface SxwlPieChartProps extends Omit<SxwlChartProps, 'chartType' | 'xField' | 'yField' | 'coordinate' | 'smooth'> {
  /** 角度字段（数值） */
  angleField: string;
  /** 类别字段（颜色分组） */
  colorField: string;
}

/**
 * SxwlPieChart — 饼图
 *
 * 内部使用 interval + theta 坐标系实现饼图。
 *
 * @example
 * ```tsx
 * <SxwlPieChart
 *   data={[{ type: '分类A', value: 30 }, { type: '分类B', value: 70 }]}
 *   angleField="value"
 *   colorField="type"
 * />
 * ```
 */
const SxwlPieChart = (props: SxwlPieChartProps) => {
  const { angleField, ...rest } = props;
  return (
    <SxwlChart
      {...rest}
      chartType="interval"
      coordinate={{ type: 'theta' }}
      thetaField={angleField}
    />
  );
};

export default SxwlPieChart;
