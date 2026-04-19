<script setup>
import { computed, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { useAuthSession } from '@/composables/useAuthSession.js'
import LanguageDropdown from '@/components/LanguageDropdown.vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { useSortedDictionaryOptions } from '@/composables/useSortedDictionaryOptions.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { showToast } from '@/utils/toast.js'

const FALLBACK_ROLES = [
  { value: 'super', label: '超级管理员' },
  { value: 'manager', label: '管理员' },
  { value: 'keeper', label: '仓管' },
  { value: 'driver', label: '司机' },
  { value: 'reviewer', label: '审核员' },
]

const router = useRouter()
const route = useRoute()
const { login } = useAuthSession()
const { uiLang } = useUiLang()
const { t: loginT } = useMultiDictionary(['page_login', 'api_message', 'api_auth'], uiLang)
const { options: roleOptions } = useSortedDictionaryOptions('login_role_option', uiLang, FALLBACK_ROLES)

const username = ref('')
const password = ref('')
const requiredRoleKey = ref('manager')
const loading = ref(false)

const fieldErrors = ref({ role: '', username: '', password: '' })

function clearFieldError(key) {
  fieldErrors.value = { ...fieldErrors.value, [key]: '' }
}

function validateLoginForm() {
  fieldErrors.value = { role: '', username: '', password: '' }
  let ok = true
  const fb = (key) => pageDictFallback('page_login', key, uiLang.value)
  if (!requiredRoleKey.value || !String(requiredRoleKey.value).trim()) {
    fieldErrors.value.role = loginT('page_login', 'err_role_required', fb('err_role_required'))
    ok = false
  }
  if (!username.value.trim()) {
    fieldErrors.value.username = loginT('page_login', 'err_username_required', fb('err_username_required'))
    ok = false
  }
  if (!password.value) {
    fieldErrors.value.password = loginT('page_login', 'err_password_required', fb('err_password_required'))
    ok = false
  }
  return ok
}

const submitLabel = computed(() =>
  loading.value
    ? loginT('page_login', 'btn_submit_loading', '登录中…')
    : loginT('page_login', 'btn_submit', '登录'),
)

async function onSubmit() {
  if (!validateLoginForm()) {
    return
  }
  loading.value = true
  try {
    await login({
      username: username.value.trim(),
      password: password.value,
      requiredRoleKey: requiredRoleKey.value,
    })
    const redir = route.query.redirect
    const dest =
      typeof redir === 'string' && redir.startsWith('/') && !redir.startsWith('//') ? redir : '/'
    await router.replace(dest)
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'login_failed'
    const text = translateApiMessage(raw, loginT, uiLang.value)
    showToast(text, { type: 'error' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="split-page">
    <div class="panel-left">
      <div class="left-top">
        <RouterLink to="/" class="brand-link">
          <span class="brand-wordmark" role="img" aria-label="Global Logistics">
            <span class="wordmark-orange">GLOBAL</span><span class="wordmark-blue">LOGISTICS</span>
          </span>
        </RouterLink>
        <LanguageDropdown v-model="uiLang" />
      </div>

      <div class="form-shell">
        <h1 class="welcome">{{ loginT('page_login', 'welcome_title', '欢迎回来') }}</h1>

        <form class="form" novalidate @submit.prevent="onSubmit">
          <label class="field">
            <span class="label">{{ loginT('page_login', 'label_role', '身份') }}</span>
            <select
              v-model="requiredRoleKey"
              class="input select"
              :class="{ 'input--error': fieldErrors.role }"
              :aria-invalid="fieldErrors.role ? 'true' : 'false'"
              @change="clearFieldError('role')"
            >
              <option v-for="opt in roleOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
            <p v-if="fieldErrors.role" class="field-error">{{ fieldErrors.role }}</p>
          </label>
          <label class="field">
            <span class="label">{{ loginT('page_login', 'label_username', '用户名') }}</span>
            <input
              v-model="username"
              class="input"
              :class="{ 'input--error': fieldErrors.username }"
              type="text"
              autocomplete="username"
              :aria-invalid="fieldErrors.username ? 'true' : 'false'"
              @input="clearFieldError('username')"
            />
            <p v-if="fieldErrors.username" class="field-error">{{ fieldErrors.username }}</p>
          </label>
          <label class="field">
            <span class="label">{{ loginT('page_login', 'label_password', '密码') }}</span>
            <input
              v-model="password"
              class="input"
              :class="{ 'input--error': fieldErrors.password }"
              type="password"
              autocomplete="current-password"
              :aria-invalid="fieldErrors.password ? 'true' : 'false'"
              @input="clearFieldError('password')"
            />
            <p v-if="fieldErrors.password" class="field-error">{{ fieldErrors.password }}</p>
          </label>

          <div class="row-forgot">
            <RouterLink to="/" class="link-quiet">{{ loginT('page_login', 'link_home', '返回首页') }}</RouterLink>
            <a href="#" class="link-forgot" @click.prevent>{{ loginT('page_login', 'link_forgot', '忘记密码') }}</a>
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            {{ submitLabel }}
          </button>
        </form>

        <p class="signup-hint">
          {{ loginT('page_login', 'signup_prefix', '没有账号？') }}
          <RouterLink to="/register" class="link-accent">{{
            loginT('page_login', 'signup_link', '创建账号')
          }}</RouterLink>
        </p>
      </div>

      <footer class="left-footer">
        <a href="#">关于我们</a>
        <span class="sep">|</span>
        <a href="#">联系我们</a>
        <span class="sep">|</span>
        <a href="#">许可协议</a>
        <span class="sep">|</span>
        <a href="#">版本声明</a>
        <span class="sep">|</span>
        <a href="#">隐私政策</a>
        <span class="sep">|</span>
        <a href="#">帮助中心</a>
      </footer>
    </div>

    <div class="panel-right" aria-hidden="true">
      <div class="right-grid" />
      <div class="right-glow" />
      <div class="mock-window">
        <div class="win-dots">
          <span /><span /><span />
        </div>
        <div class="win-rows">
          <div class="win-row">
            <span class="win-ico ico-pick" />已揽收
          </div>
          <div class="win-row">
            <span class="win-ico ico-transit" />运输中
          </div>
          <div class="win-row">
            <span class="win-ico ico-done" />已签收
          </div>
        </div>
        <div class="win-bars">
          <span /><span /><span />
        </div>
      </div>
      <div class="carrier-hub">
        <div class="hub-line" />
        <div class="hub-box">
          <span class="hub-truck" aria-hidden="true" />
          <span class="hub-num">3200+</span>
        </div>
        <div class="hub-branches">
          <span class="mini-logo">EMS</span>
          <span class="mini-logo">FedEx</span>
          <span class="mini-logo">DHL</span>
          <span class="mini-logo">UPS</span>
        </div>
      </div>
      <div class="right-copy">
        <h2 class="right-title">覆盖全球主要承运商</h2>
        <p class="right-lead">统一查询轨迹，多角色协同，助力跨境物流数字化运营。</p>
      </div>
      <div class="carousel-dots">
        <span class="dot active" /><span class="dot" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.split-page {
  min-height: 100vh;
  display: flex;
  flex-direction: row;
  background: var(--17-bg);
}

.panel-left {
  flex: 1;
  min-width: 0;
  max-width: 560px;
  display: flex;
  flex-direction: column;
  padding: 28px 40px 20px 12px;
  box-sizing: border-box;
  background: var(--17-bg-soft);
  color: var(--17-ink);
}

.left-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 48px;
}

.brand-link {
  text-decoration: none;
  color: inherit;
}

.brand-wordmark {
  display: inline-flex;
  align-items: baseline;
  letter-spacing: -0.045em;
  font-size: clamp(1.1rem, 2.4vw, 1.4rem);
}

.wordmark-orange,
.wordmark-blue {
  font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Arial Black', 'Impact', sans-serif;
  font-weight: 900;
  text-transform: uppercase;
  line-height: 1;
}

.wordmark-orange {
  color: var(--17-brand-orange);
  position: relative;
  z-index: 1;
  padding-right: 0.02em;
  clip-path: polygon(0 0, 100% 0, calc(100% - 0.22em) 100%, 0 100%);
}

.wordmark-blue {
  color: var(--17-brand-blue);
  margin-left: -0.2em;
  position: relative;
  z-index: 0;
  clip-path: polygon(0.18em 0, 100% 0, 100% 100%, 0 100%);
}

.form-shell {
  flex: 1;
  width: 100%;
  max-width: 380px;
  margin: 0 auto;
}

.welcome {
  margin: 0 0 28px;
  font-size: 26px;
  font-weight: 700;
  color: var(--17-ink);
  letter-spacing: -0.02em;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 5px;
  text-align: left;
}

.label {
  font-size: 12px;
  font-weight: 600;
  color: var(--17-muted);
}

.input,
.select {
  display: block;
  width: 100%;
  max-width: 100%;
  min-width: 0;
  box-sizing: border-box;
}

.input {
  border: 1px solid var(--17-border);
  border-radius: 4px;
  padding: 7px 10px;
  font-size: 13px;
  font-family: inherit;
  color: var(--17-ink);
  background: var(--17-input-bg);
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.input:focus {
  border-color: var(--17-teal);
  box-shadow: 0 0 0 2px rgba(61, 141, 255, 0.2);
}

.input--error {
  border-color: #c62828;
}

.input--error:focus {
  border-color: #c62828;
  box-shadow: 0 0 0 2px rgba(198, 40, 40, 0.2);
}

.field-error {
  margin: 0;
  font-size: 12px;
  line-height: 1.35;
  color: #c62828;
}

.select {
  cursor: pointer;
}

.row-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: -6px;
}

.link-forgot {
  font-size: 13px;
  font-weight: 500;
  color: var(--17-link);
  text-decoration: none;
}

.link-forgot:hover {
  text-decoration: underline;
}

.link-quiet {
  font-size: 13px;
  color: var(--17-muted);
  text-decoration: none;
}

.link-quiet:hover {
  color: var(--17-link-hover);
}

.btn-submit {
  width: 100%;
  margin-top: 4px;
  border: none;
  border-radius: 4px;
  padding: 9px 16px;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(180deg, #3d8dff, #256fea);
  cursor: pointer;
  transition: filter 0.15s, opacity 0.15s;
}

.btn-submit:hover:not(:disabled) {
  filter: brightness(1.06);
}

.btn-submit:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.signup-hint {
  margin: 28px 0 0;
  text-align: center;
  font-size: 14px;
  color: var(--17-muted);
}

.link-accent {
  color: var(--17-link);
  font-weight: 600;
  text-decoration: none;
}

.link-accent:hover {
  text-decoration: underline;
}

.left-footer {
  margin-top: auto;
  padding-top: 32px;
  font-size: 11px;
  color: var(--17-muted);
  text-align: center;
  line-height: 1.8;
}

.left-footer a {
  color: var(--17-muted);
  text-decoration: none;
}

.left-footer a:hover {
  color: var(--17-link-hover);
}

.sep {
  margin: 0 5px;
  opacity: 0.7;
}

/* —— 右侧宣传区 —— */
.panel-right {
  display: none;
  flex: 1;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: radial-gradient(ellipse 90% 70% at 40% 45%, #1e3a6e 0%, #0d1f45 42%, #060f24 100%);
}

@media (min-width: 960px) {
  .panel-right {
    display: block;
  }

  .panel-left {
    flex: 0 0 50%;
    max-width: none;
  }
}

.right-grid {
  position: absolute;
  inset: 0;
  opacity: 0.14;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.5) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.5) 1px, transparent 1px);
  background-size: 28px 28px;
}

.right-glow {
  position: absolute;
  width: 80%;
  height: 60%;
  left: 10%;
  top: 18%;
  background: radial-gradient(circle, rgba(100, 160, 255, 0.18) 0%, transparent 65%);
  pointer-events: none;
}

.mock-window {
  position: absolute;
  left: 12%;
  top: 14%;
  width: min(340px, 42vw);
  padding: 14px 16px 16px;
  border-radius: 10px;
  background: rgba(8, 24, 56, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(8px);
}

.win-dots {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}

.win-dots span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.win-dots span:nth-child(1) {
  background: #ff5f57;
}

.win-dots span:nth-child(2) {
  background: #febc2e;
}

.win-dots span:nth-child(3) {
  background: #28c840;
}

.win-rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.88);
}

.win-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.win-ico {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  flex-shrink: 0;
}

.ico-pick {
  background: linear-gradient(135deg, #4a9fff, #2563eb);
}

.ico-transit {
  background: linear-gradient(135deg, #60a5fa, #1d4ed8);
}

.ico-done {
  background: linear-gradient(135deg, #34d399, #059669);
}

.win-bars {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.win-bars span {
  height: 6px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.1);
}

.win-bars span:nth-child(1) {
  width: 92%;
}

.win-bars span:nth-child(2) {
  width: 76%;
}

.win-bars span:nth-child(3) {
  width: 55%;
}

.carrier-hub {
  position: absolute;
  left: 48%;
  top: 22%;
  display: flex;
  align-items: flex-start;
  gap: 0;
}

.hub-line {
  width: 48px;
  height: 2px;
  margin-top: 36px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.45), rgba(255, 255, 255, 0.08));
}

.hub-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 14px;
  border-radius: 8px;
  background: rgba(30, 80, 180, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.hub-truck {
  width: 28px;
  height: 18px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 3px;
  clip-path: polygon(0 35%, 22% 35%, 28% 0, 100% 0, 100% 100%, 0 100%);
}

.hub-num {
  font-size: 13px;
  font-weight: 800;
  color: #fff;
  letter-spacing: 0.02em;
}

.hub-branches {
  margin-left: 10px;
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mini-logo {
  font-size: 10px;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.85);
  padding: 4px 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.right-copy {
  position: absolute;
  left: 12%;
  right: 12%;
  bottom: 14%;
}

.right-title {
  margin: 0 0 10px;
  font-size: clamp(1.25rem, 2.2vw, 1.65rem);
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.02em;
}

.right-lead {
  margin: 0;
  font-size: 13px;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.78);
  max-width: 420px;
}

.carousel-dots {
  position: absolute;
  bottom: 6%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.carousel-dots .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
}

.carousel-dots .dot.active {
  background: rgba(255, 255, 255, 0.9);
  width: 22px;
  border-radius: 4px;
}
</style>
