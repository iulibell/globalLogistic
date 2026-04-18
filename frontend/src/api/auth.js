import { postJson } from './http.js'

/**
 * @param {{ username: string, password: string, requiredRoleKey: string }} payload
 */
export function login(payload) {
  return postJson('/auth/login', payload, { auth: false })
}

/**
 * 与后端 {@code RegisterParamDto} 字段一致：userName、userType 为 "1"–"5" 字符串。
 * @param {{ userName: string, password: string, phone: string, userType: string, nickname?: string }} payload
 */
export function register(payload) {
  return postJson('/auth/register', payload, { auth: false })
}

export function logout() {
  return postJson('/auth/logout', {}, { auth: true })
}
