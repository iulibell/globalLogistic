<script setup>
import { computed, onUnmounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { register as registerApi, sendRegisterCaptcha } from '@/api/auth.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { showToast } from '@/utils/toast.js'
import LanguageDropdown from '@/components/LanguageDropdown.vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { useSortedDictionaryOptions } from '@/composables/useSortedDictionaryOptions.js'

const FALLBACK_USER_TYPES = [
  { value: '1', label: '超级管理员' },
  { value: '2', label: '管理员' },
  { value: '3', label: '仓库管理员' },
  { value: '4', label: '司机' },
  { value: '5', label: '审核员' },
]

const router = useRouter()
const { uiLang } = useUiLang()
const { t: regT } = useMultiDictionary(['page_register', 'api_message', 'api_auth'], uiLang)
const { options: userTypeOptions } = useSortedDictionaryOptions(
  'register_user_type',
  uiLang,
  FALLBACK_USER_TYPES,
)

const userName = ref('')
const password = ref('')
const nickname = ref('')
const phone = ref('')
const verifyCode = ref('')
const userType = ref('2')
const showPassword = ref(false)
const agreed = ref(false)

const loading = ref(false)
const sendLoading = ref(false)
const countdown = ref(0)
/** @type {ReturnType<typeof setInterval> | null} */
let countdownTimer = null
const successMsg = ref('')

const fieldErrors = ref({
  userName: '',
  password: '',
  phone: '',
  verifyCode: '',
  userType: '',
  agreed: '',
})

function clearFieldError(key) {
  if (fieldErrors.value[key]) {
    fieldErrors.value = { ...fieldErrors.value, [key]: '' }
  }
}

function validateRegisterForm() {
  const fb = (key) => pageDictFallback('page_register', key, uiLang.value)
  const next = {
    userName: '',
    password: '',
    phone: '',
    verifyCode: '',
    userType: '',
    agreed: '',
  }
  let ok = true
  if (!agreed.value) {
    next.agreed = regT('page_register', 'err_must_agree', fb('err_must_agree'))
    ok = false
  }
  if (!userName.value.trim()) {
    next.userName = regT('page_register', 'err_username_required', fb('err_username_required'))
    ok = false
  }
  if (!password.value) {
    next.password = regT('page_register', 'err_password_required', fb('err_password_required'))
    ok = false
  }
  const p = phone.value.trim()
  if (!p) {
    next.phone = regT('page_register', 'err_phone_required', fb('err_phone_required'))
    ok = false
  } else if (!/^1\d{10}$/.test(p)) {
    next.phone = regT('page_register', 'err_phone_invalid', fb('err_phone_invalid'))
    ok = false
  }
  const code = verifyCode.value.trim()
  if (!code) {
    next.verifyCode = regT('page_register', 'err_verify_required', fb('err_verify_required'))
    ok = false
  } else if (!/^\d{6}$/.test(code)) {
    next.verifyCode = regT('page_register', 'err_verify_format', fb('err_verify_format'))
    ok = false
  }
  if (!userType.value || !String(userType.value).trim()) {
    next.userType = regT('page_register', 'err_user_type_required', fb('err_user_type_required'))
    ok = false
  }
  fieldErrors.value = next
  return ok
}

const passwordInputType = computed(() => (showPassword.value ? 'text' : 'password'))

/** 显式依赖 uiLang，保证切换语言后标题/标签等与字典一并刷新 */
const reg = computed(() => {
  void uiLang.value
  const g = (k, fb) => regT('page_register', k, fb)
  return {
    titleSuccess: g('title_success', '提交成功'),
    titleCreate: g('title_create', '创建账号'),
    labelUsername: g('label_username', '用户名'),
    labelPassword: g('label_password', '密码'),
    labelPhone: g('label_phone', '手机号'),
    labelRole: g('label_role', '身份'),
    labelNickname: g('label_nickname', '昵称（选填）'),
    termsPrefix: g('terms_prefix', '我已阅读并同意'),
    termsLinkTos: g('terms_link_tos', '《服务条款》'),
    termsJoin: g('terms_join', '与'),
    termsLinkPrivacy: g('terms_link_privacy', '《隐私政策》'),
    msgSuccessDefault: g('msg_success_default', '提交成功'),
    errRegisterFailed: g('err_register_failed', '注册申请失败'),
    footerAbout: g('footer_about', '关于我们'),
    footerContact: g('footer_contact', '联系我们'),
    footerLicense: g('footer_license', '许可协议'),
    footerVersion: g('footer_version', '版本声明'),
    footerPrivacy: g('footer_privacy', '隐私政策'),
    footerHelp: g('footer_help', '帮助中心'),
    visualNotifyPill: g('visual_notify_pill', '邮件通知'),
    visualNodePlaced: g('visual_node_placed', '已下单'),
    visualNodePickup: g('visual_node_pickup', '已揽收'),
    visualNodeTransit: g('visual_node_transit', '运输中'),
    visualNodeDelivered: g('visual_node_delivered', '已签收'),
    visualDcBadge: g('visual_dc_badge', '已签收'),
    visualRightTitle: g('visual_right_title', '关键轨迹，心中有数'),
    visualRightLead: g(
      'visual_right_lead',
      '提交注册申请并通过审核后，即可使用统一账号查询与协同：多承运商轨迹整合、节点状态清晰可追踪。',
    ),
  }
})

const submitLabel = computed(() =>
  loading.value
    ? regT('page_register', 'btn_submit_loading', '提交中…')
    : regT('page_register', 'btn_submit', '创建'),
)

const ariaTogglePw = computed(() =>
  showPassword.value
    ? regT('page_register', 'aria_hide_password', '隐藏密码')
    : regT('page_register', 'aria_show_password', '显示密码'),
)

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
})

function startCaptchaCountdown() {
  countdown.value = 60
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  countdownTimer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0 && countdownTimer) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }, 1000)
}

async function sendCaptcha() {
  const p = phone.value.trim()
  const fb = (key) => pageDictFallback('page_register', key, uiLang.value)
  if (!p) {
    fieldErrors.value = {
      ...fieldErrors.value,
      phone: regT('page_register', 'err_phone_required', fb('err_phone_required')),
    }
    return
  }
  if (!/^1\d{10}$/.test(p)) {
    fieldErrors.value = {
      ...fieldErrors.value,
      phone: regT('page_register', 'err_phone_invalid', fb('err_phone_invalid')),
    }
    return
  }
  fieldErrors.value = { ...fieldErrors.value, phone: '' }
  if (countdown.value > 0 || sendLoading.value) {
    return
  }
  sendLoading.value = true
  try {
    const res = await sendRegisterCaptcha({ phone: p })
    if (res.code !== 200) {
      const raw =
        res.message && String(res.message).trim()
          ? String(res.message).trim()
          : 'register_captcha_send_failed'
      throw new Error(raw)
    }
    startCaptchaCountdown()
    if (res.data && typeof res.data === 'object' && res.data !== null && 'debugCode' in res.data) {
      // 仅后端开启 debug-return 时存在，便于本地联调
      console.info('[dev] register captcha debugCode:', res.data.debugCode)
    }
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'register_captcha_send_failed'
    showToast(translateApiMessage(raw, regT, uiLang.value), { type: 'error' })
  } finally {
    sendLoading.value = false
  }
}

function pickSuccessText(res) {
  if (typeof res.data === 'string' && res.data.trim()) {
    return translateApiMessage(res.data.trim(), regT, uiLang.value)
  }
  if (res.message && String(res.message).trim()) {
    return translateApiMessage(String(res.message).trim(), regT, uiLang.value)
  }
  return reg.value.msgSuccessDefault
}

async function onSubmit() {
  successMsg.value = ''
  if (!validateRegisterForm()) {
    return
  }
  loading.value = true
  try {
    const body = {
      userName: userName.value.trim(),
      password: password.value,
      phone: phone.value.trim(),
      verifyCode: verifyCode.value.trim(),
      userType: userType.value,
    }
    const nick = nickname.value.trim()
    if (nick) {
      body.nickname = nick
    }
    const res = await registerApi(body)
    if (res.code !== 200) {
      const raw =
        res.message && String(res.message).trim() ? String(res.message).trim() : 'register_failed'
      throw new Error(raw)
    }
    successMsg.value = pickSuccessText(res)
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'register_failed'
    showToast(translateApiMessage(raw, regT, uiLang.value), { type: 'error' })
  } finally {
    loading.value = false
  }
}

function goLogin() {
  router.push('/login')
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
        <template v-if="successMsg">
          <h1 class="page-title">{{ reg.titleSuccess }}</h1>
          <div class="success-block" role="status">
            <p class="success-text">{{ successMsg }}</p>
            <div class="success-actions">
              <button type="button" class="btn-secondary" @click="router.push('/')">{{
                regT('page_register', 'btn_back_home', '返回首页')
              }}</button>
              <button type="button" class="btn-submit" @click="goLogin">{{
                regT('page_register', 'btn_go_login', '去登录')
              }}</button>
            </div>
          </div>
        </template>

        <template v-else>
          <h1 class="page-title">{{ reg.titleCreate }}</h1>

          <form class="form" novalidate @submit.prevent="onSubmit">
            <label class="field">
              <span class="label">{{ reg.labelUsername }}</span>
              <input
                v-model="userName"
                class="input"
                :class="{ 'input--error': fieldErrors.userName }"
                type="text"
                autocomplete="username"
                :aria-invalid="fieldErrors.userName ? 'true' : 'false'"
                @input="clearFieldError('userName')"
              />
              <p v-if="fieldErrors.userName" class="field-error">{{ fieldErrors.userName }}</p>
            </label>

            <label class="field">
              <span class="label">{{ reg.labelPassword }}</span>
              <div class="password-wrap">
                <input
                  v-model="password"
                  class="input input-password"
                  :class="{ 'input--error': fieldErrors.password }"
                  :type="passwordInputType"
                  autocomplete="new-password"
                  :aria-invalid="fieldErrors.password ? 'true' : 'false'"
                  @input="clearFieldError('password')"
                />
                <button
                  type="button"
                  class="toggle-pw"
                  tabindex="-1"
                  :aria-label="ariaTogglePw"
                  @click="showPassword = !showPassword"
                >
                  <svg
                    v-if="!showPassword"
                    class="toggle-pw-icon"
                    viewBox="0 0 24 24"
                    aria-hidden="true"
                  >
                    <path
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"
                    />
                    <circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="2" />
                  </svg>
                  <svg v-else class="toggle-pw-icon" viewBox="0 0 24 24" aria-hidden="true">
                    <path
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"
                    />
                    <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                  </svg>
                </button>
              </div>
              <p v-if="fieldErrors.password" class="field-error">{{ fieldErrors.password }}</p>
            </label>

            <label class="field">
              <span class="label">{{ reg.labelPhone }}</span>
              <div class="phone-row">
                <input
                  v-model="phone"
                  class="input input-phone"
                  :class="{ 'input--error': fieldErrors.phone }"
                  type="tel"
                  inputmode="numeric"
                  maxlength="11"
                  autocomplete="tel"
                  :aria-invalid="fieldErrors.phone ? 'true' : 'false'"
                  @input="clearFieldError('phone')"
                />
                <button
                  type="button"
                  class="btn-code"
                  :disabled="sendLoading || countdown > 0"
                  @click="sendCaptcha"
                >
                  {{
                    countdown > 0
                      ? regT('page_register', 'btn_send_code_wait', '{n}秒后可重发').replace(
                          '{n}',
                          String(countdown),
                        )
                      : regT('page_register', 'btn_send_code', '获取验证码')
                  }}
                </button>
              </div>
              <p v-if="fieldErrors.phone" class="field-error">{{ fieldErrors.phone }}</p>
            </label>

            <label class="field">
              <span class="label">{{
                regT('page_register', 'label_verify_code', '验证码')
              }}</span>
              <input
                v-model="verifyCode"
                class="input"
                :class="{ 'input--error': fieldErrors.verifyCode }"
                type="text"
                inputmode="numeric"
                maxlength="6"
                autocomplete="one-time-code"
                :aria-invalid="fieldErrors.verifyCode ? 'true' : 'false'"
                @input="clearFieldError('verifyCode')"
              />
              <p v-if="fieldErrors.verifyCode" class="field-error">{{ fieldErrors.verifyCode }}</p>
            </label>

            <label class="field">
              <span class="label">{{ reg.labelRole }}</span>
              <select
                v-model="userType"
                class="input select"
                :class="{ 'input--error': fieldErrors.userType }"
                :aria-invalid="fieldErrors.userType ? 'true' : 'false'"
                @change="clearFieldError('userType')"
              >
                <option v-for="opt in userTypeOptions" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </option>
              </select>
              <p v-if="fieldErrors.userType" class="field-error">{{ fieldErrors.userType }}</p>
            </label>

            <label class="field">
              <span class="label">{{ reg.labelNickname }}</span>
              <input v-model="nickname" class="input" type="text" autocomplete="nickname" />
            </label>

            <div class="terms-block">
              <label class="terms-row">
                <input
                  v-model="agreed"
                  type="checkbox"
                  class="terms-check"
                  :aria-invalid="fieldErrors.agreed ? 'true' : 'false'"
                  @change="clearFieldError('agreed')"
                />
                <span class="terms-text">
                  {{ reg.termsPrefix }}
                  <a href="#" @click.prevent>{{ reg.termsLinkTos }}</a>
                  {{ reg.termsJoin }}
                  <a href="#" @click.prevent>{{ reg.termsLinkPrivacy }}</a>
                </span>
              </label>
              <p v-if="fieldErrors.agreed" class="field-error terms-error">{{ fieldErrors.agreed }}</p>
            </div>

            <button type="submit" class="btn-submit" :disabled="loading">
              {{ submitLabel }}
            </button>
          </form>

          <p class="login-hint">
            {{ regT('page_register', 'hint_has_account', '已有账号？') }}
            <RouterLink to="/login" class="link-accent">{{
              regT('page_register', 'hint_go_login', '去登录')
            }}</RouterLink>
          </p>
        </template>
      </div>

      <footer class="left-footer">
        <a href="#">{{ reg.footerAbout }}</a>
        <span class="sep">|</span>
        <a href="#">{{ reg.footerContact }}</a>
        <span class="sep">|</span>
        <a href="#">{{ reg.footerLicense }}</a>
        <span class="sep">|</span>
        <a href="#">{{ reg.footerVersion }}</a>
        <span class="sep">|</span>
        <a href="#">{{ reg.footerPrivacy }}</a>
        <span class="sep">|</span>
        <a href="#">{{ reg.footerHelp }}</a>
      </footer>
    </div>

    <div class="panel-right" aria-hidden="true">
      <div class="right-grid" />
      <div class="right-glow" />

      <div class="reg-visual">
        <div class="notify-pill">{{ reg.visualNotifyPill }}</div>
        <div class="track-board">
          <div class="track-rail">
            <span class="node">{{ reg.visualNodePlaced }}</span>
            <span class="node">{{ reg.visualNodePickup }}</span>
            <span class="node">{{ reg.visualNodeTransit }}</span>
            <span class="node active">{{ reg.visualNodeDelivered }}</span>
          </div>
          <div class="delivered-card">
            <span class="dc-badge">{{ reg.visualDcBadge }}</span>
            <div class="dc-lines"><span /><span /></div>
          </div>
        </div>
        <div class="side-tools">
          <span class="tool-sq" /><span class="tool-sq" /><span class="tool-sq" /><span class="tool-sq" />
        </div>
      </div>

      <div class="right-copy">
        <h2 class="right-title">{{ reg.visualRightTitle }}</h2>
        <p class="right-lead">
          {{ reg.visualRightLead }}
        </p>
      </div>
      <div class="carousel-dots">
        <span class="dot" /><span class="dot active" />
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
  margin-bottom: 40px;
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

.page-title {
  margin: 0 0 26px;
  font-size: 26px;
  font-weight: 700;
  color: var(--17-ink);
  letter-spacing: -0.02em;
  text-align: center;
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

.select {
  cursor: pointer;
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

.terms-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
  text-align: left;
}

.terms-error {
  padding-left: 26px;
}

.input-password {
  padding-right: 40px;
}

.phone-row {
  display: flex;
  gap: 8px;
  align-items: stretch;
  width: 100%;
}

.input-phone {
  flex: 1;
  min-width: 0;
}

.btn-code {
  flex-shrink: 0;
  border: 1px solid var(--17-border);
  border-radius: 4px;
  padding: 7px 10px;
  font-size: 12px;
  font-weight: 700;
  color: var(--17-link);
  background: var(--17-card-elevated);
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 0.15s, border-color 0.15s;
}

.btn-code:hover:not(:disabled) {
  border-color: var(--17-teal);
}

.btn-code:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.password-wrap {
  position: relative;
  display: block;
  width: 100%;
}

.toggle-pw {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  color: var(--17-muted);
  cursor: pointer;
  padding: 0;
  border-radius: 0 4px 4px 0;
}

.toggle-pw:hover {
  color: var(--17-link);
  background: rgba(61, 141, 255, 0.1);
}

.toggle-pw-icon {
  width: 20px;
  height: 20px;
  display: block;
  flex-shrink: 0;
}

.terms-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-top: 2px;
  cursor: pointer;
  text-align: left;
}

.terms-check {
  margin-top: 3px;
  flex-shrink: 0;
  width: 16px;
  height: 16px;
  accent-color: #256fea;
}

.terms-text {
  font-size: 12px;
  line-height: 1.55;
  color: var(--17-muted);
}

.terms-text a {
  color: var(--17-link);
  font-weight: 600;
  text-decoration: none;
}

.terms-text a:hover {
  text-decoration: underline;
}

.btn-submit {
  width: 100%;
  margin-top: 6px;
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

.btn-secondary {
  border: 1px solid var(--17-border);
  border-radius: 4px;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 600;
  color: var(--17-ink);
  background: var(--17-card-elevated);
  cursor: pointer;
}

.btn-secondary:hover {
  border-color: #3d4f66;
}

.success-block {
  text-align: center;
}

.success-text {
  margin: 0 0 22px;
  font-size: 15px;
  font-weight: 600;
  color: var(--17-link);
  line-height: 1.55;
}

.success-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.login-hint {
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

.panel-right {
  display: none;
  flex: 1;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: radial-gradient(ellipse 90% 70% at 42% 42%, #1e3a6e 0%, #0d1f45 42%, #060f24 100%);
}

@media (min-width: 960px) {
  .panel-right {
    display: block;
  }

  .panel-left {
    flex: 0 0 52%;
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
  width: 85%;
  height: 55%;
  left: 8%;
  top: 16%;
  background: radial-gradient(circle, rgba(100, 160, 255, 0.2) 0%, transparent 62%);
  pointer-events: none;
}

.reg-visual {
  position: absolute;
  left: 50%;
  top: 18%;
  transform: translateX(-50%);
  width: min(420px, 88%);
}

.notify-pill {
  position: absolute;
  left: 0;
  top: 48px;
  font-size: 11px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.92);
  padding: 8px 12px;
  border-radius: 8px;
  background: rgba(25, 60, 130, 0.75);
  border: 1px solid rgba(255, 255, 255, 0.14);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

.track-board {
  margin: 0 auto;
  padding: 20px 18px 18px;
  border-radius: 12px;
  background: rgba(8, 24, 56, 0.75);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(8px);
}

.track-rail {
  display: flex;
  justify-content: space-between;
  gap: 6px;
  margin-bottom: 16px;
  padding: 0 4px;
  position: relative;
}

.track-rail::before {
  content: '';
  position: absolute;
  left: 8%;
  right: 8%;
  top: 50%;
  height: 2px;
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-50%);
  z-index: 0;
}

.node {
  position: relative;
  z-index: 1;
  font-size: 10px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.55);
  text-align: center;
  max-width: 22%;
  line-height: 1.25;
}

.node::before {
  content: '';
  display: block;
  width: 10px;
  height: 10px;
  margin: 0 auto 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.35);
}

.node.active {
  color: #fff;
}

.node.active::before {
  background: #22c55e;
  border-color: #4ade80;
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.35);
}

.delivered-card {
  padding: 14px 16px;
  border-radius: 8px;
  background: rgba(20, 83, 45, 0.45);
  border: 1px solid rgba(74, 222, 128, 0.35);
}

.dc-badge {
  display: inline-block;
  font-size: 13px;
  font-weight: 800;
  color: #bbf7d0;
  margin-bottom: 10px;
}

.dc-lines {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.dc-lines span {
  height: 6px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.12);
}

.dc-lines span:nth-child(1) {
  width: 88%;
}

.dc-lines span:nth-child(2) {
  width: 62%;
}

.side-tools {
  position: absolute;
  right: -8px;
  top: 120px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tool-sq {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: rgba(37, 99, 235, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.right-copy {
  position: absolute;
  left: 12%;
  right: 12%;
  bottom: 14%;
}

.right-title {
  margin: 0 0 10px;
  font-size: clamp(1.2rem, 2.1vw, 1.55rem);
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.02em;
}

.right-lead {
  margin: 0;
  font-size: 13px;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.78);
  max-width: 440px;
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
