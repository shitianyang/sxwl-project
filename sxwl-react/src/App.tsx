import AppRouter from '@/router'
import { App as AntApp, ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import { useEffect } from 'react'
import { THEME_CONFIG } from '@/config'
import { initMessageInstance } from '@/components/SxwlMessage'

/** 在 antd App 组件内初始化上下文感知的 message 实例 */
function MessageInitializer() {
  const { message } = AntApp.useApp()

  useEffect(() => {
    initMessageInstance(message)
  }, [message])

  return null
}

export default function App() {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        token: {
          colorPrimary: THEME_CONFIG.colorPrimary,
        },
      }}
    >
      <AntApp>
        <AppRouter />
        <MessageInitializer />
      </AntApp>
    </ConfigProvider>
  )
}
