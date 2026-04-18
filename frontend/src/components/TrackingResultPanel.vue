<script setup>
import { computed, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'

const trackingInput = defineModel('trackingInput', { type: String, default: '' })

const props = defineProps({
  numbers: {
    type: Array,
    required: true,
  },
  timeline: {
    type: Array,
    required: true,
  },
  canQuery: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['reset', 'query', 'clear'])

const copiedTip = ref('')

const { uiLang } = useUiLang()
const { t: dictT } = useMultiDictionary(
  ['tracking_status_tab', 'tracking_progress_label', 'page_tracking_result'],
  uiLang,
)

const primaryNumber = computed(() => props.numbers[0] || '')
const extraCount = computed(() => Math.max(0, props.numbers.length - 1))

const latest = computed(() => props.timeline[0] || null)

const STATUS_TAB_KEYS = [
  { key: 'all', icon: 'all' },
  { key: 'transit', icon: 'plane' },
  { key: 'pickup', icon: 'box' },
  { key: 'except', icon: 'alert' },
  { key: 'delivered', icon: 'check' },
]

const statusTabs = computed(() =>
  STATUS_TAB_KEYS.map(({ key, icon }) => ({
    key,
    icon,
    label: dictT('tracking_status_tab', key, fallbackTabLabel(key)),
  })),
)

function fallbackTabLabel(key) {
  const m = {
    all: '全部',
    transit: '运输途中',
    pickup: '待揽收',
    except: '可能异常',
    delivered: '成功签收',
  }
  return m[key] ?? key
}

async function copyNo() {
  const t = primaryNumber.value
  if (!t) return
  try {
    await navigator.clipboard.writeText(t)
    copiedTip.value = dictT('page_tracking_result', 'toast_copied_no', '已复制')
    setTimeout(() => {
      copiedTip.value = ''
    }, 1600)
  } catch {
    copiedTip.value = ''
  }
}

function copyAllDetail() {
  const lines = props.timeline.map((e) => `${e.time}  ${e.place} ${e.detail}`)
  navigator.clipboard?.writeText(lines.join('\n')).catch(() => {})
  copiedTip.value = dictT('page_tracking_result', 'toast_copied_trace', '轨迹已复制')
  setTimeout(() => {
    copiedTip.value = ''
  }, 1600)
}
</script>

<template>
  <main class="result-page">
    <div class="result-shell">
      <div class="result-columns">
        <div class="result-main">
          <article class="detail-card">
            <!-- 状态条与主卡一体，右侧栏与主卡顶部对齐 -->
            <div class="detail-card-status">
              <div
                class="status-strip"
                role="tablist"
                :aria-label="dictT('page_tracking_result', 'aria_status_filter', '运单状态筛选')"
              >
                <button
                  v-for="tab in statusTabs"
                  :key="tab.key"
                  type="button"
                  class="status-tab"
                  :class="{ active: tab.key === 'all' }"
                >
                  <span class="status-tab-ico" :data-i="tab.icon" aria-hidden="true" />
                  <span class="status-tab-num">{{
                    tab.key === 'all' ? numbers.length : tab.key === 'transit' ? 1 : 0
                  }}</span>
                  <span class="status-tab-label">{{ tab.label }}</span>
                </button>
              </div>
            </div>

            <header class="detail-head">
            <div class="detail-head-left">
              <span class="carrier-ico" aria-hidden="true">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
                  <path
                    d="M3 12L12 3l4 4-6 6h6l2 2-2 2H8l-5-5z"
                    fill="currentColor"
                    opacity="0.95"
                  />
                </svg>
              </span>
              <div class="detail-head-text">
                <div class="detail-no-row">
                  <span class="detail-no">{{ primaryNumber }}</span>
                  <button
                    type="button"
                    class="icon-btn"
                    :title="dictT('page_tracking_result', 'title_copy_no', '复制单号')"
                    @click="copyNo"
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="9" y="9" width="11" height="11" rx="2" />
                      <rect x="4" y="4" width="11" height="11" rx="2" />
                    </svg>
                  </button>
                  <span v-if="extraCount > 0" class="detail-no-more">+{{ extraCount }}</span>
                  <span class="detail-status-pill">{{
                    dictT('page_tracking_result', 'detail_status_pill', '运输途中')
                  }}</span>
                  <span v-if="copiedTip" class="copy-toast">{{ copiedTip }}</span>
                </div>
                <p class="detail-route">中国 中国邮政 → 俄罗斯联邦（演示数据）</p>
              </div>
            </div>
            <div class="detail-head-actions">
              <button
                type="button"
                class="icon-btn"
                :title="dictT('page_tracking_result', 'title_copy', '复制')"
                @click="copyNo"
              >
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="9" y="9" width="11" height="11" rx="2" />
                  <rect x="4" y="4" width="11" height="11" rx="2" />
                </svg>
              </button>
              <button
                type="button"
                class="icon-btn"
                :title="dictT('page_tracking_result', 'title_refresh', '刷新（演示）')"
                @click="emit('query')"
              >
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 12a8 8 0 0 1 8-8V2M20 12a8 8 0 0 1-8 8v2" />
                </svg>
              </button>
              <button
                type="button"
                class="icon-btn icon-btn-danger"
                :title="dictT('page_tracking_result', 'title_close', '关闭并返回')"
                @click="emit('reset')"
              >
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M6 6l12 12M18 6L6 18" />
                </svg>
              </button>
            </div>
          </header>

          <div v-if="latest" class="detail-latest">
            <time class="detail-latest-time">{{ latest.time }}</time>
            <p class="detail-latest-txt">{{ latest.place }}，{{ latest.detail }}</p>
          </div>

          <!-- 横向进度（示意） -->
          <div class="progress-wrap" aria-hidden="true">
            <div class="progress-track">
              <div class="progress-fill" style="width: 46%" />
            </div>
            <div class="progress-nodes">
              <div class="pnode done">
                <span class="pnode-dot" />
                <span class="pnode-label">{{
                  dictT('tracking_progress_label', 'picked_up', '已揽收')
                }}</span>
              </div>
              <div class="pnode current">
                <span class="pnode-dot">
                  <svg class="pnode-plane" width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M3 12L12 3l4 4-6 6h6l2 2-2 2H8l-5-5z" />
                  </svg>
                </span>
                <span class="pnode-label">{{
                  dictT('tracking_progress_label', 'in_transit', '运输途中')
                }}</span>
              </div>
              <div class="pnode">
                <span class="pnode-dot hollow" />
                <span class="pnode-label">{{
                  dictT('tracking_progress_label', 'out_for_delivery', '派送中')
                }}</span>
              </div>
              <div class="pnode">
                <span class="pnode-dot hollow" />
                <span class="pnode-label">{{ dictT('tracking_progress_label', 'signed', '签收') }}</span>
              </div>
            </div>
          </div>

          <section class="trace-section">
            <div class="trace-toolbar">
              <h2 class="trace-title">{{ dictT('page_tracking_result', 'trace_title', '轨迹信息') }}</h2>
              <div class="trace-actions">
                <button type="button" class="linkish" @click="copyAllDetail">{{
                  dictT('page_tracking_result', 'btn_copy_detail', '复制详细')
                }}</button>
                <span class="trace-sep">|</span>
                <button type="button" class="linkish">{{
                  dictT('page_tracking_result', 'btn_copy_link', '复制链接')
                }}</button>
              </div>
            </div>

            <ol class="trace-list">
              <li v-for="(ev, idx) in timeline" :key="idx" class="trace-row">
                <div class="trace-time">{{ ev.time }}</div>
                <div class="trace-body">
                  <div class="trace-line">{{ ev.place }}，{{ ev.detail }}</div>
                  <div class="trace-sub">{{ ev.status }}</div>
                </div>
              </li>
            </ol>
          </section>
          </article>
        </div>

        <aside class="result-side">
        <div class="side-query-card">
          <textarea
            v-model="trackingInput"
            class="side-textarea"
            rows="7"
            spellcheck="false"
            :placeholder="
              dictT(
                'page_tracking_result',
                'side_textarea_placeholder',
                '1. 每行输入一个单号，最多只允许提交 40 个单号。\n2. 用空格、逗号或换行分隔。',
              )
            "
          />
          <div class="side-query-actions">
            <button type="button" class="btn-text-side" @click="emit('clear')">{{
              dictT('page_tracking_result', 'side_btn_clear', '清空')
            }}</button>
            <button type="button" class="btn-query-orange" :disabled="!canQuery" @click="emit('query')">
              <span class="btn-query-ico" aria-hidden="true" />
              {{ dictT('page_tracking_result', 'side_btn_query', '查询') }}
            </button>
          </div>
        </div>

        <div class="side-spacer" aria-hidden="true" />

        <div class="side-promo-stack">
          <div class="side-promo">
            <p class="side-promo-title">{{
              dictT('page_tracking_result', 'side_promo_api_title', 'API 集成全球 3200+ 运输商')
            }}</p>
            <p class="side-promo-desc">{{
              dictT(
                'page_tracking_result',
                'side_promo_api_desc',
                '稳定轨迹数据，一次对接多承运商查询与推送（演示文案）。',
              )
            }}</p>
          </div>

          <div class="side-promo side-promo--mid">
            <p class="side-promo-title">{{
              dictT('page_tracking_result', 'side_promo_webhook_title', 'Webhook 轨迹推送')
            }}</p>
            <p class="side-promo-desc">{{
              dictT(
                'page_tracking_result',
                'side_promo_webhook_desc',
                '订阅运输节点变更，由系统主动向您的业务 URL 推送 JSON 载荷（演示文案）。',
              )
            }}</p>
          </div>

          <div class="side-promo side-promo--alt">
            <p class="side-promo-title">{{
              dictT('page_tracking_result', 'side_promo_console_title', '企业物流中控台')
            }}</p>
            <p class="side-promo-desc">{{
              dictT(
                'page_tracking_result',
                'side_promo_console_desc',
                '多角色协同、轨迹聚合与异常提醒，助力跨境履约。',
              )
            }}</p>
          </div>
        </div>
        </aside>
      </div>
    </div>
  </main>
</template>

<style scoped>
.result-page {
  flex: 1;
  padding: 16px 16px 32px;
  background: var(--17-bg);
}

.result-shell {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-columns {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: stretch;
}

@media (min-width: 1024px) {
  .result-columns {
    flex-direction: row;
    align-items: stretch;
    gap: 24px;
  }
}

.result-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-card-status {
  padding: 12px 16px 10px;
  border-bottom: 1px solid var(--17-border);
  background: var(--17-panel-subtle);
}

.detail-card-status .status-strip {
  padding: 0;
}

.result-side {
  width: 100%;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

@media (min-width: 1024px) {
  .result-side {
    width: 300px;
    align-self: stretch;
    min-height: 0;
  }

  /* 占位吸收纵向剩余空间，底部信息卡与左侧主卡底对齐，不拉高输入框 */
  .side-spacer {
    display: block;
    flex: 1 1 auto;
    min-height: 0;
  }
}

.side-spacer {
  display: none;
}

/* 三张信息卡纵向间距（占位仍负责与左侧底对齐） */
.side-promo-stack {
  display: flex;
  flex-direction: column;
  gap: 18px;
  flex-shrink: 0;
}

/* —— 状态条 —— */
.status-strip {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 4px 0 8px;
}

.status-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid var(--17-border);
  background: rgba(15, 22, 36, 0.55);
  color: var(--17-muted);
  font-size: 12px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition:
    background 0.15s,
    border-color 0.15s,
    color 0.15s;
}

.status-tab:hover {
  border-color: rgba(94, 176, 255, 0.35);
  color: var(--17-ink);
}

.status-tab.active {
  border-color: rgba(61, 141, 255, 0.45);
  background: var(--17-teal-soft);
  color: var(--17-teal-dark);
}

.status-tab-ico {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid var(--17-border);
}

.status-tab-num {
  font-weight: 800;
  color: var(--17-ink);
}

.status-tab.active .status-tab-num {
  color: var(--17-teal);
}

/* —— 主卡片 —— */
.detail-card {
  background: var(--17-card);
  border: 1px solid var(--17-border);
  border-radius: 14px;
  box-shadow: var(--17-shadow);
  overflow: hidden;
}

.detail-head {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 18px 18px 14px;
  border-bottom: 1px solid var(--17-border);
  background: var(--17-panel-subtle);
}

.detail-head-left {
  display: flex;
  gap: 12px;
  min-width: 0;
}

.carrier-ico {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: linear-gradient(145deg, #3d8dff, #256fea);
  color: #fff;
}

.detail-head-text {
  min-width: 0;
}

.detail-no-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.detail-no {
  font-size: 17px;
  font-weight: 800;
  letter-spacing: 0.02em;
  color: var(--17-ink);
  word-break: break-all;
}

.detail-no-more {
  font-size: 12px;
  font-weight: 700;
  color: var(--17-muted);
}

.detail-status-pill {
  font-size: 12px;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(61, 141, 255, 0.15);
  color: var(--17-teal-dark);
  border: 1px solid rgba(61, 141, 255, 0.35);
}

.copy-toast {
  font-size: 12px;
  color: #7dffb3;
  font-weight: 600;
}

.detail-route {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--17-muted);
  font-weight: 500;
}

.detail-head-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  padding: 0;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--17-muted);
  cursor: pointer;
  transition:
    background 0.15s,
    color 0.15s;
}

.icon-btn:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--17-ink);
}

.icon-btn-danger:hover {
  background: rgba(239, 68, 68, 0.12);
  color: #f87171;
}

.detail-latest {
  padding: 14px 18px;
  border-bottom: 1px solid var(--17-border);
}

.detail-latest-time {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: var(--17-teal-dark);
  font-variant-numeric: tabular-nums;
  margin-bottom: 6px;
}

.detail-latest-txt {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  color: var(--17-ink);
  font-weight: 500;
}

/* —— 横向进度 —— */
.progress-wrap {
  padding: 20px 18px 8px;
  border-bottom: 1px solid var(--17-border);
}

.progress-track {
  height: 4px;
  border-radius: 4px;
  background: #243044;
  position: relative;
  margin-bottom: 18px;
}

.progress-fill {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  border-radius: 4px;
  background: linear-gradient(90deg, #3d8dff, #5eb0ff);
}

.progress-nodes {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.pnode {
  flex: 1;
  text-align: center;
  min-width: 0;
}

.pnode-dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--17-teal);
  color: #fff;
  margin-bottom: 6px;
}

.pnode-dot.hollow {
  background: transparent;
  border: 2px solid #4a5f78;
  box-sizing: border-box;
}

.pnode.done .pnode-dot {
  background: #3d8dff;
}

.pnode.current .pnode-dot {
  width: 28px;
  height: 28px;
  box-shadow: 0 0 0 4px rgba(61, 141, 255, 0.2);
}

.pnode-plane {
  display: block;
}

.pnode-label {
  display: block;
  font-size: 11px;
  font-weight: 600;
  color: var(--17-muted);
  line-height: 1.3;
}

.pnode.current .pnode-label {
  color: var(--17-teal-dark);
}

/* —— 轨迹列表 —— */
.trace-section {
  padding: 16px 18px 20px;
}

.trace-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 14px;
}

.trace-title {
  margin: 0;
  font-size: 15px;
  font-weight: 800;
  color: var(--17-ink);
}

.trace-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.trace-sep {
  color: var(--17-border);
  user-select: none;
}

.linkish {
  padding: 0;
  border: none;
  background: none;
  font-size: 12px;
  font-weight: 600;
  font-family: inherit;
  color: var(--17-link);
  cursor: pointer;
}

.linkish:hover {
  text-decoration: underline;
  color: var(--17-link-hover);
}

.trace-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.trace-row {
  display: grid;
  grid-template-columns: 132px 1fr;
  gap: 12px 16px;
  padding: 12px 0;
  border-top: 1px solid var(--17-border);
  font-size: 13px;
}

.trace-row:first-child {
  border-top: none;
  padding-top: 0;
}

@media (max-width: 560px) {
  .trace-row {
    grid-template-columns: 1fr;
    gap: 4px;
  }
}

.trace-time {
  font-variant-numeric: tabular-nums;
  color: var(--17-muted);
  font-weight: 600;
}

.trace-line {
  color: var(--17-ink);
  line-height: 1.55;
  font-weight: 500;
}

.trace-sub {
  margin-top: 4px;
  font-size: 12px;
  color: var(--17-muted);
}

/* —— 右侧边栏 —— */
.side-query-card {
  background: var(--17-card);
  border: 1px solid var(--17-border);
  border-radius: 12px;
  padding: 14px;
  box-shadow: var(--17-shadow);
}

.side-textarea {
  width: 100%;
  box-sizing: border-box;
  resize: none;
  min-height: 140px;
  max-height: 260px;
  border-radius: 8px;
  border: 1px solid var(--17-border);
  padding: 10px 11px;
  font-size: 13px;
  line-height: 1.5;
  font-family: inherit;
  color: var(--17-ink);
  background: var(--17-input-bg);
  outline: none;
}

.side-textarea:focus {
  border-color: rgba(245, 130, 32, 0.55);
  box-shadow: 0 0 0 2px rgba(245, 130, 32, 0.12);
}

.side-query-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-top: 10px;
}

.btn-text-side {
  border: none;
  background: none;
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  color: var(--17-muted);
  cursor: pointer;
  padding: 8px 4px;
}

.btn-text-side:hover {
  color: var(--17-ink);
}

.btn-query-orange {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 11px 16px;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 800;
  font-family: inherit;
  color: #fff;
  background: linear-gradient(180deg, #ff9330, #f56600);
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(245, 102, 0, 0.28);
  max-width: 220px;
}

.btn-query-orange:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-query-orange:not(:disabled):hover {
  filter: brightness(1.04);
}

.btn-query-ico {
  width: 16px;
  height: 16px;
  border: 2.5px solid rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  position: relative;
}

.btn-query-ico::after {
  content: '';
  position: absolute;
  width: 6px;
  height: 2px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 1px;
  right: -4px;
  bottom: -1px;
  transform: rotate(45deg);
}

.side-promo {
  padding: 20px 16px;
  min-height: 118px;
  border-radius: 12px;
  border: 1px solid rgba(61, 141, 255, 0.25);
  background: linear-gradient(160deg, rgba(30, 50, 90, 0.55) 0%, var(--17-card) 100%);
}

.side-promo--mid {
  border-color: rgba(61, 141, 255, 0.22);
  background: linear-gradient(160deg, rgba(28, 48, 82, 0.52) 0%, var(--17-card) 100%);
}

.side-promo--alt {
  border-color: rgba(61, 141, 255, 0.2);
  background: linear-gradient(160deg, rgba(25, 40, 72, 0.5) 0%, var(--17-card) 100%);
}

.side-promo-title {
  margin: 0 0 10px;
  font-size: 14px;
  font-weight: 800;
  color: var(--17-ink);
  line-height: 1.35;
}

.side-promo-desc {
  margin: 0;
  font-size: 12px;
  line-height: 1.65;
  color: var(--17-muted);
}
</style>
