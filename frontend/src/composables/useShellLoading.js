import { computed, ref } from 'vue'

/** 路由转场至少展示时长，避免同步组件切换过快导致看不到加载 */
const ROUTE_OVERLAY_MIN_MS = 240

const routeNavActive = ref(false)
const dictLangSwitchInFlight = ref(0)

let routeNavStartedAt = 0
let routeNavEndTimer = null

export function routeNavBegin() {
  if (routeNavEndTimer) {
    clearTimeout(routeNavEndTimer)
    routeNavEndTimer = null
  }
  routeNavStartedAt = Date.now()
  routeNavActive.value = true
}

export function routeNavEnd() {
  if (!routeNavActive.value) {
    return
  }
  if (routeNavEndTimer) {
    clearTimeout(routeNavEndTimer)
    routeNavEndTimer = null
  }
  const elapsed = Date.now() - routeNavStartedAt
  const wait = Math.max(0, ROUTE_OVERLAY_MIN_MS - elapsed)
  const finish = () => {
    routeNavActive.value = false
    routeNavEndTimer = null
  }
  if (wait > 0) {
    routeNavEndTimer = setTimeout(finish, wait)
  } else {
    finish()
  }
}

export function dictLangSwitchBegin() {
  dictLangSwitchInFlight.value++
}

export function dictLangSwitchEnd() {
  dictLangSwitchInFlight.value = Math.max(0, dictLangSwitchInFlight.value - 1)
}

export function useShellLoading() {
  const shellLoading = computed(
    () => routeNavActive.value || dictLangSwitchInFlight.value > 0,
  )
  return { shellLoading }
}
