// src/api/favoriteData.js
// 用户收藏（景点 / 攻略 / 住宿）接口
import request from './request'

// 我的收藏列表
export function getFavoriteListApi(userId) {
  return request.get('/api/favorite/list', { params: { userId } })
}

// 是否已收藏
export function checkFavoriteApi(userId, targetType, targetId) {
  return request.get('/api/favorite/check', { params: { userId, targetType, targetId } })
}

// 添加收藏
export function addFavoriteApi(data) {
  return request.post('/api/favorite/add', data)
}

// 取消收藏
export function removeFavoriteApi(userId, targetType, targetId) {
  return request.delete('/api/favorite/remove', { params: { userId, targetType, targetId } })
}

// 取消收藏（按记录 id）
export function removeFavoriteByIdApi(id) {
  return request.delete(`/api/favorite/remove/${id}`)
}
