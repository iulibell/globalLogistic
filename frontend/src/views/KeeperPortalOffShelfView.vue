<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchKeeperPortalOffShelfPending, postKeeperPortalOffShelfSetFee } from '@/api/keeper.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() =>
  t('page_profile', 'section_keeper_portal_off_shelf', pageDictFallback('page_profile', 'section_keeper_portal_off_shelf', uiLang.value)),
)
const lead = computed(() =>
  t('page_profile', 'lead_keeper_portal_off_shelf', pageDictFallback('page_profile', 'lead_keeper_portal_off_shelf', uiLang.value)),
)
const colId = computed(() =>
  t('page_profile', 'col_keeper_off_shelf_id', pageDictFallback('page_profile', 'col_keeper_off_shelf_id', uiLang.value)),
)
const colGoodsId = computed(() =>
  t('page_profile', 'manager_order_field_goodsId', pageDictFallback('page_profile', 'manager_order_field_goodsId', uiLang.value)),
)
const colSkuName = computed(() =>
  t('page_profile', 'col_keeper_sku_name', pageDictFallback('page_profile', 'col_keeper_sku_name', uiLang.value)),
)
const colMerchant = computed(() =>
  t('page_profile', 'col_keeper_merchant_id', pageDictFallback('page_profile', 'col_keeper_merchant_id', uiLang.value)),
)
const colCity = computed(() =>
  t('page_profile', 'col_keeper_off_shelf_city', pageDictFallback('page_profile', 'col_keeper_off_shelf_city', uiLang.value)),
)
const colFee = computed(() =>
  t('page_profile', 'col_keeper_off_shelf_fee', pageDictFallback('page_profile', 'col_keeper_off_shelf_fee', uiLang.value)),
)
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value)))
const btnSetFee = computed(() =>
  t('page_profile', 'btn_keeper_set_off_shelf_fee', pageDictFallback('page_profile', 'btn_keeper_set_off_shelf_fee', uiLang.value)),
)
const labelFee = computed(() =>
  t('page_profile', 'keeper_off_shelf_fee_label', pageDictFallback('page_profile', 'keeper_off_shelf_fee_label', uiLang.value)),
)
const btnCancel = computed(() => t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value)))
const btnConfirm = computed(() =>
  t('page_profile', 'btn_confirm_assign', pageDictFallback('page_profile', 'btn_confirm_assign', uiLang.value)),
)
const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)
const emptyList = computed(() =>
  t('page_profile', 'keeper_off_shelf_empty', pageDictFallback('page_profile', 'keeper_off_shelf_empty', uiLang.value)),
)
const loadError = computed(() =>
  t('page_profile', 'keeper_off_shelf_load_error', pageDictFallback('page_profile', 'keeper_off_shelf_load_error', uiLang.value)),
)
const feeOk = computed(() =>
  t('page_profile', 'keeper_off_shelf_fee_ok', pageDictFallback('page_profile', 'keeper_off_shelf_fee_ok', uiLang.value)),
)
const feeFail = computed(() =>
  t('page_profile', 'keeper_off_shelf_fee_fail', pageDictFallback('page_profile', 'keeper_off_shelf_fee_fail', uiLang.value)),
)
const valueEmpty = computed(() => t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)))
const btnPrev = computed(() => t('page_profile', 'super_btn_prev', pageDictFallback('page_profile', 'super_btn_prev', uiLang.value)))
const btnNext = computed(() => t('page_profile', 'super_btn_next', pageDictFallback('page_profile', 'super_btn_next', uiLang.value)))

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const dialogOpen = ref(false)
const feeInput = ref('')
const currentId = ref(null)
const acting = ref(false)

function rowId(row) {
  const v = row?.id
  return v != null && v !== '' ? Number(v) : NaN
}

async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchKeeperPortalOffShelfPending(pageNum.value, pageSize.value)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    rows.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    errorMsg.value = translateApiMessage(raw, t, uiLang.value)
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  const id = rowId(row)
  if (!Number.isFinite(id) || acting.value) return
  currentId.value = id
  feeInput.value = ''
  dialogOpen.value = true
}

function closeDialog() {
  dialogOpen.value = false
  currentId.value = null
  feeInput.value = ''
}

async function submitFee() {
  const id = currentId.value
  const fee = Number(String(feeInput.value || '').trim())
  if (!Number.isFinite(id) || !Number.isFinite(fee) || fee <= 0) return
  acting.value = true
  try {
    const res = await postKeeperPortalOffShelfSetFee(id, fee)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    if (res.data === false) {
      throw new Error('generic_error')
    }
    showToast(feeOk.value, { type: 'success' })
    closeDialog()
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${feeFail.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    acting.value = false
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
                <th>{{ colId }}</th>
                <th>{{ colGoodsId }}</th>
                <th>{{ colSkuName }}</th>
                <th>{{ colMerchant }}</th>
                <th>{{ colCity }}</th>
                <th>{{ colFee }}</th>
                <th class="th-actions">{{ colActions }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in rows" :key="row?.id ?? `os-${idx}`">
                <td>{{ row.id ?? valueEmpty }}</td>
                <td>{{ row.goodsId ?? valueEmpty }}</td>
                <td>{{ row.skuName ?? valueEmpty }}</td>
                <td>{{ row.merchantId ?? valueEmpty }}</td>
                <td>{{ row.city ?? valueEmpty }}</td>
                <td>{{ row.fee ?? valueEmpty }}</td>
                <td class="td-actions">
                  <div class="actions-wrap">
                    <button
                      type="button"
                      class="btn-action btn-action--primary"
                      :disabled="acting"
                      @click="openDialog(row)"
                    >
                      {{ btnSetFee }}
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="pager">
          <button type="button" class="pager-btn" :disabled="pageNum <= 1 || loading" @click="prevPage">{{ btnPrev }}</button>
          <span class="pager-meta">{{ pageNum }}</span>
          <button type="button" class="pager-btn" :disabled="loading || rows.length < pageSize" @click="nextPage">{{ btnNext }}</button>
        </div>
      </template>
    </div>

    <div v-if="dialogOpen" class="dialog-backdrop" @click.self="closeDialog">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ btnSetFee }}</h2>
        <div class="dialog-body">
          <label class="field">
            <span class="field-label">{{ labelFee }}</span>
            <input v-model="feeInput" type="number" min="0" step="0.01" class="field-input" :disabled="acting" />
          </label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="acting" @click="closeDialog">{{ btnCancel }}</button>
          <button type="button" class="btn-action btn-action--primary" :disabled="acting" @click="submitFee">{{ btnConfirm }}</button>
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
}
.main-title {
  margin: 0 0 8px;
  font-size: clamp(22px, 2.2vw, 28px);
  font-weight: 700;
  color: #e8eef6;
}
.main-lead {
  margin: 0;
  font-size: 14px;
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
}
.state-msg {
  margin: 0;
  padding: 24px 8px;
  text-align: center;
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
.th-actions,
.td-actions {
  width: 160px;
  text-align: center;
  white-space: nowrap;
}
.actions-wrap {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  min-width: 120px;
}
.btn-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 700;
  border-radius: 6px;
  border: 1px solid transparent;
  cursor: pointer;
}
.btn-action:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.btn-action--primary {
  color: #e8f4ff;
  background: linear-gradient(180deg, #3d8dff, #2563c7);
  border-color: rgba(61, 141, 255, 0.55);
}
.btn-action--ghost {
  color: #d4e5ff;
  background: rgba(61, 141, 255, 0.12);
  border-color: rgba(61, 141, 255, 0.35);
}
.pager {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
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
  border-radius: 6px;
  border: 1px solid rgba(61, 141, 255, 0.35);
  background: rgba(61, 141, 255, 0.12);
  color: #d4e5ff;
  cursor: pointer;
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
.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
}
</style>
