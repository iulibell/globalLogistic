<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { useSortedDictionaryOptions } from '@/composables/useSortedDictionaryOptions.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import {
  fetchSuperUserList,
  fetchSuperUserByUserType,
  fetchSuperUserByUserId,
  postUpdateSysUser,
  postDeleteSysUser,
} from '@/api/super.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const FALLBACK_USER_TYPES = [
  { value: '1', label: '超级管理员' },
  { value: '2', label: '管理员' },
  { value: '3', label: '仓库管理员' },
  { value: '4', label: '司机' },
  { value: '5', label: '审核员' },
]

const route = useRoute()
const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message', 'api_auth'], uiLang)
const { options: userTypeOptions } = useSortedDictionaryOptions(
  'register_user_type',
  uiLang,
  FALLBACK_USER_TYPES,
)

const mode = computed(() => {
  const n = route.name
  if (n === 'profile-super-by-type') return 'byType'
  if (n === 'profile-super-by-id') return 'byId'
  return 'list'
})

const sectionTitle = computed(() => {
  const key =
    mode.value === 'list'
      ? 'section_super_users'
      : mode.value === 'byType'
        ? 'section_super_by_type'
        : 'section_super_by_id'
  return t('page_profile', key, pageDictFallback('page_profile', key, uiLang.value))
})

const lead = computed(() => {
  const key =
    mode.value === 'list'
      ? 'lead_super_users'
      : mode.value === 'byType'
        ? 'lead_super_by_type'
        : 'lead_super_by_id'
  return t('page_profile', key, pageDictFallback('page_profile', key, uiLang.value))
})

const colSysUserId = computed(() =>
  t('page_profile', 'col_sys_user_id', pageDictFallback('page_profile', 'col_sys_user_id', uiLang.value)),
)
const colUsername = computed(() => t('page_profile', 'col_username', pageDictFallback('page_profile', 'col_username', uiLang.value)))
const colNickname = computed(() => t('page_profile', 'col_nickname', pageDictFallback('page_profile', 'col_nickname', uiLang.value)))
const colUserTypeCol = computed(() =>
  t('page_profile', 'super_col_user_type', pageDictFallback('page_profile', 'super_col_user_type', uiLang.value)),
)
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value)))

const btnUpdate = computed(() =>
  t('page_profile', 'btn_update_user', pageDictFallback('page_profile', 'btn_update_user', uiLang.value)),
)
const btnDelete = computed(() =>
  t('page_profile', 'btn_delete_user', pageDictFallback('page_profile', 'btn_delete_user', uiLang.value)),
)
const btnSearch = computed(() =>
  t('page_profile', 'super_btn_search', pageDictFallback('page_profile', 'super_btn_search', uiLang.value)),
)
const btnPrev = computed(() =>
  t('page_profile', 'super_btn_prev', pageDictFallback('page_profile', 'super_btn_prev', uiLang.value)),
)
const btnNext = computed(() =>
  t('page_profile', 'super_btn_next', pageDictFallback('page_profile', 'super_btn_next', uiLang.value)),
)
const labelFilterUserType = computed(() =>
  t('page_profile', 'super_filter_user_type', pageDictFallback('page_profile', 'super_filter_user_type', uiLang.value)),
)
const labelFilterUserId = computed(() =>
  t('page_profile', 'super_filter_user_id', pageDictFallback('page_profile', 'super_filter_user_id', uiLang.value)),
)
const dialogTitle = computed(() =>
  t('page_profile', 'dialog_edit_user_title', pageDictFallback('page_profile', 'dialog_edit_user_title', uiLang.value)),
)
const btnSave = computed(() => t('page_profile', 'btn_save', pageDictFallback('page_profile', 'btn_save', uiLang.value)))
const btnCancel = computed(() => t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value)))

const emptyList = computed(() =>
  t('page_profile', 'super_empty', pageDictFallback('page_profile', 'super_empty', uiLang.value)),
)
const loadError = computed(() =>
  t('page_profile', 'super_load_error', pageDictFallback('page_profile', 'super_load_error', uiLang.value)),
)
const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)
const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)
const byIdHint = computed(() =>
  t('page_profile', 'super_by_id_hint', pageDictFallback('page_profile', 'super_by_id_hint', uiLang.value)),
)
const byIdNotFound = computed(() =>
  t('page_profile', 'super_user_not_found', pageDictFallback('page_profile', 'super_user_not_found', uiLang.value)),
)

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const filterUserType = ref('1')
const idQuery = ref('')
const hasSearchedById = ref(false)

const editOpen = ref(false)
const editSaving = ref(false)
const editForm = ref({
  userId: '',
  username: '',
  userType: '1',
  nickname: '',
})

const actingUserId = ref(null)
const deleteConfirmOpen = ref(false)
const deleteCandidate = ref(null)

function rowKey(row, i) {
  const id = row?.userId ?? row?.id
  return id != null ? String(id) : `row-${i}`
}

function rowUserId(row) {
  const v = row?.userId ?? row?.id
  return v != null ? String(v) : ''
}

function userTypeLabel(ut) {
  const s = ut != null ? String(ut) : ''
  return userTypeOptions.value.find((o) => o.value === s)?.label || s || valueEmpty.value
}

/** 后端未命中或旧版空壳 DTO 时视为未找到 */
function isValidUserDto(d) {
  if (d == null || typeof d !== 'object') return false
  const uid = d.userId ?? d.id
  if (uid != null && String(uid).trim() !== '') return true
  if (d.username != null && String(d.username).trim() !== '') return true
  return false
}

function buildUpdatePayload() {
  const uid = editForm.value.userId
  let userId = uid
  if (typeof uid === 'string' && /^\d+$/.test(uid.trim())) {
    userId = Number(uid.trim())
  }
  return {
    userId,
    username: String(editForm.value.username || '').trim(),
    userType: String(editForm.value.userType || '1'),
    nickname: String(editForm.value.nickname || '').trim(),
  }
}

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    if (mode.value === 'list') {
      const res = await fetchSuperUserList(pageNum.value, pageSize.value)
      if (res.code !== 200) {
        throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
      }
      rows.value = Array.isArray(res.data) ? res.data : []
      return
    }
    if (mode.value === 'byType') {
      const res = await fetchSuperUserByUserType(pageNum.value, pageSize.value, filterUserType.value)
      if (res.code !== 200) {
        throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
      }
      rows.value = Array.isArray(res.data) ? res.data : []
      return
    }
    if (!idQuery.value.trim()) {
      rows.value = []
      hasSearchedById.value = false
      return
    }
    hasSearchedById.value = true
    const res = await fetchSuperUserByUserId(idQuery.value.trim())
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const d = res.data
    rows.value = isValidUserDto(d) ? [d] : []
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    errorMsg.value = translateApiMessage(raw, t, uiLang.value)
    rows.value = []
  } finally {
    loading.value = false
  }
}

function canGoNext() {
  return rows.value.length >= pageSize.value
}

function goPrev() {
  if (pageNum.value <= 1 || mode.value === 'byId') return
  pageNum.value -= 1
  load()
}

function goNext() {
  if (!canGoNext() || mode.value === 'byId') return
  pageNum.value += 1
  load()
}

function openEdit(row) {
  actingUserId.value = null
  editForm.value = {
    userId: rowUserId(row),
    username: row?.username != null ? String(row.username) : '',
    userType: row?.userType != null ? String(row.userType) : '1',
    nickname: row?.nickname != null ? String(row.nickname) : '',
  }
  editOpen.value = true
}

function closeEdit() {
  if (editSaving.value) return
  editOpen.value = false
}

async function saveEdit() {
  if (editSaving.value) return
  editSaving.value = true
  try {
    const res = await postUpdateSysUser(buildUpdatePayload())
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const msg = translateApiMessage(
      res.message && String(res.message).trim() ? String(res.message) : 'admin_user_updated',
      t,
      uiLang.value,
    )
    showToast(msg, { type: 'success' })
    editOpen.value = false
    await load()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    editSaving.value = false
  }
}

function confirmDeleteTextFor(userIdStr) {
  const tpl = t(
    'page_profile',
    'super_confirm_delete',
    pageDictFallback('page_profile', 'super_confirm_delete', uiLang.value),
  )
  return tpl.replace('{userId}', userIdStr)
}

async function onDelete(row) {
  const uid = rowUserId(row)
  if (!uid || actingUserId.value) return
  deleteCandidate.value = row
  deleteConfirmOpen.value = true
}

function closeDeleteConfirm() {
  if (actingUserId.value) return
  deleteConfirmOpen.value = false
  deleteCandidate.value = null
}

async function confirmDelete() {
  const row = deleteCandidate.value
  const uid = rowUserId(row)
  if (!uid || actingUserId.value) return
  actingUserId.value = uid
  try {
    const res = await postDeleteSysUser(uid)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const msg = translateApiMessage(
      res.message && String(res.message).trim() ? String(res.message) : 'admin_user_deleted',
      t,
      uiLang.value,
    )
    showToast(msg, { type: 'success' })
    await load()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    actingUserId.value = null
    deleteConfirmOpen.value = false
    deleteCandidate.value = null
  }
}

function onSearchById() {
  pageNum.value = 1
  load()
}

function onFilterTypeChange() {
  pageNum.value = 1
  load()
}

watch(
  () => route.name,
  () => {
    pageNum.value = 1
    errorMsg.value = ''
    idQuery.value = ''
    hasSearchedById.value = false
    rows.value = []
    load()
  },
  { immediate: true },
)
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <h1 class="main-title">{{ sectionTitle }}</h1>
      <p class="main-lead">{{ lead }}</p>
    </header>

    <div v-if="mode === 'byType'" class="toolbar">
      <label class="toolbar-label">{{ labelFilterUserType }}</label>
      <select v-model="filterUserType" class="toolbar-select" @change="onFilterTypeChange">
        <option v-for="opt in userTypeOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
      </select>
    </div>

    <div v-if="mode === 'byId'" class="toolbar toolbar--row">
      <label class="toolbar-label">{{ labelFilterUserId }}</label>
      <input
        v-model="idQuery"
        type="text"
        class="toolbar-input"
        autocomplete="off"
        @keyup.enter="onSearchById"
      />
      <button type="button" class="btn-toolbar" @click="onSearchById">{{ btnSearch }}</button>
    </div>

    <div class="panel">
      <p v-if="loading" class="state-msg">{{ loadingText }}</p>
      <p v-else-if="errorMsg" class="state-msg state-msg--err">{{ loadError }}：{{ errorMsg }}</p>
      <p v-else-if="mode === 'byId' && !hasSearchedById" class="state-msg">{{ byIdHint }}</p>
      <p v-else-if="mode === 'byId' && hasSearchedById && !rows.length" class="state-msg">{{ byIdNotFound }}</p>
      <p v-else-if="!rows.length" class="state-msg">{{ emptyList }}</p>
      <div v-else class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>{{ colSysUserId }}</th>
              <th>{{ colUsername }}</th>
              <th>{{ colNickname }}</th>
              <th>{{ colUserTypeCol }}</th>
              <th class="th-actions">{{ colActions }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, i) in rows" :key="rowKey(row, i)">
              <td>{{ rowUserId(row) || valueEmpty }}</td>
              <td>{{ row.username || valueEmpty }}</td>
              <td>{{ row.nickname || valueEmpty }}</td>
              <td>{{ userTypeLabel(row.userType) }}</td>
              <td class="td-actions">
                <button
                  type="button"
                  class="btn-action btn-action--ok"
                  :disabled="actingUserId === rowUserId(row)"
                  @click="openEdit(row)"
                >
                  {{ btnUpdate }}
                </button>
                <button
                  type="button"
                  class="btn-action btn-action--danger"
                  :disabled="actingUserId === rowUserId(row)"
                  @click="onDelete(row)"
                >
                  {{ btnDelete }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="!loading && !errorMsg && mode !== 'byId' && rows.length" class="pager">
        <button type="button" class="btn-pager" :disabled="pageNum <= 1" @click="goPrev">{{ btnPrev }}</button>
        <span class="pager-info">{{ pageNum }} / {{ pageSize }}</span>
        <button type="button" class="btn-pager" :disabled="!canGoNext()" @click="goNext">{{ btnNext }}</button>
      </div>
    </div>

    <div v-if="editOpen" class="dialog-backdrop" @click.self="closeEdit">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ dialogTitle }}</h2>
        <div class="dialog-body">
          <label class="field">
            <span class="field-label">{{ colSysUserId }}</span>
            <input v-model="editForm.userId" type="text" class="field-input" disabled />
          </label>
          <label class="field">
            <span class="field-label">{{ colUsername }}</span>
            <input v-model="editForm.username" type="text" class="field-input" autocomplete="off" />
          </label>
          <label class="field">
            <span class="field-label">{{ colUserTypeCol }}</span>
            <select v-model="editForm.userType" class="field-input">
              <option v-for="opt in userTypeOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
          </label>
          <label class="field">
            <span class="field-label">{{ colNickname }}</span>
            <input v-model="editForm.nickname" type="text" class="field-input" autocomplete="off" />
          </label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="editSaving" @click="closeEdit">
            {{ btnCancel }}
          </button>
          <button type="button" class="btn-action btn-action--ok" :disabled="editSaving" @click="saveEdit">
            {{ btnSave }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="deleteConfirmOpen" class="dialog-backdrop" @click.self="closeDeleteConfirm">
      <div class="dialog dialog--confirm" role="dialog" aria-modal="true">
        <h2 class="dialog-title">确认删除用户</h2>
        <p class="confirm-text">{{ confirmDeleteTextFor(rowUserId(deleteCandidate)) }}</p>
        <div class="confirm-meta">
          <span>用户 ID：{{ rowUserId(deleteCandidate) || valueEmpty }}</span>
          <span>用户名：{{ deleteCandidate?.username || valueEmpty }}</span>
          <span>角色：{{ userTypeLabel(deleteCandidate?.userType) }}</span>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="!!actingUserId" @click="closeDeleteConfirm">
            {{ btnCancel }}
          </button>
          <button type="button" class="btn-action btn-action--danger" :disabled="!!actingUserId" @click="confirmDelete">
            {{ btnDelete }}
          </button>
        </div>
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
  margin-bottom: clamp(16px, 2vw, 22px);
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

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.toolbar--row {
  max-width: 520px;
}

.toolbar-label {
  font-size: 13px;
  font-weight: 600;
  color: #9eb0c8;
}

.toolbar-select,
.toolbar-input {
  min-width: 240px;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.25);
  background: rgba(8, 12, 20, 0.9);
  color: #e8eef6;
  font-size: 14px;
}

.toolbar-select {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  padding-right: 34px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='6' viewBox='0 0 10 6'%3E%3Cpath fill='%239eb0c8' d='M0.94 0.72a.75.75 0 0 1 1.06 0L5 3.72 8 0.72a.75.75 0 1 1 1.06 1.06L5.53 5.31a.75.75 0 0 1-1.06 0L0.94 1.78a.75.75 0 0 1 0-1.06Z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
}

.toolbar-input {
  flex: 1;
  min-width: 120px;
}

.btn-toolbar {
  padding: 10px 18px;
  border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.35);
  background: rgba(61, 141, 255, 0.15);
  color: #d4e5ff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.btn-toolbar:hover {
  background: rgba(61, 141, 255, 0.22);
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
  text-align: center;
  white-space: nowrap;
  width: 140px;
  min-width: 140px;
}

.td-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: nowrap;
  align-items: center;
}

.btn-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid transparent;
  cursor: pointer;
  transition:
    background 0.15s,
    opacity 0.15s;
}

.btn-action:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-action--ok {
  background: rgba(61, 141, 255, 0.18);
  border-color: rgba(61, 141, 255, 0.35);
  color: #d4e5ff;
}

.btn-action--ok:hover:not(:disabled) {
  background: rgba(61, 141, 255, 0.28);
}

.btn-action--danger {
  background: rgba(220, 80, 80, 0.12);
  border-color: rgba(220, 100, 100, 0.35);
  color: #ffb4b4;
}

.btn-action--danger:hover:not(:disabled) {
  background: rgba(220, 80, 80, 0.2);
}

.btn-action--ghost {
  background: transparent;
  border-color: rgba(126, 184, 255, 0.25);
  color: #9eb0c8;
}

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 20px;
  padding-top: 12px;
  border-top: 1px solid rgba(36, 48, 68, 0.5);
}

.pager-info {
  font-size: 13px;
  color: #8b9cb4;
}

.btn-pager {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.3);
  background: rgba(61, 141, 255, 0.08);
  color: #c5d8f0;
  font-size: 13px;
  cursor: pointer;
}

.btn-pager:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.dialog-backdrop {
  position: fixed;
  inset: 0;
  z-index: 80;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(2, 6, 12, 0.72);
  backdrop-filter: blur(4px);
}

.dialog {
  width: 100%;
  max-width: 440px;
  border-radius: 12px;
  padding: 22px 22px 18px;
  background: linear-gradient(160deg, #121a28, #0a0e16);
  border: 1px solid rgba(61, 141, 255, 0.2);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.55);
}

.dialog--confirm {
  max-width: 500px;
  border-color: rgba(220, 100, 100, 0.28);
}

.dialog-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 700;
  color: #e8eef6;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.confirm-text {
  margin: 0 0 12px;
  color: #b7c7da;
  font-size: 14px;
}

.confirm-meta {
  display: grid;
  gap: 8px;
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 10px;
  background: rgba(220, 80, 80, 0.08);
  border: 1px solid rgba(220, 100, 100, 0.2);
  color: #f0d3d3;
  font-size: 13px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 12px;
  font-weight: 600;
  color: #8fa3bc;
}

.field-input {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.22);
  background: rgba(6, 10, 18, 0.95);
  color: #e8eef6;
  font-size: 14px;
}

.field select.field-input {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  padding-right: 34px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='6' viewBox='0 0 10 6'%3E%3Cpath fill='%239eb0c8' d='M0.94 0.72a.75.75 0 0 1 1.06 0L5 3.72 8 0.72a.75.75 0 1 1 1.06 1.06L5.53 5.31a.75.75 0 0 1-1.06 0L0.94 1.78a.75.75 0 0 1 0-1.06Z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
}

.field-input:disabled {
  opacity: 0.65;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
