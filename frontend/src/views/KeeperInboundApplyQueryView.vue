<script setup>
import { computed, reactive, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'
import { fetchKeeperInboundApplyById, postKeeperAccessInboundApply, postKeeperRejectInboundApply } from '@/api/keeper.js'
import { translateApiMessage } from '@/utils/apiMessageI18n.js'
import { showToast } from '@/utils/toast.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_profile', 'api_message'], uiLang)

const sectionTitle = computed(() =>
  t('page_profile', 'section_keeper_inbound_query', pageDictFallback('page_profile', 'section_keeper_inbound_query', uiLang.value) || '入库查询'),
)
const lead = computed(() =>
  t(
    'page_profile',
    'lead_keeper_inbound_query',
    pageDictFallback('page_profile', 'lead_keeper_inbound_query', uiLang.value) || '输入申请单号查询单条入库申请。',
  ),
)
const labelApplyId = computed(() =>
  t('page_profile', 'col_keeper_apply_id', pageDictFallback('page_profile', 'col_keeper_apply_id', uiLang.value) || '申请单号'),
)
const btnSearch = computed(() =>
  t('page_profile', 'super_btn_search', pageDictFallback('page_profile', 'super_btn_search', uiLang.value) || '查询'),
)
const colStatus = computed(() =>
  t('page_profile', 'col_status', pageDictFallback('page_profile', 'col_status', uiLang.value) || '状态'),
)
const colActions = computed(() =>
  t('page_profile', 'col_actions', pageDictFallback('page_profile', 'col_actions', uiLang.value) || '操作'),
)
const btnPass = computed(() =>
  t('page_profile', 'btn_keeper_apply_pass', pageDictFallback('page_profile', 'btn_keeper_apply_pass', uiLang.value) || '通过'),
)
const btnReject = computed(() =>
  t('page_profile', 'btn_keeper_apply_reject', pageDictFallback('page_profile', 'btn_keeper_apply_reject', uiLang.value) || '退回'),
)
const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value) || '-'),
)
const notFound = computed(() =>
  t(
    'page_profile',
    'keeper_apply_not_found',
    pageDictFallback('page_profile', 'keeper_apply_not_found', uiLang.value) || '未找到该入库申请',
  ),
)
const dialogPassTitle = computed(() =>
  t('page_profile', 'keeper_apply_dialog_pass', pageDictFallback('page_profile', 'keeper_apply_dialog_pass', uiLang.value) || '通过入库申请'),
)
const dialogRejectTitle = computed(() =>
  t('page_profile', 'keeper_apply_dialog_reject', pageDictFallback('page_profile', 'keeper_apply_dialog_reject', uiLang.value) || '退回入库申请'),
)
const labelFee = computed(() =>
  t('page_profile', 'keeper_apply_fee_label', pageDictFallback('page_profile', 'keeper_apply_fee_label', uiLang.value) || '费用'),
)
const labelRemark = computed(() =>
  t('page_profile', 'keeper_apply_remark_label', pageDictFallback('page_profile', 'keeper_apply_remark_label', uiLang.value) || '退回备注'),
)
const btnCancel = computed(() =>
  t('page_profile', 'btn_cancel', pageDictFallback('page_profile', 'btn_cancel', uiLang.value) || '取消'),
)
const btnConfirm = computed(() =>
  t('page_profile', 'btn_confirm_assign', pageDictFallback('page_profile', 'btn_confirm_assign', uiLang.value) || '确认'),
)
const needFeeMsg = computed(() =>
  t('page_profile', 'keeper_apply_need_fee', pageDictFallback('page_profile', 'keeper_apply_need_fee', uiLang.value) || '请填写合法费用'),
)
const needRemarkMsg = computed(() =>
  t(
    'page_profile',
    'keeper_apply_need_remark',
    pageDictFallback('page_profile', 'keeper_apply_need_remark', uiLang.value) || '请填写退回备注',
  ),
)
const passOk = computed(() =>
  t('page_profile', 'keeper_apply_pass_ok', pageDictFallback('page_profile', 'keeper_apply_pass_ok', uiLang.value) || '已通过入库申请'),
)
const rejectOk = computed(() =>
  t('page_profile', 'keeper_apply_reject_ok', pageDictFallback('page_profile', 'keeper_apply_reject_ok', uiLang.value) || '已退回入库申请'),
)
const actionFail = computed(() =>
  t('page_profile', 'keeper_apply_action_fail', pageDictFallback('page_profile', 'keeper_apply_action_fail', uiLang.value) || '操作失败'),
)

const applyIdInput = ref('')
const detail = ref(null)
const loading = ref(false)
const actingApplyId = ref('')
const dialogOpen = ref(false)
const dialogType = ref('pass')
const form = reactive({ fee: '', remark: '' })

function rowApplyId(row) {
  const v = row?.applyId ?? row?.apply_id
  return v != null ? String(v).trim() : ''
}

function rowStatus(row) {
  const s = Number(row?.status)
  if (s === 0) return '待审核'
  if (s === 1) return '未通过'
  if (s === 2) return '待支付'
  if (s === 3) return '超时未支付'
  if (s === 4) return '已支付'
  return row?.status != null && row?.status !== '' ? String(row.status) : valueEmpty.value
}

function canOperate(row) {
  // 仅待审核(0)允许仓管操作；通过(2)或退回(1)后按钮应消失
  return Number(row?.status) === 0
}

async function onSearch() {
  const applyId = String(applyIdInput.value ?? '').trim()
  if (!applyId) {
    showToast(labelApplyId.value, { type: 'warning' })
    return
  }
  loading.value = true
  detail.value = null
  try {
    const res = await fetchKeeperInboundApplyById(applyId)
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

function openPass() {
  if (!detail.value || actingApplyId.value) return
  dialogType.value = 'pass'
  form.fee = ''
  form.remark = ''
  dialogOpen.value = true
}

function openReject() {
  if (!detail.value || actingApplyId.value) return
  dialogType.value = 'reject'
  form.fee = ''
  form.remark = ''
  dialogOpen.value = true
}

function closeDialog() {
  if (actingApplyId.value) return
  dialogOpen.value = false
}

async function confirmDialog() {
  const applyId = rowApplyId(detail.value)
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
    await onSearch()
  } catch (e) {
    const raw = e instanceof Error ? e.message : 'generic_error'
    showToast(`${actionFail.value}：${translateApiMessage(raw, t, uiLang.value)}`, { type: 'error' })
  } finally {
    actingApplyId.value = ''
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
        <label class="search-label" for="keeper-apply-id">{{ labelApplyId }}</label>
        <input id="keeper-apply-id" v-model="applyIdInput" type="text" class="search-input" autocomplete="off" @keyup.enter="onSearch" />
        <button type="button" class="search-btn" :disabled="loading" @click="onSearch">{{ btnSearch }}</button>
      </div>

      <div v-if="detail" class="detail-card">
        <table class="data-table">
          <thead>
            <tr>
              <th>{{ labelApplyId }}</th>
              <th>{{ colStatus }}</th>
              <th class="th-actions">{{ colActions }}</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ rowApplyId(detail) || valueEmpty }}</td>
              <td>{{ rowStatus(detail) || valueEmpty }}</td>
              <td class="td-actions">
                <div class="actions-wrap">
                  <template v-if="canOperate(detail)">
                    <button type="button" class="btn-action btn-action--ok" :disabled="!!actingApplyId" @click="openPass">
                      {{ btnPass }}
                    </button>
                    <button type="button" class="btn-action btn-action--danger" :disabled="!!actingApplyId" @click="openReject">
                      {{ btnReject }}
                    </button>
                  </template>
                  <span v-else class="op-empty">{{ valueEmpty }}</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
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
          <button type="button" class="btn-action btn-action--ghost" :disabled="!!actingApplyId" @click="closeDialog">{{ btnCancel }}</button>
          <button type="button" class="btn-action btn-action--ok" :disabled="!!actingApplyId" @click="confirmDialog">{{ btnConfirm }}</button>
        </div>
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
.actions-wrap { display: inline-flex; justify-content: center; align-items: center; gap: 8px; min-width: 150px; }
.op-empty { color: #8fa3bc; font-size: 13px; }
.btn-action { display: inline-flex; align-items: center; justify-content: center; padding: 6px 14px; font-size: 12px; font-weight: 700; border-radius: 6px; border: 1px solid transparent; cursor: pointer; }
.btn-action:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-action--ok { color: #e8fff4; background: linear-gradient(180deg, #2ebc7a, #1e9c62); border-color: rgba(46, 188, 122, 0.5); }
.btn-action--danger { color: #ffe8e8; background: linear-gradient(180deg, #cb4a4a, #a12f2f); border-color: rgba(203, 74, 74, 0.5); }
.btn-action--ghost { color: #d4e5ff; background: rgba(61, 141, 255, 0.12); border-color: rgba(61, 141, 255, 0.35); }
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
