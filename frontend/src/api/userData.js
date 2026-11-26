// src/api/userData.js
// 原前端模拟内存用户数据全部移除，本文件现在存放用户相关axios接口
// 注意：后端用户接口挂在 /user 下（不是 /api），这里用绝对地址直连后端，不再依赖 Vite 代理
import axios from 'axios'

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

// 用户登录接口
export function loginApi(data) {
  return service.post('/user/login', data)
}

// 用户注册接口
export function registerApi(data) {
  return service.post('/user/register', data)
}

// 获取全部用户列表（后台用户管理页面，可按角色 / 关键词筛选）
export function getUserListApi(params = {}) {
  return service.get('/user/list', { params })
}

// 根据id查询单个用户
export function getUserByIdApi(id) {
  return service.get('/user/getById', { params: { id } })
}

// 后台新增用户
export function addUserApi(data) {
  return service.post('/user/add', data)
}

// 后台编辑用户
export function updateUserApi(data) {
  return service.put('/user/update', data)
}

// 删除用户
export function deleteUserApi(id) {
  return service.delete(`/user/delete/${id}`)
}

// 忘记密码：校验账号+手机号后重置密码
export function resetPwdApi(data) {
  return service.post('/user/resetPwd', data)
}

// 个人中心：修改个人资料
export function updateUserInfoApi(data) {
  return service.put('/user/updateInfo', data)
}

// 后台启用 / 禁用账号
export function updateUserStatusApi(id, status) {
  return service.put('/user/status', null, { params: { id, status } })
}

// 后台重置指定用户密码
export function adminResetPwdApi(data) {
  return service.post('/user/adminResetPwd', data)
}
