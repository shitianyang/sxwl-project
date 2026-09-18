/**
 * ServerMonitor 服务器监控页面
 * 
 * <p>核心功能：实时监控 CPU、内存、磁盘、JVM、Redis、数据库连接池</p>
 * <p>设计规范：品牌色暖橙 #DE5F0E + Token 化设计 + Ant Design 6</p>
 */
import { SxwlCard, SxwlRow, SxwlCol, SxwlStatistic, SxwlTag, SxwlTable } from '@/components';
import { Button } from 'antd';
import ReloadOutlined from '@ant-design/icons';
import SxwlLineChart from '@/components/SxwlChart/SxwlLineChart';
import SxwlChart from '@/components/SxwlChart';
import { useMonitorSSE } from '@/hooks/useMonitorSSE';
import { useState, useCallback } from 'react';
import './index.scss';

/** 品牌主色（与 variables.scss $sxwl-color-primary 对齐） */
const BRAND = '#DE5F0E';
const BRAND_LIGHT = '#F0972D';
/** 面积图填充/描边（替代原硬编码蓝 #1677ff33） */
const AREA_STYLE = { fill: 'rgba(222, 95, 14, 0.16)', stroke: BRAND, fillOpacity: 1, lineWidth: 2 };
/** 单系列折线品牌橙 */
const LINE_STYLE = { stroke: BRAND, lineWidth: 2.5 };
/** 多系列（堆内存已用/最大）橙系深浅，可区分且统一 */
const DUAL_RANGE = [BRAND, BRAND_LIGHT];

/** 字节格式化 */
function formatBytes(bytes: number | null | undefined): string {
  if (bytes == null || bytes === 0) return '0 B';
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + units[i];
}

/** 百分比格式化 */
function formatPercent(value: number | null | undefined): string {
  if (value == null) return '-';
  return value.toFixed(1) + '%';
}

/**
 * 获取 CPU 负载颜色（异常阈值视觉提示）
 * 
 * <p>> 95% 红色危险，> 80% 橙色警告，≤ 80% 默认文字色</p>
 * <p>符合监控页面规范：快速识别异常</p>
 */
function getCpuLoadColor(cpuLoad: number): string {
  if (cpuLoad > 95) return '#FF4D4F';  // 红色危险
  if (cpuLoad > 80) return BRAND;      // 橙色警告
  return '';                           // 默认文字色
}

/**
 * 获取内存使用率颜色（异常阈值视觉提示）
 * 
 * <p>> 90% 红色危险，> 75% 橙色警告，≤ 75% 默认文字色</p>
 */
function getMemUsageColor(memUsed: number, memTotal: number): string {
  if (memTotal === 0) return '';
  const usagePercent = (memUsed / memTotal) * 100;
  if (usagePercent > 90) return '#FF4D4F';  // 红色危险
  if (usagePercent > 75) return BRAND;       // 橙色警告
  return '';                                 // 默认文字色
}

/**
 * 刷新时间格式为友好显示
 * 
 * <p>例如：14:30:25、刚刚</p>
 */
function formatRelativeTime(date: Date): string {
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffSec = Math.floor(diffMs / 1000);
  
  if (diffSec < 60) return '刚刚';
  if (diffSec < 3600) return `${Math.floor(diffSec / 60)} 分钟前`;
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
}

/**
 * 计算图表响应式高度
 * 
 * <p>基于容器宽度动态调整，最小 200px，最大 300px</p>
 * <p>在窄屏（span <= 8）时使用 200px，宽屏时使用 250px</p>
 */
function getChartHeight(span: number = 12): number {
  return span <= 8 ? 200 : 250;
}

export default function ServerMonitorPage() {
  const { data, connected, history } = useMonitorSSE();
  const [lastUpdateTime, setLastUpdateTime] = useState<Date>(new Date());

  const serverInfo = data?.server;
  const jvmInfo = data?.jvm;
  const redisInfo = data?.redis;
  const dbInfo = data?.db;

  const loading = !data;

  /** 响应式图表高度 */
  const [chartHeight] = useState(() => getChartHeight(12));

  /** 手动刷新处理 */
  const handleRefresh = useCallback(() => {
    setLastUpdateTime(new Date());
    // 触发重新获取数据（根据实际项目需求调整）
    window.dispatchEvent(new CustomEvent('monitor:refresh'));
  }, []);

  const gcColumns = [
    { title: 'GC 名称', dataIndex: 'name', key: 'name', width: 200 },
    { title: '次数', dataIndex: 'count', key: 'count', width: 100 },
    { title: '总耗时（ms）', dataIndex: 'totalTimeMs', key: 'totalTimeMs', width: 120 },
  ];

  return (
    <div className="sxwl-monitor-page">
      <div className="sxwl-monitor-head">
        <span className="sxwl-monitor-breadcrumb">监控运维 / 系统监控</span>
        <div className="sxwl-monitor-status">
          <SxwlTag color={connected ? BRAND : 'red'}>
            {connected ? '实时' : '连接断开'}
          </SxwlTag>
          <span className="sxwl-monitor-last-update">
            最后更新：{formatRelativeTime(lastUpdateTime)}
          </span>
          <Button 
            type="text" 
            icon={<ReloadOutlined />}
            onClick={handleRefresh}
            className="sxwl-monitor-refresh-btn"
            title="手动刷新"
          >
            刷新
          </Button>
        </div>
      </div>

      {/* 服务器信息 */}
      <SxwlCard title="服务器状态" style={{ marginBottom: 16 }} loading={loading}>
        <SxwlRow gutter={[16, 16]}>
          <SxwlCol span={6}>
            <SxwlStatistic 
              title="CPU 负载" 
              value={serverInfo ? formatPercent(serverInfo.cpuLoad) : '-'}
              style={{ 
                color: serverInfo?.cpuLoad && serverInfo.cpuLoad > 80 ? getCpuLoadColor(serverInfo.cpuLoad) : undefined,
                fontWeight: serverInfo?.cpuLoad && serverInfo.cpuLoad > 80 ? 600 : 400
              }}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="内存"
              value={serverInfo ? formatBytes(serverInfo.memUsed) : '-'}
              suffix={`/ ${serverInfo ? formatBytes(serverInfo.memTotal) : ''}`}
              style={{ 
                color: serverInfo?.memUsed && serverInfo.memTotal ? getMemUsageColor(serverInfo.memUsed, serverInfo.memTotal) : undefined,
                fontWeight: serverInfo?.memUsed && serverInfo.memTotal && ((serverInfo.memUsed / serverInfo.memTotal) * 100) > 75 ? 600 : 400
              }}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="磁盘"
              value={serverInfo ? formatBytes(serverInfo.diskUsed) : '-'}
              suffix={`/ ${serverInfo ? formatBytes(serverInfo.diskTotal) : ''}`}
            />
          </SxwlCol>
        </SxwlRow>
        {/* 趋势图：CPU 负载（折线图）+ 内存（面积图） */}
        <SxwlRow gutter={16} style={{ marginTop: 16 }}>
          <SxwlCol span={12}>
            <SxwlLineChart
              data={history.server.map(d => ({ time: d.time, cpuLoad: d.cpuLoad }))}
              xField="time"
              yField="cpuLoad"
              height={chartHeight}
              markStyle={LINE_STYLE}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => formatPercent(v) }}
              axis={{ x: { title: '时间', labelFormatter: (v: string) => v.includes('T') ? v.split('T')[1].substring(0, 5) : v }, y: { title: 'CPU 负载 (%)' } }}
              scale={{ y: { min: 0, max: 100 } }}
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
              height={chartHeight}
              markStyle={AREA_STYLE}
              axis={{ x: { title: '时间', labelFormatter: (v: string) => v.includes('T') ? v.split('T')[1].substring(0, 5) : v }, y: { title: '内存使用 (MB)' } }}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => formatBytes(v * 1024 * 1024) }}
            />
          </SxwlCol>
        </SxwlRow>
      </SxwlCard>

      {/* JVM 信息 */}
      <SxwlCard title="JVM 健康" style={{ marginBottom: 16 }} loading={loading}>
        <SxwlRow gutter={[16, 16]}>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="堆内存已用"
              value={jvmInfo ? formatBytes(jvmInfo.heapUsed) : '-'}
              suffix={`/ ${jvmInfo ? formatBytes(jvmInfo.heapMax) : ''}`}
              style={{ 
                color: jvmInfo?.heapUsed && jvmInfo.heapMax ? getMemUsageColor(jvmInfo.heapUsed, jvmInfo.heapMax) : undefined,
                fontWeight: jvmInfo?.heapUsed && jvmInfo.heapMax && ((jvmInfo.heapUsed / jvmInfo.heapMax) * 100) > 90 ? 600 : 400
              }}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="堆内存提交"
              value={jvmInfo ? formatBytes(jvmInfo.heapCommitted) : '-'}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic 
              title="线程数" 
              value={jvmInfo?.threadCount ?? '-'}
              suffix={`/ 峰值 ${jvmInfo?.peakThreadCount ?? ''}`}
              style={{ 
                color: jvmInfo?.threadCount && jvmInfo?.peakThreadCount && (jvmInfo.threadCount / jvmInfo.peakThreadCount) > 0.85 ? BRAND : undefined,
                fontWeight: jvmInfo?.threadCount && jvmInfo?.peakThreadCount && (jvmInfo.threadCount / jvmInfo.peakThreadCount) > 0.85 ? 600 : 400
              }}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic
              title="类加载数"
              value={jvmInfo?.classLoadedCount ?? '-'}
            />
          </SxwlCol>
        </SxwlRow>
        {/* 趋势图：堆内存双线（已用+最大）+ 线程 */}
        <SxwlRow gutter={16} style={{ marginTop: 16 }}>
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
              height={chartHeight}
              scale={{ color: { range: DUAL_RANGE } }}
              axis={{ x: { title: '时间', labelFormatter: (v: string) => v.includes('T') ? v.split('T')[1].substring(0, 5) : v }, y: { title: '堆内存 (MB)' } }}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => `${v} MB` }}
            />
          </SxwlCol>
          <SxwlCol span={12}>
            <SxwlLineChart
              data={history.jvm.map(d => ({ time: d.time, threadCount: d.threadCount }))}
              xField="time"
              yField="threadCount"
              height={chartHeight}
              markStyle={LINE_STYLE}
              axis={{ x: { title: '时间', labelFormatter: (v: string) => v.includes('T') ? v.split('T')[1].substring(0, 5) : v }, y: { title: '线程数' } }}
            />
          </SxwlCol>
        </SxwlRow>
        {jvmInfo?.gcInfos && jvmInfo.gcInfos.length > 0 && (
          <SxwlTable
            dataSource={jvmInfo.gcInfos}
            columns={gcColumns}
            rowKey="name"
            pagination={false}
            size="small"
            style={{ marginTop: 16 }}
          />
        )}
      </SxwlCard>

      {/* Redis 信息 */}
      <SxwlCard title="Redis 状态" style={{ marginBottom: 16 }} loading={loading}>
        <SxwlRow gutter={[16, 16]}>
          <SxwlCol span={6}>
            <SxwlStatistic title="已连接客户端" value={redisInfo?.connectedClients ?? '-'} />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic title="内存使用" value={redisInfo ? formatBytes(redisInfo.usedMemory) : '-'} />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic 
              title="缓存命中率" 
              value={redisInfo ? formatPercent(redisInfo.hitRate) : '-'}
              style={{ 
                color: redisInfo?.hitRate !== undefined && redisInfo.hitRate < 90 ? '#FF4D4F' : undefined,
                fontWeight: redisInfo?.hitRate !== undefined && redisInfo.hitRate < 90 ? 600 : 400
              }}
            />
          </SxwlCol>
          <SxwlCol span={6}>
            <SxwlStatistic title="Key 总数" value={redisInfo?.totalKeys ?? '-'} />
          </SxwlCol>
        </SxwlRow>
        {/* 趋势图：命中率（折线图）+ 内存（面积图） */}
        <SxwlRow gutter={16} style={{ marginTop: 16 }}>
          <SxwlCol span={12}>
            <SxwlLineChart
              data={history.redis.map(d => ({ time: d.time, hitRate: d.hitRate }))}
              xField="time"
              yField="hitRate"
              height={chartHeight}
              markStyle={LINE_STYLE}
              axis={{ x: { title: '时间', labelFormatter: (v: string) => v.includes('T') ? v.split('T')[1].substring(0, 5) : v }, y: { title: '命中率 (%)' } }}
              scale={{ y: { min: 0, max: 100 } }}
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
              height={chartHeight}
              markStyle={AREA_STYLE}
              axis={{ x: { title: '时间', labelFormatter: (v: string) => v.includes('T') ? v.split('T')[1].substring(0, 5) : v }, y: { title: '内存使用 (MB)' } }}
              tooltip={{ channel: 'y', valueFormatter: (v: number) => formatBytes(v * 1024 * 1024) }}
            />
          </SxwlCol>
        </SxwlRow>
      </SxwlCard>

      {/* 数据库信息 */}
      <SxwlCard title="数据库连接池" loading={loading}>
        <SxwlRow gutter={[16, 16]}>
          <SxwlCol span={6}>
            <SxwlStatistic title="活跃连接数" value={dbInfo?.activeConnections ?? '-'} />
          </SxwlCol>
        </SxwlRow>
        {/* 趋势图：数据库连接（折线图） */}
        <SxwlRow gutter={16} style={{ marginTop: 16 }}>
          <SxwlCol span={12}>
            <SxwlLineChart
              data={history.db.map(d => ({ time: d.time, activeConnections: d.activeConnections }))}
              xField="time"
              yField="activeConnections"
              height={chartHeight}
              markStyle={LINE_STYLE}
              axis={{ x: { title: '时间', labelFormatter: (v: string) => v.includes('T') ? v.split('T')[1].substring(0, 5) : v }, y: { title: '活跃连接数' } }}
            />
          </SxwlCol>
        </SxwlRow>
      </SxwlCard>
    </div>
  );
}
