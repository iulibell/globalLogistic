<script setup>
import { computed, onMounted, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { useSortedDictionaryOptions } from '@/composables/useSortedDictionaryOptions.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import {
  fetchRegisterApplicationPage,
  postAccessRegister,
  postRejectRegister,
} from '@/api/reviewer.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const FALLBACK_USER_TYPES = [
  { value: '1', label: '超级管理员' },
  { value: '2', label: '管理员' },
  { value: '3', label: '仓库管理员' },
  { value: '4', label: '司机' },
  { value: '5', label: '审核员' },
]

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message', 'api_auth'], uiLang)
const { options: userTypeOptions } = useSortedDictionaryOptions(
  'register_user_type',
  uiLang,
  FALLBACK_USER_TYPES,
)

const sectionTitle = computed(() =>
  t(
    'page_profile',
    'section_register_application',
    pageDictFallback('page_profile', 'section_register_application', uiLang.value),
  ),
)
const lead = computed(() =>
  t('page_profile', 'lead_register_application', pageDictFallback('page_profile', 'lead_register_application', uiLang.value)),
)

const colUsername = computed(() => t('page_profile', 'col_username', '用户名'))
const colNickname = computed(() => t('page_profile', 'col_nickname', '昵称'))
const colPhone = computed(() => t('page_profile', 'col_phone', '手机号'))
const colUserType = computed(() => t('page_profile', 'col_user_type', '申请身份'))
const colCity = computed(() => t('page_profile', 'col_city', '所在城市'))
const colStatus = computed(() => t('page_profile', 'col_status', '状态'))

const emptyList = computed(() =>
  t('page_profile', 'register_apps_empty', pageDictFallback('page_profile', 'register_apps_empty', uiLang.value)),
)

const loadError = computed(() =>
  t('page_profile', 'register_apps_load_error', pageDictFallback('page_profile', 'register_apps_load_error', uiLang.value)),
)

const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)

const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)

const colActions = computed(() =>
  t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value)),
)
const btnApprove = computed(() =>
  t('page_profile', 'btn_approve', pageDictFallback('page_profile', 'btn_approve', uiLang.value)),
)
const btnReject = computed(() =>
  t('page_profile', 'btn_reject', pageDictFallback('page_profile', 'btn_reject', uiLang.value)),
)

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
/** 正在处理的手机号，用于行内禁用 */
const actingPhone = ref(null)
const assignedKeeperCity = ref({})

function isPending(row) {
  const s = row?.status
  return s === 0 || s === '0'
}

/** @param {Record<string, unknown>} row RegisterApplication */
function buildRegisterBody(row) {
  const ut = row.userType != null ? String(row.userType) : '2'
  const nick =
    row.nickname != null && String(row.nickname).trim()
      ? String(row.nickname).trim()
      : String(row.username || '').trim()
  const rowCity = row.city != null ? String(row.city).trim() : ''
  const city =
    ut === '3'
      ? String(assignedKeeperCity.value[row.phone] || rowCity).trim()
      : rowCity
  return {
    username: String(row.username || '').trim(),
    password: String(row.password || ''),
    nickname: nick,
    userType: ut,
    phone: String(row.phone || '').trim(),
    city,
  }
}

async function loadList() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchRegisterApplicationPage(1, 50)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const data = res.data
    rows.value = Array.isArray(data) ? data : []
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    errorMsg.value = translateApiMessage(raw, t, uiLang.value)
  } finally {
    loading.value = false
  }
}

async function onApprove(row) {
  if (!isPending(row) || actingPhone.value) return
  const ut = row.userType != null ? String(row.userType) : ''
  const city = String(assignedKeeperCity.value[row.phone] || row.city || '').trim()
  if ((ut === '3' || ut === '4') && !city) {
    showToast(
      t('page_profile', 'register_apps_city_required', pageDictFallback('page_profile', 'register_apps_city_required', uiLang.value) || '仓管/司机审批前请填写所在城市'),
      { type: 'warning' },
    )
    return
  }
  actingPhone.value = row.phone
  try {
    const res = await postAccessRegister(buildRegisterBody(row))
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const msg = translateApiMessage(
      res.message && String(res.message).trim() ? String(res.message) : 'reviewer_register_approved',
      t,
      uiLang.value,
    )
    showToast(msg, { type: 'success' })
    await loadList()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    actingPhone.value = null
  }
}

async function onReject(row) {
  if (!isPending(row) || actingPhone.value) return
  actingPhone.value = row.phone
  try {
    const res = await postRejectRegister(buildRegisterBody(row))
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const msg = translateApiMessage(
      res.message && String(res.message).trim() ? String(res.message) : 'reviewer_register_rejected',
      t,
      uiLang.value,
    )
    showToast(msg, { type: 'success' })
    await loadList()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    actingPhone.value = null
  }
}

function userTypeLabel(ut) {
  if (ut == null) return valueEmpty.value
  const key = String(ut)
  return userTypeOptions.value.find((o) => o.value === key)?.label || key
}

function statusLabel(s) {
  const key =
    s === 0 || s === '0'
      ? 'status_pending'
      : s === 1 || s === '1'
        ? 'status_approved'
        : s === 2 || s === '2'
          ? 'status_rejected'
          : null
  if (!key) return String(s ?? valueEmpty.value)
  return t(
    'page_profile',
    key,
    pageDictFallback('page_profile', key, uiLang.value),
  )
}

onMounted(() => loadList())
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <h1 class="main-title">{{ sectionTitle }}</h1>
      <p class="main-lead">{{ lead }}</p>
    </header>

    <div class="panel">
      <p v-if="loading" class="state-msg">{{ loadingText }}</p>
      <p v-else-if="errorMsg" class="state-msg state-msg--err">{{ loadError }}：{{ errorMsg }}</p>
      <p v-else-if="!rows.length" class="state-msg">{{ emptyList }}</p>
      <div v-else class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>{{ colUsername }}</th>
              <th>{{ colNickname }}</th>
              <th>{{ colPhone }}</th>
              <th>{{ colUserType }}</th>
              <th>{{ colCity }}</th>
              <th>{{ colStatus }}</th>
              <th class="th-actions">{{ colActions }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id ?? row.phone + row.username">
              <td>{{ row.username || valueEmpty }}</td>
              <td>{{ (row.nickname && String(row.nickname).trim()) || row.username || valueEmpty }}</td>
              <td>{{ row.phone || valueEmpty }}</td>
              <td>{{ userTypeLabel(row.userType) }}</td>
              <td>
                <template v-if="isPending(row) && String(row.userType) === '3'">
                  <input
                    v-model="assignedKeeperCity[row.phone]"
                    class="city-input"
                    type="text"
                    :placeholder="String(row.city || '').trim() || '例如：上海'"
                  />
                </template>
                <template v-else>
                  {{ (row.city && String(row.city).trim()) || valueEmpty }}
                </template>
              </td>
              <td>{{ statusLabel(row.status) }}</td>
              <td class="td-actions">
                <template v-if="isPending(row)">
                  <button
                    type="button"
                    class="btn-action btn-action--ok"
                    :disabled="actingPhone === row.phone"
                    @click="onApprove(row)"
                  >
                    {{ btnApprove }}
                  </button>
                  <button
                    type="button"
                    class="btn-action btn-action--danger"
                    :disabled="actingPhone === row.phone"
                    @click="onReject(row)"
                  >
                    {{ btnReject }}
                  </button>
                </template>
                <span v-else class="actions-placeholder">—</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
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
  margin: 0 0 6px;
  font-size: clamp(13px, 1.1vw, 15px);
  line-height: 1.45;
  color: #7a8fa8;
}

.panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: min(360px, calc(100vh - 260px));
  border-radius: calc(var(--17-radius-lg) + 2px);
  padding: clamp(20px, 2.5vw, 32px) clamp(16px, 2.5vw, 28px);
  background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99));
  border: 1px solid rgba(61, 141, 255, 0.14);
  box-shadow:
    0 0 0 1px rgba(0, 0, 0, 0.4) inset,
    0 12px 40px rgba(0, 0, 0, 0.35);
}

.state-msg {
  margin: 0;
  padding: 24px 8px;
  text-align: center;
  font-size: 14px;
  color: #8b9cb4;
}

.state-msg--err {
  color: #f0a8a8;
}

.table-wrap {
  width: 100%;
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table th,
.data-table td {
  padding: 12px 14px;
  text-align: left;
  border-bottom: 1px solid rgba(36, 48, 68, 0.75);
}

.data-table th {
  font-weight: 600;
  color: #8fa3bc;
  white-space: nowrap;
}

.data-table td {
  color: #e8eef6;
}

.data-table tbody tr:hover td {
  background: rgba(61, 141, 255, 0.06);
}

.th-actions,
.td-actions {
  text-align: right;
  white-space: nowrap;
}

.td-actions {
  vertical-align: middle;
}

.btn-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 8px;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 700;
  font-family: inherit;
  border-radius: 6px;
  border: 1px solid transparent;
  cursor: pointer;
  transition:
    opacity 0.15s,
    filter 0.15s;
}

.btn-action:first-child {
  margin-left: 0;
}

.btn-action:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-action--ok {
  color: #e8fff4;
  background: linear-gradient(180deg, #2ebc7a, #1e9c62);
  border-color: rgba(46, 188, 122, 0.5);
}

.btn-action--ok:hover:not(:disabled) {
  filter: brightness(1.08);
}

.btn-action--danger {
  color: #ffeef0;
  background: rgba(220, 80, 80, 0.25);
  border-color: rgba(220, 100, 100, 0.45);
}

.btn-action--danger:hover:not(:disabled) {
  background: rgba(220, 80, 80, 0.4);
}

.actions-placeholder {
  color: #5c6d82;
  font-size: 13px;
}

.city-input {
  width: 120px;
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid rgba(61, 141, 255, 0.35);
  background: rgba(12, 18, 30, 0.9);
  color: #e8eef6;
  font-size: 12px;
}

@media (max-width: 768px) {
  .data-table th,
  .data-table td {
    padding: 10px 8px;
    font-size: 13px;
  }
}
</style>
