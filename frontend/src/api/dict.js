import { getJson, postJson } from '@/api/http.js'

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

/**
 * 超管分页查询字典
 * @param {number} [pageNum]
 * @param {number} [pageSize]
 */
export function fetchSuperDictionaryPage(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams({
    pageNum: String(pageNum),
    pageSize: String(pageSize),
  })
  return getJson(`/admin/sys/super/getDictionary?${q.toString()}`, { auth: true })
}

/**
 * 超管更新字典项
 * @param {{ dictType: string, dictName: string, dictValue: string, lang?: string }} body
 */
export function postSuperUpdateDictionary(body) {
  return postJson('/admin/sys/super/updateDictionary', body, { auth: true })
}

/**
 * 超管删除字典项
 * @param {{ dictType: string, dictName: string, dictValue: string, lang?: string }} body
 */
export function postSuperDeleteDictionary(body) {
  return postJson('/admin/sys/super/deleteDictionary', body, { auth: true })
}

/**
 * 超管新增字典项（后端为 List<DictionaryDto>）
 * @param {{ dictType: string, dictName: string, dictValue: string, sort: number, lang: string }} body
 */
export function postSuperAddDictionary(body) {
  return postJson('/admin/sys/super/addDictionary', [body], { auth: true })
}
