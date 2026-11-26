// 订单展示状态：数据库只存 1/2/3，已过期是按日期推导出来的显示状态
export function getOrderEndTime(order) {
  if (!order?.travelDate) return null

  const dateText = String(order.travelDate).slice(0, 10)
  const start = new Date(`${dateText}T00:00:00`)
  if (Number.isNaN(start.getTime())) return null

  const isHotel = Number(order.orderType) === 2 || (!order.orderType && !!order.hotelId)
  const days = isHotel ? Math.max(1, Number(order.count) || 1) : 1
  const end = new Date(start)
  end.setDate(end.getDate() + days)
  end.setHours(23, 59, 59, 999)
  return end.getTime()
}

export function isOrderExpired(order, now = Date.now()) {
  if (Number(order?.status) !== 1) return false
  const endTime = getOrderEndTime(order)
  return endTime !== null && now > endTime
}

export function getOrderDisplayStatus(order) {
  const status = Number(order?.status)
  if (status === 1) return isOrderExpired(order) ? '已过期' : '待出行'
  if (status === 2) return '已完成'
  if (status === 3) return '已取消'
  return '未知状态'
}
