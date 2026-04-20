<script setup>
import { computed, onMounted, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import {
  fetchDriverCurrentAssignmentDetail,
  fetchDriverLineDetail,
  postDriverConfirmArrived,
  postDriverConfirmDeparture,
  postDriverConfirmReceived,
} from '@/api/driver.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const title = computed(() => t('page_profile', 'section_driver_current_assignment', pageDictFallback('page_profile', 'section_driver_current_assignment', uiLang.value) || '当前派单'))
const lead = computed(() => t('page_profile', 'lead_driver_current_assignment', pageDictFallback('page_profile', 'lead_driver_current_assignment', uiLang.value) || '上半部分展示路线详情，下半部分展示运输单详情。'))
const loadingText = computed(() => t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)))
const valueEmpty = computed(() => t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)))
const emptyText = computed(() => t('page_profile', 'driver_current_assignment_empty', pageDictFallback('page_profile', 'driver_current_assignment_empty', uiLang.value) || '当前没有进行中的派单'))
const loadErrorLabel = computed(() => t('page_profile', 'driver_current_assignment_load_error', pageDictFallback('page_profile', 'driver_current_assignment_load_error', uiLang.value) || '加载失败'))
const sectionRoute = computed(() => t('page_profile', 'section_driver_route_detail', pageDictFallback('page_profile', 'section_driver_route_detail', uiLang.value) || '路线详情'))
const sectionOrder = computed(() => t('page_profile', 'section_driver_order_detail', pageDictFallback('page_profile', 'section_driver_order_detail', uiLang.value) || '运输单详情'))
const btnDeparture = computed(() => t('page_profile', 'btn_driver_confirm_departure', pageDictFallback('page_profile', 'btn_driver_confirm_departure', uiLang.value) || '确认发车'))
const btnArrived = computed(() => t('page_profile', 'btn_driver_confirm_arrived', pageDictFallback('page_profile', 'btn_driver_confirm_arrived', uiLang.value) || '确认送达'))
const btnReceived = computed(() => t('page_profile', 'btn_driver_confirm_received', pageDictFallback('page_profile', 'btn_driver_confirm_received', uiLang.value) || '确认签收'))

const loading = ref(false)
const errorMsg = ref('')
const order = ref(null)
const lineDetail = ref(null)
const actionLoading = ref(false)

function fmt(v) {
  return v == null || String(v).trim() === '' ? valueEmpty.value : String(v)
}

function transportStatusLabel(s) {
  if (s === 1 || s === '1') return '已接单'
  if (s === 2 || s === '2') return '运输中'
  if (s === 3 || s === '3') return '已送达'
  if (s === 4 || s === '4') return '已签收'
  return fmt(s)
}

function canDeparture() {
  return order.value?.status === 1 || order.value?.status === '1'
}
function canArrived() {
  return order.value?.status === 2 || order.value?.status === '2'
}
function canReceived() {
  return order.value?.status === 3 || order.value?.status === '3'
}

async function loadData() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchDriverCurrentAssignmentDetail()
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    order.value = res.data && typeof res.data === 'object' ? res.data : null
    lineDetail.value = null
    if (order.value?.origin && order.value?.dest) {
      const lineRes = await fetchDriverLineDetail(order.value.origin, order.value.dest)
      if (lineRes.code === 200 && lineRes.data && typeof lineRes.data === 'object') {
        lineDetail.value = lineRes.data
      }
    }
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    errorMsg.value = translateApiMessage(raw, t, uiLang.value)
    order.value = null
    lineDetail.value = null
  } finally {
    loading.value = false
  }
}

async function doConfirm(action) {
  const id = order.value?.transportOrderId ? String(order.value.transportOrderId).trim() : ''
  if (!id || actionLoading.value) return
  actionLoading.value = true
  try {
    let res
    if (action === 'departure') {
      res = await postDriverConfirmDeparture(id, order.value?.origin || '')
    } else if (action === 'arrived') {
      res = await postDriverConfirmArrived(id)
    } else {
      res = await postDriverConfirmReceived(id)
    }
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const okRaw = res.data != null && String(res.data).trim() ? String(res.data) : String(res.message ?? 'operation_success')
    showToast(translateApiMessage(okRaw, t, uiLang.value), { type: 'success' })
    await loadData()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(translateApiMessage(raw, t, uiLang.value), { type: 'error' })
  } finally {
    actionLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <h1 class="main-title">{{ title }}</h1>
      <p class="main-lead">{{ lead }}</p>
    </header>
    <div class="panel">
      <p v-if="loading" class="state-msg">{{ loadingText }}</p>
      <p v-else-if="errorMsg" class="state-msg state-msg--err">{{ loadErrorLabel }}：{{ errorMsg }}</p>
      <p v-else-if="!order" class="state-msg">{{ emptyText }}</p>
      <div v-else class="detail-stack">
        <section class="box">
          <h3 class="box-title">{{ sectionRoute }}</h3>
          <div class="map-grid">
            <div class="map-dot map-dot--start" />
            <div class="map-dot map-dot--end" />
            <div class="map-route" />
            <p class="map-label map-label--start">{{ fmt(order.origin) }}</p>
            <p class="map-label map-label--end">{{ fmt(order.dest) }}</p>
          </div>
          <div class="info-grid">
            <div class="info-item"><p class="info-k">业务路线 ID</p><p class="info-v">{{ fmt(lineDetail?.lineId) }}</p></div>
            <div class="info-item"><p class="info-k">预计耗时</p><p class="info-v">{{ fmt(lineDetail?.estimation) }}</p></div>
          </div>
        </section>
        <section class="box">
          <h3 class="box-title">{{ sectionOrder }}</h3>
          <div class="info-grid info-grid--order">
            <div class="info-item"><p class="info-k">运输单号</p><p class="info-v">{{ fmt(order.transportOrderId) }}</p></div>
            <div class="info-item"><p class="info-k">订单号</p><p class="info-v">{{ fmt(order.orderId) }}</p></div>
            <div class="info-item"><p class="info-k">起点</p><p class="info-v">{{ fmt(order.origin) }}</p></div>
            <div class="info-item"><p class="info-k">终点</p><p class="info-v">{{ fmt(order.dest) }}</p></div>
            <div class="info-item"><p class="info-k">联系电话</p><p class="info-v">{{ fmt(order.phone) }}</p></div>
            <div class="info-item"><p class="info-k">运费</p><p class="info-v">{{ fmt(order.fee) }}</p></div>
            <div class="info-item"><p class="info-k">状态</p><p class="info-v">{{ transportStatusLabel(order.status) }}</p></div>
          </div>
          <div class="actions-wrap">
            <button type="button" class="btn-action btn-action--ok" :disabled="actionLoading || !canDeparture()" @click="doConfirm('departure')">{{ btnDeparture }}</button>
            <button type="button" class="btn-action btn-action--ghost" :disabled="actionLoading || !canArrived()" @click="doConfirm('arrived')">{{ btnArrived }}</button>
            <button type="button" class="btn-action btn-action--primary" :disabled="actionLoading || !canReceived()" @click="doConfirm('received')">{{ btnReceived }}</button>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main-inner { flex: 1; display: flex; flex-direction: column; width: 100%; max-width: min(1100px, 100%); margin: 0 auto; }
.main-head { margin-bottom: clamp(20px, 2.5vw, 28px); flex-shrink: 0; }
.main-title { margin: 0 0 8px; font-size: clamp(22px, 2.2vw, 28px); font-weight: 700; color: #e8eef6; letter-spacing: -0.02em; }
.main-lead { margin: 0; font-size: clamp(13px, 1.1vw, 15px); line-height: 1.45; color: #7a8fa8; }
.panel { flex: 1; display: flex; flex-direction: column; min-height: min(360px, calc(100vh - 260px)); border-radius: calc(var(--17-radius-lg) + 2px); padding: clamp(20px, 2.5vw, 32px) clamp(16px, 2.5vw, 28px); background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99)); border: 1px solid rgba(61, 141, 255, 0.14); box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.4) inset, 0 12px 40px rgba(0, 0, 0, 0.35); }
.state-msg { margin: 0; padding: 24px 8px; text-align: center; font-size: 14px; color: #8b9cb4; }
.state-msg--err { color: #f0a8a8; }
.detail-stack { display: flex; flex-direction: column; gap: 14px; }
.box { border: 1px solid rgba(61, 141, 255, 0.18); border-radius: 12px; padding: 14px; background: rgba(6, 12, 20, 0.45); }
.box-title { margin: 0 0 10px; font-size: 16px; font-weight: 700; color: #dce9f8; }
.map-grid { position: relative; width: 100%; height: 180px; border-radius: 10px; background: linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px), radial-gradient(circle at 25% 20%, rgba(75, 145, 255, 0.24), transparent 50%), radial-gradient(circle at 75% 80%, rgba(46, 188, 122, 0.18), transparent 45%), #091220; background-size: 28px 28px, 28px 28px, 100% 100%, 100% 100%, auto; overflow: hidden; margin-bottom: 10px; }
.map-route { position: absolute; left: 18%; right: 18%; top: 52%; height: 3px; border-radius: 99px; background: linear-gradient(90deg, #5aa3ff, #2ebc7a); box-shadow: 0 0 18px rgba(90, 163, 255, 0.5); }
.map-dot { position: absolute; width: 12px; height: 12px; border-radius: 50%; top: calc(52% - 5px); }
.map-dot--start { left: calc(18% - 5px); background: #5aa3ff; }
.map-dot--end { right: calc(18% - 5px); background: #2ebc7a; }
.map-label { position: absolute; margin: 0; font-size: 13px; font-weight: 600; color: #d4e5ff; max-width: 38%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.map-label--start { left: 12%; top: calc(52% + 14px); }
.map-label--end { right: 12%; top: calc(52% + 14px); text-align: right; }
.info-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; }
.info-grid--order { margin-top: 2px; }
.info-item { border: 1px solid rgba(61, 141, 255, 0.16); border-radius: 8px; padding: 10px 12px; background: rgba(4, 8, 14, 0.35); }
.info-k { margin: 0 0 5px; color: #8fa3bc; font-size: 12px; }
.info-v { margin: 0; color: #e8eef6; font-size: 14px; font-weight: 600; }
.actions-wrap { display: flex; justify-content: flex-end; gap: 10px; margin-top: 12px; flex-wrap: wrap; }
.btn-action { display: inline-flex; align-items: center; justify-content: center; padding: 7px 14px; font-size: 12px; font-weight: 700; border-radius: 6px; border: 1px solid transparent; cursor: pointer; }
.btn-action:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-action--ok { color: #e8fff4; background: linear-gradient(180deg, #2ebc7a, #1e9c62); border-color: rgba(46, 188, 122, 0.5); }
.btn-action--ghost { color: #d4e5ff; background: rgba(61, 141, 255, 0.12); border-color: rgba(61, 141, 255, 0.35); }
.btn-action--primary { color: #f0f6ff; background: linear-gradient(180deg, #4a8df8, #2b65d1); border-color: rgba(74, 141, 248, 0.45); }
@media (max-width: 768px) { .info-grid { grid-template-columns: 1fr; } }
</style>

