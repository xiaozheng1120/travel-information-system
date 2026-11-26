// spotData.js
// 引入axios请求封装
import request from './request'
// ========== 分类相关接口（替换原来前端内存假数据函数） ==========
/**
 * 获取全部分类列表
 */
export function getCategoryListApi() {
  return request.get('/api/category/list')
}
/**
 * 新增分类
 */
export function addCategoryApi(category) {
  return request.post('/api/category/add', category)
}
/**
 * 编辑分类
 */
export function editCategoryApi(data) {
  return request.put('/api/category/update', data)
}
/**
 * 删除分类
 */
export function deleteCategoryApi(id) {
  return request.delete(`/api/category/delete/${id}`)
}
// ========== 景点管理后台接口（AdminSpot.vue后台管理页面使用） ==========
/**
 * 分页获取景点列表（后台管理）
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页条数
 * @param {string} name 景点名称搜索条件
 * @param {number} categoryId 分类id搜索条件
 */
export function getSpotListApi(pageNum, pageSize, name, categoryId) {
  return request.get('/api/spot/page', {
    params: {
      pageNum,
      pageSize,
      name,
      categoryId
    }
  })
}
/**
 * 【新增】获取景点总数（后台首页统计卡片用）
 */
export function getSpotCountApi() {
  return request.get('/api/spot/count')
}
/**
 * 新增景点
 */
export function addSpotApi(data) {
  return request.post('/api/spot/add', data)
}
/**
 * 修改景点
 */
export function updateSpotApi(data) {
  return request.put('/api/spot/update', data)
}
/**
 * 删除景点
 */
export function deleteSpotApi(id) {
  return request.delete(`/api/spot/delete/${id}`)
}
// ========== 前台景点页面接口（Spot.vue前台展示页面使用） ==========
/**
 * 前台：获取全部启用状态景点
 */
export function getFrontSpotListApi() {
  return request.get('/api/spot/list')
}
/**
 * 前台：根据id查询单个景点（详情页用）
 */
export function getSpotByIdApi(id) {
  return request.get(`/api/spot/getById/${id}`)
}