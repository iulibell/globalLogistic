import { getJson } from '@/api/http.js'

/**
 * 对应后端 {@code GET /admin/sys/getDictionary?dictType=...}，返回 CommonResult，data 为字典行列表。
 * @param {string} dictType
 * @param {{ auth?: boolean }} [opts]
 */
export function fetchDictionary(dictType, opts = {}) {
  const q = new URLSearchParams({ dictType })
  const path = `/admin/sys/getDictionary?${q.toString()}`
  return getJson(path, { auth: false, ...opts })
}
