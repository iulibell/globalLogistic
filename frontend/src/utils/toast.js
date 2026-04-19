/** @type {ReturnType<typeof setTimeout> | null} */
let hideTimer = null
/** @type {ReturnType<typeof setTimeout> | null} */
let removeTimer = null

/**
 * 顶部居中轻提示（错误/成功），不引入额外 UI 库。
 * @param {string} message
 * @param {{ type?: 'error' | 'success' | 'info', duration?: number }} [opts]
 */
export function showToast(message, opts = {}) {
  const { type = 'error', duration = 3400 } = opts
  if (typeof document === 'undefined') return

  const text = String(message ?? '').trim()
  if (!text) return

  let root = document.getElementById('app-toast-root')
  if (!root) {
    root = document.createElement('div')
    root.id = 'app-toast-root'
    root.setAttribute('aria-live', 'assertive')
    document.body.appendChild(root)
  }

  const el = document.createElement('div')
  el.className = `app-toast app-toast--${type}`
  el.setAttribute('role', 'alert')
  el.textContent = text
  root.replaceChildren(el)

  if (hideTimer) clearTimeout(hideTimer)
  if (removeTimer) clearTimeout(removeTimer)

  requestAnimationFrame(() => {
    el.classList.add('app-toast--visible')
  })

  hideTimer = setTimeout(() => {
    el.classList.remove('app-toast--visible')
    removeTimer = setTimeout(() => {
      el.remove()
      if (root && root.childElementCount === 0) {
        root.remove()
      }
    }, 260)
  }, duration)
}
