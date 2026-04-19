import { getJson, postJson } from '@/api/http.js'

/**
 * 审核员：分页拉取注册申请（需登录且具备 reviewer 权限）
 * @param {number} [pageNum]
 * @param {number} [pageSize]
 */
export function fetchRegisterApplicationPage(pageNum = 1, pageSize = 20) {
  const q = new URLSearchParams({
    pageNum: String(pageNum),
    pageSize: String(pageSize),
  })
  return getJson(`/admin/reviewer/fetchRegisterApplication?${q.toString()}`, { auth: true })
}

/**
 * 与后端 RegisterParamDto 字段一致（username）
 * @param {{ username: string, password: string, nickname?: string, userType: number|string, phone: string }} row
 */
export function postAccessRegister(body) {
  return postJson('/admin/reviewer/accessRegister', body, { auth: true })
}

export function postRejectRegister(body) {
  return postJson('/admin/reviewer/rejectRegister', body, { auth: true })
}

/** 审核员：特殊商品订单审核列表 */
export function fetchOrderReviewPage(pageNum = 1, pageSize = 50) {
  const q = new URLSearchParams({
    pageNum: String(pageNum),
    pageSize: String(pageSize),
  })
  return getJson(`/admin/reviewer/getOrderReview?${q.toString()}`, { auth: true })
}

/** @param {string} remark 可为空 */
export function postAccessOrderReview(orderId, remark = '') {
  const q = new URLSearchParams({
    orderId: String(orderId),
    remark: remark != null ? String(remark) : '',
  })
  return postJson(`/admin/reviewer/accessOrderReview?${q.toString()}`, {}, { auth: true })
}

export function postRejectOrderReview(orderId, remark = '') {
  const q = new URLSearchParams({
    orderId: String(orderId),
    remark: remark != null ? String(remark) : '',
  })
  return postJson(`/admin/reviewer/rejectOrderReview?${q.toString()}`, {}, { auth: true })
}
