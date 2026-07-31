import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { useAuthStore } from '@/stores/authStore';
import { SxwlIcon, SxwlCard, SxwlRow, SxwlCol, SxwlTitle, SxwlText } from '@/components';
import { getDashboardStatistics, type DashboardStatistics } from '@/api/system/dashboardApi';
import useDashboardStyles from './index.style';

interface StatCardConfig {
  title: string;
  field: keyof DashboardStatistics;
  icon: React.ReactNode;
  color: string;
  bgColor: string;
}

const STAT_CARDS: StatCardConfig[] = [
  { title: '用户总数', field: 'userCount', icon: <SxwlIcon name="UserOutlined" />, color: '#3b82f6', bgColor: '#eff6ff' },
  { title: '角色总数', field: 'roleCount', icon: <SxwlIcon name="TeamOutlined" />, color: '#10b981', bgColor: '#ecfdf5' },
  { title: '菜单总数', field: 'menuCount', icon: <SxwlIcon name="ApartmentOutlined" />, color: '#f59e0b', bgColor: '#fffbeb' },
  { title: '今日日志', field: 'todayLogCount', icon: <SxwlIcon name="FileTextOutlined" />, color: '#ec4899', bgColor: '#fdf2f8' },
];

const QUICK_LINKS = [
  { label: '用户管理', icon: <SxwlIcon name="UserOutlined" />, path: '/system/user', color: '#3b82f6' },
  { label: '角色管理', icon: <SxwlIcon name="TeamOutlined" />, path: '/system/role', color: '#10b981' },
  { label: '菜单管理', icon: <SxwlIcon name="ApartmentOutlined" />, path: '/system/menu', color: '#8b5cf6' },
  { label: '组织架构', icon: <SxwlIcon name="SafetyOutlined" />, path: '/system/organization', color: '#f59e0b' },
  { label: '岗位管理', icon: <SxwlIcon name="ReadOutlined" />, path: '/system/position', color: '#ec4899' },
  { label: '字典管理', icon: <SxwlIcon name="SettingOutlined" />, path: '/system/dict', color: '#06b6d4' },
  { label: '操作日志', icon: <SxwlIcon name="FileTextOutlined" />, path: '/log/operation', color: '#64748b' },
  { label: '登录日志', icon: <SxwlIcon name="LoginOutlined" />, path: '/log/login', color: '#84cc16' },
];

export default function DashboardPage() {
  const { styles, cx } = useDashboardStyles();
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
    <div className={styles.page}>
      {/* 欢迎横幅 */}
      <div className={styles.banner}>
        <div className={styles.bannerContent}>
          <div className="banner-text">
            <SxwlTitle level={4} className={styles.bannerTitle}>欢迎回来{username ? `，${username}` : ''}</SxwlTitle>
            <SxwlText className={styles.bannerDesc}>数行未来·御权 — 统一权限管控平台</SxwlText>
          </div>
          <div className={cx(styles.bannerTip, systemStatus === 'error' && 'banner-tip--error')}>
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
      <SxwlRow gutter={[16, 16]} className={styles.stats}>
        {STAT_CARDS.map((card) => (
          <SxwlCol xs={24} sm={12} lg={6} key={card.title}>
            <SxwlCard className={styles.statCard}>
              <div className={styles.statCardBody}>
                <div
                  className={styles.statCardIcon}
                  style={{ background: card.bgColor, color: card.color }}
                >
                  {card.icon}
                </div>
                <div className={styles.statCardContent}>
                  <div className={styles.statCardValue}>{displayValue(card.field)}</div>
                  <div className={styles.statCardLabel}>{card.title}</div>
                </div>
              </div>
            </SxwlCard>
          </SxwlCol>
        ))}
      </SxwlRow>

      {/* 快捷入口 */}
      <SxwlCard
        title={<span className={styles.sectionTitle}>快捷入口</span>}
        className={styles.quickLinks}
      >
        <SxwlRow gutter={[16, 16]}>
          {QUICK_LINKS.map((link) => (
            <SxwlCol xs={12} sm={8} md={6} lg={3} key={link.label}>
              <div
                className={styles.quickLinkItem}
                onClick={() => navigate(link.path)}
              >
                <span className={styles.quickLinkIcon} style={{ color: link.color, background: `${link.color}14` }}>
                  {link.icon}
                </span>
                <span className={styles.quickLinkLabel}>{link.label}</span>
              </div>
            </SxwlCol>
          ))}
        </SxwlRow>
      </SxwlCard>
    </div>
  );
}
