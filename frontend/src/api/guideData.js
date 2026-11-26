import request from '@/api/request'
/**
 * 获取攻略分页列表
 * @param {number} pageNum
 * @param {number} pageSize
 * @param {Object} guide 查询条件
 */
export function getGuidePageApi(pageNum, pageSize, guide) {
  return request.get('/api/guide/page', {
    params: {
      pageNum,
      pageSize,
      ...guide
    }
  })
}
/**
 * 根据id查询攻略详情
 * @param {number} id 攻略id
 */
export function getGuideDetailApi(id) {
  return request.get(`/api/guide/detail/${id}`)
}
/**
 * 新增攻略（写入数据库）
 * @param {Object} guide 攻略表单对象
 */
export function addGuideApi(guide) {
  return request.post('/api/guide/add', guide)
}
/**
 * 修改攻略（更新数据库）
 * @param {Object} guide 攻略表单对象
 */
export function updateGuideApi(guide) {
  return request.put('/api/guide/update', guide)
}
/**
 * 删除攻略（删除数据库记录）
 * @param {number} id 攻略id
 */
export function deleteGuideApi(id) {
  return request.delete(`/api/guide/delete/${id}`)
}