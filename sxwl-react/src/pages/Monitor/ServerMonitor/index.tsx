/**
 * ServerMonitor 服务器监控页面
 *
 * <p>核心功能：实时监控 CPU、内存、磁盘、JVM、Redis、数据库连接池</p>
 * <p>设计规范：admin-dense 面板（1px 描边 + 无阴影 + 2px 品牌标题条），配色全部取自 SXWL_COLOR</p>
 */
import { SxwlCard, SxwlRow, SxwlCol, SxwlStatistic, SxwlTable, SxwlButton, SxwlIcon, SxwlTag } from '@/components';
import SxwlLineChart from '@/components/SxwlChart/SxwlLineChart';
import SxwlChart from '@/components/SxwlChart';
import { SXWL_COLOR } from '@/styles/theme.token';
import { useMonitorSSE } from '@/hooks/useMonitorSSE';
import { formatFileSize, formatPercent, formatRelativeTime } from '@/utils/formatUtils';
import { useState, useCallback } from 'react';
import './index.scss';

/** 图表高度：单值即可，窄屏由 SxwlCol 的 span 换行处理 */
const CHART_HEIGHT = 240;

const SERIES_1 = SXWL_COLOR.chartSeries[0];
const SERIES_2 = SXWL_COLOR.chartSeries[1];

/** G2 的 fill 需要 rgba 字符串，透明度从品牌主色派生，避免再写一遍十六进制 */
function withAlpha(hex: string, alpha: number): string {
  const n = hex.replace('#', '');
  const [r, g, b] = [0, 2, 4].map((i) => parseInt(n.slice(i, i + 2), 16));
  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}

/** 面积图：品牌主色描边 + 16% 同色填充 */
const AREA_STYLE = { fill: withAlpha(SERIES_1, 0.16), stroke: SERIES_1, fillOpacity: 1, lineWidth: 2 };
/** 单系列折线 */
const LINE_STYLE = { stroke: SERIES_1, lineWidth: 2 };
/** 多系列（堆内存已用/最大）：主色 + 品牌亮阶 */
const DUAL_RANGE = [SERIES_1, SERIES_2];

/** 轴通用配置：SSE 采样间隔在秒级，只到分钟会让相邻刻度重名；到秒则长到会竖排 */
const AXIS_TITLE_TIME = '时间';
function formatAxisTime(v: string): string {
  return v.includes('T') ? v.split('T')[1].slice(3, 8) : v;
}
const xAxisTime = { title: AXIS_TITLE_TIME, labelFormatter: formatAxisTime };

/** 轴刻度：命中率这类浮点值不截断会印出 99.2932862191 */
function formatAxisNumber(v: number | string): string {
  const n = Number(v);
  return Number.isInteger(n) ? String(n) : n.toFixed(0);
}

/**
 * 按阈值给出统计数值的语义档位
 *
 * <p>文字压在白底上，所以走 -text 档（≥4.5:1），不用无后缀的填充档</p>
 */
function toneFor(ratio: number, warnAt: number, dangerAt: number): string {
  if (ratio >= dangerAt) return ' sxwl-monitor-stat--danger';
  if (ratio >= warnAt) return ' sxwl-monitor-stat--warn';
  return '';
}

/** 使用率类指标：值越大越危险 */
function usageTone(used: number | null | undefined, total: number | null | undefined, warnAt: number, dangerAt: number): string {
  if (!used || !total) return '';
  return toneFor((used / total) * 100, warnAt, dangerAt);
}

/** 命中率类指标：值越小越危险 */
function inverseTone(value: number | null | undefined, warnBelow: number, dangerBelow: number): string {
  if (value == null) return '';
  if (value < dangerBelow) return ' sxwl-monitor-stat--danger';
  if (value < warnBelow) return ' sxwl-monitor-stat--warn';
  return '';
}

export default function ServerMonitorPage() {
  const { data, connected, history } = useMonitorSSE();
  const [lastUpdateTime, setLastUpdateTime] = useState<Date>(new Date());

  const serverInfo = data?.server;
  const jvmInfo = data?.jvm;
  const redisInfo = data?.redis;
  const dbInfo = data?.db;

  const loading = !data;

  /** 手动刷新处理 */
  const handleRefresh = useCallback(() => {
    setLastUpdateTime(new Date());
    // 触发重新获取数据（根据实际项目需求调整）
    window.dispatchEvent(new CustomEvent('monitor:refresh'));
  }, []);

  const gcColumns = [
    { title: 'GC 名称', dataIndex: 'name', key: 'name', width: 200 },
    { title: '次数', dataIndex: 'count', key: 'count', width: 100, render: (v: number) => <span className="sxwl-num">{v}</span> },
    { title: '总耗时（ms）', dataIndex: 'totalTimeMs', key: 'totalTimeMs', width: 120, render: (v: number) => <span className="sxwl-num">{v}</span> },
  ];

  return (
    <div className="sxwl-monitor-page">
      <div className="sxwl-monitor-head">
        <div className="sxwl-monitor-head-text">
          <div className="sxwl-monitor-crumb">监控运维 / 系统监控</div>
          <h1 className="sxwl-monitor-title">系统监控</h1>
          <p className="sxwl-monitor-desc">服务器、JVM、Redis 与数据库连接池的实时指标</p>
        </div>
        <div className="sxwl-monitor-head-side">
          <SxwlTag tone={connected ? 'success' : 'danger'}>
            {connected ? '实时' : '连接断开'}
          </SxwlTag>
          <span className="sxwl-monitor-last-update">最后更新 {formatRelativeTime(lastUpdateTime)}</span>
          <SxwlButton icon={<SxwlIcon name="ReloadOutlined" />} onClick={handleRefresh}>
            刷新
          </SxwlButton>
        </div>
      </div>

      {/* 服务器信息 */}
      <SxwlCard variant="outlined" className="sxwl-monitor-panel" title="服务器状态" loading={loading}>
        <SxwlRow gutter={[16, 16]}>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="CPU 负载"
              value={serverInfo ? formatPercent(serverInfo.cpuLoad) : '-'}
              className={'sxwl-monitor-stat' + toneFor(serverInfo?.cpuLoad ?? 0, 80, 95)}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="内存"
              value={serverInfo ? formatFileSize(serverInfo.memUsed) : '-'}
              suffix={serverInfo ? `/ ${formatFileSize(serverInfo.memTotal)}` : undefined}
              className={'sxwl-monitor-stat' + usageTone(serverInfo?.memUsed, serverInfo?.memTotal, 75, 90)}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="磁盘"
              value={serverInfo ? formatFileSize(serverInfo.diskUsed) : '-'}
              suffix={serverInfo ? `/ ${formatFileSize(serverInfo.diskTotal)}` : undefined}
              className="sxwl-monitor-stat"
            />
          </SxwlCol>
        </SxwlRow>
        {/* 趋势图：CPU 负载（折线图）+ 内存（面积图） */}
        <SxwlRow gutter={16} className="sxwl-monitor-charts">
          <SxwlCol span={12}>
            <SxwlLineChart
              data={history.server.map(d => ({ time: d.time, cpuLoad: d.cpuLoad }))}
              xField="time"
              yField="cpuLoad"
              height={CHART_HEIGHT}
              markStyle={LINE_STYLE}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => formatPercent(v) }}
              axis={{ x: xAxisTime, y: { title: 'CPU 负载 (%)', labelFormatter: formatAxisNumber } }}
              scale={{ y: { domainMin: 0, domainMax: 100 } }}
            />
          </SxwlCol>
          <SxwlCol span={12}>
            <SxwlChart
              chartType="area"
              data={history.server.map(d => ({
                time: d.time,
                memUsedMB: Math.round(d.memUsed / 1024 / 1024),
              }))}
              xField="time"
              yField="memUsedMB"
              height={CHART_HEIGHT}
              markStyle={AREA_STYLE}
              axis={{ x: xAxisTime, y: { title: '内存使用 (MB)' } }}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => formatFileSize(v * 1024 * 1024) }}
            />
          </SxwlCol>
        </SxwlRow>
      </SxwlCard>

      {/* JVM 信息 */}
      <SxwlCard variant="outlined" className="sxwl-monitor-panel" title="JVM 健康" loading={loading}>
        <SxwlRow gutter={[16, 16]}>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="堆内存已用"
              value={jvmInfo ? formatFileSize(jvmInfo.heapUsed) : '-'}
              suffix={jvmInfo ? `/ ${formatFileSize(jvmInfo.heapMax)}` : undefined}
              className={'sxwl-monitor-stat' + usageTone(jvmInfo?.heapUsed, jvmInfo?.heapMax, 75, 90)}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="堆内存提交"
              value={jvmInfo ? formatFileSize(jvmInfo.heapCommitted) : '-'}
              className="sxwl-monitor-stat"
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="线程数"
              value={jvmInfo?.threadCount ?? '-'}
              suffix={jvmInfo ? `/ 峰值 ${jvmInfo.peakThreadCount}` : undefined}
              className="sxwl-monitor-stat"
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="类加载数"
              value={jvmInfo?.classLoadedCount ?? '-'}
              className="sxwl-monitor-stat"
            />
          </SxwlCol>
        </SxwlRow>
        {/* 趋势图：堆内存双线（已用+最大）+ 线程 */}
        <SxwlRow gutter={16} className="sxwl-monitor-charts">
          <SxwlCol span={12}>
            <SxwlChart
              chartType="line"
              data={history.jvm.flatMap(d => [
                { time: d.time, metric: 'heapUsedMB', value: Math.round(d.heapUsed / 1024 / 1024), type: '已用' },
                { time: d.time, metric: 'heapMaxMB', value: Math.round(d.heapMax / 1024 / 1024), type: '最大' },
              ])}
              xField="time"
              yField="value"
              colorField="type"
              height={CHART_HEIGHT}
              scale={{ color: { range: DUAL_RANGE } }}
              axis={{ x: xAxisTime, y: { title: '堆内存 (MB)' } }}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => `${v} MB` }}
            />
          </SxwlCol>
          <SxwlCol span={12}>
            <SxwlLineChart
              data={history.jvm.map(d => ({ time: d.time, threadCount: d.threadCount }))}
              xField="time"
              yField="threadCount"
              height={CHART_HEIGHT}
              markStyle={LINE_STYLE}
              axis={{ x: xAxisTime, y: { title: '线程数' } }}
            />
          </SxwlCol>
        </SxwlRow>
        {jvmInfo?.gcInfos && jvmInfo.gcInfos.length > 0 && (
          <SxwlTable
            className="sxwl-monitor-table"
            dataSource={jvmInfo.gcInfos}
            columns={gcColumns}
            rowKey="name"
            pagination={false}
            size="small"
          />
        )}
      </SxwlCard>

      {/* Redis 信息 */}
      <SxwlCard variant="outlined" className="sxwl-monitor-panel" title="Redis 状态" loading={loading}>
        <SxwlRow gutter={[16, 16]}>
          <SxwlCol span={6}>
            <SxwlStatistic title="已连接客户端" value={redisInfo?.connectedClients ?? '-'} className="sxwl-monitor-stat" />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic title="内存使用" value={redisInfo ? formatFileSize(redisInfo.usedMemory) : '-'} className="sxwl-monitor-stat" />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="缓存命中率"
              value={redisInfo ? formatPercent(redisInfo.hitRate) : '-'}
              className={'sxwl-monitor-stat' + inverseTone(redisInfo?.hitRate, 95, 90)}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic title="Key 总数" value={redisInfo?.totalKeys ?? '-'} className="sxwl-monitor-stat" />
          </SxwlCol>
        </SxwlRow>
        {/* 趋势图：命中率（折线图）+ 内存（面积图） */}
        <SxwlRow gutter={16} className="sxwl-monitor-charts">
          <SxwlCol span={12}>
            <SxwlLineChart
              data={history.redis.map(d => ({ time: d.time, hitRate: d.hitRate }))}
              xField="time"
              yField="hitRate"
              height={CHART_HEIGHT}
              markStyle={LINE_STYLE}
              axis={{ x: xAxisTime, y: { title: '命中率 (%)', labelFormatter: formatAxisNumber } }}
              scale={{ y: { domainMin: 0, domainMax: 100 } }}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => formatPercent(v) }}
            />
          </SxwlCol>
          <SxwlCol span={12}>
            <SxwlChart
              chartType="area"
              data={history.redis.map(d => ({
                time: d.time,
                usedMemoryMB: Math.round(d.usedMemory / 1024 / 1024),
              }))}
              xField="time"
              yField="usedMemoryMB"
              height={CHART_HEIGHT}
              markStyle={AREA_STYLE}
              axis={{ x: xAxisTime, y: { title: '内存使用 (MB)' } }}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => formatFileSize(v * 1024 * 1024) }}
            />
          </SxwlCol>
        </SxwlRow>
      </SxwlCard>

      {/* 数据库信息 */}
      <SxwlCard variant="outlined" className="sxwl-monitor-panel" title="数据库连接池" loading={loading}>
        <SxwlRow gutter={[16, 16]}>
          <SxwlCol span={6}>
            <SxwlStatistic title="活跃连接数" value={dbInfo?.activeConnections ?? '-'} className="sxwl-monitor-stat" />
          </SxwlCol>
        </SxwlRow>
        {/* 趋势图：数据库连接（折线图） */}
        <SxwlRow gutter={16} className="sxwl-monitor-charts">
          <SxwlCol span={12}>
            <SxwlLineChart
              data={history.db.map(d => ({ time: d.time, activeConnections: d.activeConnections }))}
              xField="time"
              yField="activeConnections"
              height={CHART_HEIGHT}
              markStyle={LINE_STYLE}
              axis={{ x: xAxisTime, y: { title: '活跃连接数' } }}
            />
          </SxwlCol>
        </SxwlRow>
      </SxwlCard>
    </div>
  );
}
