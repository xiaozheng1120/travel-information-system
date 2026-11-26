import request from '@/api/request'
// 提取所有酒店类型，导出typeList，解决刚才的报错
export const typeList = [
  { id: 1, name: "酒店" },
  { id: 2, name: "客栈" },
  { id: 3, name: "民宿" },
  { id: 4, name: "度假村" },
  { id: 5, name: "青旅" }
]
/**
 * 获取酒店分页列表
 * @param {number} pageNum
 * @param {number} pageSize
 * @param {Object} hotel 查询条件
 */
export function getHotelPageApi(pageNum, pageSize, hotel) {
  return request.get('/api/hotel/page', {
    params: {
      pageNum,
      pageSize,
      ...hotel
    }
  })
}
/**
 * 根据id获取酒店详情
 * @param {number} id
 */
export function getHotelDetailApi(id) {
  return request.get(`/api/hotel/${id}`)
}
//新增酒店
export function addHotel(hotel) {
  return request.post('/api/hotel/add', hotel)
}
//编辑酒店
export function editHotel(hotel) {
  return request.put('/api/hotel/update', hotel)
}
//删除酒店
export function deleteHotel(id) {
  // 后端接口：@DeleteMapping("/delete/{id}")，路径补上 /delete/
  return request.delete(`/api/hotel/delete/${id}`)
}