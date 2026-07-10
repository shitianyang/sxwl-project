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
      },
    },
  },
})
