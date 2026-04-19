import { getJson, postJson } from '@/api/http.js'

/**
 * 超级管理员：分页用户列表
 * @param {number} [pageNum]
 * @param {number} [pageSize]
 */
export function fetchSuperUserList(pageNum = 1, pageSize = 10) {
  const q = new URLSearchParams({
    pageNum: String(pageNum),
    pageSize: String(pageSize),
  })
  return getJson(`/admin/super/fetchSysUserInfo?${q.toString()}`, { auth: true })
}

/**
 * 按身份（userType：1–5）分页查询
 * @param {number} pageNum
 * @param {number} pageSize
 * @param {string} userType
 */
export function fetchSuperUserByUserType(pageNum, pageSize, userType) {
  const q = new URLSearchParams({
    pageNum: String(pageNum),
    pageSize: String(pageSize),
    userType: String(userType),
  })
  return getJson(`/admin/super/fetchSysUserByUserType?${q.toString()}`, { auth: true })
}

/** @param {string} userId */
export function fetchSuperUserByUserId(userId) {
  const q = new URLSearchParams({ userId: String(userId) })
  return getJson(`/admin/super/fetchSysUserByUserId?${q.toString()}`, { auth: true })
}

/**
 * @param {{ userId: number|string, username: string, userType: string, nickname?: string }} body
 */
export function postUpdateSysUser(body) {
  return postJson('/admin/super/updateSysUserInfo', body, { auth: true })
}

/** @param {string} userId */
export function postDeleteSysUser(userId) {
  const q = new URLSearchParams({ userId: String(userId) })
  return postJson(`/admin/super/deleteSysUser?${q.toString()}`, {}, { auth: true })
}
