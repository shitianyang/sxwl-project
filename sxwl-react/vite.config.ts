import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import svgr from 'vite-plugin-svgr'
import path from "path"

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), svgr()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    host: '127.0.0.1',
    port: 31001,
    strictPort: true,
    proxy: {
      '/sxwl-api': {
        target: 'http://127.0.0.1:30101',
        changeOrigin: true,
        // SSE 长连接不超时
        timeout: 0,
        proxyTimeout: 0,
      },
    },
  },
  build: {
    rolldownOptions: {
      preserveEntrySignatures: 'allow-extension',
      output: {
        strictExecutionOrder: true,
        codeSplitting: {
          includeDependenciesRecursively: false,
          groups: [
            {
              name: 'react-vendor',
              test: /node_modules[\\/](react|react-dom|react-router|react-router-dom|scheduler|zustand)[\\/]/,
              priority: 40,
            },
            {
              name: 'antd-vendor',
              test: /node_modules[\\/]antd[\\/]/,
              priority: 30,
            },
            {
              name: 'ant-design-vendor',
              test: /node_modules[\\/]@ant-design[\\/]/,
              priority: 30,
            },
            {
              name: 'rc-vendor',
              test: /node_modules[\\/](@rc-component|rc-[^\\/]+)[\\/]/,
              priority: 29,
            },
            {
              name: 'markdown-vendor',
              test: /node_modules[\\/](react-markdown|remark-gfm|highlight\.js|unified|remark-|rehype-|micromark|mdast-util-|hast-util-|unist-util-|vfile|devlop|comma-separated-tokens|property-information|space-separated-tokens|trim-lines|web-namespaces|zwitch)[\\/]/,
              priority: 25,
            },
            {
              name: 'crypto-vendor',
              test: /node_modules[\\/](sm-crypto|spark-md5|jsbn)[\\/]/,
              priority: 20,
            },
            {
              name: 'vendor',
              test: /node_modules[\\/]/,
              priority: 10,
              maxSize: 450 * 1024,
            },
          ],
        },
      },
    },
  },
})
