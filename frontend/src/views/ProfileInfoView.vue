<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthSession } from '@/composables/useAuthSession.js'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { useSortedDictionaryOptions } from '@/composables/useSortedDictionaryOptions.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import {
  fetchDriverInfo,
  fetchDriverPendingAssignedOrders,
  postDriverAcceptAssignedOrder,
  postDriverAddVehicle,
  postDriverRejectAssignedOrder,
  postDriverUpdateStatus,
} from '@/api/driver.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const FALLBACK_ROLES = [
  { value: 'super', label: '超级管理员' },
  { value: 'manager', label: '管理员' },
  { value: 'keeper', label: '仓管' },
  { value: 'driver', label: '司机' },
  { value: 'reviewer', label: '审核员' },
]

const router = useRouter()
const { isLoggedIn, profile } = useAuthSession()
const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)
const { options: roleOptions } = useSortedDictionaryOptions('login_role_option', uiLang, FALLBACK_ROLES)

const title = computed(() => t('page_profile', 'title', '个人中心'))
const sectionAccount = computed(() => t('page_profile', 'section_account', '账户信息'))
const labelUsername = computed(() => t('page_profile', 'label_username', '用户名'))
const labelNickname = computed(() => t('page_profile', 'label_nickname', '昵称'))
const labelRole = computed(() => t('page_profile', 'label_role', '角色'))
const labelCity = computed(() => t('page_profile', 'label_city', '所在城市'))
const hintNotLoggedIn = computed(() => t('page_profile', 'hint_not_logged_in', '未登录，无法查看个人中心。'))
const btnGoLogin = computed(() => t('page_profile', 'btn_go_login', '去登录'))
const sectionDriver = computed(() => t('page_profile', 'section_driver_info', pageDictFallback('page_profile', 'section_driver_info', uiLang.value) || '司机信息'))
const labelDriverId = computed(() => t('page_profile', 'label_driver_id', pageDictFallback('page_profile', 'label_driver_id', uiLang.value) || '司机ID'))
const labelVehicleNo = computed(() => t('page_profile', 'label_driver_vehicle_no', pageDictFallback('page_profile', 'label_driver_vehicle_no', uiLang.value) || '车牌号'))
const labelCurrentCity = computed(() => t('page_profile', 'label_driver_current_city', pageDictFallback('page_profile', 'label_driver_current_city', uiLang.value) || '当前城市'))
const labelDriverStatus = computed(() => t('page_profile', 'label_driver_status', pageDictFallback('page_profile', 'label_driver_status', uiLang.value) || '状态'))
const btnBindVehicle = computed(() => t('page_profile', 'btn_driver_bind_vehicle', pageDictFallback('page_profile', 'btn_driver_bind_vehicle', uiLang.value) || '绑定载具'))
const labelHeight = computed(() => t('page_profile', 'label_vehicle_height', pageDictFallback('page_profile', 'label_vehicle_height', uiLang.value) || '车高'))
const labelLength = computed(() => t('page_profile', 'label_vehicle_length', pageDictFallback('page_profile', 'label_vehicle_length', uiLang.value) || '车长'))
const labelWide = computed(() => t('page_profile', 'label_vehicle_wide', pageDictFallback('page_profile', 'label_vehicle_wide', uiLang.value) || '车宽'))
const labelTonnage = computed(() => t('page_profile', 'label_vehicle_tonnage', pageDictFallback('page_profile', 'label_vehicle_tonnage', uiLang.value) || '吨位'))
const loadingDriverText = computed(() => t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value) || '加载中...'))
const driverLoadFailed = computed(() => t('page_profile', 'driver_info_load_failed', pageDictFallback('page_profile', 'driver_info_load_failed', uiLang.value) || '司机信息加载失败'))
const bindVehicleFailed = computed(() => t('page_profile', 'driver_bind_vehicle_failed', pageDictFallback('page_profile', 'driver_bind_vehicle_failed', uiLang.value) || '绑定载具失败'))
const bindVehicleOk = computed(() => t('page_profile', 'driver_bind_vehicle_ok', pageDictFallback('page_profile', 'driver_bind_vehicle_ok', uiLang.value) || '载具绑定成功'))
const btnUpdateStatus = computed(() => t('page_profile', 'btn_driver_update_status', pageDictFallback('page_profile', 'btn_driver_update_status', uiLang.value) || '修改状态'))
const btnOnline = computed(() => t('page_profile', 'btn_driver_online', pageDictFallback('page_profile', 'btn_driver_online', uiLang.value) || '上线'))
const btnOffline = computed(() => t('page_profile', 'btn_driver_offline', pageDictFallback('page_profile', 'btn_driver_offline', uiLang.value) || '下线'))
const statusUpdateFailed = computed(() => t('page_profile', 'driver_status_update_failed', pageDictFallback('page_profile', 'driver_status_update_failed', uiLang.value) || '状态修改失败'))
const assignDialogTitle = computed(() => t('page_profile', 'driver_assign_dialog_title', pageDictFallback('page_profile', 'driver_assign_dialog_title', uiLang.value) || '人工派单提醒'))
const btnAcceptAssign = computed(() => t('page_profile', 'btn_driver_accept_assignment', pageDictFallback('page_profile', 'btn_driver_accept_assignment', uiLang.value) || '接受'))
const btnRejectAssign = computed(() => t('page_profile', 'btn_driver_reject_assignment', pageDictFallback('page_profile', 'btn_driver_reject_assignment', uiLang.value) || '拒绝'))
const assignActionFailed = computed(() => t('page_profile', 'driver_assign_action_failed', pageDictFallback('page_profile', 'driver_assign_action_failed', uiLang.value) || '派单处理失败'))

const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)

const displayName = computed(() => {
  const p = profile.value
  if (!p) return ''
  const nick = p.nickname != null && String(p.nickname).trim() ? String(p.nickname).trim() : ''
  if (nick) return nick
  return p.username != null && String(p.username).trim() ? String(p.username).trim() : ''
})

const userRoleLabel = computed(() => {
  const r = profile.value?.role
  if (!r) return ''
  return roleOptions.value.find((o) => o.value === r)?.label || String(r)
})
const isDriver = computed(() => profile.value?.role === 'driver')
const driverInfo = ref(null)
const driverLoading = ref(false)
const assignedOrder = ref(null)
const assignDialogOpen = ref(false)
const assignActing = ref(false)
const vehicleDialogOpen = ref(false)
const savingVehicle = ref(false)
const updatingStatus = ref(false)
const vehicleForm = reactive({ vehicleNo: '', height: '', length: '', wide: '', tonnage: '' })
let pendingPollTimer = null

const sidebarWelcome = computed(() => {
  if (!isLoggedIn.value) {
    return title.value
  }
  const name = displayName.value || valueEmpty.value
  const role = userRoleLabel.value
  const rolePart = role ? ` ${role}` : ''
  const tpl = t(
    'page_profile',
    'sidebar_welcome',
    pageDictFallback('page_profile', 'sidebar_welcome', uiLang.value),
  )
  return tpl.replace('{name}', name).replace('{role}', rolePart)
})

function goLogin() {
  router.push({ name: 'login', query: { redirect: '/profile' } })
}

function driverStatusText(s) {
  if (s === 0 || s === '0') return '空闲'
  if (s === 1 || s === '1') return '运输中'
  if (s === 2 || s === '2') return '休息中'
  if (s === 3 || s === '3') return '离职'
  return s != null && s !== '' ? String(s) : valueEmpty.value
}

async function loadDriverInfo() {
  if (!isLoggedIn.value || !isDriver.value) return
  driverLoading.value = true
  try {
    const res = await fetchDriverInfo()
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    driverInfo.value = res.data && typeof res.data === 'object' ? res.data : null
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${driverLoadFailed.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    driverLoading.value = false
  }
}

async function loadPendingAssignedOrder() {
  if (!isLoggedIn.value || !isDriver.value) return
  try {
    const res = await fetchDriverPendingAssignedOrders()
    if (res.code !== 200) return
    const list = Array.isArray(res.data) ? res.data : []
    if (list.length > 0 && !assignDialogOpen.value && !assignActing.value) {
      assignedOrder.value = list[0]
      assignDialogOpen.value = true
    }
  } catch {
    // 静默轮询，不打断主界面
  }
}

function openVehicleDialog() {
  if (savingVehicle.value || !isDriver.value || !profile.value?.userId) return
  vehicleForm.vehicleNo = ''
  vehicleForm.height = ''
  vehicleForm.length = ''
  vehicleForm.wide = ''
  vehicleForm.tonnage = ''
  vehicleDialogOpen.value = true
}

function closeVehicleDialog() {
  if (savingVehicle.value) return
  vehicleDialogOpen.value = false
}

async function submitVehicle() {
  const driverId = profile.value?.userId ? String(profile.value.userId).trim() : ''
  const vehicleNo = String(vehicleForm.vehicleNo ?? '').trim()
  const height = Number(vehicleForm.height)
  const length = Number(vehicleForm.length)
  const wide = Number(vehicleForm.wide)
  const tonnage = Number(vehicleForm.tonnage)
  if (!driverId || !vehicleNo || !Number.isFinite(height) || !Number.isFinite(length) || !Number.isFinite(wide) || !Number.isFinite(tonnage)) {
    showToast('请完整填写载具信息', { type: 'warning' })
    return
  }
  savingVehicle.value = true
  try {
    const res = await postDriverAddVehicle({ driverId, vehicleNo, height, length, wide, tonnage })
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    showToast(translateApiMessage(String(res.message ?? 'operation_success'), t, uiLang.value) || bindVehicleOk.value, { type: 'success' })
    vehicleDialogOpen.value = false
    await loadDriverInfo()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${bindVehicleFailed.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    savingVehicle.value = false
  }
}

async function updateDriverStatus(targetStatus) {
  const driverId = profile.value?.userId ? String(profile.value.userId).trim() : ''
  if (!driverId || updatingStatus.value) return
  updatingStatus.value = true
  try {
    const res = await postDriverUpdateStatus(driverId, targetStatus)
    if (res.code !== 200) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    const okRaw = res.data != null && String(res.data).trim() ? String(res.data) : String(res.message ?? 'operation_success')
    showToast(translateApiMessage(okRaw, t, uiLang.value), { type: 'success' })
    await loadDriverInfo()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${statusUpdateFailed.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    updatingStatus.value = false
  }
}

async function onAcceptAssignedOrder() {
  const transportId = assignedOrder.value?.transportId ? String(assignedOrder.value.transportId).trim() : ''
  if (!transportId || assignActing.value) return
  assignActing.value = true
  try {
    const res = await postDriverAcceptAssignedOrder(transportId)
    if (res.code !== 200 || res.data !== true) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    assignDialogOpen.value = false
    assignedOrder.value = null
    await loadPendingAssignedOrder()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${assignActionFailed.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    assignActing.value = false
  }
}

async function onRejectAssignedOrder() {
  const transportId = assignedOrder.value?.transportId ? String(assignedOrder.value.transportId).trim() : ''
  if (!transportId || assignActing.value) return
  assignActing.value = true
  try {
    const res = await postDriverRejectAssignedOrder(transportId)
    if (res.code !== 200 || res.data !== true) {
      throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
    }
    assignDialogOpen.value = false
    assignedOrder.value = null
    await loadDriverInfo()
    await loadPendingAssignedOrder()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${assignActionFailed.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    assignActing.value = false
  }
}

onMounted(() => {
  loadDriverInfo()
  loadPendingAssignedOrder()
  if (pendingPollTimer) clearInterval(pendingPollTimer)
  pendingPollTimer = setInterval(() => {
    loadPendingAssignedOrder()
  }, 15000)
})

onUnmounted(() => {
  if (pendingPollTimer) {
    clearInterval(pendingPollTimer)
    pendingPollTimer = null
  }
})
</script>

<template>
  <div class="main-inner">
    <header class="main-head">
      <h1 class="main-title">{{ sectionAccount }}</h1>
      <p class="main-lead">{{ isLoggedIn ? sidebarWelcome : title }}</p>
    </header>

    <div v-if="!isLoggedIn" class="panel panel--empty">
      <p class="hint">{{ hintNotLoggedIn }}</p>
      <button type="button" class="btn-primary" @click="goLogin">{{ btnGoLogin }}</button>
    </div>

    <div v-else class="panel">
      <dl class="kv">
        <div class="kv-row">
          <dt>{{ labelUsername }}</dt>
          <dd>{{ profile?.username || valueEmpty }}</dd>
        </div>
        <div class="kv-row">
          <dt>{{ labelNickname }}</dt>
          <dd>{{ (profile?.nickname && String(profile.nickname).trim()) || valueEmpty }}</dd>
        </div>
        <div class="kv-row">
          <dt>{{ labelRole }}</dt>
          <dd>{{ userRoleLabel || valueEmpty }}</dd>
        </div>
        <div class="kv-row">
          <dt>{{ labelCity }}</dt>
          <dd>{{ (profile?.city && String(profile.city).trim()) || valueEmpty }}</dd>
        </div>
      </dl>

      <template v-if="isDriver">
        <div class="driver-head">
          <h2 class="driver-title">{{ sectionDriver }}</h2>
          <button type="button" class="btn-primary" :disabled="savingVehicle" @click="openVehicleDialog">{{ btnBindVehicle }}</button>
        </div>
        <p v-if="driverLoading" class="hint">{{ loadingDriverText }}</p>
        <dl v-else class="kv driver-kv">
          <div class="kv-row">
            <dt>{{ labelDriverId }}</dt>
            <dd>{{ driverInfo?.userId || valueEmpty }}</dd>
          </div>
          <div class="kv-row">
            <dt>{{ labelVehicleNo }}</dt>
            <dd>{{ driverInfo?.vehicleNo || valueEmpty }}</dd>
          </div>
          <div class="kv-row">
            <dt>{{ labelCurrentCity }}</dt>
            <dd>{{ driverInfo?.currentCity || valueEmpty }}</dd>
          </div>
          <div class="kv-row">
            <dt>{{ labelDriverStatus }}</dt>
            <dd>
              <div class="status-cell">
                <span>{{ driverStatusText(driverInfo?.status) }}</span>
                <span class="status-edit-label">{{ btnUpdateStatus }}</span>
                <button
                  type="button"
                  class="btn-mini btn-mini--ok"
                  :disabled="updatingStatus || driverInfo?.status === 0 || driverInfo?.status === '0'"
                  @click="updateDriverStatus(0)"
                >
                  {{ btnOnline }}
                </button>
                <button
                  type="button"
                  class="btn-mini btn-mini--ghost"
                  :disabled="updatingStatus || driverInfo?.status === 2 || driverInfo?.status === '2'"
                  @click="updateDriverStatus(2)"
                >
                  {{ btnOffline }}
                </button>
              </div>
            </dd>
          </div>
        </dl>
      </template>
    </div>

    <div v-if="vehicleDialogOpen" class="dialog-backdrop" @click.self="closeVehicleDialog">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ btnBindVehicle }}</h2>
        <div class="dialog-body">
          <label class="field"><span class="field-label">{{ labelVehicleNo }}</span><input v-model="vehicleForm.vehicleNo" class="field-input" :disabled="savingVehicle" /></label>
          <label class="field"><span class="field-label">{{ labelHeight }}</span><input v-model="vehicleForm.height" type="number" step="0.01" class="field-input" :disabled="savingVehicle" /></label>
          <label class="field"><span class="field-label">{{ labelLength }}</span><input v-model="vehicleForm.length" type="number" step="0.01" class="field-input" :disabled="savingVehicle" /></label>
          <label class="field"><span class="field-label">{{ labelWide }}</span><input v-model="vehicleForm.wide" type="number" step="0.01" class="field-input" :disabled="savingVehicle" /></label>
          <label class="field"><span class="field-label">{{ labelTonnage }}</span><input v-model="vehicleForm.tonnage" type="number" step="0.01" class="field-input" :disabled="savingVehicle" /></label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="savingVehicle" @click="closeVehicleDialog">取消</button>
          <button type="button" class="btn-action btn-action--ok" :disabled="savingVehicle" @click="submitVehicle">保存</button>
        </div>
      </div>
    </div>

    <div v-if="assignDialogOpen && assignedOrder" class="dialog-backdrop">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ assignDialogTitle }}</h2>
        <div class="dialog-body">
          <dl class="kv driver-kv">
            <div class="kv-row">
              <dt>运输单号</dt>
              <dd>{{ assignedOrder.transportId || valueEmpty }}</dd>
            </div>
            <div class="kv-row">
              <dt>起点</dt>
              <dd>{{ assignedOrder.origin || valueEmpty }}</dd>
            </div>
            <div class="kv-row">
              <dt>终点</dt>
              <dd>{{ assignedOrder.dest || valueEmpty }}</dd>
            </div>
            <div class="kv-row">
              <dt>运费</dt>
              <dd>{{ assignedOrder.fee ?? valueEmpty }}</dd>
            </div>
          </dl>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--danger" :disabled="assignActing" @click="onRejectAssignedOrder">{{ btnRejectAssign }}</button>
          <button type="button" class="btn-action btn-action--ok" :disabled="assignActing" @click="onAcceptAssignedOrder">{{ btnAcceptAssign }}</button>
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
  min-height: min(420px, calc(100vh - 220px));
  border-radius: calc(var(--17-radius-lg) + 2px);
  padding: clamp(24px, 3vw, 40px) clamp(24px, 3.5vw, 44px);
  background: linear-gradient(155deg, rgba(22, 32, 50, 0.98), rgba(10, 15, 24, 0.99));
  border: 1px solid rgba(61, 141, 255, 0.14);
  box-shadow:
    0 0 0 1px rgba(0, 0, 0, 0.4) inset,
    0 12px 40px rgba(0, 0, 0, 0.35),
    0 0 80px rgba(37, 111, 234, 0.06);
}

.panel--empty {
  text-align: center;
  padding: clamp(36px, 5vw, 56px) clamp(24px, 4vw, 40px);
  justify-content: center;
}

.hint {
  margin: 0 0 18px;
  font-size: 14px;
  color: #8b9cb4;
  line-height: 1.55;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 26px;
  font-size: 14px;
  font-weight: 700;
  font-family: inherit;
  color: #fff;
  background: linear-gradient(180deg, #4a9dff, #256fea);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(37, 111, 234, 0.35);
  transition:
    filter 0.15s,
    transform 0.15s;
}

.btn-primary:hover {
  filter: brightness(1.06);
  transform: translateY(-1px);
}

.driver-head {
  margin-top: 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.driver-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #dbe9f8;
}

.driver-kv {
  margin-top: 8px;
}

.status-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.status-edit-label {
  color: #8fa3bc;
  font-size: 12px;
}

.kv {
  margin: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.kv-row {
  display: grid;
  grid-template-columns: minmax(112px, 200px) minmax(0, 1fr);
  gap: 12px 28px;
  align-items: baseline;
  padding: clamp(16px, 2vw, 22px) 0;
  border-bottom: 1px solid rgba(36, 48, 68, 0.75);
  font-size: clamp(14px, 1.15vw, 16px);
}

.kv-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.kv-row:first-child {
  padding-top: 0;
}

.kv-row dt {
  margin: 0;
  font-weight: 600;
  color: #8fa3bc;
}

.kv-row dd {
  margin: 0;
  color: #f0f5fc;
  font-weight: 500;
  word-break: break-word;
  line-height: 1.5;
}

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
  max-width: 440px;
  border-radius: 12px;
  padding: 22px 22px 18px;
  background: linear-gradient(160deg, #121a28, #0a0e16);
  border: 1px solid rgba(61, 141, 255, 0.2);
}

.dialog-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 700;
  color: #e8eef6;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 12px;
  color: #9eb0c8;
}

.field-input {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(61, 141, 255, 0.25);
  background: rgba(5, 9, 14, 0.66);
  color: #e8eef6;
  font-size: 13px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
}

.btn-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 700;
  border-radius: 6px;
  border: 1px solid transparent;
  cursor: pointer;
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

.btn-action--ghost {
  color: #d4e5ff;
  background: rgba(61, 141, 255, 0.12);
  border-color: rgba(61, 141, 255, 0.35);
}

.btn-action--danger {
  color: #ffe8e8;
  background: linear-gradient(180deg, #cb4a4a, #a12f2f);
  border-color: rgba(203, 74, 74, 0.5);
}

.btn-mini {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 700;
  border-radius: 6px;
  border: 1px solid transparent;
  cursor: pointer;
}

.btn-mini:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-mini--ok {
  color: #e8fff4;
  background: linear-gradient(180deg, #2ebc7a, #1e9c62);
  border-color: rgba(46, 188, 122, 0.5);
}

.btn-mini--ghost {
  color: #d4e5ff;
  background: rgba(61, 141, 255, 0.12);
  border-color: rgba(61, 141, 255, 0.35);
}

@media (max-width: 768px) {
  .panel {
    min-height: 280px;
  }

  .kv-row {
    grid-template-columns: 1fr;
    gap: 6px;
    padding: 14px 0;
  }
}
</style>
