import { AUTH_TOKEN_KEY } from '@/constants/authStorage.js'

function buildHeaders({ auth }) {
  const headers = {
    Accept: 'application/json',
  }
  if (auth) {
    const token = sessionStorage.getItem(AUTH_TOKEN_KEY)
    if (token) {
      headers.satoken = token
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
  return parseJsonResponse(res)
}
