<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchKeeperInboundApplyPage, postKeeperAccessInboundApply, postKeeperRejectInboundApply } from '@/api/keeper.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() =>
  t('page_profile', 'section_keeper_inbound_apply', pageDictFallback('page_profile', 'section_keeper_inbound_apply', uiLang.value)),
)
const lead = computed(() =>
  t('page_profile', 'lead_keeper_inbound_apply', pageDictFallback('page_profile', 'lead_keeper_inbound_apply', uiLang.value)),
)
const colApplyId = computed(() =>
  t('page_profile', 'col_keeper_apply_id', pageDictFallback('page_profile', 'col_keeper_apply_id', uiLang.value)),
)
const colSkuName = computed(() =>
  t('page_profile', 'col_keeper_sku_name', pageDictFallback('page_profile', 'col_keeper_sku_name', uiLang.value)),
)
const colStatus = computed(() =>
  t('page_profile', 'col_status', pageDictFallback('page_profile', 'col_status', uiLang.value)),
)
const colActions = computed(() => t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value)))
const btnPass = computed(() =>
  t('page_profile', 'btn_keeper_apply_pass', pageDictFallback('page_profile', 'btn_keeper_apply_pass', uiLang.value)),
)
const btnReject = computed(() =>
  t('page_profile', 'btn_keeper_apply_reject', pageDictFallback('page_profile', 'btn_keeper_apply_reject', uiLang.value)),
)
const dialogPassTitle = computed(() =>
  t('page_profile', 'keeper_apply_dialog_pass', pageDictFallback('page_profile', 'keeper_apply_dialog_pass', uiLang.value)),
)
const dialogRejectTitle = computed(() =>
  t('page_profile', 'keeper_apply_dialog_reject', pageDictFallback('page_profile', 'keeper_apply_dialog_reject', uiLang.value)),
)
const labelFee = computed(() =>
  t('page_profile', 'keeper_apply_fee_label', pageDictFallback('page_profile', 'keeper_apply_fee_label', uiLang.value)),
)
const labelRemark = computed(() =>
  t('page_profile', 'keeper_apply_remark_label', pageDictFallback('page_profile', 'keeper_apply_remark_label', uiLang.value)),
)
const btnCancel = computed(() => t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value)))
const btnConfirm = computed(() =>
  t('page_profile', 'btn_confirm_assign', pageDictFallback('page_profile', 'btn_confirm_assign', uiLang.value)),
)
const loadingText = computed(() =>
  t('page_profile', 'register_apps_loading', pageDictFallback('page_profile', 'register_apps_loading', uiLang.value)),
)
const emptyList = computed(() =>
  t('page_profile', 'keeper_apply_empty', pageDictFallback('page_profile', 'keeper_apply_empty', uiLang.value)),
)
const loadError = computed(() =>
  t('page_profile', 'keeper_apply_load_error', pageDictFallback('page_profile', 'keeper_apply_load_error', uiLang.value)),
)
const needFeeMsg = computed(() =>
  t('page_profile', 'keeper_apply_need_fee', pageDictFallback('page_profile', 'keeper_apply_need_fee', uiLang.value)),
)
const needRemarkMsg = computed(() =>
  t('page_profile', 'keeper_apply_need_remark', pageDictFallback('page_profile', 'keeper_apply_need_remark', uiLang.value)),
)
const passOk = computed(() =>
  t('page_profile', 'keeper_apply_pass_ok', pageDictFallback('page_profile', 'keeper_apply_pass_ok', uiLang.value)),
)
const rejectOk = computed(() =>
  t('page_profile', 'keeper_apply_reject_ok', pageDictFallback('page_profile', 'keeper_apply_reject_ok', uiLang.value)),
)
const actionFail = computed(() =>
  t('page_profile', 'keeper_apply_action_fail', pageDictFallback('page_profile', 'keeper_apply_action_fail', uiLang.value)),
)
const btnPrev = computed(() =>
  t('page_profile', 'super_btn_prev', pageDictFallback('page_profile', 'super_btn_prev', uiLang.value)),
)
const btnNext = computed(() =>
  t('page_profile', 'super_btn_next', pageDictFallback('page_profile', 'super_btn_next', uiLang.value)),
)
const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)

const rows = ref([])
const loading = ref(true)
const errorMsg = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const actingApplyId = ref('')

const dialogOpen = ref(false)
const dialogType = ref('pass')
const currentApplyId = ref('')
const form = reactive({ fee: '', remark: '' })

function rowApplyId(row) {
  const v = row?.applyId ?? row?.apply_id
  return v != null ? String(v).trim() : ''
}
function rowSkuName(row) {
  return row?.skuName ?? row?.sku_name ?? ''
}
function rowStatus(row) {
  return row?.status ?? ''
}

async function loadPage() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetchKeeperInboundApplyPage(pageNum.value, pageSize.value)
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

function openPass(row) {
  if (actingApplyId.value) return
  dialogType.value = 'pass'
  currentApplyId.value = rowApplyId(row)
  form.fee = ''
  form.remark = ''
  dialogOpen.value = true
}
function openReject(row) {
  if (actingApplyId.value) return
  dialogType.value = 'reject'
  currentApplyId.value = rowApplyId(row)
  form.fee = ''
  form.remark = ''
  dialogOpen.value = true
}
function closeDialog() {
  if (actingApplyId.value) return
  dialogOpen.value = false
}

async function confirmDialog() {
  const applyId = currentApplyId.value
  if (!applyId) return
  actingApplyId.value = applyId
  try {
    if (dialogType.value === 'pass') {
      const fee = Number(form.fee)
      if (!Number.isFinite(fee) || fee < 0) {
        showToast(needFeeMsg.value, { type: 'warning' })
        return
      }
      await postKeeperAccessInboundApply(applyId, fee)
      showToast(passOk.value, { type: 'success' })
    } else {
      const remark = String(form.remark ?? '').trim()
      if (!remark) {
        showToast(needRemarkMsg.value, { type: 'warning' })
        return
      }
      await postKeeperRejectInboundApply(applyId, remark)
      showToast(rejectOk.value, { type: 'success' })
    }
    dialogOpen.value = false
    await loadPage()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${actionFail.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    actingApplyId.value = ''
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
                <th>{{ colApplyId }}</th>
                <th>{{ colSkuName }}</th>
                <th>{{ colStatus }}</th>
                <th class="th-actions">{{ colActions }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in rows" :key="rowApplyId(row) || `ap-${idx}`">
                <td>{{ rowApplyId(row) || valueEmpty }}</td>
                <td>{{ rowSkuName(row) || valueEmpty }}</td>
                <td>{{ rowStatus(row) || valueEmpty }}</td>
                <td class="td-actions">
                  <button type="button" class="btn-action btn-action--ok" :disabled="!!actingApplyId" @click="openPass(row)">
                    {{ btnPass }}
                  </button>
                  <button type="button" class="btn-action btn-action--danger" :disabled="!!actingApplyId" @click="openReject(row)">
                    {{ btnReject }}
                  </button>
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

    <div v-if="dialogOpen" class="dialog-backdrop" @click.self="closeDialog">
      <div class="dialog" role="dialog" aria-modal="true">
        <h2 class="dialog-title">{{ dialogType === 'pass' ? dialogPassTitle : dialogRejectTitle }}</h2>
        <div class="dialog-body">
          <label v-if="dialogType === 'pass'" class="field">
            <span class="field-label">{{ labelFee }}</span>
            <input v-model="form.fee" type="number" min="0" step="0.01" class="field-input" :disabled="!!actingApplyId" />
          </label>
          <label v-else class="field">
            <span class="field-label">{{ labelRemark }}</span>
            <textarea v-model="form.remark" class="field-input field-input--textarea" :disabled="!!actingApplyId" />
          </label>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-action btn-action--ghost" :disabled="!!actingApplyId" @click="closeDialog">
            {{ btnCancel }}
          </button>
          <button type="button" class="btn-action btn-action--ok" :disabled="!!actingApplyId" @click="confirmDialog">
            {{ btnConfirm }}
          </button>
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
.td-actions { display: flex; justify-content: center; align-items: center; gap: 8px; }
.btn-action { display: inline-flex; align-items: center; justify-content: center; padding: 6px 14px; font-size: 12px; font-weight: 700; border-radius: 6px; border: 1px solid transparent; cursor: pointer; }
.btn-action:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-action--ok { color: #e8fff4; background: linear-gradient(180deg, #2ebc7a, #1e9c62); border-color: rgba(46, 188, 122, 0.5); }
.btn-action--danger { color: #ffe8e8; background: linear-gradient(180deg, #cb4a4a, #a12f2f); border-color: rgba(203, 74, 74, 0.5); }
.btn-action--ghost { color: #d4e5ff; background: rgba(61, 141, 255, 0.12); border-color: rgba(61, 141, 255, 0.35); }
.pager { display: flex; align-items: center; justify-content: flex-end; gap: 12px; margin-top: 16px; }
.pager-meta { font-size: 13px; font-weight: 600; color: #9eb0c8; min-width: 2ch; text-align: center; }
.pager-btn { padding: 8px 14px; font-size: 13px; font-weight: 600; border-radius: 6px; border: 1px solid rgba(61, 141, 255, 0.35); background: rgba(61, 141, 255, 0.12); color: #d4e5ff; cursor: pointer; }
.pager-btn:disabled { opacity: 0.45; cursor: not-allowed; }
.dialog-backdrop { position: fixed; inset: 0; z-index: 80; display: flex; align-items: center; justify-content: center; padding: 24px; background: rgba(2, 6, 12, 0.72); backdrop-filter: blur(4px); }
.dialog { width: 100%; max-width: 440px; border-radius: 12px; padding: 22px 22px 18px; background: linear-gradient(160deg, #121a28, #0a0e16); border: 1px solid rgba(61, 141, 255, 0.2); }
.dialog-title { margin: 0 0 16px; font-size: 18px; font-weight: 700; color: #e8eef6; }
.dialog-body { display: flex; flex-direction: column; gap: 12px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 12px; color: #9eb0c8; }
.field-input { width: 100%; box-sizing: border-box; padding: 10px 12px; border-radius: 8px; border: 1px solid rgba(61, 141, 255, 0.25); background: rgba(5, 9, 14, 0.66); color: #e8eef6; font-size: 13px; }
.field-input--textarea { min-height: 92px; resize: vertical; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 18px; }
</style>
