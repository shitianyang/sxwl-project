import { useNavigate } from 'react-router-dom';
import { SxwlIcon, SxwlCard, SxwlRow, SxwlCol, SxwlTitle, SxwlText } from '@/components';
import './index.scss';

interface StatCard {
  title: string;
  value: number;
  icon: React.ReactNode;
  color: string;
  bgColor: string;
}

const STAT_CARDS: StatCard[] = [
  { title: '用户总数', value: 0, icon: <SxwlIcon name="UserOutlined" />, color: '#3b82f6', bgColor: '#eff6ff' },
  { title: '角色总数', value: 0, icon: <SxwlIcon name="TeamOutlined" />, color: '#10b981', bgColor: '#ecfdf5' },
  { title: '菜单总数', value: 0, icon: <SxwlIcon name="ApartmentOutlined" />, color: '#f59e0b', bgColor: '#fffbeb' },
  { title: '今日日志', value: 0, icon: <SxwlIcon name="FileTextOutlined" />, color: '#ec4899', bgColor: '#fdf2f8' },
];

const QUICK_LINKS = [
  { label: '用户管理', icon: <SxwlIcon name="UserOutlined" />, path: '/system/user', color: '#3b82f6' },
  { label: '角色管理', icon: <SxwlIcon name="TeamOutlined" />, path: '/system/role', color: '#10b981' },
  { label: '菜单管理', icon: <SxwlIcon name="ApartmentOutlined" />, path: '/system/menu', color: '#8b5cf6' },
  { label: '组织架构', icon: <SxwlIcon name="SafetyOutlined" />, path: '/system/organization', color: '#f59e0b' },
  { label: '岗位管理', icon: <SxwlIcon name="ReadOutlined" />, path: '/system/position', color: '#ec4899' },
  { label: '字典管理', icon: <SxwlIcon name="SettingOutlined" />, path: '/system/dict', color: '#06b6d4' },
  { label: '操作日志', icon: <SxwlIcon name="FileTextOutlined" />, path: '/system/log', color: '#64748b' },
  { label: '文件管理', icon: <SxwlIcon name="CloudUploadOutlined" />, path: '/system/file', color: '#84cc16' },
];

export default function DashboardPage() {
  const navigate = useNavigate();

  return (
    <div className="dashboard-page">
      {/* 欢迎横幅 */}
      <div className="dashboard-banner">
        <div className="banner-content">
          <div className="banner-text">
            <SxwlTitle level={4} className="banner-title">欢迎回来</SxwlTitle>
            <SxwlText className="banner-desc">数行未来·御权 — 统一权限管控平台</SxwlText>
          </div>
          <div className="banner-tip">
            <SxwlIcon name="SafetyOutlined" />
            <span>系统运行正常</span>
          </div>
        </div>
      </div>

      {/* 统计卡片 */}
      <SxwlRow gutter={[16, 16]} className="dashboard-stats">
        {STAT_CARDS.map((card) => (
          <SxwlCol xs={24} sm={12} lg={6} key={card.title}>
            <SxwlCard className="stat-card">
              <div className="stat-card-body">
                <div
                  className="stat-card-icon"
                  style={{ background: card.bgColor, color: card.color }}
                >
                  {card.icon}
                </div>
                <div className="stat-card-content">
                  <div className="stat-card-value">{card.value}</div>
                  <div className="stat-card-label">{card.title}</div>
                </div>
              </div>
            </SxwlCard>
          </SxwlCol>
        ))}
      </SxwlRow>

      {/* 快捷入口 */}
      <SxwlCard
        title={<span className="section-title">快捷入口</span>}
        className="dashboard-quick-links"
      >
        <SxwlRow gutter={[16, 16]}>
          {QUICK_LINKS.map((link) => (
            <SxwlCol xs={12} sm={8} md={6} lg={3} key={link.label}>
              <div
                className="quick-link-item"
                onClick={() => navigate(link.path)}
              >
                <span className="quick-link-icon" style={{ color: link.color, background: `${link.color}14` }}>
                  {link.icon}
                </span>
                <span className="quick-link-label">{link.label}</span>
              </div>
            </SxwlCol>
          ))}
        </SxwlRow>
      </SxwlCard>
    </div>
  );
}
