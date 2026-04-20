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
