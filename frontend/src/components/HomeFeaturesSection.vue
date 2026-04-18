<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'

const root = ref(null)
const visible = ref(false)
let observer

const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['home_feature_card', 'home_feature_section', 'home_feature_body'], uiLang)

const FALLBACK_TITLES = {
  feat_search: '提供高质量查询',
  feat_devices: '多端同步',
  feat_carriers: '3200+ 运输商',
  feat_automation: '自动化解决方案',
  feat_support: '7×24 小时客户服务',
  feat_gdpr: '遵守《通用数据保护条例》',
}

const FALLBACK_BODIES = {
  feat_search:
    '自动识别超过 80% 的运输商，全程跟踪物流信息，提供清晰标准的轨迹状态点，完整准确的轨迹信息，查询准确率达 99.9%。',
  feat_devices:
    '提供丰富的查询产品与服务，实现多端数据同步，满足不同场景需求。支持官网、移动端应用与小程序等查询方式。',
  feat_carriers:
    '已与 3200+ 物流商建立合作，覆盖全球主要线路。如需追踪更多承运商包裹，可向客服反馈，我们将尽力支持您的业务场景。',
  feat_automation:
    '为用户提供自动化能力以提升效率：自动识别运输商、包裹自动跟踪、物流状态自动推送（减少反复查询）等。',
  feat_support:
    '专业客户服务团队快速响应问题，为用户提供 7×24 小时在线支持，保障查件与对接体验稳定顺畅。',
  feat_gdpr:
    '与电商平台及物流伙伴长期协作，通过合规手段获取与处理数据，遵守 GDPR 等隐私规范，保护用户数据与隐私安全。',
}

const CARD_DEF = [
  { icon: 'search-map', key: 'feat_search' },
  { icon: 'devices', key: 'feat_devices' },
  { icon: 'truck', key: 'feat_carriers' },
  { icon: 'gears', key: 'feat_automation' },
  { icon: 'headset', key: 'feat_support' },
  { icon: 'shield', key: 'feat_gdpr' },
]

const featuresList = computed(() =>
  CARD_DEF.map((c) => ({
    icon: c.icon,
    key: c.key,
    title: t('home_feature_card', c.key, FALLBACK_TITLES[c.key]),
    body: t('home_feature_body', c.key, FALLBACK_BODIES[c.key]),
  })),
)

const sectionTitle = computed(() =>
  t('home_feature_section', 'section_title', '让全球包裹查询更便捷'),
)
const sectionSub = computed(() =>
  t('home_feature_section', 'section_sub', '升级消费者用户的购物体验，更好地助力跨境人的全球生意。'),
)

onMounted(() => {
  const el = root.value
  if (!el || typeof IntersectionObserver === 'undefined') {
    visible.value = true
    return
  }
  observer = new IntersectionObserver(
    (entries) => {
      for (const e of entries) {
        if (e.isIntersecting) {
          visible.value = true
          observer?.disconnect()
          observer = undefined
          break
        }
      }
    },
    { root: null, rootMargin: '0px 0px -8% 0px', threshold: 0.08 },
  )
  observer.observe(el)
})

onBeforeUnmount(() => {
  observer?.disconnect()
})
</script>

<template>
  <section ref="root" class="features" :class="{ 'features--visible': visible }">
    <div class="features-bg" aria-hidden="true" />
    <div class="features-inner">
      <header class="features-head">
        <h2 class="features-title">{{ sectionTitle }}</h2>
        <p class="features-sub">
          {{ sectionSub }}
        </p>
      </header>

      <ul class="features-grid">
        <li v-for="(item, i) in featuresList" :key="item.key" class="features-card" :style="{ '--st': `${i * 45}ms` }">
          <div class="features-icon" :data-icon="item.icon" aria-hidden="true">
            <!-- search + map -->
            <svg v-if="item.icon === 'search-map'" class="ico-svg" viewBox="0 0 64 64" fill="none">
              <path
                d="M8 44 L22 18 L38 28 L52 12 L56 48 L8 48 Z"
                :fill="`url(#feat-${i}-m1)`"
                opacity="0.9"
              />
              <circle cx="26" cy="26" r="10" stroke="rgba(255,255,255,0.35)" stroke-width="2.5" fill="rgba(15,22,36,0.5)" />
              <line
                x1="33"
                y1="33"
                x2="44"
                y2="44"
                :stroke="`url(#feat-${i}-m2)`"
                stroke-width="3"
                stroke-linecap="round"
              />
              <defs>
                <linearGradient :id="`feat-${i}-m1`" x1="8" y1="12" x2="56" y2="48" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#6b5cff" />
                  <stop offset="1" stop-color="#3d8dff" />
                </linearGradient>
                <linearGradient :id="`feat-${i}-m2`" x1="33" y1="33" x2="44" y2="44" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#9ccdff" />
                  <stop offset="1" stop-color="#5c9dff" />
                </linearGradient>
              </defs>
            </svg>
            <!-- devices -->
            <svg v-else-if="item.icon === 'devices'" class="ico-svg" viewBox="0 0 64 64" fill="none">
              <rect x="6" y="14" width="40" height="28" rx="4" :fill="`url(#feat-${i}-d1)`" stroke="rgba(255,255,255,0.2)" />
              <rect x="34" y="26" width="22" height="34" rx="4" :fill="`url(#feat-${i}-d2)`" stroke="rgba(255,255,255,0.25)" />
              <rect x="40" y="32" width="10" height="14" rx="1.5" fill="rgba(0,0,0,0.25)" />
              <defs>
                <linearGradient :id="`feat-${i}-d1`" x1="6" y1="14" x2="46" y2="42">
                  <stop stop-color="#4a7dff" />
                  <stop offset="1" stop-color="#256fea" />
                </linearGradient>
                <linearGradient :id="`feat-${i}-d2`" x1="34" y1="26" x2="56" y2="60">
                  <stop stop-color="#7eb8ff" />
                  <stop offset="1" stop-color="#3d8dff" />
                </linearGradient>
              </defs>
            </svg>
            <!-- truck -->
            <svg v-else-if="item.icon === 'truck'" class="ico-svg" viewBox="0 0 64 64" fill="none">
              <path
                d="M6 34h28v14H6V34zm30 8h10l8 6v8H36V42z"
                :fill="`url(#feat-${i}-t1)`"
                stroke="rgba(255,255,255,0.2)"
                stroke-width="1.2"
              />
              <circle cx="16" cy="50" r="5" fill="#1a2434" stroke="rgba(255,255,255,0.35)" />
              <circle cx="44" cy="50" r="5" fill="#1a2434" stroke="rgba(255,255,255,0.35)" />
              <defs>
                <linearGradient :id="`feat-${i}-t1`" x1="6" y1="34" x2="54" y2="52">
                  <stop stop-color="#5c9dff" />
                  <stop offset="1" stop-color="#256fea" />
                </linearGradient>
              </defs>
            </svg>
            <!-- gears -->
            <svg v-else-if="item.icon === 'gears'" class="ico-svg" viewBox="0 0 64 64" fill="none">
              <circle cx="22" cy="28" r="12" :fill="`url(#feat-${i}-g1)`" stroke="rgba(255,255,255,0.2)" />
              <circle cx="40" cy="36" r="10" :fill="`url(#feat-${i}-g2)`" stroke="rgba(255,255,255,0.2)" />
              <circle cx="22" cy="28" r="5" fill="rgba(10,14,22,0.55)" />
              <circle cx="40" cy="36" r="4" fill="rgba(10,14,22,0.55)" />
              <defs>
                <linearGradient :id="`feat-${i}-g1`" x1="10" y1="16" x2="34" y2="40">
                  <stop stop-color="#6b5cff" />
                  <stop offset="1" stop-color="#4a6dff" />
                </linearGradient>
                <linearGradient :id="`feat-${i}-g2`" x1="30" y1="26" x2="50" y2="46">
                  <stop stop-color="#5c9dff" />
                  <stop offset="1" stop-color="#3d8dff" />
                </linearGradient>
              </defs>
            </svg>
            <!-- headset -->
            <svg v-else-if="item.icon === 'headset'" class="ico-svg" viewBox="0 0 64 64" fill="none">
              <path
                d="M16 30c0-10 9-18 20-18s20 8 20 18v6h-6v-6c0-7-6-12-14-12s-14 5-14 12v14c0 3 2 5 5 5h4v6h-4c-6 0-11-5-11-11V30z"
                :fill="`url(#feat-${i}-h1)`"
                opacity="0.95"
              />
              <rect x="8" y="32" width="10" height="16" rx="3" fill="#7eb8ff" stroke="rgba(255,255,255,0.25)" />
              <rect x="46" y="32" width="10" height="16" rx="3" fill="#7eb8ff" stroke="rgba(255,255,255,0.25)" />
              <defs>
                <linearGradient :id="`feat-${i}-h1`" x1="16" y1="12" x2="56" y2="40">
                  <stop stop-color="#9ccdff" />
                  <stop offset="1" stop-color="#3d8dff" />
                </linearGradient>
              </defs>
            </svg>
            <!-- shield server -->
            <svg v-else class="ico-svg" viewBox="0 0 64 64" fill="none">
              <rect x="10" y="14" width="44" height="10" rx="2" fill="rgba(255,255,255,0.12)" stroke="rgba(255,255,255,0.2)" />
              <rect x="10" y="28" width="44" height="10" rx="2" fill="rgba(255,255,255,0.1)" stroke="rgba(255,255,255,0.18)" />
              <rect x="10" y="42" width="44" height="10" rx="2" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.15)" />
              <path
                d="M32 4 L48 10 V26c0 12-8 22-16 26-8-4-16-14-16-26V10L32 4Z"
                :fill="`url(#feat-${i}-s1)`"
                opacity="0.92"
              />
              <defs>
                <linearGradient :id="`feat-${i}-s1`" x1="16" y1="4" x2="48" y2="40">
                  <stop stop-color="#3d8dff" />
                  <stop offset="1" stop-color="#256fea" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h3 class="features-card-title">{{ item.title }}</h3>
          <p class="features-card-body">{{ item.body }}</p>
        </li>
      </ul>
    </div>
  </section>
</template>

<style scoped>
.features {
  position: relative;
  padding: 56px 20px 64px;
  overflow: hidden;
}

.features-bg {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse 90% 55% at 50% -10%, rgba(61, 141, 255, 0.14) 0%, transparent 55%),
    linear-gradient(168deg, #0a101c 0%, var(--17-bg) 38%, #0c1422 100%);
  pointer-events: none;
}

.features-inner {
  position: relative;
  z-index: 1;
  max-width: 1120px;
  margin: 0 auto;
}

.features-head {
  text-align: center;
  margin-bottom: 40px;
  opacity: 0;
  transform: translateY(16px);
  transition:
    opacity 0.55s ease,
    transform 0.55s ease;
}

.features--visible .features-head {
  opacity: 1;
  transform: translateY(0);
}

.features-title {
  margin: 0 0 12px;
  font-size: clamp(1.5rem, 3.2vw, 2rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #f0f4fc;
}

.features-sub {
  margin: 0 auto;
  max-width: 560px;
  font-size: 15px;
  line-height: 1.65;
  font-weight: 500;
  color: rgba(232, 238, 246, 0.72);
}

.features-grid {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

@media (min-width: 720px) {
  .features-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (min-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 20px;
  }
}

.features-card {
  margin: 0;
  padding: 22px 20px 24px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(20, 28, 42, 0.72);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35);
  opacity: 0;
  transform: translateY(18px);
  transition:
    opacity 0.5s ease var(--st, 0ms),
    transform 0.5s ease var(--st, 0ms),
    border-color 0.2s ease,
    background 0.2s ease;
}

.features--visible .features-card {
  opacity: 1;
  transform: translateY(0);
}

.features-card:hover {
  border-color: rgba(94, 176, 255, 0.28);
  background: rgba(24, 34, 52, 0.88);
}

.features-icon {
  width: 56px;
  height: 56px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ico-svg {
  width: 52px;
  height: 52px;
  display: block;
  filter: drop-shadow(0 6px 12px rgba(0, 0, 0, 0.35));
}

.features-card-title {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 700;
  color: #f4f7fc;
  letter-spacing: -0.02em;
}

.features-card-body {
  margin: 0;
  font-size: 13px;
  line-height: 1.65;
  color: rgba(232, 238, 246, 0.78);
  font-weight: 500;
}
</style>
