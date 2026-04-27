import { computed, ref } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/auth.js'
import { AUTH_PROFILE_KEY, AUTH_TOKEN_KEY } from '@/constants/authStorage.js'

function readProfile() {
  const raw = sessionStorage.getItem(AUTH_PROFILE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

const profile = ref(readProfile())
const tokenPresent = ref(!!sessionStorage.getItem(AUTH_TOKEN_KEY))

export function useAuthSession() {
  const isLoggedIn = computed(() => tokenPresent.value && !!profile.value)

  /**
   * @param {{ username: string, password: string, requiredRoleKey: string }} credentials
   */
  async function login(credentials) {
    const res = await loginApi(credentials)
    if (res.code !== 200) {
      const msg = res.message && String(res.message).trim() ? res.message : 'login_failed'
      throw new Error(msg)
    }
    const d = res.data || {}
    if (!d.token) {
      throw new Error('login_response_missing_token')
    }
    sessionStorage.setItem(AUTH_TOKEN_KEY, d.token)
    const snapshot = {
      userId: d.userId,
      username: d.username,
      nickname: d.nickname,
      role: d.role,
      city: d.city,
    }
    sessionStorage.setItem(AUTH_PROFILE_KEY, JSON.stringify(snapshot))
    profile.value = snapshot
    tokenPresent.value = true
    return d
  }

  async function logout() {
    try {
      await logoutApi()
    } catch {
      /* 令牌过期等情况仍清理本地态 */
    } finally {
      sessionStorage.removeItem(AUTH_TOKEN_KEY)
      sessionStorage.removeItem(AUTH_PROFILE_KEY)
      profile.value = null
      tokenPresent.value = false
    }
  }

  return {
    profile,
    isLoggedIn,
    login,
    logout,
  }
}
