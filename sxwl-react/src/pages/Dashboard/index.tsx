import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { useAuthStore } from '@/stores/authStore';
import { SxwlIcon, SxwlCard, SxwlRow, SxwlCol, SxwlTitle, SxwlText, SxwlButton, SxwlTag } from '@/components';
import { getDashboardStatistics, type DashboardStatistics } from '@/api/system/dashboardApi';
import { useMonitorSSE } from '@/hooks/useMonitorSSE';
import { formatFileSize, formatPercent } from '@/utils/formatUtils';
import './index.scss';

interface StatCardConfig {
  title: string;
  field: keyof DashboardStatistics;
  icon: React.ReactNode;
}

const STAT_CARDS: StatCardConfig[] = [
  { title: '用户总数', field: 'userCount', icon: <SxwlIcon name="UserOutlined" /> },
  { title: '角色总数', field: 'roleCount', icon: <SxwlIcon name="TeamOutlined" /> },
  { title: '菜单总数', field: 'menuCount', icon: <SxwlIcon name="ApartmentOutlined" /> },
  { title: '今日日志', field: 'todayLogCount', icon: <SxwlIcon name="FileTextOutlined" /> },
];

const QUICK_LINKS = [
  { label: '用户管理', icon: <SxwlIcon name="UserOutlined" />, path: '/system/user' },
  { label: '角色管理', icon: <SxwlIcon name="TeamOutlined" />, path: '/system/role' },
  { label: '菜单管理', icon: <SxwlIcon name="ApartmentOutlined" />, path: '/system/menu' },
  { label: '组织架构', icon: <SxwlIcon name="SafetyOutlined" />, path: '/system/organization' },
  { label: '岗位管理', icon: <SxwlIcon name="ReadOutlined" />, path: '/system/position' },
  { label: '字典管理', icon: <SxwlIcon name="SettingOutlined" />, path: '/system/dict' },
  { label: '操作日志', icon: <SxwlIcon name="FileTextOutlined" />, path: '/system/log/operation' },
  { label: '登录日志', icon: <SxwlIcon name="LoginOutlined" />, path: '/system/log/login' },
];

/** 运行状态面板要展示的实时指标 */
const RUNTIME_ROWS = [
  { key: 'cpu', label: 'CPU 负载' },
  { key: 'mem', label: '系统内存' },
  { key: 'heap', label: 'JVM 堆内存' },
  { key: 'redis', label: 'Redis 命中率' },
] as const;

export default function DashboardPage() {
  const navigate = useNavigate();
  const username = useAuthStore((s) => s.username);
  const [statistics, setStatistics] = useState<DashboardStatistics | null>(null);
  const [systemStatus, setSystemStatus] = useState<'loading' | 'ok' | 'error'>('loading');
  const { data: monitor, connected } = useMonitorSSE();

  useEffect(() => {
    setSystemStatus('loading');
    getDashboardStatistics()
      .then((res) => {
        setStatistics(res.data.data);
        setSystemStatus('ok');
      })
      .catch(() => {
        setStatistics(null);
        setSystemStatus('error');
      });
  }, []);

  const displayValue = (field: keyof DashboardStatistics): string | number => {
    if (statistics === null) return '--';
    return statistics[field] ?? '--';
  };

  const runtimeValue = (key: (typeof RUNTIME_ROWS)[number]['key']): string => {
    const s = monitor?.server;
    const j = monitor?.jvm;
    const r = monitor?.redis;
    if (!connected) return '未连接';
    switch (key) {
      case 'cpu':
        return s ? formatPercent(s.cpuLoad) : '-';
      case 'mem':
        return s ? `${formatFileSize(s.memUsed)} / ${formatFileSize(s.memTotal)}` : '-';
      case 'heap':
        return j ? `${formatFileSize(j.heapUsed)} / ${formatFileSize(j.heapMax)}` : '-';
      case 'redis':
        return r ? formatPercent(r.hitRate) : '-';
    }
  };

  return (
    <div className="sxwl-dashboard-page">
      {/* 欢迎横幅 */}
      <div className="sxwl-dashboard-banner">
        <div className="sxwl-dashboard-banner-text">
          <SxwlTitle level={5} className="sxwl-dashboard-banner-title">欢迎回来{username ? `，${username}` : ''}</SxwlTitle>
          <SxwlText className="sxwl-dashboard-banner-desc">数行未来·御权 — 统一权限管控平台</SxwlText>
        </div>
        <div className={`sxwl-dashboard-banner-tip${systemStatus === 'error' ? ' banner-tip--error' : ''}`}>
          <SxwlIcon name={systemStatus === 'error' ? 'CloseCircleOutlined' : 'SafetyOutlined'} />
          <span>
            {systemStatus === 'loading' ? '系统检查中...' :
             systemStatus === 'error' ? '系统异常' :
             '系统运行正常'}
          </span>
        </div>
      </div>

      {/* 统计卡片 */}
      <SxwlRow gutter={[16, 16]} className="sxwl-dashboard-stats">
        {STAT_CARDS.map((card) => (
          <SxwlCol xs={24} sm={12} lg={6} key={card.title}>
            <SxwlCard variant="outlined" className="sxwl-dashboard-stat-card">
              <div className="sxwl-dashboard-stat-card-body">
                <div className="sxwl-dashboard-stat-card-icon">
                  {card.icon}
                </div>
                <div className="sxwl-dashboard-stat-card-content">
                  <div className="sxwl-dashboard-stat-card-value sxwl-num">{displayValue(card.field)}</div>
                  <div className="sxwl-dashboard-stat-card-label">{card.title}</div>
                </div>
              </div>
            </SxwlCard>
          </SxwlCol>
        ))}
      </SxwlRow>

      <SxwlRow gutter={[16, 16]} className="sxwl-dashboard-bottom">
        {/* 运行状态：来自系统监控的实时推送，只放 4 个总览指标 */}
        <SxwlCol xs={24} lg={8}>
          <SxwlCard
            variant="outlined"
            className="sxwl-dashboard-runtime"
            title={<span className="sxwl-dashboard-section-title">运行状态</span>}
            extra={
              <SxwlButton type="link" size="small" onClick={() => navigate('/monitor/server')}>
                查看详情
              </SxwlButton>
            }
          >
            <dl className="sxwl-dashboard-runtime-list">
              {RUNTIME_ROWS.map((row) => (
                <div className="sxwl-dashboard-runtime-row" key={row.key}>
                  <dt>{row.label}</dt>
                  <dd className="sxwl-num">{runtimeValue(row.key)}</dd>
                </div>
              ))}
            </dl>
            <div className="sxwl-dashboard-runtime-foot">
              <SxwlTag tone={connected ? 'success' : 'neutral'}>
                {connected ? '实时推送' : '未连接'}
              </SxwlTag>
            </div>
          </SxwlCard>
        </SxwlCol>

        {/* 快捷入口 */}
        <SxwlCol xs={24} lg={16}>
          <SxwlCard
            variant="outlined"
            title={<span className="sxwl-dashboard-section-title">快捷入口</span>}
            className="sxwl-dashboard-quick-links"
          >
            <SxwlRow gutter={[16, 16]}>
              {QUICK_LINKS.map((link) => (
                <SxwlCol xs={12} sm={8} md={6} key={link.label}>
                  <div
                    className="sxwl-dashboard-quick-link-item"
                    role="button"
                    tabIndex={0}
                    onClick={() => navigate(link.path)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); navigate(link.path); }
                    }}
                  >
                    <span className="sxwl-dashboard-quick-link-icon">
                      {link.icon}
                    </span>
                    <span className="sxwl-dashboard-quick-link-label">{link.label}</span>
                  </div>
                </SxwlCol>
              ))}
            </SxwlRow>
          </SxwlCard>
        </SxwlCol>
      </SxwlRow>
    </div>
  );
}
