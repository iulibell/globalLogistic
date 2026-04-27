import { getJson, postJson } from '@/api/http.js'

/** 司机：分页获取可接运输单（按当前城市） */
export function fetchDriverTransportOrders(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/tms/driver/getTransportOrder?${q.toString()}`, { auth: true })
}

/** 司机：获取个人详细信息 */
export function fetchDriverInfo() {
  return getJson('/tms/driver/getDriverInfo', { auth: true })
}

/** 司机：待处理人工派单提示 */
export function fetchDriverPendingAssignedOrders() {
  return getJson('/tms/driver/getPendingAssignedOrders', { auth: true })
}

/** 司机：已接受人工派单（用于当前派单页） */
export function fetchDriverCurrentAssignedOrders() {
  return getJson('/tms/driver/getCurrentAssignedOrders', { auth: true })
}

/** 司机：当前派单详情（进行中） */
export function fetchDriverCurrentAssignmentDetail() {
  return getJson('/tms/driver/getCurrentAssignmentDetail', { auth: true })
}

/** 司机：按起终点查询路线详情 */
export function fetchDriverLineDetail(origin, dest) {
  const q = new URLSearchParams()
  q.set('origin', String(origin ?? '').trim())
  q.set('dest', String(dest ?? '').trim())
  return getJson(`/tms/sys/getLineDetail?${q.toString()}`, { auth: true })
}

/** 司机：接受派单 */
export function postDriverAcceptAssignment(transportOrderId, driverId) {
  const q = new URLSearchParams()
  q.set('transportOrderId', String(transportOrderId ?? '').trim())
  q.set('driverId', String(driverId ?? '').trim())
  return postJson(`/tms/driver/accessAssignment?${q.toString()}`, {}, { auth: true })
}

/** 司机：绑定载具 */
export function postDriverAddVehicle(payload) {
  return postJson('/tms/driver/addVehicle', payload || {}, { auth: true })
}

/** 司机：修改上线/下线状态（0=上线，2=下线休息） */
export function postDriverUpdateStatus(driverId, status) {
  const q = new URLSearchParams()
  q.set('driverId', String(driverId ?? '').trim())
  q.set('status', String(status))
  return postJson(`/tms/driver/updateStatus?${q.toString()}`, {}, { auth: true })
}

/** 司机：接受人工派单提示 */
export function postDriverAcceptAssignedOrder(transportId) {
  const q = new URLSearchParams()
  q.set('transportId', String(transportId ?? '').trim())
  return postJson(`/tms/driver/acceptAssignedOrder?${q.toString()}`, {}, { auth: true })
}

/** 司机：拒绝人工派单提示（触发扣减权重） */
export function postDriverRejectAssignedOrder(transportId) {
  const q = new URLSearchParams()
  q.set('transportId', String(transportId ?? '').trim())
  return postJson(`/tms/driver/rejectAssignedOrder?${q.toString()}`, {}, { auth: true })
}

/** 司机：确认发车 */
export function postDriverConfirmDeparture(transportOrderId, city) {
  const q = new URLSearchParams()
  q.set('transportOrderId', String(transportOrderId ?? '').trim())
  q.set('city', String(city ?? '').trim())
  return postJson(`/tms/driver/confirmDeparture?${q.toString()}`, {}, { auth: true })
}

/** 司机：确认送达 */
export function postDriverConfirmArrived(transportOrderId) {
  const q = new URLSearchParams()
  q.set('transportOrderId', String(transportOrderId ?? '').trim())
  return postJson(`/tms/driver/confirmArrived?${q.toString()}`, {}, { auth: true })
}

/** 司机：确认签收 */
export function postDriverConfirmReceived(transportOrderId) {
  const q = new URLSearchParams()
  q.set('transportOrderId', String(transportOrderId ?? '').trim())
  return postJson(`/tms/driver/confirmReceived?${q.toString()}`, {}, { auth: true })
}

/** 司机：查询收货方是否已确认签收 */
export function fetchDriverConsigneeSigned(transportOrderId) {
  const q = new URLSearchParams()
  q.set('transportOrderId', String(transportOrderId ?? '').trim())
  return getJson(`/tms/driver/isConsigneeSigned?${q.toString()}`, { auth: true })
}
