<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import {
  fetchManagerAvailableDrivers,
  fetchManagerManualAssignmentPage,
  postManagerManualAssignDriver,
} from '@/api/manager.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() =>
  t('page_profile', 'section_manager_manual_assign', pageDictFallback('page_profile', 'section_manager_manual_assign', uiLang.value)),
)
const lead = computed(() =>
  t('page_profile', 'lead_manager_manual_assign', pageDictFallback('page_profile', 'lead_manager_manual_assign', uiLang.value)),
)

const colTransportOrderId = computed(() =>
  t('page_profile', 'col_transport_order_id', pageDictFallback('page_profile', 'col_transport_order_id', uiLang.value)),
)
const colOrigin = computed(() =>
  t('page_profile', 'col_transport_origin', pageDictFallback('page_profile', 'col_transport_origin', uiLang.value)),
)
const colDest = computed(() =>
  t('page_profile', 'col_transport_dest', pageDictFallback('page_profile', 'col_transport_dest', uiLang.value)),
)
const colFee = computed(() =>
  t('page_profile', 'col_transport_fee', pageDictFallback('page_profile', 'col_transport_fee', uiLang.value)),
)
const colDriverUserId = computed(() =>
  t('page_profile', 'col_driver_user_id', pageDictFallback('page_profile', 'col_driver_user_id', uiLang.value)),
)
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value)))
const btnAssign = computed(() =>
  t('page_profile', 'btn_manager_assign_driver', pageDictFallback('page_profile', 'btn_manager_assign_driver', uiLang.value)),
)

const emptyList = computed(() =>
  t('page_profile', 'manager_manual_assign_empty', pageDictFallback('page_profile', 'manager_manual_assign_empty', uiLang.value)),
)
const loadError = computed(() =>
  t('page_profile', 'manager_manual_assign_load_error', pageDictFallback('page_profile', 'manager_manual_assign_load_error', uiLang.value)),
)
const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)
const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)
const pickDriverMsg = computed(() =>
  t('page_profile', 'manager_manual_assign_pick_driver', pageDictFallback('page_profile', 'manager_manual_assign_pick_driver', uiLang.value)),
)
const msgOk = computed(() =>
  t('page_profile', 'manager_manual_assign_ok', pageDictFallback('page_profile', 'manager_manual_assign_ok', uiLang.value)),
)
const msgFail = computed(() =>
  t('page_profile', 'manager_manual_assign_fail', pageDictFallback('page_profile', 'manager_manual_assign_fail', uiLang.value)),
)
const btnPrev = computed(() =>
  t('page_profile', 'super_btn_prev', pageDictFallback('page_profile', 'super_btn_prev', uiLang.value)),
)
const btnNext = computed(() =>
  t('page_profile', 'super_btn_next', pageDictFallback('page_profile', 'super_btn_next', uiLang.value)),
)

const modalTitle = computed(() =>
  t('page_profile', 'manager_manual_assign_modal_title', pageDictFallback('page_profile', 'manager_manual_assign_modal_title', uiLang.value)),
)
const modalLeadPrefix = computed(() =>
  t('page_profile', 'manager_manual_assign_modal_lead', pageDictFallback('page_profile', 'manager_manual_assign_modal_lead', uiLang.value)),
)
const driversEmpty = computed(() =>
  t('page_profile', 'manager_manual_assign_drivers_empty', pageDictFallback('page_profile', 'manager_manual_assign_drivers_empty', uiLang.value)),
)
const driversLoadError = computed(() =>
  t(
    'page_profile',
    'manager_manual_assign_drivers_load_error',
    pageDictFallback('page_profile', 'manager_manual_assign_drivers_load_error', uiLang.value),
  ),
)
const colVehicleNo = computed(() =>
  t('page_profile', 'col_driver_vehicle_no', pageDictFallback('page_profile', 'col_driver_vehicle_no', uiLang.value)),
)
const colDriverCity = computed(() =>
  t('page_profile', 'col_driver_current_city', pageDictFallback('page_profile', 'col_driver_current_city', uiLang.value)),
)
const colDriverStatus = computed(() =>
  t('page_profile', 'col_driver_status', pageDictFallback('page_profile', 'col_driver_status', uiLang.value)),
)
const btnCancel = computed(() => t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value)))
const btnConfirmAssign = computed(() =>
  t('page_profile', 'btn_confirm_assign', pageDictFallback('page_profile', 'btn_confirm_assign', uiLang.value)),
)

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const actingTransportOrderId = ref(null)

const assignModalOpen = ref(false)
const modalTransportOrderId = ref('')
const driverRows = ref([])
const driverPageNum = ref(1)
const driverPageSize = ref(10)
const driverLoading = ref(false)
const driverErrorMsg = ref('')
const selectedDriverUserId = ref('')

function fmtFee(v) {
  if (v == null || v === '') return valueEmpty.value
  return String(v)
}

function driverKey(row) {
  return row?.transportOrderId != null && String(row.transportOrderId).trim()
    ? String(row.transportOrderId).trim()
    : ''
}

async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchManagerManualAssignmentPage(pageNum.value, pageSize.value)
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

function openAssignModal(row) {
  const tid = driverKey(row)
  if (!tid || assignModalOpen.value || actingTransportOrderId.value) return
  modalTransportOrderId.value = tid
  selectedDriverUserId.value = ''
  driverPageNum.value = 1
  driverRows.value = []
  driverErrorMsg.value = ''
  assignModalOpen.value = true
  loadDriverModalPage()
}

function closeAssignModal() {
  if (actingTransportOrderId.value) return
  assignModalOpen.value = false
  modalTransportOrderId.value = ''
  selectedDriverUserId.value = ''
  driverRows.value = []
  driverErrorMsg.value = ''
}

async function loadDriverModalPage() {
  driverLoading.value = true
  driverErrorMsg.value = ''
  try {
    const res = await fetchManagerAvailableDrivers(driverPageNum.value, driverPageSize.value)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const data = res.data
    driverRows.value = Array.isArray(data) ? data : []
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    driverErrorMsg.value = translateApiMessage(raw, t, uiLang.value)
    driverRows.value = []
  } finally {
    driverLoading.value = false
  }
}

function selectDriverRow(d) {
  const id = d?.userId != null ? String(d.userId).trim() : ''
  if (!id) return
  selectedDriverUserId.value = id
}

function driverModalPrev() {
  if (driverPageNum.value <= 1 || driverLoading.value) return
  driverPageNum.value -= 1
}

function driverModalNext() {
  if (driverLoading.value || !driverRows.value.length || driverRows.value.length < driverPageSize.value) return
  driverPageNum.value += 1
}

watch(driverPageNum, () => {
  if (assignModalOpen.value) loadDriverModalPage()
})

async function confirmModalAssign() {
  const tid = modalTransportOrderId.value
  const driverId = String(selectedDriverUserId.value ?? '').trim()
  if (!tid || !driverId) {
    showToast(pickDriverMsg.value, { type: 'warning' })
    return
  }
  actingTransportOrderId.value = tid
  try {
    const res = await postManagerManualAssignDriver(tid, driverId)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    if (res.data === true) {
      showToast(msgOk.value, { type: 'success' })
      assignModalOpen.value = false
      modalTransportOrderId.value = ''
      selectedDriverUserId.value = ''
      driverRows.value = []
      driverErrorMsg.value = ''
      await loadPage()
    } else {
      showToast(msgFail.value, { type: 'error' })
    }
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    actingTransportOrderId.value = null
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

function fmtDriverStatus(s) {
  if (s == null || s === '') return valueEmpty.value
  const raw = String(s).trim()
  if (raw === '0') return '上线'
  if (raw === '1') return '运输中'
  if (raw === '2' || raw === '3') return '下线'
  return raw
}

function isDriverRowSelected(d) {
  const id = d?.userId != null ? String(d.userId).trim() : ''
  return id && id === selectedDriverUserId.value
}
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
      <template v-else>
        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th>{{ colTransportOrderId }}</th>
                <th>{{ colOrigin }}</th>
                <th>{{ colDest }}</th>
                <th>{{ colFee }}</th>
                <th class="th-actions">{{ colActions }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in rows" :key="driverKey(row) || `row-${idx}`">
                <td>{{ row.transportOrderId || valueEmpty }}</td>
                <td>{{ row.origin || valueEmpty }}</td>
                <td>{{ row.dest || valueEmpty }}</td>
                <td>{{ fmtFee(row.fee) }}</td>
                <td class="td-actions">
                  <button
                    type="button"
                    class="btn-action btn-action--ok"
                    :disabled="!!actingTransportOrderId || assignModalOpen"
                    @click="openAssignModal(row)"
                  >
                    {{ btnAssign }}
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

    <div v-if="assignModalOpen" class="dialog-backdrop" @click.self="closeAssignModal">
      <div class="dialog dialog--wide" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ modalTitle }}</h2>
        <p class="dialog-lead">
          {{ modalLeadPrefix }}<strong>{{ modalTransportOrderId || valueEmpty }}</strong>
        </p>
        <div class="dialog-body">
          <p v-if="driverLoading" class="dialog-state">{{ loadingText }}</p>
          <p v-else-if="driverErrorMsg" class="dialog-state dialog-state--err">{{ driversLoadError }}：{{ driverErrorMsg }}</p>
          <p v-else-if="!driverRows.length" class="dialog-state">{{ driversEmpty }}</p>
          <div v-else class="dialog-table-wrap">
            <table class="data-table data-table--compact">
              <thead>
                <tr>
                  <th>{{ colDriverUserId }}</th>
                  <th>{{ colVehicleNo }}</th>
                  <th>{{ colDriverCity }}</th>
                  <th>{{ colDriverStatus }}</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(d, i) in driverRows"
                  :key="d.userId != null ? String(d.userId) : `d-${i}`"
                  class="driver-row"
                  :class="{ 'driver-row--selected': isDriverRowSelected(d) }"
                  @click="selectDriverRow(d)"
                >
                  <td>{{ d.userId || valueEmpty }}</td>
                  <td>{{ d.vehicleNo || valueEmpty }}</td>
                  <td>{{ d.currentCity || valueEmpty }}</td>
                  <td>{{ fmtDriverStatus(d.status) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="driverRows.length && !driverLoading && !driverErrorMsg" class="dialog-pager">
            <button type="button" class="pager-btn" :disabled="driverPageNum <= 1 || driverLoading" @click="driverModalPrev">
              {{ btnPrev }}
            </button>
            <span class="pager-meta">{{ driverPageNum }}</span>
            <button
              type="button"
              class="pager-btn"
              :disabled="driverLoading || driverRows.length < driverPageSize"
              @click="driverModalNext"
            >
              {{ btnNext }}
            </button>
          </div>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="!!actingTransportOrderId" @click="closeAssignModal">
            {{ btnCancel }}
          </button>
          <button
            type="button"
            class="btn-action btn-action--ok"
            :disabled="!!actingTransportOrderId || driverLoading"
            @click="confirmModalAssign"
          >
            {{ btnConfirmAssign }}
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

.data-table--compact th,
.data-table--compact td {
  padding: 10px 12px;
  font-size: 13px;
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

.driver-row {
  cursor: pointer;
}

.driver-row--selected td {
  background: rgba(46, 188, 122, 0.12);
}

.th-actions,
.td-actions {
  text-align: right;
  white-space: nowrap;
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
    filter 0.15s;
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
  color: #c8d8ee;
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(61, 141, 255, 0.25);
}

.btn-action--ghost:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
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

.dialog--wide {
  max-width: min(640px, 100%);
}

.dialog-title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  color: #e8eef6;
}

.dialog-lead {
  margin: 0 0 16px;
  font-size: 13px;
  line-height: 1.45;
  color: #8fa3bc;
}

.dialog-lead strong {
  color: #e8eef6;
  font-weight: 600;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 120px;
}

.dialog-state {
  margin: 0;
  padding: 16px 4px;
  text-align: center;
  font-size: 14px;
  color: #8b9cb4;
}

.dialog-state--err {
  color: #f0a8a8;
}

.dialog-table-wrap {
  width: 100%;
  overflow-x: auto;
  border-radius: 8px;
  border: 1px solid rgba(36, 48, 68, 0.75);
}

.dialog-pager {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 4px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
  padding-top: 4px;
}

@media (max-width: 768px) {
  .data-table th,
  .data-table td {
    padding: 10px 8px;
    font-size: 13px;
  }
}
</style>
