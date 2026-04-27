<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchDriverInfo, fetchDriverTransportOrders, postDriverAcceptAssignment } from '@/api/driver.js'
import { useAuthSession } from '@/composables/useAuthSession.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)
const router = useRouter()

const sectionTitle = computed(
  () => t('page_profile', 'section_driver_transport_orders', pageDictFallback('page_profile', 'section_driver_transport_orders', uiLang.value) || '运输派单'),
)
const lead = computed(
  () => t('page_profile', 'lead_driver_transport_orders', pageDictFallback('page_profile', 'lead_driver_transport_orders', uiLang.value) || '系统将按司机当前城市自动筛选可接运输单。'),
)
const colTransportOrderId = computed(
  () => t('page_profile', 'col_transport_order_id', pageDictFallback('page_profile', 'col_transport_order_id', uiLang.value) || '运输单号'),
)
const colOrigin = computed(
  () => t('page_profile', 'col_transport_origin', pageDictFallback('page_profile', 'col_transport_origin', uiLang.value) || '起点'),
)
const colDest = computed(
  () => t('page_profile', 'col_transport_dest', pageDictFallback('page_profile', 'col_transport_dest', uiLang.value) || '终点'),
)
const colFee = computed(
  () => t('page_profile', 'col_transport_fee', pageDictFallback('page_profile', 'col_transport_fee', uiLang.value) || '运费'),
)
const colActions = computed(
  () => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value) || '操作'),
)
const btnAccept = computed(
  () => t('page_profile', 'btn_driver_accept_assignment', pageDictFallback('page_profile', 'btn_driver_accept_assignment', uiLang.value) || '接受'),
)
const btnLineDetail = computed(
  () => t('page_profile', 'btn_driver_line_detail', pageDictFallback('page_profile', 'btn_driver_line_detail', uiLang.value) || '路线详情'),
)
const loadingText = computed(
  () => t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value) || '加载中...'),
)
const emptyList = computed(
  () => t('page_profile', 'driver_transport_empty', pageDictFallback('page_profile', 'driver_transport_empty', uiLang.value) || '暂无可接运输单'),
)
const loadError = computed(
  () => t('page_profile', 'driver_transport_load_error', pageDictFallback('page_profile', 'driver_transport_load_error', uiLang.value) || '加载失败'),
)
const valueEmpty = computed(() => t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value) || '-'))
const btnPrev = computed(() => t('page_profile', 'super_btn_prev', pageDictFallback('page_profile', 'super_btn_prev', uiLang.value) || '上一页'))
const btnNext = computed(() => t('page_profile', 'super_btn_next', pageDictFallback('page_profile', 'super_btn_next', uiLang.value) || '下一页'))
const acceptFailed = computed(
  () => t('page_profile', 'driver_accept_failed', pageDictFallback('page_profile', 'driver_accept_failed', uiLang.value) || '接单失败'),
)
const blockedByStatusText = computed(
  () => t('page_profile', 'driver_transport_blocked_by_status', pageDictFallback('page_profile', 'driver_transport_blocked_by_status', uiLang.value) || '当前状态无法进行接单'),
)

const { profile } = useAuthSession()

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const actingOrderId = ref('')
const blockedByStatus = ref(false)

function isDriverAvailableStatus(s) {
  // 0=上线空闲；1=运输中（仍视为在线）；2=下线休息；3=离职
  return !(s === 2 || s === '2' || s === 3 || s === '3')
}

async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  blockedByStatus.value = false
  try {
    const driverRes = await fetchDriverInfo()
    if (driverRes?.code === 200) {
      const s = driverRes?.data?.status
      if (!isDriverAvailableStatus(s)) {
        rows.value = []
        blockedByStatus.value = true
        return
      }
    }
    const res = await fetchDriverTransportOrders(pageNum.value, pageSize.value)
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

function prevPage() {
  if (pageNum.value <= 1 || loading.value) return
  pageNum.value -= 1
}
function nextPage() {
  if (loading.value || !rows.value.length || rows.value.length < pageSize.value) return
  pageNum.value += 1
}
function fmtFee(v) {
  if (v == null || v === '') return valueEmpty.value
  return String(v)
}

async function onAccept(row) {
  if (blockedByStatus.value) return
  const transportOrderId = row?.transportOrderId != null ? String(row.transportOrderId).trim() : ''
  const driverId = profile.value?.userId != null ? String(profile.value.userId).trim() : ''
  if (!transportOrderId || !driverId || !!actingOrderId.value) return
  actingOrderId.value = transportOrderId
  try {
    const res = await postDriverAcceptAssignment(transportOrderId, driverId)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    showToast(translateApiMessage(String(res.message ?? 'operation_success'), t, uiLang.value), { type: 'success' })
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${acceptFailed.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    actingOrderId.value = ''
  }
}

function goLineDetail(row) {
  const origin = row?.origin != null ? String(row.origin).trim() : ''
  const dest = row?.dest != null ? String(row.dest).trim() : ''
  if (!origin || !dest) return
  router.push({
    name: 'profile-driver-line-detail',
    query: { origin, dest },
  })
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
      <p v-else-if="blockedByStatus" class="state-msg">{{ blockedByStatusText }}</p>
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
              <tr v-for="(row, idx) in rows" :key="row.transportOrderId || `to-${idx}`">
                <td>{{ row.transportOrderId || valueEmpty }}</td>
                <td>{{ row.origin || valueEmpty }}</td>
                <td>{{ row.dest || valueEmpty }}</td>
                <td>{{ fmtFee(row.fee) }}</td>
                <td class="td-actions">
                  <div class="actions-wrap">
                    <button type="button" class="btn-action btn-action--ghost" :disabled="!!actingOrderId" @click="goLineDetail(row)">
                      {{ btnLineDetail }}
                    </button>
                    <button type="button" class="btn-action btn-action--ok" :disabled="actingOrderId === row.transportOrderId" @click="onAccept(row)">
                      {{ btnAccept }}
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
  </div>
</template>

<style scoped>
.main-inner { flex: 1; display: flex; flex-direction: column; width: 100%; max-width: min(1100px, 100%); margin: 0 auto; }
.main-head { margin-bottom: clamp(20px, 2.5vw, 28px); }
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
.th-actions, .td-actions { width: 220px; text-align: center; white-space: nowrap; }
.actions-wrap { display: inline-flex; align-items: center; justify-content: center; gap: 8px; }
.btn-action { display: inline-flex; align-items: center; justify-content: center; padding: 6px 14px; font-size: 12px; font-weight: 700; border-radius: 6px; border: 1px solid transparent; cursor: pointer; }
.btn-action:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-action--ok { color: #e8fff4; background: linear-gradient(180deg, #2ebc7a, #1e9c62); border-color: rgba(46, 188, 122, 0.5); }
.btn-action--ghost { color: #d4e5ff; background: rgba(61, 141, 255, 0.12); border-color: rgba(61, 141, 255, 0.35); }
.pager { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; }
.pager-meta { font-size: 13px; font-weight: 600; color: #9eb0c8; min-width: 2ch; text-align: center; }
.pager-btn { padding: 8px 14px; font-size: 13px; font-weight: 600; border-radius: 6px; border: 1px solid rgba(61, 141, 255, 0.35); background: rgba(61, 141, 255, 0.12); color: #d4e5ff; cursor: pointer; }
.pager-btn:disabled { opacity: 0.45; cursor: not-allowed; }
</style>
