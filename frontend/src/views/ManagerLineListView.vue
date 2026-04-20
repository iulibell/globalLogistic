<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchManagerLinePage, postManagerAddLine, postManagerUpdateLine } from '@/api/manager.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)
const router = useRouter()

const sectionTitle = computed(() =>
  t('page_profile', 'section_manager_line_list', pageDictFallback('page_profile', 'section_manager_line_list', uiLang.value)),
)
const lead = computed(() =>
  t('page_profile', 'lead_manager_line_list', pageDictFallback('page_profile', 'lead_manager_line_list', uiLang.value)),
)

const colDbId = computed(() =>
  t('page_profile', 'col_line_db_id', pageDictFallback('page_profile', 'col_line_db_id', uiLang.value)),
)
const colLineId = computed(() =>
  t('page_profile', 'col_line_id', pageDictFallback('page_profile', 'col_line_id', uiLang.value)),
)
const colOrigin = computed(() =>
  t('page_profile', 'col_transport_origin', pageDictFallback('page_profile', 'col_transport_origin', uiLang.value)),
)
const colDest = computed(() =>
  t('page_profile', 'col_transport_dest', pageDictFallback('page_profile', 'col_transport_dest', uiLang.value)),
)
const colEstimation = computed(() =>
  t('page_profile', 'col_estimation', pageDictFallback('page_profile', 'col_estimation', uiLang.value)),
)
const colStatus = computed(() =>
  t('page_profile', 'col_line_status', pageDictFallback('page_profile', 'col_line_status', uiLang.value)),
)
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value)))
const btnAddLine = computed(() =>
  t('page_profile', 'btn_manager_add_line', pageDictFallback('page_profile', 'btn_manager_add_line', uiLang.value)),
)
const btnUpdateLine = computed(() =>
  t('page_profile', 'btn_manager_update_line', pageDictFallback('page_profile', 'btn_manager_update_line', uiLang.value)),
)
const btnDetail = computed(() =>
  t('page_profile', 'btn_manager_line_detail', pageDictFallback('page_profile', 'btn_manager_line_detail', uiLang.value)),
)

const emptyList = computed(() =>
  t('page_profile', 'manager_line_list_empty', pageDictFallback('page_profile', 'manager_line_list_empty', uiLang.value)),
)
const loadError = computed(() =>
  t('page_profile', 'manager_line_list_load_error', pageDictFallback('page_profile', 'manager_line_list_load_error', uiLang.value)),
)
const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)
const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)
const btnPrev = computed(() =>
  t('page_profile', 'super_btn_prev', pageDictFallback('page_profile', 'super_btn_prev', uiLang.value)),
)
const btnNext = computed(() =>
  t('page_profile', 'super_btn_next', pageDictFallback('page_profile', 'super_btn_next', uiLang.value)),
)
const btnSave = computed(() => t('page_profile', 'btn_save', pageDictFallback('page_profile', 'btn_save', uiLang.value)))
const btnCancel = computed(() => t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value)))
const dialogAddTitle = computed(() =>
  t('page_profile', 'manager_line_dialog_add_title', pageDictFallback('page_profile', 'manager_line_dialog_add_title', uiLang.value)),
)
const dialogUpdateTitle = computed(() =>
  t(
    'page_profile',
    'manager_line_dialog_update_title',
    pageDictFallback('page_profile', 'manager_line_dialog_update_title', uiLang.value),
  ),
)
const submitOk = computed(() =>
  t('page_profile', 'manager_line_submit_ok', pageDictFallback('page_profile', 'manager_line_submit_ok', uiLang.value)),
)
const submitFail = computed(() =>
  t('page_profile', 'manager_line_submit_fail', pageDictFallback('page_profile', 'manager_line_submit_fail', uiLang.value)),
)
const needLineId = computed(() =>
  t('page_profile', 'manager_line_need_line_id', pageDictFallback('page_profile', 'manager_line_need_line_id', uiLang.value)),
)
const needOrigin = computed(() =>
  t('page_profile', 'manager_line_need_origin', pageDictFallback('page_profile', 'manager_line_need_origin', uiLang.value)),
)
const needDest = computed(() =>
  t('page_profile', 'manager_line_need_dest', pageDictFallback('page_profile', 'manager_line_need_dest', uiLang.value)),
)
const needEstimation = computed(() =>
  t(
    'page_profile',
    'manager_line_need_estimation',
    pageDictFallback('page_profile', 'manager_line_need_estimation', uiLang.value),
  ),
)
const needStatus = computed(() =>
  t('page_profile', 'manager_line_need_status', pageDictFallback('page_profile', 'manager_line_need_status', uiLang.value)),
)

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const dialogOpen = ref(false)
const dialogMode = ref('add')
const dialogSaving = ref(false)
const form = reactive({
  lineId: '',
  origin: '',
  dest: '',
  estimation: '',
  status: '1',
})

function statusLabel(s) {
  if (s === 0 || s === '0') {
    return t('page_profile', 'line_status_disabled', pageDictFallback('page_profile', 'line_status_disabled', uiLang.value))
  }
  if (s === 1 || s === '1') {
    return t('page_profile', 'line_status_enabled', pageDictFallback('page_profile', 'line_status_enabled', uiLang.value))
  }
  return String(s ?? valueEmpty.value)
}

function fmtEst(v) {
  if (v == null || v === '') return valueEmpty.value
  return String(v)
}

function resetForm() {
  form.lineId = ''
  form.origin = ''
  form.dest = ''
  form.estimation = ''
  form.status = '1'
}

function openAddDialog() {
  if (dialogSaving.value) return
  dialogMode.value = 'add'
  resetForm()
  dialogOpen.value = true
}

function openUpdateDialog(row) {
  if (dialogSaving.value) return
  dialogMode.value = 'update'
  form.lineId = row?.lineId != null ? String(row.lineId) : ''
  form.origin = row?.origin != null ? String(row.origin) : ''
  form.dest = row?.dest != null ? String(row.dest) : ''
  form.estimation = row?.estimation != null ? String(row.estimation) : ''
  form.status = row?.status != null ? String(row.status) : '1'
  dialogOpen.value = true
}

function closeDialog() {
  if (dialogSaving.value) return
  dialogOpen.value = false
}

function goDetail(row) {
  const origin = row?.origin != null ? String(row.origin).trim() : ''
  const dest = row?.dest != null ? String(row.dest).trim() : ''
  if (!origin || !dest) {
    showToast(needOrigin.value, { type: 'warning' })
    return
  }
  router.push({
    name: 'profile-manager-line-detail',
    query: { origin, dest },
  })
}

function validateForm() {
  if (!String(form.origin).trim()) return needOrigin.value
  if (!String(form.dest).trim()) return needDest.value
  const est = Number(form.estimation)
  if (!Number.isFinite(est)) return needEstimation.value
  const st = Number(form.status)
  if (!(st === 0 || st === 1)) return needStatus.value
  if (dialogMode.value === 'update' && !String(form.lineId).trim()) return needLineId.value
  return ''
}

async function submitDialog() {
  const msg = validateForm()
  if (msg) {
    showToast(msg, { type: 'warning' })
    return
  }
  dialogSaving.value = true
  try {
    const payload = {
      lineId: dialogMode.value === 'add' ? 0 : Number(String(form.lineId).trim()),
      origin: String(form.origin).trim(),
      dest: String(form.dest).trim(),
      estimation: Number(form.estimation),
      status: Number(form.status),
    }
    const res =
      dialogMode.value === 'add' ? await postManagerAddLine(payload) : await postManagerUpdateLine(payload)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    showToast(submitOk.value, { type: 'success' })
    dialogOpen.value = false
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${submitFail.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    dialogSaving.value = false
  }
}

async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchManagerLinePage(pageNum.value, pageSize.value)
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

function prevPage() {
  if (pageNum.value <= 1 || loading.value) return
  pageNum.value -= 1
}

function nextPage() {
  if (loading.value || !rows.value.length || rows.value.length < pageSize.value) return
  pageNum.value += 1
}

watch(pageNum, () => loadPage())

onMounted(() => loadPage())
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <h1 class="main-title">{{ sectionTitle }}</h1>
      <p class="main-lead">{{ lead }}</p>
      <div class="head-actions">
        <button type="button" class="btn-action btn-action--ok" :disabled="loading || dialogSaving" @click="openAddDialog">
          {{ btnAddLine }}
        </button>
      </div>
    </header>

    <div class="panel">
      <p v-if="loading" class="state-msg">{{ loadingText }}</p>
      <p v-else-if="errorMsg" class="state-msg state-msg--err">{{ loadError }}：{{ errorMsg }}</p>
      <p v-else-if="!rows.length" class="state-msg">{{ emptyList }}</p>
      <template v-else>
        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th>{{ colDbId }}</th>
                <th>{{ colLineId }}</th>
                <th>{{ colOrigin }}</th>
                <th>{{ colDest }}</th>
                <th>{{ colEstimation }}</th>
                <th>{{ colStatus }}</th>
                <th class="th-actions">{{ colActions }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in rows" :key="row.id ?? row.lineId ?? idx">
                <td>{{ row.id != null ? row.id : valueEmpty }}</td>
                <td>{{ row.lineId != null ? row.lineId : valueEmpty }}</td>
                <td>{{ row.origin || valueEmpty }}</td>
                <td>{{ row.dest || valueEmpty }}</td>
                <td>{{ fmtEst(row.estimation) }}</td>
                <td>{{ statusLabel(row.status) }}</td>
                <td class="td-actions">
                  <button
                    type="button"
                    class="btn-action btn-action--ghost"
                    :disabled="dialogSaving"
                    @click="goDetail(row)"
                  >
                    {{ btnDetail }}
                  </button>
                  <button
                    type="button"
                    class="btn-action btn-action--ghost"
                    :disabled="dialogSaving"
                    @click="openUpdateDialog(row)"
                  >
                    {{ btnUpdateLine }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="pager">
          <button type="button" class="pager-btn" :disabled="pageNum <= 1 || loading" @click="prevPage">
            {{ btnPrev }}
          </button>
          <span class="pager-meta">{{ pageNum }}</span>
          <button
            type="button"
            class="pager-btn"
            :disabled="loading || rows.length < pageSize"
            @click="nextPage"
          >
            {{ btnNext }}
          </button>
        </div>
      </template>
    </div>

    <div v-if="dialogOpen" class="dialog-backdrop" @click.self="closeDialog">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ dialogMode === 'add' ? dialogAddTitle : dialogUpdateTitle }}</h2>
        <div class="dialog-body">
          <label class="field">
            <span class="field-label">{{ colLineId }}</span>
            <input
              v-model="form.lineId"
              type="number"
              min="0"
              class="field-input"
              :disabled="dialogMode === 'add' || dialogSaving"
            />
          </label>
          <label class="field">
            <span class="field-label">{{ colOrigin }}</span>
            <input v-model="form.origin" type="text" class="field-input" :disabled="dialogSaving" />
          </label>
          <label class="field">
            <span class="field-label">{{ colDest }}</span>
            <input v-model="form.dest" type="text" class="field-input" :disabled="dialogSaving" />
          </label>
          <label class="field">
            <span class="field-label">{{ colEstimation }}</span>
            <input v-model="form.estimation" type="number" step="0.1" class="field-input" :disabled="dialogSaving" />
          </label>
          <label class="field">
            <span class="field-label">{{ colStatus }}</span>
            <select v-model="form.status" class="field-input field-input--select" :disabled="dialogSaving">
              <option value="1">{{ statusLabel(1) }}</option>
              <option value="0">{{ statusLabel(0) }}</option>
            </select>
          </label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="dialogSaving" @click="closeDialog">
            {{ btnCancel }}
          </button>
          <button type="button" class="btn-action btn-action--ok" :disabled="dialogSaving" @click="submitDialog">
            {{ btnSave }}
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
  max-width: min(1200px, 100%);
  margin: 0 auto;
}

.main-head {
  margin-bottom: clamp(20px, 2.5vw, 28px);
  flex-shrink: 0;
}

.head-actions {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
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
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.btn-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 700;
  font-family: inherit;
  border-radius: 6px;
  border: 1px solid transparent;
  cursor: pointer;
  transition:
    opacity 0.15s,
    filter 0.15s,
    background 0.15s;
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

.btn-action--ghost {
  color: #d4e5ff;
  background: rgba(61, 141, 255, 0.12);
  border-color: rgba(61, 141, 255, 0.35);
}

.btn-action--ghost:hover:not(:disabled) {
  background: rgba(61, 141, 255, 0.24);
}

.pager {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
  padding-top: 8px;
}

.pager-meta {
  font-size: 13px;
  font-weight: 600;
  color: #9eb0c8;
  min-width: 2ch;
  text-align: center;
}

.pager-btn {
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  border-radius: 6px;
  border: 1px solid rgba(61, 141, 255, 0.35);
  background: rgba(61, 141, 255, 0.12);
  color: #d4e5ff;
  cursor: pointer;
  transition:
    background 0.15s,
    opacity 0.15s;
}

.pager-btn:hover:not(:disabled) {
  background: rgba(61, 141, 255, 0.22);
}

.pager-btn:disabled {
  opacity: 0.45;
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

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 12px;
  color: #9eb0c8;
}

.field-input {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.25);
  background: rgba(5, 9, 14, 0.66);
  color: #e8eef6;
  font-size: 13px;
}

.field-input:focus {
  outline: none;
  border-color: rgba(112, 176, 255, 0.55);
}

.field-input--select {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  padding-right: 34px;
  background-image: linear-gradient(45deg, transparent 50%, #8fa3bc 50%), linear-gradient(135deg, #8fa3bc 50%, transparent 50%);
  background-position:
    calc(100% - 16px) calc(50% - 2px),
    calc(100% - 11px) calc(50% - 2px);
  background-size:
    5px 5px,
    5px 5px;
  background-repeat: no-repeat;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
}

@media (max-width: 768px) {
  .data-table th,
  .data-table td {
    padding: 10px 8px;
    font-size: 13px;
  }
}
</style>
