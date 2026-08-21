import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { useAuthStore } from '@/stores/authStore';
import { SxwlIcon, SxwlCard, SxwlRow, SxwlCol, SxwlTitle, SxwlText } from '@/components';
import { getDashboardStatistics, type DashboardStatistics } from '@/api/system/dashboardApi';
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
  { label: '操作日志', icon: <SxwlIcon name="FileTextOutlined" />, path: '/log/operation' },
  { label: '登录日志', icon: <SxwlIcon name="LoginOutlined" />, path: '/log/login' },
];

export default function DashboardPage() {
  const navigate = useNavigate();
  const username = useAuthStore((s) => s.username);
  const [statistics, setStatistics] = useState<DashboardStatistics | null>(null);
  const [systemStatus, setSystemStatus] = useState<'loading' | 'ok' | 'error'>('loading');

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

  return (
    <div className="sxwl-dashboard-page">
      {/* 欢迎横幅 */}
      <div className="sxwl-dashboard-banner">
        <div className="sxwl-dashboard-banner-content">
          <div className="banner-text">
            <SxwlTitle level={4} className="sxwl-dashboard-banner-title">欢迎回来{username ? `，${username}` : ''}</SxwlTitle>
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
      </div>

      {/* 统计卡片 */}
      <SxwlRow gutter={[16, 16]} className="sxwl-dashboard-stats">
        {STAT_CARDS.map((card) => (
          <SxwlCol xs={24} sm={12} lg={6} key={card.title}>
            <SxwlCard className="sxwl-dashboard-stat-card">
              <div className="sxwl-dashboard-stat-card-body">
                <div className="sxwl-dashboard-stat-card-icon">
                  {card.icon}
                </div>
                <div className="sxwl-dashboard-stat-card-content">
                  <div className="sxwl-dashboard-stat-card-value">{displayValue(card.field)}</div>
                  <div className="sxwl-dashboard-stat-card-label">{card.title}</div>
                </div>
              </div>
            </SxwlCard>
          </SxwlCol>
        ))}
      </SxwlRow>

      {/* 快捷入口 */}
      <SxwlCard
        title={<span className="sxwl-dashboard-section-title">快捷入口</span>}
        className="sxwl-dashboard-quick-links"
      >
        <SxwlRow gutter={[16, 16]}>
          {QUICK_LINKS.map((link) => (
            <SxwlCol xs={12} sm={8} md={6} lg={3} key={link.label}>
              <div
                className="sxwl-dashboard-quick-link-item"
                onClick={() => navigate(link.path)}
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
    </div>
  );
}
