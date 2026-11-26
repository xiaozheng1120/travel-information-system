// src/api/orderData.js
import { ref } from 'vue'
import { ticketAllList } from './ticketData.js'
import request from './request'
// 订单列表，用来接收后端返回数据，不再用来保存假数据
export const orderList = ref([])
/**
 * 添加新订单（调用后端新增订单接口）
 * @param {Object} order 订单对象
 */
export function addOrder(order) {
  return request.post('/api/order/add', order)
}
/**
 * 根据订单id取消订单（后端接口接收id，不是orderNo）
 * @param {Number} id 订单主键id
 */
export function cancelOrder(id) {
  return request.put('/api/order/cancel', null, {
    params: {
      id
    }
  })
}
/**
 * 更新订单数据
 * @param {Object} order 完整订单实体
 */
export function updateOrderList(order) {
  return request.put('/api/order/update', order)
}
/**
 * 删除订单
 * @param {Number} id 订单主键id
 */
export function deleteOrderApi(id) {
  return request.delete(`/api/order/delete/${id}`)
}
/**
 * 从后端加载订单列表（分页，前台我的订单页面 + 后台管理订单页面共用）
 * @param {number} pageNum
 * @param {number} pageSize
 * @param {Object} query 筛选条件
 */
export function loadOrderFromBackend(pageNum = 1, pageSize = 10, query = {}) {
  return request.get('/api/order/page', {
    params: {
      pageNum,
      pageSize,
      ...query
    }
  })
}
/**
 * 根据id查询订单详情
 * @param {Number} id
 */
export function getOrderByIdApi(id) {
  return request.get(`/api/order/${id}`)
}
/**
 * 清空前端页面订单列表（仅清空页面展示，不操作数据库）
 */
export function resetAllOrder() {
  orderList.value = []
}
// ========= 新增导出，给AdminOrder.vue后台订单管理页面使用 =========
// 后台订单分页查询（直接复用loadOrderFromBackend，别名导出，方便页面调用）
export const getOrderPageApi = loadOrderFromBackend

// ===== 核销订单接口，对接后端 /api/order/verify 接口 =====
export function verifyOrderApi(id) {
  return request.put('/api/order/verify', null, {
    params: {
      id
    }
  })
}
