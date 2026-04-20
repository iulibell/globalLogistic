import { getJson, postJson } from '@/api/http.js'
import { AUTH_TOKEN_KEY } from '@/constants/authStorage.js'

/** 仓管：分页入库单列表 */
export function fetchKeeperInboundPage(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/wms/keeper/getInbound?${q.toString()}`, { auth: true })
}

/** 仓管：确认入库 */
export function postKeeperConfirmInbound(inboundId, skuCode) {
  const q = new URLSearchParams()
  q.set('inboundId', String(inboundId ?? '').trim())
  q.set('skuCode', String(skuCode ?? '').trim())
  return postJson(`/wms/keeper/confirmInbound?${q.toString()}`, {}, { auth: true })
}

/** 仓管：分页入库申请列表 */
export function fetchKeeperInboundApplyPage(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/wms/keeper/getInboundApply?${q.toString()}`, { auth: true })
}

/** 仓管：按申请单号查询单条入库申请 */
export function fetchKeeperInboundApplyById(applyId) {
  const q = new URLSearchParams()
  q.set('applyId', String(applyId ?? '').trim())
  return getJson(`/wms/keeper/getInboundApplyById?${q.toString()}`, { auth: true })
}

function satokenHeaderValue(raw) {
  if (raw == null || typeof raw !== 'string') return ''
  const t = raw.trim()
  if (!t) return ''
  return /^Bearer\s/i.test(t) ? t : `Bearer ${t}`
}

async function postKeeperNoJson(path) {
  const token = sessionStorage.getItem(AUTH_TOKEN_KEY)
  const satoken = satokenHeaderValue(token)
  const headers = {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  }
  if (satoken) headers.satoken = satoken
  const base = import.meta.env.VITE_API_BASE || ''
  const res = await fetch(`${base}${path}`, {
    method: 'POST',
    headers,
    body: '{}',
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text && text.trim() ? text.trim() : `http_${res.status}`)
  }
  return { code: 200, message: 'ok', data: true }
}

/** 仓管：通过入库申请（后端 void，前端按状态码判定） */
export function postKeeperAccessInboundApply(applyId, fee) {
  const q = new URLSearchParams()
  q.set('applyId', String(applyId ?? '').trim())
  q.set('fee', String(fee))
  return postKeeperNoJson(`/wms/keeper/accessInboundApply?${q.toString()}`)
}

/** 仓管：退回入库申请（后端 void，前端按状态码判定） */
export function postKeeperRejectInboundApply(applyId, remark) {
  const q = new URLSearchParams()
  q.set('applyId', String(applyId ?? '').trim())
  q.set('remark', String(remark ?? '').trim())
  return postKeeperNoJson(`/wms/keeper/rejectInboundApply?${q.toString()}`)
}

/** 仓管：分页出库单列表 */
export function fetchKeeperOutboundPage(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/wms/keeper/getOutbound?${q.toString()}`, { auth: true })
}

/** 仓管：按出库单号查询 */
export function fetchKeeperOutboundById(outboundId) {
  const q = new URLSearchParams()
  q.set('outboundId', String(outboundId ?? '').trim())
  return getJson(`/wms/keeper/getOutboundById?${q.toString()}`, { auth: true })
}

/** 仓管：确认出库 */
export function postKeeperConfirmOutbound(outboundId) {
  const q = new URLSearchParams()
  q.set('outboundId', String(outboundId ?? '').trim())
  return postJson(`/wms/keeper/confirmOutbound?${q.toString()}`, {}, { auth: true })
}

/** 仓管：分页库存列表 */
export function fetchKeeperStockPage(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams()
  q.set('pageNum', String(pageNum))
  q.set('pageSize', String(pageSize))
  return getJson(`/wms/keeper/getStock?${q.toString()}`, { auth: true })
}

/** 仓管：按库存ID查询 */
export function fetchKeeperStockById(stockId) {
  const q = new URLSearchParams()
  q.set('stockId', String(stockId ?? '').trim())
  return getJson(`/wms/keeper/getStockById?${q.toString()}`, { auth: true })
}

/** 仓管：新增库存 */
export function postKeeperAddStock(payload) {
  return postJson('/wms/keeper/addStock', payload || {}, { auth: true })
}

/** 仓管：更新库存 */
export function postKeeperUpdateStock(payload) {
  return postJson('/wms/keeper/updateStock', payload || {}, { auth: true })
}

/** 仓管：删除库存 */
export function postKeeperDeleteStock(stockId) {
  const q = new URLSearchParams()
  q.set('stockId', String(stockId ?? '').trim())
  return postJson(`/wms/keeper/deleteStock?${q.toString()}`, {}, { auth: true })
}
