<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchDriverLineDetail } from '@/api/driver.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'

const route = useRoute()
const router = useRouter()
const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(
  () => t('page_profile', 'section_driver_line_detail', pageDictFallback('page_profile', 'section_driver_line_detail', uiLang.value) || '路线详情'),
)
const lead = computed(
  () => t('page_profile', 'lead_driver_line_detail', pageDictFallback('page_profile', 'lead_driver_line_detail', uiLang.value) || '查看当前派单对应的路线信息。'),
)
const loadingText = computed(
  () => t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value) || '加载中...'),
)
const valueEmpty = computed(() => t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value) || '-'))
const loadErrorLabel = computed(
  () => t('page_profile', 'driver_line_detail_load_error', pageDictFallback('page_profile', 'driver_line_detail_load_error', uiLang.value) || '路线详情加载失败'),
)
const missingParamsText = computed(
  () => t('page_profile', 'driver_line_detail_missing_params', pageDictFallback('page_profile', 'driver_line_detail_missing_params', uiLang.value) || '缺少起点或终点参数'),
)
const btnBack = computed(
  () => t('page_profile', 'driver_line_detail_back', pageDictFallback('page_profile', 'driver_line_detail_back', uiLang.value) || '返回派单列表'),
)
const colLineId = computed(() => t('page_profile', 'col_line_id', pageDictFallback('page_profile', 'col_line_id', uiLang.value) || '业务路线 ID'))
const colOrigin = computed(() => t('page_profile', 'col_transport_origin', pageDictFallback('page_profile', 'col_transport_origin', uiLang.value) || '起点'))
const colDest = computed(() => t('page_profile', 'col_transport_dest', pageDictFallback('page_profile', 'col_transport_dest', uiLang.value) || '终点'))
const colEstimation = computed(() => t('page_profile', 'col_estimation', pageDictFallback('page_profile', 'col_estimation', uiLang.value) || '预计耗时'))
const colStatus = computed(() => t('page_profile', 'col_line_status', pageDictFallback('page_profile', 'col_line_status', uiLang.value) || '状态'))

const loading = ref(true)
const errorMsg = ref('')
const detail = ref(null)

function originFromQuery() {
  return route.query.origin != null ? String(route.query.origin).trim() : ''
}
function destFromQuery() {
  return route.query.dest != null ? String(route.query.dest).trim() : ''
}
function statusLabel(s) {
  if (s === 0 || s === '0') return t('page_profile', 'line_status_disabled', pageDictFallback('page_profile', 'line_status_disabled', uiLang.value) || '禁用')
  if (s === 1 || s === '1') return t('page_profile', 'line_status_enabled', pageDictFallback('page_profile', 'line_status_enabled', uiLang.value) || '启用')
  return String(s ?? valueEmpty.value)
}

async function loadDetail() {
  const origin = originFromQuery()
  const dest = destFromQuery()
  loading.value = true
  errorMsg.value = ''
  detail.value = null
  if (!origin || !dest) {
    loading.value = false
    errorMsg.value = missingParamsText.value
    return
  }
  try {
    const res = await fetchDriverLineDetail(origin, dest)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    detail.value = res.data && typeof res.data === 'object' ? res.data : {}
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    errorMsg.value = `${loadErrorLabel.value}：${translateApiMessage(raw, t, uiLang.value)}`
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push({ name: 'profile-driver-transport-orders' })
}

watch(() => [route.query.origin, route.query.dest], () => loadDetail())
onMounted(() => loadDetail())
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <h1 class="main-title">{{ sectionTitle }}</h1>
      <p class="main-lead">{{ lead }}</p>
      <div class="head-actions">
        <button type="button" class="btn-back" @click="goBack">{{ btnBack }}</button>
      </div>
    </header>

    <section class="map-panel">
      <div class="map-grid">
        <div class="map-dot map-dot--start" />
        <div class="map-dot map-dot--end" />
        <div class="map-route" />
        <p class="map-label map-label--start">{{ originFromQuery() || valueEmpty }}</p>
        <p class="map-label map-label--end">{{ destFromQuery() || valueEmpty }}</p>
      </div>
    </section>

    <section class="info-panel">
      <p v-if="loading" class="state-msg">{{ loadingText }}</p>
      <p v-else-if="errorMsg" class="state-msg state-msg--err">{{ errorMsg }}</p>
      <div v-else class="info-grid">
        <div class="info-item">
          <p class="info-k">{{ colLineId }}</p>
          <p class="info-v">{{ detail?.lineId ?? valueEmpty }}</p>
        </div>
        <div class="info-item">
          <p class="info-k">{{ colOrigin }}</p>
          <p class="info-v">{{ detail?.origin ?? valueEmpty }}</p>
        </div>
        <div class="info-item">
          <p class="info-k">{{ colDest }}</p>
          <p class="info-v">{{ detail?.dest ?? valueEmpty }}</p>
        </div>
        <div class="info-item">
          <p class="info-k">{{ colEstimation }}</p>
          <p class="info-v">{{ detail?.estimation ?? valueEmpty }}</p>
        </div>
        <div class="info-item">
          <p class="info-k">{{ colStatus }}</p>
          <p class="info-v">{{ statusLabel(detail?.status) }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.main-inner { flex: 1; display: flex; flex-direction: column; gap: 14px; width: 100%; max-width: min(1200px, 100%); margin: 0 auto; min-height: 0; }
.main-head { flex-shrink: 0; }
.main-title { margin: 0 0 8px; font-size: clamp(22px, 2.2vw, 28px); font-weight: 700; color: #e8eef6; }
.main-lead { margin: 0; font-size: 14px; color: #7a8fa8; }
.head-actions { margin-top: 12px; }
.btn-back { padding: 8px 14px; border-radius: 8px; border: 1px solid rgba(61, 141, 255, 0.35); background: rgba(61, 141, 255, 0.12); color: #d4e5ff; font-size: 13px; font-weight: 600; cursor: pointer; }
.map-panel, .info-panel { border-radius: calc(var(--17-radius-lg) + 2px); border: 1px solid rgba(61, 141, 255, 0.14); background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99)); box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.4) inset, 0 12px 40px rgba(0, 0, 0, 0.35); }
.map-panel { flex: 1 1 50%; min-height: 260px; padding: 14px; }
.map-grid { position: relative; width: 100%; height: 100%; border-radius: 12px; background: linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px), radial-gradient(circle at 25% 20%, rgba(75, 145, 255, 0.24), transparent 50%), radial-gradient(circle at 75% 80%, rgba(46, 188, 122, 0.18), transparent 45%), #091220; background-size: 28px 28px, 28px 28px, 100% 100%, 100% 100%, auto; overflow: hidden; }
.map-route { position: absolute; left: 18%; right: 18%; top: 52%; height: 3px; border-radius: 99px; background: linear-gradient(90deg, #5aa3ff, #2ebc7a); box-shadow: 0 0 18px rgba(90, 163, 255, 0.5); }
.map-dot { position: absolute; width: 12px; height: 12px; border-radius: 50%; top: calc(52% - 5px); }
.map-dot--start { left: calc(18% - 5px); background: #5aa3ff; }
.map-dot--end { right: calc(18% - 5px); background: #2ebc7a; }
.map-label { position: absolute; margin: 0; font-size: 13px; font-weight: 600; color: #d4e5ff; max-width: 38%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.map-label--start { left: 12%; top: calc(52% + 14px); }
.map-label--end { right: 12%; top: calc(52% + 14px); text-align: right; }
.info-panel { flex: 1 1 50%; min-height: 240px; padding: 20px; }
.state-msg { margin: 0; padding: 24px 8px; text-align: center; color: #8b9cb4; }
.state-msg--err { color: #f0a8a8; }
.info-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.info-item { border: 1px solid rgba(61, 141, 255, 0.18); border-radius: 10px; padding: 12px; background: rgba(4, 8, 14, 0.35); }
.info-k { margin: 0 0 6px; color: #8fa3bc; font-size: 12px; }
.info-v { margin: 0; color: #e8eef6; font-size: 15px; font-weight: 600; }
@media (max-width: 768px) { .main-inner { gap: 10px; } .map-panel, .info-panel { min-height: 220px; } .info-grid { grid-template-columns: 1fr; } }
</style>
