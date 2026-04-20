<script setup>
import { computed, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchKeeperOutboundById, postKeeperConfirmOutbound } from '@/api/keeper.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(
  () => t('page_profile', 'section_keeper_outbound_query', pageDictFallback('page_profile', 'section_keeper_outbound_query', uiLang.value) || '出库查询'),
)
const lead = computed(
  () => t('page_profile', 'lead_keeper_outbound_query', pageDictFallback('page_profile', 'lead_keeper_outbound_query', uiLang.value) || '按出库单号查询并确认出库。'),
)
const labelOutboundId = computed(
  () => t('page_profile', 'col_keeper_outbound_id', pageDictFallback('page_profile', 'col_keeper_outbound_id', uiLang.value) || '出库单号'),
)
const btnSearch = computed(
  () => t('page_profile', 'super_btn_search', pageDictFallback('page_profile', 'super_btn_search', uiLang.value) || '查询'),
)
const colStatus = computed(() => t('page_profile', 'col_status', pageDictFallback('page_profile', 'col_status', uiLang.value) || '状态'))
const colActions = computed(
  () => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value) || '操作'),
)
const btnConfirm = computed(
  () => t('page_profile', 'btn_keeper_confirm_outbound', pageDictFallback('page_profile', 'btn_keeper_confirm_outbound', uiLang.value) || '确认出库'),
)
const notFound = computed(
  () => t('page_profile', 'keeper_outbound_not_found', pageDictFallback('page_profile', 'keeper_outbound_not_found', uiLang.value) || '未找到该出库单'),
)
const confirmFail = computed(
  () => t('page_profile', 'keeper_outbound_confirm_fail', pageDictFallback('page_profile', 'keeper_outbound_confirm_fail', uiLang.value) || '确认出库失败'),
)
const valueEmpty = computed(() => t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value) || '-'))

const outboundIdInput = ref('')
const detail = ref(null)
const loading = ref(false)
const actingOutboundId = ref('')

function rowOutboundId(row) {
  const v = row?.outboundId ?? row?.outbound_id
  return v != null ? String(v).trim() : ''
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

async function onSearch() {
  const outboundId = String(outboundIdInput.value ?? '').trim()
  if (!outboundId) {
    showToast(labelOutboundId.value, { type: 'warning' })
    return
  }
  loading.value = true
  detail.value = null
  try {
    const res = await fetchKeeperOutboundById(outboundId)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    if (!res.data || typeof res.data !== 'object') {
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

async function onConfirm() {
  if (!detail.value || !canOperate(detail.value) || actingOutboundId.value) return
  const outboundId = rowOutboundId(detail.value)
  if (!outboundId) return
  actingOutboundId.value = outboundId
  try {
    const res = await postKeeperConfirmOutbound(outboundId)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    showToast(translateApiMessage('wms_outbound_confirmed', t, uiLang.value), { type: 'success' })
    await onSearch()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${confirmFail.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    actingOutboundId.value = ''
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
        <label class="search-label" for="keeper-outbound-id">{{ labelOutboundId }}</label>
        <input id="keeper-outbound-id" v-model="outboundIdInput" type="text" class="search-input" autocomplete="off" @keyup.enter="onSearch" />
        <button type="button" class="search-btn" :disabled="loading" @click="onSearch">{{ btnSearch }}</button>
      </div>

      <div v-if="detail" class="detail-card">
        <table class="data-table">
          <thead>
            <tr>
              <th>{{ labelOutboundId }}</th>
              <th>{{ colStatus }}</th>
              <th class="th-actions">{{ colActions }}</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ rowOutboundId(detail) || valueEmpty }}</td>
              <td>{{ rowStatus(detail) }}</td>
              <td class="td-actions">
                <div class="actions-wrap">
                  <button
                    v-if="canOperate(detail)"
                    type="button"
                    class="btn-action btn-action--ok"
                    :disabled="!!actingOutboundId"
                    @click="onConfirm"
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
    </div>
  </div>
</template>

<style scoped>
.main-inner { flex: 1; display: flex; flex-direction: column; width: 100%; max-width: min(860px, 100%); margin: 0 auto; }
.main-head { margin-bottom: clamp(20px, 2.5vw, 28px); }
.main-title { margin: 0 0 8px; font-size: clamp(22px, 2.2vw, 28px); font-weight: 700; color: #e8eef6; }
.main-lead { margin: 0; font-size: 14px; color: #7a8fa8; }
.panel {
  flex: 1; display: flex; flex-direction: column; gap: 24px; min-height: min(320px, calc(100vh - 260px));
  border-radius: calc(var(--17-radius-lg) + 2px); padding: clamp(20px, 2.5vw, 32px) clamp(16px, 2.5vw, 28px);
  background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99));
  border: 1px solid rgba(61, 141, 255, 0.14);
}
.search-row { display: flex; flex-wrap: wrap; align-items: center; gap: 12px; }
.search-label { font-size: 13px; font-weight: 600; color: #8fa3bc; }
.search-input {
  flex: 1; min-width: 180px; padding: 10px 14px; font-size: 14px; color: #e8eef6; background: rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(61, 141, 255, 0.2); border-radius: 8px;
}
.search-btn {
  padding: 10px 18px; font-size: 14px; font-weight: 700; border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.4); background: linear-gradient(180deg, rgba(61, 141, 255, 0.35), rgba(37, 111, 234, 0.45));
  color: #f0f6ff; cursor: pointer;
}
.detail-card { border-top: 1px solid rgba(36, 48, 68, 0.75); padding-top: 20px; }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th, .data-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid rgba(36, 48, 68, 0.75); color: #e8eef6; }
.data-table th { color: #8fa3bc; }
.th-actions, .td-actions { width: 190px; text-align: center; white-space: nowrap; }
.actions-wrap { display: inline-flex; justify-content: center; align-items: center; gap: 8px; min-width: 120px; }
.op-empty { color: #8fa3bc; font-size: 13px; }
.btn-action { display: inline-flex; align-items: center; justify-content: center; padding: 6px 14px; font-size: 12px; font-weight: 700; border-radius: 6px; border: 1px solid transparent; cursor: pointer; }
.btn-action:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-action--ok { color: #e8fff4; background: linear-gradient(180deg, #2ebc7a, #1e9c62); border-color: rgba(46, 188, 122, 0.5); }
</style>
