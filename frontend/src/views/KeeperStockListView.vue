<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchKeeperStockPage, postKeeperAddStock, postKeeperUpdateStock, postKeeperDeleteStock } from '@/api/keeper.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() => t('page_profile', 'section_keeper_stock_list', pageDictFallback('page_profile', 'section_keeper_stock_list', uiLang.value) || '库存列表'))
const lead = computed(() => t('page_profile', 'lead_keeper_stock_list', pageDictFallback('page_profile', 'lead_keeper_stock_list', uiLang.value) || '分页查看库存，并可新增库存。'))
const colStockId = computed(() => t('page_profile', 'col_keeper_stock_id', pageDictFallback('page_profile', 'col_keeper_stock_id', uiLang.value) || '库存ID'))
const colWarehouseId = computed(() => t('page_profile', 'col_keeper_warehouse_id', pageDictFallback('page_profile', 'col_keeper_warehouse_id', uiLang.value) || '仓库ID'))
const colLocationId = computed(() => t('page_profile', 'col_keeper_location_id', pageDictFallback('page_profile', 'col_keeper_location_id', uiLang.value) || '库位ID'))
const colSkuCode = computed(() => t('page_profile', 'col_keeper_sku_code', pageDictFallback('page_profile', 'col_keeper_sku_code', uiLang.value) || 'SKU 编码'))
const colSkuName = computed(() => t('page_profile', 'col_keeper_sku_name', pageDictFallback('page_profile', 'col_keeper_sku_name', uiLang.value) || 'SKU 名称'))
const colStockQty = computed(() => t('page_profile', 'col_keeper_stock_qty', pageDictFallback('page_profile', 'col_keeper_stock_qty', uiLang.value) || '库存数量'))
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value) || '操作'))
const btnAdd = computed(() => t('page_profile', 'btn_keeper_add_stock', pageDictFallback('page_profile', 'btn_keeper_add_stock', uiLang.value) || '添加库存'))
const btnUpdate = computed(() => t('page_profile', 'btn_keeper_update_stock', pageDictFallback('page_profile', 'btn_keeper_update_stock', uiLang.value) || '更新'))
const btnDelete = computed(() => t('page_profile', 'btn_keeper_delete_stock', pageDictFallback('page_profile', 'btn_keeper_delete_stock', uiLang.value) || '删除'))
const loadingText = computed(() => t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value) || '加载中...'))
const emptyList = computed(() => t('page_profile', 'keeper_stock_empty', pageDictFallback('page_profile', 'keeper_stock_empty', uiLang.value) || '暂无库存数据'))
const loadError = computed(() => t('page_profile', 'keeper_stock_load_error', pageDictFallback('page_profile', 'keeper_stock_load_error', uiLang.value) || '加载失败'))
const saveFail = computed(() => t('page_profile', 'keeper_stock_save_fail', pageDictFallback('page_profile', 'keeper_stock_save_fail', uiLang.value) || '保存库存失败'))
const valueEmpty = computed(() => t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value) || '-'))
const btnPrev = computed(() => t('page_profile', 'super_btn_prev', pageDictFallback('page_profile', 'super_btn_prev', uiLang.value) || '上一页'))
const btnNext = computed(() => t('page_profile', 'super_btn_next', pageDictFallback('page_profile', 'super_btn_next', uiLang.value) || '下一页'))
const btnCancel = computed(() => t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value) || '取消'))
const btnSave = computed(() => t('page_profile', 'btn_save', pageDictFallback('page_profile', 'btn_save', uiLang.value) || '保存'))

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const dialogOpen = ref(false)
const dialogSaving = ref(false)
const dialogMode = ref('add')
const form = reactive({ stockId: '', warehouseId: '', locationId: '', skuName: '', skuCode: '', stockQuantity: '' })

function rowStockId(row) {
  return row?.stockId != null ? String(row.stockId).trim() : ''
}
function resetForm() {
  form.stockId = ''
  form.warehouseId = ''
  form.locationId = ''
  form.skuName = ''
  form.skuCode = ''
  form.stockQuantity = ''
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
  form.stockId = row?.stockId != null ? String(row.stockId) : ''
  form.warehouseId = row?.warehouseId != null ? String(row.warehouseId) : ''
  form.locationId = row?.locationId != null ? String(row.locationId) : ''
  form.skuName = row?.skuName != null ? String(row.skuName) : ''
  form.skuCode = row?.skuCode != null ? String(row.skuCode) : ''
  form.stockQuantity = row?.stockQuantity != null ? String(row.stockQuantity) : ''
  dialogOpen.value = true
}
function closeDialog() {
  if (dialogSaving.value) return
  dialogOpen.value = false
}
function toPayload() {
  return {
    stockId: String(form.stockId).trim(),
    warehouseId: Number(form.warehouseId),
    locationId: Number(form.locationId),
    skuName: String(form.skuName).trim(),
    skuCode: String(form.skuCode).trim(),
    stockQuantity: Number(form.stockQuantity),
  }
}
function validatePayload(p) {
  if (!p.stockId) return colStockId.value
  if (!Number.isFinite(p.warehouseId)) return colWarehouseId.value
  if (!Number.isFinite(p.locationId)) return colLocationId.value
  if (!p.skuName) return colSkuName.value
  if (!p.skuCode) return colSkuCode.value
  if (!Number.isFinite(p.stockQuantity)) return colStockQty.value
  return ''
}
async function submitDialog() {
  const payload = toPayload()
  const miss = validatePayload(payload)
  if (miss) {
    showToast(`${miss}不能为空`, { type: 'warning' })
    return
  }
  dialogSaving.value = true
  try {
    const res = dialogMode.value === 'add' ? await postKeeperAddStock(payload) : await postKeeperUpdateStock(payload)
    if (res.code !== 200) throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    showToast(translateApiMessage(String(res.data ?? res.message ?? 'operation_success'), t, uiLang.value), { type: 'success' })
    dialogOpen.value = false
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${saveFail.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    dialogSaving.value = false
  }
}
async function onDelete(row) {
  const stockId = rowStockId(row)
  if (!stockId || dialogSaving.value) return
  dialogSaving.value = true
  try {
    const res = await postKeeperDeleteStock(stockId)
    if (res.code !== 200) throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    showToast(translateApiMessage(String(res.data ?? res.message ?? 'operation_success'), t, uiLang.value), { type: 'success' })
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    dialogSaving.value = false
  }
}
async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchKeeperStockPage(pageNum.value, pageSize.value)
    if (res.code !== 200) throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    rows.value = Array.isArray(res.data) ? res.data : []
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
        <button type="button" class="btn-action btn-action--ok" :disabled="loading || dialogSaving" @click="openAddDialog">{{ btnAdd }}</button>
      </div>
    </header>
    <div class="panel">
      <p v-if="loading" class="state-msg">{{ loadingText }}</p>
      <p v-else-if="errorMsg" class="state-msg state-msg--err">{{ loadError }}：{{ errorMsg }}</p>
      <p v-else-if="!rows.length" class="state-msg">{{ emptyList }}</p>
      <template v-else>
        <div class="table-wrap">
          <table class="data-table">
            <thead><tr><th>{{ colStockId }}</th><th>{{ colWarehouseId }}</th><th>{{ colLocationId }}</th><th>{{ colSkuCode }}</th><th>{{ colSkuName }}</th><th>{{ colStockQty }}</th><th class="th-actions">{{ colActions }}</th></tr></thead>
            <tbody>
              <tr v-for="(row, idx) in rows" :key="rowStockId(row) || `st-${idx}`">
                <td>{{ row.stockId ?? valueEmpty }}</td><td>{{ row.warehouseId ?? valueEmpty }}</td><td>{{ row.locationId ?? valueEmpty }}</td><td>{{ row.skuCode ?? valueEmpty }}</td><td>{{ row.skuName ?? valueEmpty }}</td><td>{{ row.stockQuantity ?? valueEmpty }}</td>
                <td class="td-actions"><div class="actions-wrap">
                  <button type="button" class="btn-action btn-action--ghost" :disabled="dialogSaving" @click="openUpdateDialog(row)">{{ btnUpdate }}</button>
                  <button type="button" class="btn-action btn-action--danger" :disabled="dialogSaving" @click="onDelete(row)">{{ btnDelete }}</button>
                </div></td>
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
        <h2 class="dialog-title">{{ dialogMode === 'add' ? btnAdd : btnUpdate }}</h2>
        <div class="dialog-body">
          <label class="field"><span class="field-label">{{ colStockId }}</span><input v-model="form.stockId" class="field-input" :disabled="dialogSaving || dialogMode === 'update'" /></label>
          <label class="field"><span class="field-label">{{ colWarehouseId }}</span><input v-model="form.warehouseId" type="number" class="field-input" :disabled="dialogSaving" /></label>
          <label class="field"><span class="field-label">{{ colLocationId }}</span><input v-model="form.locationId" type="number" class="field-input" :disabled="dialogSaving" /></label>
          <label class="field"><span class="field-label">{{ colSkuName }}</span><input v-model="form.skuName" class="field-input" :disabled="dialogSaving" /></label>
          <label class="field"><span class="field-label">{{ colSkuCode }}</span><input v-model="form.skuCode" class="field-input" :disabled="dialogSaving" /></label>
          <label class="field"><span class="field-label">{{ colStockQty }}</span><input v-model="form.stockQuantity" type="number" class="field-input" :disabled="dialogSaving" /></label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="dialogSaving" @click="closeDialog">{{ btnCancel }}</button>
          <button type="button" class="btn-action btn-action--ok" :disabled="dialogSaving" @click="submitDialog">{{ btnSave }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main-inner { flex: 1; display: flex; flex-direction: column; width: 100%; max-width: min(1100px, 100%); margin: 0 auto; }
.main-head { margin-bottom: clamp(20px, 2.5vw, 28px); }
.head-actions { margin-top: 14px; display: flex; justify-content: flex-end; }
.main-title { margin: 0 0 8px; font-size: clamp(22px, 2.2vw, 28px); font-weight: 700; color: #e8eef6; }
.main-lead { margin: 0; font-size: 14px; color: #7a8fa8; }
.panel { flex: 1; display: flex; flex-direction: column; min-height: min(360px, calc(100vh - 260px)); border-radius: calc(var(--17-radius-lg) + 2px); padding: clamp(20px, 2.5vw, 32px) clamp(16px, 2.5vw, 28px); background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99)); border: 1px solid rgba(61, 141, 255, 0.14); }
.state-msg { margin: 0; padding: 24px 8px; text-align: center; color: #8b9cb4; }
.state-msg--err { color: #f0a8a8; }
.table-wrap { width: 100%; overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th, .data-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid rgba(36, 48, 68, 0.75); }
.data-table th { font-weight: 600; color: #8fa3bc; white-space: nowrap; }
.data-table td { color: #e8eef6; }
.th-actions, .td-actions { width: 190px; text-align: center; white-space: nowrap; }
.actions-wrap { display: inline-flex; justify-content: center; align-items: center; gap: 8px; min-width: 150px; }
.pager { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; }
.pager-meta { font-size: 13px; font-weight: 600; color: #9eb0c8; min-width: 2ch; text-align: center; }
.pager-btn { padding: 8px 14px; font-size: 13px; font-weight: 600; border-radius: 6px; border: 1px solid rgba(61, 141, 255, 0.35); background: rgba(61, 141, 255, 0.12); color: #d4e5ff; cursor: pointer; }
.pager-btn:disabled { opacity: 0.45; cursor: not-allowed; }
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
