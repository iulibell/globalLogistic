<script setup>
import { computed, reactive, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchKeeperStockById, postKeeperDeleteStock, postKeeperUpdateStock } from '@/api/keeper.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() => t('page_profile', 'section_keeper_stock_query', pageDictFallback('page_profile', 'section_keeper_stock_query', uiLang.value) || '查询库存'))
const lead = computed(() => t('page_profile', 'lead_keeper_stock_query', pageDictFallback('page_profile', 'lead_keeper_stock_query', uiLang.value) || '按库存ID查询并可更新、删除库存。'))
const labelStockId = computed(() => t('page_profile', 'col_keeper_stock_id', pageDictFallback('page_profile', 'col_keeper_stock_id', uiLang.value) || '库存ID'))
const colWarehouseId = computed(() => t('page_profile', 'col_keeper_warehouse_id', pageDictFallback('page_profile', 'col_keeper_warehouse_id', uiLang.value) || '仓库ID'))
const colLocationId = computed(() => t('page_profile', 'col_keeper_location_id', pageDictFallback('page_profile', 'col_keeper_location_id', uiLang.value) || '库位ID'))
const colSkuCode = computed(() => t('page_profile', 'col_keeper_sku_code', pageDictFallback('page_profile', 'col_keeper_sku_code', uiLang.value) || 'SKU 编码'))
const colSkuName = computed(() => t('page_profile', 'col_keeper_sku_name', pageDictFallback('page_profile', 'col_keeper_sku_name', uiLang.value) || 'SKU 名称'))
const colStockQty = computed(() => t('page_profile', 'col_keeper_stock_qty', pageDictFallback('page_profile', 'col_keeper_stock_qty', uiLang.value) || '库存数量'))
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value) || '操作'))
const btnSearch = computed(() => t('page_profile', 'super_btn_search', pageDictFallback('page_profile', 'super_btn_search', uiLang.value) || '查询'))
const btnUpdate = computed(() => t('page_profile', 'btn_keeper_update_stock', pageDictFallback('page_profile', 'btn_keeper_update_stock', uiLang.value) || '更新'))
const btnDelete = computed(() => t('page_profile', 'btn_keeper_delete_stock', pageDictFallback('page_profile', 'btn_keeper_delete_stock', uiLang.value) || '删除'))
const btnCancel = computed(() => t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value) || '取消'))
const btnSave = computed(() => t('page_profile', 'btn_save', pageDictFallback('page_profile', 'btn_save', uiLang.value) || '保存'))
const notFound = computed(() => t('page_profile', 'keeper_stock_not_found', pageDictFallback('page_profile', 'keeper_stock_not_found', uiLang.value) || '未找到该库存'))
const valueEmpty = computed(() => t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value) || '-'))

const stockIdInput = ref('')
const detail = ref(null)
const loading = ref(false)
const acting = ref(false)
const dialogOpen = ref(false)
const form = reactive({ warehouseId: '', locationId: '', skuName: '', skuCode: '', stockQuantity: '' })

function rowStockId(row) {
  const v = row?.stockId
  return v != null ? String(v).trim() : ''
}
function fillForm(row) {
  form.warehouseId = row?.warehouseId != null ? String(row.warehouseId) : ''
  form.locationId = row?.locationId != null ? String(row.locationId) : ''
  form.skuName = row?.skuName != null ? String(row.skuName) : ''
  form.skuCode = row?.skuCode != null ? String(row.skuCode) : ''
  form.stockQuantity = row?.stockQuantity != null ? String(row.stockQuantity) : ''
}
function buildPayload() {
  return {
    stockId: rowStockId(detail.value),
    warehouseId: Number(form.warehouseId),
    locationId: Number(form.locationId),
    skuName: String(form.skuName).trim(),
    skuCode: String(form.skuCode).trim(),
    stockQuantity: Number(form.stockQuantity),
  }
}

async function onSearch() {
  const stockId = String(stockIdInput.value ?? '').trim()
  if (!stockId) {
    showToast(labelStockId.value, { type: 'warning' })
    return
  }
  loading.value = true
  detail.value = null
  try {
    const res = await fetchKeeperStockById(stockId)
    if (res.code !== 200) throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    if (!res.data || typeof res.data !== 'object' || !res.data.stockId) {
      showToast(notFound.value, { type: 'info' })
      return
    }
    detail.value = res.data
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    loading.value = false
  }
}

function openUpdate() {
  if (!detail.value || acting.value) return
  fillForm(detail.value)
  dialogOpen.value = true
}
function closeUpdate() {
  if (acting.value) return
  dialogOpen.value = false
}

async function onUpdate() {
  if (!detail.value || acting.value) return
  const payload = buildPayload()
  if (!payload.stockId || !Number.isFinite(payload.warehouseId) || !Number.isFinite(payload.locationId) || !payload.skuName || !payload.skuCode || !Number.isFinite(payload.stockQuantity)) {
    showToast('请完整填写库存字段', { type: 'warning' })
    return
  }
  acting.value = true
  try {
    const res = await postKeeperUpdateStock(payload)
    if (res.code !== 200) throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    showToast(translateApiMessage(String(res.data ?? res.message ?? 'operation_success'), t, uiLang.value), { type: 'success' })
    dialogOpen.value = false
    await onSearch()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    acting.value = false
  }
}

async function onDelete() {
  if (!detail.value || acting.value) return
  const stockId = rowStockId(detail.value)
  if (!stockId) return
  acting.value = true
  try {
    const res = await postKeeperDeleteStock(stockId)
    if (res.code !== 200) throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    showToast(translateApiMessage(String(res.data ?? res.message ?? 'operation_success'), t, uiLang.value), { type: 'success' })
    detail.value = null
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    acting.value = false
  }
}
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <h1 class="main-title">{{ sectionTitle }}</h1>
      <p class="main-lead">{{ lead }}</p>
    </header>
    <div class="panel">
      <div class="search-row">
        <label class="search-label" for="keeper-stock-id">{{ labelStockId }}</label>
        <input id="keeper-stock-id" v-model="stockIdInput" class="search-input" @keyup.enter="onSearch" />
        <button type="button" class="search-btn" :disabled="loading || acting" @click="onSearch">{{ btnSearch }}</button>
      </div>

      <div v-if="detail" class="detail-card">
        <table class="data-table">
          <thead><tr><th>{{ labelStockId }}</th><th>{{ colWarehouseId }}</th><th>{{ colLocationId }}</th><th>{{ colSkuCode }}</th><th>{{ colSkuName }}</th><th>{{ colStockQty }}</th><th class="th-actions">{{ colActions }}</th></tr></thead>
          <tbody>
            <tr>
              <td>{{ detail.stockId ?? valueEmpty }}</td><td>{{ detail.warehouseId ?? valueEmpty }}</td><td>{{ detail.locationId ?? valueEmpty }}</td><td>{{ detail.skuCode ?? valueEmpty }}</td><td>{{ detail.skuName ?? valueEmpty }}</td><td>{{ detail.stockQuantity ?? valueEmpty }}</td>
              <td class="td-actions"><div class="actions-wrap">
                <button type="button" class="btn-action btn-action--ghost" :disabled="acting" @click="openUpdate">{{ btnUpdate }}</button>
                <button type="button" class="btn-action btn-action--danger" :disabled="acting" @click="onDelete">{{ btnDelete }}</button>
              </div></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="dialogOpen" class="dialog-backdrop" @click.self="closeUpdate">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ btnUpdate }}</h2>
        <div class="dialog-body">
          <label class="field"><span class="field-label">{{ labelStockId }}</span><input :value="rowStockId(detail)" class="field-input" disabled /></label>
          <label class="field"><span class="field-label">{{ colWarehouseId }}</span><input v-model="form.warehouseId" type="number" class="field-input" :disabled="acting" /></label>
          <label class="field"><span class="field-label">{{ colLocationId }}</span><input v-model="form.locationId" type="number" class="field-input" :disabled="acting" /></label>
          <label class="field"><span class="field-label">{{ colSkuName }}</span><input v-model="form.skuName" class="field-input" :disabled="acting" /></label>
          <label class="field"><span class="field-label">{{ colSkuCode }}</span><input v-model="form.skuCode" class="field-input" :disabled="acting" /></label>
          <label class="field"><span class="field-label">{{ colStockQty }}</span><input v-model="form.stockQuantity" type="number" class="field-input" :disabled="acting" /></label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="acting" @click="closeUpdate">{{ btnCancel }}</button>
          <button type="button" class="btn-action btn-action--ok" :disabled="acting" @click="onUpdate">{{ btnSave }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main-inner { flex: 1; display: flex; flex-direction: column; width: 100%; max-width: min(1100px, 100%); margin: 0 auto; }
.main-head { margin-bottom: clamp(20px, 2.5vw, 28px); }
.main-title { margin: 0 0 8px; font-size: clamp(22px, 2.2vw, 28px); font-weight: 700; color: #e8eef6; }
.main-lead { margin: 0; font-size: 14px; color: #7a8fa8; }
.panel { flex: 1; display: flex; flex-direction: column; gap: 24px; min-height: min(320px, calc(100vh - 260px)); border-radius: calc(var(--17-radius-lg) + 2px); padding: clamp(20px, 2.5vw, 32px) clamp(16px, 2.5vw, 28px); background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99)); border: 1px solid rgba(61, 141, 255, 0.14); }
.search-row { display: flex; flex-wrap: wrap; align-items: center; gap: 12px; }
.search-label { font-size: 13px; font-weight: 600; color: #8fa3bc; }
.search-input { flex: 1; min-width: 180px; padding: 10px 14px; font-size: 14px; color: #e8eef6; background: rgba(0, 0, 0, 0.25); border: 1px solid rgba(61, 141, 255, 0.2); border-radius: 8px; }
.search-btn { padding: 10px 18px; font-size: 14px; font-weight: 700; border-radius: 8px; border: 1px solid rgba(61, 141, 255, 0.4); background: linear-gradient(180deg, rgba(61, 141, 255, 0.35), rgba(37, 111, 234, 0.45)); color: #f0f6ff; cursor: pointer; }
.detail-card { border-top: 1px solid rgba(36, 48, 68, 0.75); padding-top: 20px; }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th, .data-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid rgba(36, 48, 68, 0.75); color: #e8eef6; }
.data-table th { color: #8fa3bc; white-space: nowrap; }
.th-actions, .td-actions { width: 190px; text-align: center; white-space: nowrap; }
.actions-wrap { display: inline-flex; justify-content: center; align-items: center; gap: 8px; min-width: 150px; }
.btn-action { display: inline-flex; align-items: center; justify-content: center; padding: 6px 14px; font-size: 12px; font-weight: 700; border-radius: 6px; border: 1px solid transparent; cursor: pointer; }
.btn-action:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-action--ok { color: #e8fff4; background: linear-gradient(180deg, #2ebc7a, #1e9c62); border-color: rgba(46, 188, 122, 0.5); }
.btn-action--ghost { color: #d4e5ff; background: rgba(61, 141, 255, 0.12); border-color: rgba(61, 141, 255, 0.35); }
.btn-action--danger { color: #ffe8e8; background: linear-gradient(180deg, #cb4a4a, #a12f2f); border-color: rgba(203, 74, 74, 0.5); }
.dialog-backdrop { position: fixed; inset: 0; z-index: 80; display: flex; align-items: center; justify-content: center; padding: 24px; background: rgba(2, 6, 12, 0.72); backdrop-filter: blur(4px); }
.dialog { width: 100%; max-width: 440px; border-radius: 12px; padding: 22px 22px 18px; background: linear-gradient(160deg, #121a28, #0a0e16); border: 1px solid rgba(61, 141, 255, 0.2); }
.dialog-title { margin: 0 0 16px; font-size: 18px; font-weight: 700; color: #e8eef6; }
.dialog-body { display: flex; flex-direction: column; gap: 12px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 12px; color: #9eb0c8; }
.field-input { width: 100%; box-sizing: border-box; padding: 10px 12px; border-radius: 8px; border: 1px solid rgba(61, 141, 255, 0.25); background: rgba(5, 9, 14, 0.66); color: #e8eef6; font-size: 13px; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 18px; }
</style>
