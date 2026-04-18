<script setup>
import { computed } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'

defineEmits(['reset'])

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_shipment_summary'], uiLang)

const btnReset = computed(() => t('page_shipment_summary', 'btn_reset', '重新查询'))
</script>

<template>
  <aside class="side-card">
    <div class="side-title">运单摘要</div>
    <dl class="kv">
      <div>
        <dt>最新状态</dt>
        <dd>运输中</dd>
      </div>
      <div>
        <dt>承运商</dt>
        <dd>自动识别</dd>
      </div>
      <div>
        <dt>目的地</dt>
        <dd>中国 · 上海</dd>
      </div>
    </dl>
    <button type="button" class="btn-ghost full" @click="$emit('reset')">{{ btnReset }}</button>
  </aside>
</template>

<style scoped>
.side-card {
  padding: 18px 20px;
  border-bottom: 1px solid var(--17-border);
  background: var(--17-side-muted);
}

@media (min-width: 900px) {
  .side-card {
    border-bottom: 0;
    border-right: 1px solid var(--17-border);
  }
}

.side-title {
  font-weight: 700;
  font-size: 13px;
  color: var(--17-muted);
  letter-spacing: 0.06em;
  text-transform: uppercase;
  margin-bottom: 12px;
}

.kv {
  margin: 0;
}

.kv > div {
  display: grid;
  grid-template-columns: 92px 1fr;
  gap: 10px;
  padding: 10px 0;
  border-top: 1px dashed var(--17-border);
}

.kv > div:first-of-type {
  border-top: 0;
  padding-top: 0;
}

.kv dt {
  margin: 0;
  color: var(--17-muted);
  font-size: 13px;
  font-weight: 600;
}

.kv dd {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
}
</style>
