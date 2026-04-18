<script setup>
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { useSortedDictionaryOptions } from '@/composables/useSortedDictionaryOptions.js'

const model = defineModel({ type: String, default: 'zh-CN' })

const open = ref(false)
const root = ref(null)

const FALLBACK_LANG_OPTIONS = [
  { value: 'zh-CN', label: '简体中文' },
  { value: 'en', label: 'English' },
  { value: 'ru', label: 'Русский' },
]

/** 固定三种界面语言，字典缺项时用兜底文案（避免库未同步时少一项） */
const LANG_CODES = ['zh-CN', 'en', 'ru']

const { options: langOptions } = useSortedDictionaryOptions('ui_lang_option', model, FALLBACK_LANG_OPTIONS)

const displayLangOptions = computed(() => {
  const labelByValue = new Map(langOptions.value.map((o) => [o.value, o.label]))
  return LANG_CODES.map((value) => ({
    value,
    label:
      labelByValue.get(value) ??
      FALLBACK_LANG_OPTIONS.find((f) => f.value === value)?.label ??
      value,
  }))
})

const { t: dictT } = useMultiDictionary(['ui_lang_panel'], model)

const helpTranslateLabel = computed(() =>
  dictT('ui_lang_panel', 'help_translate', '帮助我们翻译'),
)

const currentLabel = computed(() => {
  const v = model.value
  return (
    displayLangOptions.value.find((l) => l.value === v)?.label ??
    (v === 'en' ? 'English' : v === 'ru' ? 'Русский' : '简体中文')
  )
})

function persist(v) {
  try {
    localStorage.setItem('gl_ui_lang', v)
  } catch {
    /* ignore */
  }
}

function selectLang(v) {
  model.value = v
  persist(v)
  open.value = false
}

function onDocClick(e) {
  const el = root.value
  if (!el || el.contains(e.target)) return
  open.value = false
}

watch(open, (isOpen) => {
  if (!isOpen) {
    document.removeEventListener('click', onDocClick, true)
    return
  }
  nextTick(() => {
    document.addEventListener('click', onDocClick, true)
  })
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocClick, true)
})
</script>

<template>
  <div ref="root" class="lang-dropdown">
    <button
      type="button"
      class="lang-trigger"
      :aria-expanded="open"
      aria-haspopup="listbox"
      @click.stop="open = !open"
    >
      <span class="lang-trigger-text">{{ currentLabel }}</span>
      <span class="lang-trigger-caret" aria-hidden="true" />
    </button>

    <div v-show="open" class="lang-panel" role="listbox" @click.stop>
      <a href="#" class="lang-panel-top" @click.prevent="open = false">{{ helpTranslateLabel }}</a>
      <div class="lang-divider" />
      <button
        v-for="opt in displayLangOptions"
        :key="opt.value"
        type="button"
        class="lang-option"
        :class="{ active: model === opt.value }"
        role="option"
        :aria-selected="model === opt.value"
        @click="selectLang(opt.value)"
      >
        {{ opt.label }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.lang-dropdown {
  position: relative;
}

.lang-trigger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin: 0;
  padding: 4px 0;
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  font-family: inherit;
  color: var(--17-ink);
  cursor: pointer;
  line-height: 1.4;
}

.lang-trigger:hover {
  color: var(--17-link-hover);
}

.lang-trigger:focus {
  outline: none;
}

.lang-trigger:focus-visible {
  border-radius: 4px;
  box-shadow: 0 0 0 2px rgba(61, 141, 255, 0.35);
}

.lang-trigger-caret {
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid var(--17-ink);
  flex-shrink: 0;
  margin-top: 2px;
}

.lang-panel {
  position: absolute;
  top: calc(100% + 6px);
  right: 0;
  min-width: 200px;
  max-height: 280px;
  overflow-y: auto;
  padding: 0;
  margin: 0;
  background: var(--17-mega-surface);
  border: 1px solid var(--17-mega-border);
  border-radius: 6px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.45);
  z-index: 100;
  text-align: left;
}

.lang-panel::-webkit-scrollbar {
  width: 5px;
}

.lang-panel::-webkit-scrollbar-track {
  background: transparent;
}

.lang-panel::-webkit-scrollbar-thumb {
  background: #3d4f66;
  border-radius: 3px;
}

.lang-panel-top {
  display: block;
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  color: var(--17-ink);
  text-decoration: none;
  cursor: default;
}

.lang-panel-top:hover {
  background: var(--17-card-elevated);
}

.lang-divider {
  height: 1px;
  margin: 0;
  background: var(--17-border);
  border: none;
}

.lang-option {
  display: block;
  width: 100%;
  margin: 0;
  padding: 12px 16px;
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  font-family: inherit;
  color: var(--17-ink);
  text-align: left;
  cursor: pointer;
  line-height: 1.45;
}

.lang-option:hover {
  background: var(--17-card-elevated);
}

.lang-option.active {
  font-weight: 700;
  background: var(--17-teal-soft);
}
</style>
