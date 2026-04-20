<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchKeeperOutboundPage, postKeeperConfirmOutbound } from '@/api/keeper.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(
  () => t('page_profile', 'section_keeper_outbound_list', pageDictFallback('page_profile', 'section_keeper_outbound_list', uiLang.value) || '出库列表'),
)
const lead = computed(
  () => t('page_profile', 'lead_keeper_outbound_list', pageDictFallback('page_profile', 'lead_keeper_outbound_list', uiLang.value) || '分页查看待处理出库单并确认出库。'),
)
const colOutboundId = computed(
  () => t('page_profile', 'col_keeper_outbound_id', pageDictFallback('page_profile', 'col_keeper_outbound_id', uiLang.value) || '出库单号'),
)
const colSkuCode = computed(
  () => t('page_profile', 'col_keeper_sku_code', pageDictFallback('page_profile', 'col_keeper_sku_code', uiLang.value) || 'SKU 编码'),
)
const colSkuName = computed(
  () => t('page_profile', 'col_keeper_sku_name', pageDictFallback('page_profile', 'col_keeper_sku_name', uiLang.value) || 'SKU 名称'),
)
const colStatus = computed(() => t('page_profile', 'col_status', pageDictFallback('page_profile', 'col_status', uiLang.value) || '状态'))
const colActions = computed(
  () => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value) || '操作'),
)
const btnConfirm = computed(
  () => t('page_profile', 'btn_keeper_confirm_outbound', pageDictFallback('page_profile', 'btn_keeper_confirm_outbound', uiLang.value) || '确认出库'),
)
const loadingText = computed(
  () => t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value) || '加载中...'),
)
const emptyList = computed(
  () => t('page_profile', 'keeper_outbound_empty', pageDictFallback('page_profile', 'keeper_outbound_empty', uiLang.value) || '暂无出库单'),
)
const loadError = computed(
  () => t('page_profile', 'keeper_outbound_load_error', pageDictFallback('page_profile', 'keeper_outbound_load_error', uiLang.value) || '加载失败'),
)
const confirmFail = computed(
  () => t('page_profile', 'keeper_outbound_confirm_fail', pageDictFallback('page_profile', 'keeper_outbound_confirm_fail', uiLang.value) || '确认出库失败'),
)
const valueEmpty = computed(() => t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value) || '-'))
const btnPrev = computed(() => t('page_profile', 'super_btn_prev', pageDictFallback('page_profile', 'super_btn_prev', uiLang.value) || '上一页'))
const btnNext = computed(() => t('page_profile', 'super_btn_next', pageDictFallback('page_profile', 'super_btn_next', uiLang.value) || '下一页'))

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const actingOutboundId = ref('')

function rowOutboundId(row) {
  const v = row?.outboundId ?? row?.outbound_id
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
  const s = row?.status
  if (s === 1 || s === '1') return '已出库'
  if (s === 0 || s === '0') return '待出库'
  return s != null && s !== '' ? String(s) : valueEmpty.value
}
function canOperate(row) {
  const s = row?.status
  return !(s === 1 || s === '1')
}

async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchKeeperOutboundPage(pageNum.value, pageSize.value)
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

async function onConfirmOutbound(row) {
  const outboundId = rowOutboundId(row)
  if (!outboundId || actingOutboundId.value || !canOperate(row)) return
  actingOutboundId.value = outboundId
  try {
    const res = await postKeeperConfirmOutbound(outboundId)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    showToast(translateApiMessage('wms_outbound_confirmed', t, uiLang.value), { type: 'success' })
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${confirmFail.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    actingOutboundId.value = ''
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
                <th>{{ colOutboundId }}</th>
                <th>{{ colSkuCode }}</th>
                <th>{{ colSkuName }}</th>
                <th>{{ colStatus }}</th>
                <th class="th-actions">{{ colActions }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in rows" :key="rowOutboundId(row) || `out-${idx}`">
                <td>{{ rowOutboundId(row) || valueEmpty }}</td>
                <td>{{ rowSkuCode(row) || valueEmpty }}</td>
                <td>{{ rowSkuName(row) || valueEmpty }}</td>
                <td>{{ rowStatus(row) }}</td>
                <td class="td-actions">
                  <div class="actions-wrap">
                    <button
                      v-if="canOperate(row)"
                      type="button"
                      class="btn-action btn-action--ok"
                      :disabled="actingOutboundId === rowOutboundId(row)"
                      @click="onConfirmOutbound(row)"
                    >
                      {{ btnConfirm }}
                    </button>
                    <span v-else class="op-empty">{{ valueEmpty }}</span>
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
  </div>
</template>

<style scoped>
.main-inner { flex: 1; display: flex; flex-direction: column; width: 100%; max-width: min(1100px, 100%); margin: 0 auto; }
.main-head { margin-bottom: clamp(20px, 2.5vw, 28px); }
.main-title { margin: 0 0 8px; font-size: clamp(22px, 2.2vw, 28px); font-weight: 700; color: #e8eef6; }
.main-lead { margin: 0; font-size: 14px; color: #7a8fa8; }
.panel {
  flex: 1; display: flex; flex-direction: column; min-height: min(360px, calc(100vh - 260px));
  border-radius: calc(var(--17-radius-lg) + 2px); padding: clamp(20px, 2.5vw, 32px) clamp(16px, 2.5vw, 28px);
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
.th-actions, .td-actions { width: 190px; text-align: center; white-space: nowrap; }
.actions-wrap { display: inline-flex; justify-content: center; align-items: center; gap: 8px; min-width: 120px; }
.op-empty { color: #8fa3bc; font-size: 13px; }
.btn-action { display: inline-flex; align-items: center; justify-content: center; padding: 6px 14px; font-size: 12px; font-weight: 700; border-radius: 6px; border: 1px solid transparent; cursor: pointer; }
.btn-action:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-action--ok { color: #e8fff4; background: linear-gradient(180deg, #2ebc7a, #1e9c62); border-color: rgba(46, 188, 122, 0.5); }
.pager { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; }
.pager-meta { font-size: 13px; font-weight: 600; color: #9eb0c8; min-width: 2ch; text-align: center; }
.pager-btn { padding: 8px 14px; font-size: 13px; font-weight: 600; border-radius: 6px; border: 1px solid rgba(61, 141, 255, 0.35); background: rgba(61, 141, 255, 0.12); color: #d4e5ff; cursor: pointer; }
.pager-btn:disabled { opacity: 0.45; cursor: not-allowed; }
</style>
