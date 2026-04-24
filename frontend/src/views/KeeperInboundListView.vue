<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchKeeperInboundPage, postKeeperConfirmInbound } from '@/api/keeper.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() =>
  t('page_profile', 'section_keeper_inbound_list', pageDictFallback('page_profile', 'section_keeper_inbound_list', uiLang.value)),
)
const lead = computed(() =>
  t('page_profile', 'lead_keeper_inbound_list', pageDictFallback('page_profile', 'lead_keeper_inbound_list', uiLang.value)),
)
const colInboundId = computed(() =>
  t('page_profile', 'col_keeper_inbound_id', pageDictFallback('page_profile', 'col_keeper_inbound_id', uiLang.value)),
)
const colSkuCode = computed(() =>
  t('page_profile', 'col_keeper_sku_code', pageDictFallback('page_profile', 'col_keeper_sku_code', uiLang.value)),
)
const colSkuName = computed(() =>
  t('page_profile', 'col_keeper_sku_name', pageDictFallback('page_profile', 'col_keeper_sku_name', uiLang.value)),
)
const colStatus = computed(() =>
  t('page_profile', 'col_status', pageDictFallback('page_profile', 'col_status', uiLang.value)),
)
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value)))
const btnConfirmInbound = computed(() =>
  t('page_profile', 'btn_keeper_confirm_inbound', pageDictFallback('page_profile', 'btn_keeper_confirm_inbound', uiLang.value)),
)
const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)
const emptyList = computed(() =>
  t('page_profile', 'keeper_inbound_empty', pageDictFallback('page_profile', 'keeper_inbound_empty', uiLang.value)),
)
const loadError = computed(() =>
  t('page_profile', 'keeper_inbound_load_error', pageDictFallback('page_profile', 'keeper_inbound_load_error', uiLang.value)),
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
const needFieldsMsg = computed(() =>
  t('page_profile', 'keeper_inbound_need_fields', pageDictFallback('page_profile', 'keeper_inbound_need_fields', uiLang.value)),
)
const confirmOk = computed(() =>
  t('page_profile', 'keeper_inbound_confirm_ok', pageDictFallback('page_profile', 'keeper_inbound_confirm_ok', uiLang.value)),
)
const confirmFail = computed(() =>
  t('page_profile', 'keeper_inbound_confirm_fail', pageDictFallback('page_profile', 'keeper_inbound_confirm_fail', uiLang.value)),
)
const locationIdLabel = computed(() =>
  t('page_profile', 'keeper_inbound_location_id', pageDictFallback('page_profile', 'keeper_inbound_location_id', uiLang.value) || '库位 ID'),
)
const needLocationMsg = computed(() =>
  t(
    'page_profile',
    'keeper_inbound_need_location',
    pageDictFallback('page_profile', 'keeper_inbound_need_location', uiLang.value) || '请填写有效的库位 ID',
  ),
)
const dialogTitle = computed(() =>
  t('page_profile', 'keeper_inbound_confirm_dialog_title', pageDictFallback('page_profile', 'keeper_inbound_confirm_dialog_title', uiLang.value) || '确认入库'),
)
const skuCodeLabel = computed(() =>
  t('page_profile', 'col_keeper_sku_code', pageDictFallback('page_profile', 'col_keeper_sku_code', uiLang.value)),
)
const needSkuCodeMsg = computed(() =>
  t('page_profile', 'keeper_inbound_need_sku_code', pageDictFallback('page_profile', 'keeper_inbound_need_sku_code', uiLang.value) || '请填写 skuCode'),
)
const categoryLabel = computed(() =>
  t('page_profile', 'keeper_inbound_category', pageDictFallback('page_profile', 'keeper_inbound_category', uiLang.value) || '商品分类'),
)
const needCategoryMsg = computed(() =>
  t('page_profile', 'keeper_inbound_need_category', pageDictFallback('page_profile', 'keeper_inbound_need_category', uiLang.value) || '请选择商品分类'),
)
const btnSave = computed(() => t('page_profile', 'btn_save', pageDictFallback('page_profile', 'btn_save', uiLang.value)))
const btnCancel = computed(() => t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value)))

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const actingInboundId = ref('')
const confirmOpen = ref(false)
const confirmRow = ref(null)
const inputSkuCode = ref('')
const inputLocationId = ref('')
const inputCategory = ref('0')

function rowInboundId(row) {
  const v = row?.inboundId ?? row?.inbound_id
  return v != null ? String(v).trim() : ''
}
function rowSkuCode(row) {
  const v = row?.skuCode ?? row?.sku_code
  return v != null ? String(v).trim() : ''
}
function rowSkuName(row) {
  return row?.skuName ?? row?.sku_name ?? ''
}
function rowStatus(row) {
  return row?.status ?? ''
}

async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchKeeperInboundPage(pageNum.value, pageSize.value)
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

async function onConfirmInbound(row) {
  const inboundId = rowInboundId(row)
  if (!inboundId || actingInboundId.value) {
    if (!inboundId) showToast(needFieldsMsg.value, { type: 'warning' })
    return
  }
  confirmRow.value = row
  inputSkuCode.value = rowSkuCode(row) || ''
  inputLocationId.value = ''
  inputCategory.value = '0'
  confirmOpen.value = true
}

function closeConfirmDialog() {
  if (actingInboundId.value) return
  confirmOpen.value = false
  confirmRow.value = null
  inputSkuCode.value = ''
  inputLocationId.value = ''
  inputCategory.value = '0'
}

async function submitConfirmInbound() {
  const row = confirmRow.value
  const inboundId = rowInboundId(row)
  const skuCode = String(inputSkuCode.value || '').trim()
  const locationIdNum = Number(String(inputLocationId.value || '').trim())
  const categoryNum = Number(String(inputCategory.value || '').trim())
  if (!skuCode) {
    showToast(needSkuCodeMsg.value, { type: 'warning' })
    return
  }
  if (!(categoryNum === 0 || categoryNum === 1)) {
    showToast(needCategoryMsg.value, { type: 'warning' })
    return
  }
  if (!Number.isInteger(locationIdNum) || locationIdNum <= 0) {
    showToast(needLocationMsg.value, { type: 'warning' })
    return
  }
  if (!inboundId || !skuCode || actingInboundId.value) return
  actingInboundId.value = inboundId
  try {
    const res = await postKeeperConfirmInbound(inboundId, skuCode, locationIdNum, categoryNum)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    showToast(translateApiMessage('wms_inbound_confirmed', t, uiLang.value) || confirmOk.value, { type: 'success' })
    closeConfirmDialog()
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${confirmFail.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    actingInboundId.value = ''
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
                <th>{{ colInboundId }}</th>
                <th>{{ colSkuCode }}</th>
                <th>{{ colSkuName }}</th>
                <th>{{ colStatus }}</th>
                <th class="th-actions">{{ colActions }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in rows" :key="rowInboundId(row) || `in-${idx}`">
                <td>{{ rowInboundId(row) || valueEmpty }}</td>
                <td>{{ rowSkuCode(row) || valueEmpty }}</td>
                <td>{{ rowSkuName(row) || valueEmpty }}</td>
                <td>{{ rowStatus(row) || valueEmpty }}</td>
                <td class="td-actions">
                  <button
                    type="button"
                    class="btn-action btn-action--ok"
                    :disabled="actingInboundId === rowInboundId(row)"
                    @click="onConfirmInbound(row)"
                  >
                    {{ btnConfirmInbound }}
                  </button>
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

    <div v-if="confirmOpen" class="dialog-backdrop" @click.self="closeConfirmDialog">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ dialogTitle }}</h2>
        <div class="dialog-body">
          <p class="dialog-meta">
            {{ colInboundId }}: {{ rowInboundId(confirmRow) || valueEmpty }} / {{ colSkuCode }}: {{ rowSkuCode(confirmRow) || valueEmpty }}
          </p>
          <label class="field">
            <span class="field-label">{{ skuCodeLabel }}</span>
            <input v-model="inputSkuCode" type="text" class="field-input" />
          </label>
          <label class="field">
            <span class="field-label">{{ locationIdLabel }}</span>
            <input v-model="inputLocationId" type="number" min="1" step="1" class="field-input" />
          </label>
          <label class="field">
            <span class="field-label">{{ categoryLabel }}</span>
            <select v-model="inputCategory" class="field-input">
              <option value="0">普通商品</option>
              <option value="1">特殊商品</option>
            </select>
          </label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="!!actingInboundId" @click="closeConfirmDialog">{{ btnCancel }}</button>
          <button type="button" class="btn-action btn-action--ok" :disabled="!!actingInboundId" @click="submitConfirmInbound">{{ btnSave }}</button>
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
.main-head { margin-bottom: clamp(20px, 2.5vw, 28px); }
.main-title { margin: 0 0 8px; font-size: clamp(22px, 2.2vw, 28px); font-weight: 700; color: #e8eef6; }
.main-lead { margin: 0; font-size: 14px; color: #7a8fa8; }
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
.state-msg { margin: 0; padding: 24px 8px; text-align: center; color: #8b9cb4; }
.state-msg--err { color: #f0a8a8; }
.table-wrap { width: 100%; overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th, .data-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid rgba(36, 48, 68, 0.75); }
.data-table th { font-weight: 600; color: #8fa3bc; white-space: nowrap; }
.data-table td { color: #e8eef6; }
.data-table tbody tr:hover td { background: rgba(61, 141, 255, 0.06); }
.th-actions, .td-actions { text-align: right; white-space: nowrap; }
.btn-action {
  display: inline-flex; align-items: center; justify-content: center; padding: 6px 14px;
  font-size: 12px; font-weight: 700; border-radius: 6px; border: 1px solid transparent; cursor: pointer;
}
.btn-action:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-action--ok { color: #e8fff4; background: linear-gradient(180deg, #2ebc7a, #1e9c62); border-color: rgba(46, 188, 122, 0.5); }
.pager { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; }
.pager-meta { font-size: 13px; font-weight: 600; color: #9eb0c8; min-width: 2ch; text-align: center; }
.pager-btn {
  padding: 8px 14px; font-size: 13px; font-weight: 600; border-radius: 6px; border: 1px solid rgba(61, 141, 255, 0.35);
  background: rgba(61, 141, 255, 0.12); color: #d4e5ff; cursor: pointer;
}
.pager-btn:disabled { opacity: 0.45; cursor: not-allowed; }

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
  max-width: 460px;
  border-radius: 12px;
  padding: 22px 22px 18px;
  background: linear-gradient(160deg, #121a28, #0a0e16);
  border: 1px solid rgba(61, 141, 255, 0.2);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.55);
}

.dialog-title {
  margin: 0 0 12px;
  font-size: 18px;
  font-weight: 700;
  color: #e8eef6;
}

.dialog-body { display: flex; flex-direction: column; gap: 12px; }
.dialog-meta { margin: 0; font-size: 13px; color: #9eb0c8; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 12px; font-weight: 600; color: #8fa3bc; }
.field-input {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.22);
  background: rgba(6, 10, 18, 0.95);
  color: #e8eef6;
  font-size: 14px;
}
.dialog-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 18px; }
.btn-action--ghost {
  color: #9eb0c8;
  background: transparent;
  border-color: rgba(126, 184, 255, 0.25);
}
</style>
