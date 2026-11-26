import { ref } from 'vue'
import request from '@/api/request'

// 全局响应式门票数据，前台Ticket 和后台AdminTicket共用
export const ticketAllList = ref([])

// 景点下拉选项（保留，用于下拉筛选，不变）
export const spotList = ref([
  { id: 1, name: "张家界" },
  { id: 2, name: "大理洱海" },
  { id: 3, name: "九寨沟" },
  { id: 4, name: "厦门鼓浪屿" },
  { id: 5, name: "黄山" },
  { id: 6, name: "杭州西湖" },
  { id: 7, name: "西安城墙" },
  { id: 8, name: "上海迪士尼乐园" },
  { id: 9, name: "广州塔" },
  { id: 10, name: "宏村" },
  { id: 11, name: "故宫博物院" },
  { id: 12, name: "芜湖方特东方神画" }
])

/**
 * 从后端加载全部门票，写入 ticketAllList（替换原来本地假数据）
 * @returns Promise
 */
export async function loadAllTicketFromBackend() {
  const res = await request.get('/api/ticket/page', {
    params: {
      pageNum: 1,
      pageSize: 100
    }
  })
  ticketAllList.value = res.records
}

// ========== 门票API方法，放到这里，方便页面导入 ==========
/**
 * 门票分页查询（后台管理页面用，支持搜索筛选）
 * @param {number} pageNum
 * @param {number} pageSize
 * @param {Object} ticket 查询条件 name/spot/type
 */
export function getTicketPageApi(pageNum, pageSize, ticket) {
  return request.get('/api/ticket/page', {
    params: {
      pageNum,
      pageSize,
      ...ticket
    }
  })
}

// 根据id获取单条门票（详情页）
export function getTicketByIdApi(id) {
  return request.get('/api/ticket/getById', {
    params: { id }
  })
}

// 新增门票
export function addTicketApi(ticket) {
  return request.post('/api/ticket/add', ticket)
}

// 修改门票
export function updateTicketApi(ticket) {
  return request.put('/api/ticket/update', ticket)
}

// 删除门票
export function deleteTicketApi(id) {
  return request.delete('/api/ticket/delete', {
    params: { id }
  })
}
