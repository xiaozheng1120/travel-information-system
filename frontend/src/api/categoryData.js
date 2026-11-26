import request from './request'
// ========== 分类相关接口 ==========
/**
 * 获取全部分类列表（前台景点下拉选择，只查启用的）
 */
export function getCategoryListApi() {
  return request.get('/api/category/list')
}
/**
 * 【新增】景点新增弹窗专用：获取启用的分类（只返回id、name）
 */
export function getCategoryEnableAllApi() {
  return request.get('/api/category/enableAll')
}
/**
 * 后台分类管理分页查询
 */
export function getCategoryPageApi(pageNum, pageSize, name) {
  return request.get('/api/category/page', {
    params: {
      pageNum,
      pageSize,
      name
    }
  })
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
