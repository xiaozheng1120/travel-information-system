// src/api/commentData.js
// 攻略评论接口
import request from './request'

// 某篇攻略的评论列表
export function getCommentListApi(guideId) {
  return request.get('/api/comment/list', { params: { guideId } })
}

// 评论数量 + 平均评分
export function getCommentStatsApi(guideId) {
  return request.get('/api/comment/stats', { params: { guideId } })
}

// 发布评论
export function addCommentApi(data) {
  return request.post('/api/comment/add', data)
}

// 删除自己的评论
export function deleteCommentApi(id, userId) {
  return request.delete(`/api/comment/delete/${id}`, { params: { userId } })
}
