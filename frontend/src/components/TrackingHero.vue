<script setup>
import { computed } from 'vue'
import TrackingSearchCard from './TrackingSearchCard.vue'
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
const { t } = useMultiDictionary(['page_home_hero'], uiLang)

const kicker = computed(() => t('page_home_hero', 'kicker', '支持多承运商 · 轨迹一站式展示'))
const title = computed(() => t('page_home_hero', 'title', '让全球包裹查询更便捷'))
</script>

<template>
  <section class="hero">
    <div class="hero-inner">
      <p class="hero-kicker">{{ kicker }}</p>
      <h1 class="hero-title">{{ title }}</h1>

      <TrackingSearchCard v-model="model" :can-query="canQuery" @query="$emit('query')" @clear="$emit('clear')" />
    </div>
  </section>
</template>

<style scoped>
.hero {
  background: radial-gradient(ellipse 120% 80% at 50% -20%, rgba(61, 141, 255, 0.12) 0%, transparent 55%),
    linear-gradient(180deg, var(--17-bg-soft) 0%, var(--17-bg) 100%);
  padding: 48px 20px 28px;
}

.hero-inner {
  max-width: 920px;
  margin: 0 auto;
  text-align: center;
}

.hero-kicker {
  margin: 0 0 10px;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--17-teal-dark);
}

.hero-title {
  margin: 0 0 26px;
  font-size: clamp(28px, 4vw, 40px);
  font-weight: 700;
  letter-spacing: -0.02em;
}
</style>
