import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api', // 替换为你的 API 基础 URL
  timeout: 10000, // 请求超时时间
});

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
