import axios from 'axios';

// 使用 Vite 的环境变量方式获取 API 地址和请求超时时间
const instance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASEURL,
  timeout: 30000
})

instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['token'] = token; // 使用 'token' 作为请求头字段
  } else {
    console.warn('No token found in localStorage');
  }
  return config;
}, error => {
  return Promise.reject(error);
});

export default instance;
