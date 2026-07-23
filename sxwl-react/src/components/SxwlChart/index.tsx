import { useEffect, useRef } from 'react';
import { Chart } from '@antv/g2';
import type { G2Spec } from '@antv/g2';

export type { G2Spec };

export interface SxwlChartProps {
  /** 数据源 */
  data?: Record<string, any>[];
  /** G2 mark 类型：line / interval / area / point / rect ... */
  chartType?: string;
  /** X 轴字段 */
  xField?: string;
  /** Y 轴字段 */
  yField?: string;
  /** 颜色分组字段 */
  colorField?: string;
  /** 饼图角度字段 */
  thetaField?: string;
  /** 坐标系配置，如 { type: 'theta' }（饼图）或 { type: 'transpose' }（条形图） */
  coordinate?: { type: string; [key: string]: any };
  /** 图表样式 */
  style?: React.CSSProperties;
  /** 图表高度（px），默认 300 */
  height?: number;
  /** 自动适配容器宽度，默认 true */
  autoFit?: boolean;
  /** mark 视觉样式 */
  markStyle?: Record<string, any>;
  /** 额外 encodes 映射 */
  encodes?: Record<string, string>;
  /** 比例尺配置 */
  scale?: Record<string, any>;
  /** 坐标轴配置 */
  axis?: Record<string, any>;
  /** 图例配置 */
  legend?: Record<string, any>;
  /** tooltip 配置，false 关闭 */
  tooltip?: boolean | Record<string, any>;
  /** 是否开启动画，默认 true */
  animate?: boolean;
  /** 是否平滑（折线图/面积图） */
  smooth?: boolean;
  /** 自定义渲染函数，覆盖以上 props */
  renderChart?: (chart: Chart) => void;
}

/**
 * SxwlChart — G2 v5 通用图表容器
 *
 * 负责 G2 Chart 实例的初始化、更新、销毁生命周期。
 * 简单场景直接传入 data / chartType / xField / yField 即可，
 * 复杂场景使用 renderChart 自定义渲染。
 */
const SxwlChart = ({
  data,
  chartType = 'line',
  xField,
  yField,
  colorField,
  thetaField,
  coordinate,
  style,
  height = 300,
  autoFit = true,
  markStyle,
  encodes,
  scale,
  axis,
  legend,
  tooltip,
  animate = true,
  smooth,
  renderChart,
}: SxwlChartProps) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<Chart | null>(null);
  const initializedRef = useRef(false);

  // ---- 初始化 Chart（仅一次） ----
  useEffect(() => {
    if (!containerRef.current) return;

    const chart = new Chart({
      container: containerRef.current,
      autoFit,
      height,
    });

    chartRef.current = chart;

    return () => {
      chart.destroy();
      chartRef.current = null;
    };
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  // ---- 渲染 / 更新 ----
  useEffect(() => {
    const chart = chartRef.current;
    if (!chart) return;

    if (renderChart) {
      // 自定义渲染模式（每次重建）
      chart.clear();
      renderChart(chart);
      chart.render();
      return;
    }

    if (!initializedRef.current) {
      // ====== 首次渲染：创建 marks + 编码 + 样式 ======
      initializedRef.current = true;

      if (data) chart.data(data);

      // 坐标系（须在 mark 前设置）
      if (coordinate) {
        chart.coordinate(coordinate);
      }

      const mark = (chart as any)[chartType]();

      // 基础编码
      if (xField) mark.encode('x', xField);
      if (yField) mark.encode('y', yField);
      if (colorField) mark.encode('color', colorField);
      if (thetaField) mark.encode('theta', thetaField);

      // 额外编码
      if (encodes) {
        Object.entries(encodes).forEach(([k, v]) => mark.encode(k, v));
      }

      // 平滑（line / area 支持）
      if (smooth !== undefined) {
        mark.encode('shape', smooth ? 'smooth' : undefined);
      }

      // 比例尺
      if (scale) mark.scale(scale);
      // 坐标轴
      if (axis) mark.axis(axis);
      // 图例
      if (legend) mark.legend(legend);
      // tooltip
      if (tooltip !== undefined) mark.tooltip(tooltip);
      // 视觉样式
      if (markStyle) mark.style(markStyle);
      // 动画
      if (animate === false) mark.animate(false);

      chart.render();
    } else {
      // ====== 后续数据更新：仅更新数据，G2 自动动画过渡 ======
      if (data) {
        chart.data(data);
        chart.render();
      }
    }
  }, [data, chartType, xField, yField, colorField, thetaField, coordinate,
      renderChart, encodes, scale, axis, legend, tooltip, markStyle, animate, smooth]);

  return (
    <div
      ref={containerRef}
      style={{ width: '100%', height, ...style }}
    />
  );
};

export default SxwlChart;
