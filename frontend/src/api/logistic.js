import { getJson } from '@/api/http.js'

/** 主页：按运输单号查询物流 */
export function fetchLogisticByTransportOrderId(transportOrderId) {
  const q = new URLSearchParams()
  q.set('transportOrderId', String(transportOrderId ?? '').trim())
  return getJson(`/tms/getLogisticById?${q.toString()}`)
}

