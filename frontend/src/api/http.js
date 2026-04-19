import { AUTH_TOKEN_KEY } from '@/constants/authStorage.js'

/** 与后端 sa-token.token-prefix: Bearer 一致：请求头 satoken 的值为 `Bearer ` + token。 */
function satokenHeaderValue(raw) {
  if (raw == null || typeof raw !== 'string') return ''
  const t = raw.trim()
  if (!t) return ''
  return /^Bearer\s/i.test(t) ? t : `Bearer ${t}`
}

function buildHeaders({ auth }) {
  const headers = {
    Accept: 'application/json',
  }
  if (auth) {
    const token = sessionStorage.getItem(AUTH_TOKEN_KEY)
    const v = satokenHeaderValue(token)
    if (v) {
      headers.satoken = v
    }
  }
  return headers
}

async function parseJsonResponse(res) {
  const text = await res.text()
  let json
  try {
    json = text ? JSON.parse(text) : {}
  } catch {
    throw new Error('服务器返回非 JSON')
  }
  return json
}

/**
 * @param {string} path 以 / 开头的路径
 * @param {{ auth?: boolean }} [options]
 * @returns {Promise<{ code: number, message: string, data: unknown }>}
 */
export async function getJson(path, { auth = false } = {}) {
  const headers = buildHeaders({ auth })
  const base = import.meta.env.VITE_API_BASE || ''
  const res = await fetch(`${base}${path}`, {
    method: 'GET',
    headers,
  })
  return parseJsonResponse(res)
}

/**
 * @param {string} path 以 / 开头的路径，如 /auth/login
 * @param {object} [body]
 * @param {{ auth?: boolean }} [options]
 * @returns {Promise<{ code: number, message: string, data: unknown }>}
 */
export async function postJson(path, body = {}, { auth = true } = {}) {
  const headers = {
    ...buildHeaders({ auth }),
    'Content-Type': 'application/json',
  }
  const base = import.meta.env.VITE_API_BASE || ''
  const res = await fetch(`${base}${path}`, {
    method: 'POST',
    headers,
    body: JSON.stringify(body ?? {}),
  })
  const json = await parseJsonResponse(res)
  if (!res.ok) {
    const msg =
      json && typeof json.message === 'string' && json.message.trim()
        ? json.message.trim()
        : json && typeof json.error === 'string' && json.error.trim()
          ? json.error.trim()
          : `http_${res.status}`
    throw new Error(msg)
  }
  return json
}
