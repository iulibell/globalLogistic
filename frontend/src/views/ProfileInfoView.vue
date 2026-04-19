<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthSession } from '@/composables/useAuthSession.js'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { useSortedDictionaryOptions } from '@/composables/useSortedDictionaryOptions.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'

const FALLBACK_ROLES = [
  { value: 'super', label: '超级管理员' },
  { value: 'manager', label: '管理员' },
  { value: 'keeper', label: '仓管' },
  { value: 'driver', label: '司机' },
  { value: 'reviewer', label: '审核员' },
]

const router = useRouter()
const { isLoggedIn, profile } = useAuthSession()
const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile'], uiLang)
const { options: roleOptions } = useSortedDictionaryOptions('login_role_option', uiLang, FALLBACK_ROLES)

const title = computed(() => t('page_profile', 'title', '个人中心'))
const sectionAccount = computed(() => t('page_profile', 'section_account', '账户信息'))
const labelUsername = computed(() => t('page_profile', 'label_username', '用户名'))
const labelNickname = computed(() => t('page_profile', 'label_nickname', '昵称'))
const labelRole = computed(() => t('page_profile', 'label_role', '角色'))
const hintNotLoggedIn = computed(() => t('page_profile', 'hint_not_logged_in', '未登录，无法查看个人中心。'))
const btnGoLogin = computed(() => t('page_profile', 'btn_go_login', '去登录'))

const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)

const displayName = computed(() => {
  const p = profile.value
  if (!p) return ''
  const nick = p.nickname != null && String(p.nickname).trim() ? String(p.nickname).trim() : ''
  if (nick) return nick
  return p.username != null && String(p.username).trim() ? String(p.username).trim() : ''
})

const userRoleLabel = computed(() => {
  const r = profile.value?.role
  if (!r) return ''
  return roleOptions.value.find((o) => o.value === r)?.label || String(r)
})

const sidebarWelcome = computed(() => {
  if (!isLoggedIn.value) {
    return title.value
  }
  const name = displayName.value || valueEmpty.value
  const role = userRoleLabel.value
  const rolePart = role ? ` ${role}` : ''
  const tpl = t(
    'page_profile',
    'sidebar_welcome',
    pageDictFallback('page_profile', 'sidebar_welcome', uiLang.value),
  )
  return tpl.replace('{name}', name).replace('{role}', rolePart)
})

function goLogin() {
  router.push({ name: 'login', query: { redirect: '/profile' } })
}
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <h1 class="main-title">{{ sectionAccount }}</h1>
      <p class="main-lead">{{ isLoggedIn ? sidebarWelcome : title }}</p>
    </header>

    <div v-if="!isLoggedIn" class="panel panel--empty">
      <p class="hint">{{ hintNotLoggedIn }}</p>
      <button type="button" class="btn-primary" @click="goLogin">{{ btnGoLogin }}</button>
    </div>

    <div v-else class="panel">
      <dl class="kv">
        <div class="kv-row">
          <dt>{{ labelUsername }}</dt>
          <dd>{{ profile?.username || valueEmpty }}</dd>
        </div>
        <div class="kv-row">
          <dt>{{ labelNickname }}</dt>
          <dd>{{ (profile?.nickname && String(profile.nickname).trim()) || valueEmpty }}</dd>
        </div>
        <div class="kv-row">
          <dt>{{ labelRole }}</dt>
          <dd>{{ userRoleLabel || valueEmpty }}</dd>
        </div>
      </dl>
    </div>
  </div>
</template>

<style scoped>
.main-inner {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: min(1100px, 100%);
  margin: 0 auto;
}

.main-head {
  margin-bottom: clamp(20px, 2.5vw, 28px);
  flex-shrink: 0;
}

.main-title {
  margin: 0 0 8px;
  font-size: clamp(22px, 2.2vw, 28px);
  font-weight: 700;
  color: #e8eef6;
  letter-spacing: -0.02em;
}

.main-lead {
  margin: 0;
  font-size: clamp(13px, 1.1vw, 15px);
  line-height: 1.45;
  color: #7a8fa8;
}

.panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: min(420px, calc(100vh - 220px));
  border-radius: calc(var(--17-radius-lg) + 2px);
  padding: clamp(24px, 3vw, 40px) clamp(24px, 3.5vw, 44px);
  background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99));
  border: 1px solid rgba(61, 141, 255, 0.14);
  box-shadow:
    0 0 0 1px rgba(0, 0, 0, 0.4) inset,
    0 12px 40px rgba(0, 0, 0, 0.35),
    0 0 80px rgba(37, 111, 234, 0.06);
}

.panel--empty {
  text-align: center;
  padding: clamp(36px, 5vw, 56px) clamp(24px, 4vw, 40px);
  justify-content: center;
}

.hint {
  margin: 0 0 18px;
  font-size: 14px;
  color: #8b9cb4;
  line-height: 1.55;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 26px;
  font-size: 14px;
  font-weight: 700;
  font-family: inherit;
  color: #fff;
  background: linear-gradient(180deg, #4a9dff, #256fea);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(37, 111, 234, 0.35);
  transition:
    filter 0.15s,
    transform 0.15s;
}

.btn-primary:hover {
  filter: brightness(1.06);
  transform: translateY(-1px);
}

.kv {
  margin: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.kv-row {
  display: grid;
  grid-template-columns: minmax(112px, 200px) minmax(0, 1fr);
  gap: 12px 28px;
  align-items: baseline;
  padding: clamp(16px, 2vw, 22px) 0;
  border-bottom: 1px solid rgba(36, 48, 68, 0.75);
  font-size: clamp(14px, 1.15vw, 16px);
}

.kv-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.kv-row:first-child {
  padding-top: 0;
}

.kv-row dt {
  margin: 0;
  font-weight: 600;
  color: #8fa3bc;
}

.kv-row dd {
  margin: 0;
  color: #f0f5fc;
  font-weight: 500;
  word-break: break-word;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .panel {
    min-height: 280px;
  }

  .kv-row {
    grid-template-columns: 1fr;
    gap: 6px;
    padding: 14px 0;
  }
}
</style>
