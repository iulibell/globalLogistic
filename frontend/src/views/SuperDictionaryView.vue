<script setup>
import { computed, ref, watch } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import {
  fetchSuperDictionaryPage,
  postSuperAddDictionary,
  postSuperDeleteDictionary,
  postSuperUpdateDictionary,
} from '@/api/dict.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() =>
  t('page_profile', 'section_super_dictionary', 'Dictionary list'),
)
const lead = computed(() =>
  t(
    'page_profile',
    'lead_super_dictionary',
    'Super admin can browse dictionary items by page, update/delete rows, and add new entries from the top right.',
  ),
)
const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)
const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)
const colDictType = computed(() => t('page_profile', 'col_dict_type', 'Dictionary type'))
const colDictName = computed(() => t('page_profile', 'col_dict_name', 'Dictionary name'))
const colDictValue = computed(() => t('page_profile', 'col_dict_value', 'Dictionary value'))
const colDictSort = computed(() => t('page_profile', 'col_dict_sort', 'Sort'))
const colDictLang = computed(() => t('page_profile', 'col_dict_lang', 'Language'))
const colStatus = computed(() => t('page_profile', 'col_status', 'Status'))
const colActions = computed(() => t('page_profile', 'col_actions', 'Actions'))
const btnAddDictionary = computed(() => t('page_profile', 'btn_add_dictionary', 'Add dictionary'))
const btnUpdateDictionary = computed(() => t('page_profile', 'btn_update_dictionary', 'Update'))
const btnDeleteDictionary = computed(() => t('page_profile', 'btn_delete_dictionary', 'Delete'))
const btnPrev = computed(() => t('page_profile', 'super_btn_prev', 'Previous'))
const btnNext = computed(() => t('page_profile', 'super_btn_next', 'Next'))
const dictStatusEnabled = computed(() => t('page_profile', 'dict_status_enabled', 'Enabled'))
const dictStatusDisabled = computed(() => t('page_profile', 'dict_status_disabled', 'Disabled'))
const txtEmpty = computed(() => t('page_profile', 'super_dictionary_empty', 'No dictionary data'))
const txtFormInvalid = computed(() =>
  t('page_profile', 'super_dictionary_form_invalid', 'Please fill in type, name, value, sort, and language.'),
)
const txtAddSuccess = computed(() => t('page_profile', 'super_dictionary_add_success', 'Dictionary added successfully'))
const txtUpdateSuccess = computed(() => t('page_profile', 'super_dictionary_update_success', 'Dictionary updated successfully'))
const txtDeleteSuccess = computed(() => t('page_profile', 'super_dictionary_delete_success', 'Dictionary deleted successfully'))
const txtConfirmTitle = computed(() => t('page_profile', 'super_dictionary_confirm_title', 'Confirm dictionary deletion'))
const txtConfirmDesc = computed(() =>
  t('page_profile', 'super_dictionary_confirm_desc', 'Deletion cannot be undone. Do you want to continue?'),
)
const txtConfirmDelete = computed(() => t('page_profile', 'btn_delete_dictionary', 'Delete'))
const txtCancel = computed(() => t('page_profile', 'btn_cancel', 'Cancel'))

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const actingRow = ref('')

const modalOpen = ref(false)
const modalMode = ref('add')
const modalSaving = ref(false)
const deleteConfirmOpen = ref(false)
const deletePendingRow = ref(null)
const form = ref({
  dictType: '',
  dictName: '',
  dictValue: '',
  sort: 0,
  lang: '1',
})

function rowKey(row, idx) {
  const id = row?.id
  if (id != null) return String(id)
  return `${row?.dictType || ''}-${row?.dictValue || ''}-${row?.lang || ''}-${idx}`
}

function rowIdentity(row) {
  return `${row?.dictType || ''}::${row?.dictValue || ''}::${row?.lang || ''}`
}

function langLabel(v) {
  const s = String(v ?? '')
  if (s === '1') return '中文'
  if (s === '2') return 'English'
  if (s === '3') return 'Русский'
  if (s === '4') return '日本語'
  return s || valueEmpty.value
}

async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchSuperDictionaryPage(pageNum.value, pageSize.value)
    if (res.code !== 200) throw new Error(res.message || 'generic_error')
    rows.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    rows.value = []
    const raw = e instanceof Error ? e.message : 'generic_error'
    errorMsg.value = translateApiMessage(raw, t, uiLang.value)
  } finally {
    loading.value = false
  }
}

function openAdd() {
  if (modalSaving.value) return
  modalMode.value = 'add'
  form.value = { dictType: '', dictName: '', dictValue: '', sort: 0, lang: '1' }
  modalOpen.value = true
}

function openUpdate(row) {
  if (modalSaving.value) return
  modalMode.value = 'update'
  form.value = {
    dictType: row?.dictType != null ? String(row.dictType) : '',
    dictName: row?.dictName != null ? String(row.dictName) : '',
    dictValue: row?.dictValue != null ? String(row.dictValue) : '',
    sort: Number(row?.sort ?? 0),
    lang: row?.lang != null ? String(row.lang) : '1',
  }
  modalOpen.value = true
}

function closeModal() {
  if (modalSaving.value) return
  modalOpen.value = false
}

function validForm() {
  return (
    String(form.value.dictType).trim() &&
    String(form.value.dictName).trim() &&
    String(form.value.dictValue).trim() &&
    Number.isFinite(Number(form.value.sort)) &&
    String(form.value.lang).trim()
  )
}

async function submitModal() {
  if (!validForm()) {
    showToast(txtFormInvalid.value, { type: 'warning' })
    return
  }
  modalSaving.value = true
  try {
    if (modalMode.value === 'add') {
      const res = await postSuperAddDictionary({
        dictType: String(form.value.dictType).trim(),
        dictName: String(form.value.dictName).trim(),
        dictValue: String(form.value.dictValue).trim(),
        sort: Number(form.value.sort),
        lang: String(form.value.lang).trim(),
      })
      if (res.code !== 200) throw new Error(res.message || 'generic_error')
    } else {
      const res = await postSuperUpdateDictionary({
        dictType: String(form.value.dictType).trim(),
        dictName: String(form.value.dictName).trim(),
        dictValue: String(form.value.dictValue).trim(),
        lang: String(form.value.lang).trim(),
      })
      if (res.code !== 200) throw new Error(res.message || 'generic_error')
    }
    showToast(modalMode.value === 'add' ? txtAddSuccess.value : txtUpdateSuccess.value, { type: 'success' })
    modalOpen.value = false
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    modalSaving.value = false
  }
}

async function onDelete(row) {
  const rid = rowIdentity(row)
  if (!rid || actingRow.value) return
  deletePendingRow.value = row
  deleteConfirmOpen.value = true
}

function closeDeleteConfirm() {
  if (actingRow.value) return
  deleteConfirmOpen.value = false
  deletePendingRow.value = null
}

async function confirmDelete() {
  const row = deletePendingRow.value
  if (!row) return
  const rid = rowIdentity(row)
  if (!rid || actingRow.value) return
  actingRow.value = rid
  try {
    const res = await postSuperDeleteDictionary({
      dictType: String(row?.dictType ?? ''),
      dictName: String(row?.dictName ?? ''),
      dictValue: String(row?.dictValue ?? ''),
      lang: String(row?.lang ?? ''),
    })
    if (res.code !== 200) throw new Error(res.message || 'generic_error')
    showToast(txtDeleteSuccess.value, { type: 'success' })
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    actingRow.value = ''
    deleteConfirmOpen.value = false
    deletePendingRow.value = null
  }
}

function prevPage() {
  if (pageNum.value <= 1 || loading.value) return
  pageNum.value -= 1
}

function nextPage() {
  if (loading.value || rows.value.length < pageSize.value) return
  pageNum.value += 1
}

watch(pageNum, () => loadPage(), { immediate: true })
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <div>
        <h1 class="main-title">{{ sectionTitle }}</h1>
        <p class="main-lead">{{ lead }}</p>
      </div>
      <button type="button" class="btn-top-add" @click="openAdd">{{ btnAddDictionary }}</button>
    </header>

    <section class="panel">
      <p v-if="loading" class="state-msg">{{ loadingText }}</p>
      <p v-else-if="errorMsg" class="state-msg state-msg--err">{{ errorMsg }}</p>
      <p v-else-if="!rows.length" class="state-msg">{{ txtEmpty }}</p>
      <div v-else class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>{{ colDictType }}</th>
              <th>{{ colDictName }}</th>
              <th>{{ colDictValue }}</th>
              <th>{{ colDictSort }}</th>
              <th>{{ colDictLang }}</th>
              <th>{{ colStatus }}</th>
              <th>{{ colActions }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in rows" :key="rowKey(row, idx)">
              <td>{{ row.dictType || valueEmpty }}</td>
              <td>{{ row.dictName || valueEmpty }}</td>
              <td>{{ row.dictValue || valueEmpty }}</td>
              <td>{{ row.sort ?? valueEmpty }}</td>
              <td>{{ langLabel(row.lang) }}</td>
              <td>{{ String(row.status ?? '') === '1' ? dictStatusEnabled : dictStatusDisabled }}</td>
              <td class="td-actions">
                <button type="button" class="btn-action btn-action--ok" :disabled="actingRow === rowIdentity(row)" @click="openUpdate(row)">
                  {{ btnUpdateDictionary }}
                </button>
                <button type="button" class="btn-action btn-action--danger" :disabled="actingRow === rowIdentity(row)" @click="onDelete(row)">
                  {{ btnDeleteDictionary }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!loading && !errorMsg && rows.length" class="pager">
        <button type="button" class="btn-pager" :disabled="pageNum <= 1" @click="prevPage">{{ btnPrev }}</button>
        <span class="pager-info">{{ pageNum }}</span>
        <button type="button" class="btn-pager" :disabled="rows.length < pageSize" @click="nextPage">{{ btnNext }}</button>
      </div>
    </section>

    <div v-if="modalOpen" class="dialog-backdrop" @click.self="closeModal">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ modalMode === 'add' ? btnAddDictionary : btnUpdateDictionary }}</h2>
        <div class="dialog-body">
          <label class="field">
            <span class="field-label">{{ colDictType }}</span>
            <input v-model="form.dictType" type="text" class="field-input" :disabled="modalMode === 'update'" />
          </label>
          <label class="field">
            <span class="field-label">{{ colDictValue }}</span>
            <input v-model="form.dictValue" type="text" class="field-input" :disabled="modalMode === 'update'" />
          </label>
          <label class="field">
            <span class="field-label">{{ colDictLang }}</span>
            <select v-model="form.lang" class="field-input" :disabled="modalMode === 'update'">
              <option value="1">中文</option>
              <option value="2">English</option>
              <option value="3">Русский</option>
              <option value="4">日本語</option>
            </select>
          </label>
          <label class="field">
            <span class="field-label">{{ colDictName }}</span>
            <input v-model="form.dictName" type="text" class="field-input" />
          </label>
          <label v-if="modalMode === 'add'" class="field">
            <span class="field-label">{{ colDictSort }}</span>
            <input v-model="form.sort" type="number" class="field-input" />
          </label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="modalSaving" @click="closeModal">{{ txtCancel }}</button>
          <button type="button" class="btn-action btn-action--ok" :disabled="modalSaving" @click="submitModal">{{ t('page_profile', 'btn_save', 'Save') }}</button>
        </div>
      </div>
    </div>

    <div v-if="deleteConfirmOpen" class="dialog-backdrop" @click.self="closeDeleteConfirm">
      <div class="dialog dialog--confirm" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ txtConfirmTitle }}</h2>
        <p class="confirm-text">{{ txtConfirmDesc }}</p>
        <div class="confirm-meta">
          <span>{{ colDictType }}: {{ deletePendingRow?.dictType || valueEmpty }}</span>
          <span>{{ colDictValue }}: {{ deletePendingRow?.dictValue || valueEmpty }}</span>
          <span>{{ colDictLang }}: {{ langLabel(deletePendingRow?.lang) }}</span>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="!!actingRow" @click="closeDeleteConfirm">{{ txtCancel }}</button>
          <button type="button" class="btn-action btn-action--danger" :disabled="!!actingRow" @click="confirmDelete">{{ txtConfirmDelete }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main-inner { flex: 1; display: flex; flex-direction: column; width: 100%; max-width: min(1200px, 100%); margin: 0 auto; }
.main-head { margin-bottom: 18px; display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; }
.main-title { margin: 0 0 8px; font-size: clamp(22px, 2.2vw, 28px); font-weight: 700; color: #e8eef6; }
.main-lead { margin: 0; font-size: 14px; color: #7a8fa8; }
.btn-top-add { padding: 10px 16px; border-radius: 8px; border: 1px solid rgba(61, 141, 255, 0.35); background: rgba(61, 141, 255, 0.18); color: #d4e5ff; font-weight: 600; cursor: pointer; }
.btn-top-add:hover { background: rgba(61, 141, 255, 0.28); }
.panel { flex: 1; display: flex; flex-direction: column; border-radius: 12px; padding: 20px; background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99)); border: 1px solid rgba(61, 141, 255, 0.14); }
.state-msg { margin: 0; padding: 22px 8px; text-align: center; color: #8b9cb4; }
.state-msg--err { color: #f0a8a8; }
.table-wrap {
  width: 100%;
  max-height: min(62vh, 560px);
  overflow: auto;
  padding-right: 4px;
  scrollbar-width: thin;
  scrollbar-color: rgba(86, 148, 255, 0.72) rgba(17, 25, 39, 0.88);
}

.table-wrap::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

.table-wrap::-webkit-scrollbar-track {
  background: linear-gradient(180deg, rgba(12, 18, 30, 0.96), rgba(9, 14, 24, 0.96));
  border-radius: 999px;
}

.table-wrap::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, rgba(104, 166, 255, 0.86), rgba(49, 121, 235, 0.92));
  border-radius: 999px;
  border: 2px solid rgba(12, 18, 30, 0.95);
}

.table-wrap::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, rgba(130, 183, 255, 0.95), rgba(66, 138, 242, 0.98));
}

.table-wrap::-webkit-scrollbar-corner {
  background: rgba(12, 18, 30, 0.96);
}
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th, .data-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid rgba(36, 48, 68, 0.75); }
.data-table th { font-weight: 600; color: #8fa3bc; white-space: nowrap; }
.data-table td { color: #e8eef6; }
.td-actions { display: flex; gap: 8px; justify-content: flex-start; padding-right: 22px; }
.btn-action { display: inline-flex; align-items: center; justify-content: center; padding: 7px 12px; border-radius: 8px; border: 1px solid transparent; font-size: 13px; font-weight: 600; cursor: pointer; }
.btn-action:disabled { opacity: 0.45; cursor: not-allowed; }
.btn-action--ok { background: rgba(61, 141, 255, 0.18); border-color: rgba(61, 141, 255, 0.35); color: #d4e5ff; }
.btn-action--danger { background: rgba(220, 80, 80, 0.12); border-color: rgba(220, 100, 100, 0.35); color: #ffb4b4; }
.btn-action--ghost { background: transparent; border-color: rgba(126, 184, 255, 0.25); color: #9eb0c8; }
.pager { display: flex; align-items: center; justify-content: center; gap: 16px; margin-top: 16px; }
.btn-pager { padding: 8px 14px; border-radius: 8px; border: 1px solid rgba(61, 141, 255, 0.3); background: rgba(61, 141, 255, 0.08); color: #c5d8f0; cursor: pointer; }
.btn-pager:disabled { opacity: 0.35; cursor: not-allowed; }
.pager-info { color: #8b9cb4; }
.dialog-backdrop { position: fixed; inset: 0; z-index: 80; display: flex; align-items: center; justify-content: center; padding: 24px; background: rgba(2, 6, 12, 0.72); }
.dialog { width: 100%; max-width: 460px; border-radius: 12px; padding: 22px 22px 18px; background: linear-gradient(160deg, #121a28, #0a0e16); border: 1px solid rgba(61, 141, 255, 0.2); }
.dialog--confirm { max-width: 500px; border-color: rgba(220, 100, 100, 0.28); box-shadow: 0 18px 42px rgba(0, 0, 0, 0.45); }
.dialog-title { margin: 0 0 16px; font-size: 18px; color: #e8eef6; }
.dialog-body { display: flex; flex-direction: column; gap: 12px; }
.confirm-text { margin: 0 0 12px; color: #b7c7da; font-size: 14px; }
.confirm-meta { display: grid; gap: 8px; margin-bottom: 12px; padding: 12px; border-radius: 10px; background: rgba(220, 80, 80, 0.08); border: 1px solid rgba(220, 100, 100, 0.2); color: #f0d3d3; font-size: 13px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 12px; font-weight: 600; color: #8fa3bc; }
.field-input { padding: 10px 12px; border-radius: 8px; border: 1px solid rgba(61, 141, 255, 0.22); background: rgba(6, 10, 18, 0.95); color: #e8eef6; font-size: 14px; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
</style>
