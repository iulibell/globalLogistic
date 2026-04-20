<script setup>
import { computed, onMounted, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import {
  fetchOrderReviewPage,
  postAccessOrderReview,
  postRejectOrderReview,
} from '@/api/reviewer.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message', 'api_auth'], uiLang)

const sectionTitle = computed(() =>
  t('page_profile', 'section_order_review', pageDictFallback('page_profile', 'section_order_review', uiLang.value)),
)
const lead = computed(() =>
  t('page_profile', 'lead_order_review', pageDictFallback('page_profile', 'lead_order_review', uiLang.value)),
)

const colOrderId = computed(() =>
  t('page_profile', 'col_order_id', pageDictFallback('page_profile', 'col_order_id', uiLang.value)),
)
const colSkuName = computed(() =>
  t('page_profile', 'col_sku_name', pageDictFallback('page_profile', 'col_sku_name', uiLang.value)),
)
const colSkuCode = computed(() =>
  t('page_profile', 'col_sku_code', pageDictFallback('page_profile', 'col_sku_code', uiLang.value)),
)
const colProductType = computed(() =>
  t('page_profile', 'col_product_type', pageDictFallback('page_profile', 'col_product_type', uiLang.value)),
)
const colQuantity = computed(() =>
  t('page_profile', 'col_quantity', pageDictFallback('page_profile', 'col_quantity', uiLang.value)),
)
const colReviewStatus = computed(() =>
  t('page_profile', 'col_review_status', pageDictFallback('page_profile', 'col_review_status', uiLang.value)),
)
const colRemark = computed(() =>
  t('page_profile', 'col_remark', pageDictFallback('page_profile', 'col_remark', uiLang.value)),
)
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value)))
const btnApprove = computed(
  () =>
    t('page_profile', 'btn_approve', pageDictFallback('page_profile', 'btn_approve', uiLang.value)) || '通过',
)
const btnReject = computed(
  () =>
    t('page_profile', 'btn_reject', pageDictFallback('page_profile', 'btn_reject', uiLang.value)) || '退回',
)

const emptyList = computed(() =>
  t('page_profile', 'order_review_empty', pageDictFallback('page_profile', 'order_review_empty', uiLang.value)),
)
const loadError = computed(() =>
  t('page_profile', 'order_review_load_error', pageDictFallback('page_profile', 'order_review_load_error', uiLang.value)),
)
const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)
const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const actingOrderId = ref(null)

function isPending(row) {
  const s = row?.status
  if (s === 1 || s === '1') return false
  // 0：待审核；未写入时视为待审核，便于展示「通过 / 退回」
  return s === 0 || s === '0' || s == null
}

function statusLabel(s) {
  if (s === 0 || s === '0') {
    return t(
      'page_profile',
      'order_review_status_pending',
      pageDictFallback('page_profile', 'order_review_status_pending', uiLang.value),
    )
  }
  if (s === 1 || s === '1') {
    return t(
      'page_profile',
      'order_review_status_done',
      pageDictFallback('page_profile', 'order_review_status_done', uiLang.value),
    )
  }
  return String(s ?? valueEmpty.value)
}

async function loadList() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchOrderReviewPage(1, 50)
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
  const oid = row?.orderId
  if (!oid || !isPending(row) || actingOrderId.value) return
  actingOrderId.value = oid
  try {
    const res = await postAccessOrderReview(oid, '')
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const msg = translateApiMessage(
      res.message && String(res.message).trim() ? String(res.message) : 'order_review_approved_ok',
      t,
      uiLang.value,
    )
    showToast(msg, { type: 'success' })
    await loadList()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    actingOrderId.value = null
  }
}

async function onReject(row) {
  const oid = row?.orderId
  if (!oid || !isPending(row) || actingOrderId.value) return
  actingOrderId.value = oid
  try {
    const res = await postRejectOrderReview(oid, '')
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const msg = translateApiMessage(
      res.message && String(res.message).trim() ? String(res.message) : 'order_review_rejected_ok',
      t,
      uiLang.value,
    )
    showToast(msg, { type: 'success' })
    await loadList()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    actingOrderId.value = null
  }
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
              <th>{{ colOrderId }}</th>
              <th>{{ colSkuName }}</th>
              <th>{{ colSkuCode }}</th>
              <th>{{ colProductType }}</th>
              <th>{{ colQuantity }}</th>
              <th>{{ colReviewStatus }}</th>
              <th>{{ colRemark }}</th>
              <th class="th-actions">{{ colActions }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.orderId || row.id">
              <td>{{ row.orderId || valueEmpty }}</td>
              <td>{{ row.skuName || valueEmpty }}</td>
              <td>{{ row.skuCode || valueEmpty }}</td>
              <td>{{ row.type || valueEmpty }}</td>
              <td>{{ row.quantity != null ? row.quantity : valueEmpty }}</td>
              <td>{{ statusLabel(row.status) }}</td>
              <td class="td-remark">{{ row.remark || valueEmpty }}</td>
              <td class="td-actions">
                <template v-if="isPending(row)">
                  <button
                    type="button"
                    class="btn-action btn-action--ok"
                    :disabled="actingOrderId === row.orderId"
                    @click="onApprove(row)"
                  >
                    {{ btnApprove }}
                  </button>
                  <button
                    type="button"
                    class="btn-action btn-action--danger"
                    :disabled="actingOrderId === row.orderId"
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

.td-remark {
  max-width: 200px;
  word-break: break-word;
  font-size: 13px;
  color: #b8c5d9;
}

.data-table tbody tr:hover td {
  background: rgba(61, 141, 255, 0.06);
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

@media (max-width: 768px) {
  .data-table th,
  .data-table td {
    padding: 10px 8px;
    font-size: 13px;
  }
}
</style>
