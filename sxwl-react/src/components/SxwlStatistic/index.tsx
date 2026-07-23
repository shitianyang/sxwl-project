import { Statistic } from 'antd';
import type { StatisticProps } from 'antd';

export type SxwlStatisticProps = StatisticProps;

/**
 * SxwlStatistic — 基于 antd Statistic 的二次封装
 */
const SxwlStatistic = (props: SxwlStatisticProps) => <Statistic {...props} />;

export default SxwlStatistic;
