// src/api/bannerData.js
import { ref } from 'vue'
import request from './request'
// 轮播列表响应式变量
export const bannerList = ref([])
/**
 * 分页查询轮播图
 * @param {Number} pageNum 页码
 * @param {Number} pageSize 每页条数
 * @param {Object} banner 查询条件
 */
export function getBannerPage(pageNum, pageSize, banner) {
  return request.get('/api/banner/page', {
    params: {
      pageNum,
      pageSize,
      ...banner
    }
  })
}
/**
 * 新增轮播
 * @param {Object} banner 轮播对象
 */
export function addBanner(banner) {
  return request.post('/api/banner/add', banner)
}
/**
 * 修改轮播
 * @param {Object} banner 轮播对象
 */
// 别名：editBanner 兼容vue页面里导入的editBanner
export function editBanner(banner) {
  return updateBanner(banner)
}
export function updateBanner(banner) {
  return request.put('/api/banner/update', banner)
}
/**
 * 删除轮播
 * @param {Number} id 轮播id
 */
// 别名：delBanner 兼容vue页面里导入的delBanner
export function delBanner(id) {
  return deleteBanner(id)
}
export function deleteBanner(id) {
  return request.delete(`/api/banner/delete/${id}`)
}
