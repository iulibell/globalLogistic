<script setup>
import { computed } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'

const model = defineModel({ type: String, default: '' })

defineProps({
  canQuery: {
    type: Boolean,
    required: true,
  },
})

defineEmits(['query', 'clear'])

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_tracking_search'], uiLang)

const chipLabel = computed(() => t('page_tracking_search', 'chip_carrier', '自动识别承运商'))
const placeholder = computed(() =>
  t(
    'page_tracking_search',
    'textarea_placeholder',
    '在此粘贴或输入运单号，例如：SF1234567890\nYT9876543210',
  ),
)
const btnClear = computed(() => t('page_tracking_search', 'btn_clear', '清空'))
const btnQuery = computed(() => t('page_tracking_search', 'btn_query', '查询'))
</script>

<template>
  <div class="search-card">
    <div class="search-toolbar">
      <span class="chip chip-active">{{ chipLabel }}</span>
    </div>
    <textarea
      v-model="model"
      class="tracking-input"
      rows="3"
      spellcheck="false"
      :placeholder="placeholder"
    />
    <div class="search-actions">
      <button type="button" class="btn-text" @click="$emit('clear')">{{ btnClear }}</button>
      <button type="button" class="btn-primary" :disabled="!canQuery" @click="$emit('query')">
        {{ btnQuery }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.search-card {
  background: var(--17-card);
  border: 1px solid var(--17-border);
  border-radius: var(--17-radius-lg);
  box-shadow: var(--17-shadow);
  padding: 12px 14px;
  text-align: left;
}

.search-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px 14px;
  margin-bottom: 12px;
}

.chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid var(--17-border);
  color: var(--17-muted);
  background: var(--17-chip-bg);
}

.chip-active {
  border-color: rgba(61, 141, 255, 0.45);
  color: var(--17-teal-dark);
  background: var(--17-teal-soft);
}

.tracking-input {
  width: 100%;
  resize: none;
  min-height: 84px;
  border-radius: var(--17-radius);
  border: 1px solid var(--17-border);
  padding: 9px 11px;
  font-size: 13px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  color: var(--17-ink);
  background: var(--17-input-bg);
  outline: none;
}

.tracking-input:focus {
  border-color: rgba(61, 141, 255, 0.55);
  box-shadow: 0 0 0 2px rgba(61, 141, 255, 0.15);
  background: var(--17-bg-soft);
}

.search-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}
</style>
