// src/api/request.js
import axios from 'axios'

// 后端springboot地址，根据你自己后端端口修改
const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(config => {
  return config
})

// 响应拦截器，直接返回后端的data
service.interceptors.response.use(
  res => res.data,
  err => Promise.reject(err)
)

export default service
