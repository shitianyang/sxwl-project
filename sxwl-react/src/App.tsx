import { RouterProvider } from 'react-router-dom'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import router from '@/router'
import { THEME_CONFIG } from '@/config'

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
      <RouterProvider router={router} />
    </ConfigProvider>
  )
}
