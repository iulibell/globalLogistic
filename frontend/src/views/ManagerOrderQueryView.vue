<script setup>
import { computed, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchManagerOrderById } from '@/api/manager.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() =>
  t('page_profile', 'section_manager_order_query', pageDictFallback('page_profile', 'section_manager_order_query', uiLang.value)),
)
const lead = computed(() =>
  t('page_profile', 'lead_manager_order_query', pageDictFallback('page_profile', 'lead_manager_order_query', uiLang.value)),
)

const labelOrderId = computed(() =>
  t('page_profile', 'col_order_id', pageDictFallback('page_profile', 'col_order_id', uiLang.value)),
)
const btnSearch = computed(() =>
  t('page_profile', 'super_btn_search', pageDictFallback('page_profile', 'super_btn_search', uiLang.value)),
)
const hint = computed(() =>
  t('page_profile', 'manager_order_query_hint', pageDictFallback('page_profile', 'manager_order_query_hint', uiLang.value)),
)
const notFound = computed(() =>
  t('page_profile', 'manager_order_not_found', pageDictFallback('page_profile', 'manager_order_not_found', uiLang.value)),
)
const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)

const orderIdInput = ref('')
const detail = ref(null)
const loading = ref(false)

const fieldRows = computed(() => {
  const o = detail.value
  if (!o || typeof o !== 'object') return []
  const entries = [
    ['orderId', o.orderId],
    ['merchantId', o.merchantId],
    ['userId', o.userId],
    ['goodsId', o.goodsId],
    ['warehouseId', o.warehouseId],
    ['locationId', o.locationId],
    ['skuName', o.skuName],
    ['skuCode', o.skuCode],
    ['price', o.price],
    ['userPhone', o.userPhone],
    ['merchantPhone', o.merchantPhone],
    ['warehouseCity', o.warehouseCity],
    ['city', o.city],
    ['category', o.category],
    ['quantity', o.quantity],
    ['type', o.type],
    ['remark', o.remark],
  ]
  return entries.map(([key, val]) => ({
    key,
    label:
      pageDictFallback('page_profile', `manager_order_field_${key}`, uiLang.value) ||
      String(key),
    value: val != null && val !== '' ? String(val) : valueEmpty.value,
  }))
})

async function onSearch() {
  const id = orderIdInput.value.trim()
  if (!id) {
    showToast(hint.value, { type: 'warning' })
    return
  }
  loading.value = true
  detail.value = null
  try {
    const res = await fetchManagerOrderById(id)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const d = res.data
    if (d == null || typeof d !== 'object' || !String(d.orderId ?? '').trim()) {
      detail.value = null
      showToast(notFound.value, { type: 'info' })
      return
    }
    detail.value = d
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    loading.value = false
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
        <label class="search-label" for="mgr-order-id">{{ labelOrderId }}</label>
        <input
          id="mgr-order-id"
          v-model="orderIdInput"
          type="text"
          class="search-input"
          autocomplete="off"
          @keyup.enter="onSearch"
        />
        <button type="button" class="search-btn" :disabled="loading" @click="onSearch">
          {{ btnSearch }}
        </button>
      </div>

      <div v-if="detail" class="detail-card">
        <dl class="detail-dl">
          <div v-for="row in fieldRows" :key="row.key" class="detail-row">
            <dt>{{ row.label }}</dt>
            <dd>{{ row.value }}</dd>
          </div>
        </dl>
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
  max-width: min(720px, 100%);
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
  gap: 24px;
  min-height: min(320px, calc(100vh - 260px));
  border-radius: calc(var(--17-radius-lg) + 2px);
  padding: clamp(20px, 2.5vw, 32px) clamp(16px, 2.5vw, 28px);
  background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99));
  border: 1px solid rgba(61, 141, 255, 0.14);
  box-shadow:
    0 0 0 1px rgba(0, 0, 0, 0.4) inset,
    0 12px 40px rgba(0, 0, 0, 0.35);
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.search-label {
  font-size: 13px;
  font-weight: 600;
  color: #8fa3bc;
}

.search-input {
  flex: 1;
  min-width: 160px;
  padding: 10px 14px;
  font-size: 14px;
  font-family: inherit;
  color: #e8eef6;
  background: rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(61, 141, 255, 0.2);
  border-radius: 8px;
}

.search-input:focus {
  outline: none;
  border-color: rgba(90, 163, 255, 0.55);
}

.search-btn {
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 700;
  font-family: inherit;
  border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.4);
  background: linear-gradient(180deg, rgba(61, 141, 255, 0.35), rgba(37, 111, 234, 0.45));
  color: #f0f6ff;
  cursor: pointer;
  transition: filter 0.15s;
}

.search-btn:hover:not(:disabled) {
  filter: brightness(1.08);
}

.search-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.detail-card {
  border-top: 1px solid rgba(36, 48, 68, 0.75);
  padding-top: 20px;
}

.detail-dl {
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.detail-row {
  display: grid;
  grid-template-columns: minmax(120px, 38%) 1fr;
  gap: 12px 20px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(36, 48, 68, 0.5);
  font-size: 14px;
}

.detail-row dt {
  margin: 0;
  font-weight: 600;
  color: #8fa3bc;
}

.detail-row dd {
  margin: 0;
  color: #e8eef6;
  word-break: break-word;
}

@media (max-width: 520px) {
  .detail-row {
    grid-template-columns: 1fr;
    gap: 4px;
  }
}
</style>
