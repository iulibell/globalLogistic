<script setup>
import { computed } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['page_marketing_stats'], uiLang)

const titleSuffix = computed(() =>
  t('page_marketing_stats', 'title_suffix', '承运商，一站式整合'),
)
const statsLead = computed(() =>
  t(
    'page_marketing_stats',
    'stats_lead',
    '选择高质量的物流商，完善包裹轨迹，提升用户购物体验。',
  ),
)
</script>

<template>
  <section class="stats">
    <div class="stats-top-notch" aria-hidden="true">
      <span class="stats-notch-glow" />
    </div>
    <div class="stats-inner">
      <h2 class="stats-title">
        <span class="stats-title-accent">3200+</span>
        {{ titleSuffix }}
      </h2>
      <p class="stats-lead">{{ statsLead }}</p>
    </div>
  </section>
</template>

<style scoped>
.stats {
  position: relative;
  padding: 0 20px 48px;
  text-align: center;
  background: linear-gradient(180deg, var(--17-bg-soft) 0%, var(--17-bg) 42%);
  overflow: hidden;
}

/* 图二：顶缘浅 V 层次（蓝黑版） */
.stats-top-notch {
  position: relative;
  height: 26px;
  margin-bottom: 10px;
  max-width: 920px;
  margin-left: auto;
  margin-right: auto;
}

.stats-notch-glow {
  position: absolute;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: min(72%, 560px);
  height: 22px;
  background: radial-gradient(ellipse 100% 100% at 50% 100%, rgba(61, 141, 255, 0.2), transparent 72%);
  clip-path: polygon(8% 0, 50% 100%, 92% 0);
  pointer-events: none;
}

.stats-inner {
  max-width: 720px;
  margin: 0 auto;
  padding-top: 8px;
}

.stats-title {
  margin: 0 0 14px;
  font-size: clamp(1.35rem, 3.4vw, 1.9rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--17-ink);
  line-height: 1.35;
}

.stats-title-accent {
  color: var(--17-teal);
  margin-right: 0.12em;
}

.stats-lead {
  margin: 0;
  font-size: clamp(14px, 1.6vw, 15px);
  line-height: 1.7;
  color: var(--17-muted);
  font-weight: 500;
}
</style>
