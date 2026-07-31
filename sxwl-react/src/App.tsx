import AppRouter from '@/router'
import { App as AntApp, ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import { useEffect } from 'react'
import { THEME_CONFIG } from '@/config'
import { initMessageInstance } from '@/components/SxwlMessage'
import useGlobalStyles from '@/styles/global.style'
import { SXWL_TOKENS } from '@/styles/theme.token'

/** 在 antd App 组件内初始化上下文感知的 message 实例 */
function MessageInitializer() {
  const { message } = AntApp.useApp()

  useEffect(() => {
    initMessageInstance(message)
  }, [message])

  return null
}

export default function App() {
  const { styles } = useGlobalStyles()

  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        token: {
          colorPrimary: THEME_CONFIG.colorPrimary,
          colorPrimaryHover: SXWL_TOKENS.colorPrimaryHover,
          colorPrimaryActive: SXWL_TOKENS.colorPrimaryActive,

          // 文字色阶（4 级）
          colorText: SXWL_TOKENS.colorText,
          colorTextSecondary: SXWL_TOKENS.colorTextSecondary,
          colorTextTertiary: SXWL_TOKENS.colorTextTertiary,
          colorTextQuaternary: SXWL_TOKENS.colorTextDisabled,

          // 背景
          colorBgLayout: SXWL_TOKENS.colorBgLayout,
          colorBgContainer: SXWL_TOKENS.colorBgContainer,

          // 边框
          colorBorderSecondary: SXWL_TOKENS.colorBorderSecondary,

          // 圆角（仅两种：4px 小元素 / 8px 容器）
          borderRadiusSM: SXWL_TOKENS.borderRadiusSM,
          borderRadius: SXWL_TOKENS.borderRadius,
          borderRadiusLG: SXWL_TOKENS.borderRadius,
        },
      }}
    >
      <AntApp>
        <div className={styles.root}>
          <AppRouter />
        </div>
        <MessageInitializer />
      </AntApp>
    </ConfigProvider>
  )
}
