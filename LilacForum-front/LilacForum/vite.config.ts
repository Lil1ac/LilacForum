import { fileURLToPath, URL } from 'node:url';
import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import VueSetupExtend from 'vite-plugin-vue-setup-extend';

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd()); // 加载环境变量

  return {
    plugins: [
      vue(),
      VueSetupExtend(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    // server: {
    //   proxy: {
    //     [env.VITE_APP_BASE_API]: {
    //       target: env.VITE_SERVE,  // 获取数据的服务器地址
    //       changeOrigin: true,      // 需要代理跨域
    //       pathRewrite: {
    //         [env.VITE_APP_BASE_API]: '',  // 重写路径
    //         }
    //     }
    //   },
    // },
    server: {
      proxy: {
        [env.VITE_APP_BASE_API]: {
          target: env.VITE_SERVE,  // 获取数据的服务器地址
          changeOrigin: true,      // 需要代理跨域
          pathRewrite: {
            [env.VITE_APP_BASE_API]: '',  // 重写路径
          }
        },
        '/chat': {
          target: 'http://localhost:8080',  // 后端 WebSocket 服务器
          changeOrigin: true,
          ws: true,  // 开启 WebSocket 支持
          rewrite: (path) => path.replace(/^\/chat/, '/chat'),  // 如果有需要，重写路径
        }
      }
    }
  };
});
