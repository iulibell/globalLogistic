import { getJson, postJson } from '@/api/http.js'

/**
 * 管理员：分页订单列表（需登录且具备 manager 权限）
 */
export function fetchManagerOrderPage(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/admin/manager/getOrder?${q.toString()}`, { auth: true })
}

/**
 * 管理员：按订单号查询单笔订单
 */
export function fetchManagerOrderById(orderId) {
  const q = new URLSearchParams()
  q.set('orderId', String(orderId).trim())
  return getJson(`/admin/manager/getOrderById?${q.toString()}`, { auth: true })
}

/** 管理员：分页运输线路（启用状态） */
export function fetchManagerLinePage(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/admin/manager/transport/getLine?${q.toString()}`, { auth: true })
}

/**
 * 管理员：按线路主键 ID 查询单条（与后端 lineId 参数一致，对应库表主键 id）
 */
export function fetchManagerLineById(lineId) {
  const q = new URLSearchParams()
  q.set('lineId', String(lineId).trim())
  return getJson(`/admin/manager/transport/getLineById?${q.toString()}`, { auth: true })
}

/** 管理员：按起终点查询线路详情 */
export function fetchManagerLineDetail(origin, dest) {
  const q = new URLSearchParams()
  q.set('origin', String(origin ?? '').trim())
  q.set('dest', String(dest ?? '').trim())
  return getJson(`/admin/manager/transport/getLineDetail?${q.toString()}`, { auth: true })
}

/** 管理员：新增线路（lineId 为后端校验占位值，实际由 TMS 生成） */
export function postManagerAddLine({ lineId = 0, origin, dest, estimation, status }) {
  const q = new URLSearchParams()
  q.set('lineId', String(lineId))
  q.set('origin', String(origin ?? '').trim())
  q.set('dest', String(dest ?? '').trim())
  q.set('estimation', String(estimation))
  q.set('status', String(status))
  return postJson(`/admin/manager/transport/addLine?${q.toString()}`, {}, { auth: true })
}

/** 管理员：更新线路（按业务 lineId） */
export function postManagerUpdateLine({ lineId, origin, dest, estimation, status }) {
  const q = new URLSearchParams()
  q.set('lineId', String(lineId).trim())
  q.set('origin', String(origin ?? '').trim())
  q.set('dest', String(dest ?? '').trim())
  q.set('estimation', String(estimation))
  q.set('status', String(status))
  return postJson(`/admin/manager/transport/updateLine?${q.toString()}`, {}, { auth: true })
}

/** 管理员：待人工派单的运输单分页 */
export function fetchManagerManualAssignmentPage(pageNum = 1, pageSize = 20) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/admin/manager/transport/getManualAssignment?${q.toString()}`, { auth: true })
}

/** 管理员：可接单司机分页（人工派单弹窗） */
export function fetchManagerAvailableDrivers(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/admin/manager/transport/getAvailableDriver?${q.toString()}`, { auth: true })
}

/** 管理员：为运输单指定司机（driverId 为司机用户 ID） */
export function postManagerManualAssignDriver(transportOrderId, driverId) {
  const q = new URLSearchParams()
  q.set('transportOrderId', String(transportOrderId).trim())
  q.set('driverId', String(driverId).trim())
  return postJson(`/admin/manager/transport/manualAssignDriver?${q.toString()}`, {}, { auth: true })
}

/** 管理员：分页仓库列表（经 logi-admin 转发 logi-wms） */
export function fetchManagerWarehousePage(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/admin/manager/wms/getWarehouse?${q.toString()}`, { auth: true })
}

/** 管理员：新增仓库 */
export function postManagerAddWarehouse(body) {
  return postJson('/admin/manager/wms/addWarehouse', body, { auth: true })
}

/** 管理员：更新仓库 */
export function postManagerUpdateWarehouse(body) {
  return postJson('/admin/manager/wms/updateWarehouse', body, { auth: true })
}

/** 管理员：删除仓库 */
export function postManagerDeleteWarehouse(warehouseId) {
  const q = new URLSearchParams()
  q.set('warehouseId', String(warehouseId).trim())
  return postJson(`/admin/manager/wms/deleteWarehouse?${q.toString()}`, {}, { auth: true })
}
