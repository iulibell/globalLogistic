<script setup>
import { computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthSession } from '@/composables/useAuthSession.js'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import LanguageDropdown from '@/components/LanguageDropdown.vue'

const emit = defineEmits(['home'])

const router = useRouter()
const { isLoggedIn, profile, logout } = useAuthSession()
const { uiLang } = useUiLang()
const { t } = useMultiDictionary(
  [
    'site_header',
    'site_nav_top',
    'site_header_mega_product',
    'site_header_mega_carrier',
    'site_header_mega_resource',
  ],
  uiLang,
)

const navProduct = computed(() => t('site_nav_top', 'product', '产品'))
const navCarrier = computed(() => t('site_nav_top', 'carrier', '承运商'))
const navResources = computed(() => t('site_nav_top', 'resources', '资源'))
const txtHelp = computed(() => t('site_header', 'link_help', '帮助'))
const txtLogin = computed(() => t('site_header', 'link_login', '登录'))
const txtRegister = computed(() => t('site_header', 'link_register', '注册'))
const txtLogout = computed(() => t('site_header', 'btn_logout', '退出'))
const txtTrackDemo = computed(() => t('site_header', 'btn_track_demo', '查询'))

/** 顶部 mega 悬浮面板：随 gl_ui_lang 从字典加载 */
const mega = computed(() => {
  const p = (k, fb) => t('site_header_mega_product', k, fb)
  const c = (k, fb) => t('site_header_mega_carrier', k, fb)
  const r = (k, fb) => t('site_header_mega_resource', k, fb)
  return {
    product: {
      ariaRegion: p('aria_region', '产品与能力'),
      item1Title: p('item1_title', '全程轨迹追踪'),
      item1Desc: p(
        'item1_desc',
        '多承运商单号一站式查询，节点清晰、异常可感知，降低客服问询量。',
      ),
      item2Title: p('item2_title', 'Tracking API'),
      item2Desc: p(
        'item2_desc',
        '无缝集成来自 3200+ 家运输商的稳定追踪数据，适配电商、仓储与自研系统。',
      ),
      item3Title: p('item3_title', '移动端工作台'),
      item3Desc: p('item3_desc', '随时随地查件、处理异常与接收提醒，业务不断线。'),
      showcaseConsole: p('showcase_console', '物流中控台'),
      demoPhoneStatus: p('demo_phone_status', '运输中'),
      demoPhoneRecommend: p('demo_phone_recommend', '你可能还喜欢'),
      demoPhoneCta: p('demo_phone_cta', '立即购买'),
      chartTitle: p('chart_title', '提升品牌曝光 / 促进复购'),
      footnote: p(
        'footnote',
        '将物流状态融入购物旅程：用户少问一句「到哪了」，商家多一份信任与回头率。Global Logistics 提供可嵌入的轨迹组件与数据能力，助力转化与客诉管理。',
      ),
    },
    carrier: {
      ariaRegion: c('aria_region', '运输商与入驻'),
      block1Title: c('block1_title', '运输商'),
      block1Desc: c('block1_desc', '支持全球 3200+ 家运输商和 190+ 家航空公司查询'),
      block2Title: c('block2_title', '运输商入驻'),
      block2Desc: c('block2_desc', '运输商免费入驻'),
      cardTagline: c('card_tagline', '支持全球 3200+ 家运输商与 190+ 家航空公司'),
      footnote: c(
        'footnote',
        '通过我们的平台，您可以轻松追踪来自超过 3200+ 家运输商的包裹，轨迹清晰、节点标准，覆盖主流国际物流场景。',
      ),
    },
    resource: {
      ariaRegion: r('aria_region', '资源与支持'),
      navAria: r('nav_aria', '资源入口'),
      navItems: [
        {
          key: 'nav1',
          title: r('nav1_title', '物流查询小部件'),
          desc: r('nav1_desc', '嵌入查询小部件，让用户在您的网站随时查询物流信息'),
        },
        {
          key: 'nav2',
          title: r('nav2_title', '合作伙伴'),
          desc: r('nav2_desc', '与知名平台 & 品牌合作'),
        },
        {
          key: 'nav3',
          title: r('nav3_title', 'API 开发文档'),
          desc: r('nav3_desc', '完整开发指南和技术支持'),
        },
        {
          key: 'nav4',
          title: r('nav4_title', '帮助中心'),
          desc: r('nav4_desc', 'Global Logistics 产品使用指南'),
        },
      ],
      widgetBrandSuffix: r('widget_brand_suffix', '物流查询'),
      widgetLabelNo: r('widget_label_no', '运单号'),
      widgetPlaceholder: r('widget_placeholder', '输入运单号'),
      widgetSample: r('widget_sample', '示例：RG017001608CN'),
      widgetAside: r('widget_aside', '在您的网站一键查件'),
      megaFootnote: r(
        'mega_footnote',
        '在您的网站上嵌入我们的免费查询小部件，让用户随时查询包裹信息。只需输入运单号，即可实时追踪包裹动态。',
      ),
    },
  }
})

/** 展示板中的承运商标识占位（示意） */
const MEGA_CARRIER_TILES = [
  'DHL',
  'FedEx',
  'UPS',
  'SF',
  'EMS',
  'GLS',
  'USPS',
  'Sagawa',
  'DPD',
  'CDEK',
  'YTO',
  'TNT',
  'OCS',
  'IND',
  'POST',
  'YANWEN',
]

function goBrand() {
  router.push('/')
  emit('home')
}

async function onLogout() {
  await logout()
  await router.push('/')
}
</script>

<template>
  <header class="topbar">
    <div class="topbar-inner">
      <a class="brand" href="/" @click.prevent="goBrand">
        <span class="brand-text">
          <span class="brand-wordmark" role="img" aria-label="Global Logistics">
            <span class="wordmark-orange">GLOBAL</span><span class="wordmark-blue">LOGISTICS</span>
          </span>
          <span class="brand-sub">{{ t('site_header', 'brand_sub', '全球物流查询') }}</span>
        </span>
      </a>
      <nav class="nav">
        <div class="nav-mega-wrap">
          <RouterLink to="/" class="nav-mega-trigger nav-mega-trigger--link">{{ navProduct }}</RouterLink>
          <div class="mega-panel mega-panel--product" role="region" :aria-label="mega.product.ariaRegion">
            <div class="mega-product-inner">
              <aside class="mega-product-nav">
                <div class="mega-product-item">
                  <h3 class="mega-product-title">{{ mega.product.item1Title }}</h3>
                  <p class="mega-product-desc">{{ mega.product.item1Desc }}</p>
                </div>
                <div class="mega-product-item">
                  <h3 class="mega-product-title">{{ mega.product.item2Title }}</h3>
                  <p class="mega-product-desc">{{ mega.product.item2Desc }}</p>
                </div>
                <div class="mega-product-item">
                  <h3 class="mega-product-title">{{ mega.product.item3Title }}</h3>
                  <p class="mega-product-desc">{{ mega.product.item3Desc }}</p>
                </div>
              </aside>
              <div class="mega-product-stage">
                <div class="product-showcase-card" aria-hidden="true">
                  <div class="psc-top">
                    <span class="psc-badge">GL</span>
                    <span class="psc-badge-label">{{ mega.product.showcaseConsole }}</span>
                  </div>
                  <div class="psc-body">
                    <div class="psc-figure" />
                    <div class="psc-phones">
                      <div class="psc-phone psc-phone-a">
                        <div class="psc-phone-head">{{ mega.product.demoPhoneStatus }}</div>
                        <div class="psc-bar" />
                        <div class="psc-bar psc-bar-short" />
                        <div class="psc-dots"><span /><span /><span /></div>
                      </div>
                      <div class="psc-phone psc-phone-b">
                        <div class="psc-phone-head">{{ mega.product.demoPhoneRecommend }}</div>
                        <div class="psc-mini-card" />
                        <div class="psc-cta">{{ mega.product.demoPhoneCta }}</div>
                      </div>
                    </div>
                    <div class="psc-chart-block">
                      <p class="psc-chart-title">{{ mega.product.chartTitle }}</p>
                      <div class="psc-bars">
                        <span class="b1" /><span class="b2" /><span class="b3" /><span class="b4" />
                      </div>
                      <div class="psc-trend-arrow" />
                    </div>
                  </div>
                </div>
                <p class="mega-footnote mega-footnote--product">
                  {{ mega.product.footnote }}
                </p>
              </div>
            </div>
          </div>
        </div>
        <div class="nav-mega-wrap">
          <a href="#" class="nav-mega-trigger" @click.prevent>{{ navCarrier }}</a>
          <div class="mega-panel mega-panel--carrier" role="region" :aria-label="mega.carrier.ariaRegion">
            <div class="mega-panel-inner">
              <aside class="mega-col-left">
                <div class="mega-left-block">
                  <h3 class="mega-left-title">{{ mega.carrier.block1Title }}</h3>
                  <p class="mega-left-desc">{{ mega.carrier.block1Desc }}</p>
                </div>
                <div class="mega-left-block">
                  <h3 class="mega-left-title">{{ mega.carrier.block2Title }}</h3>
                  <p class="mega-left-desc">{{ mega.carrier.block2Desc }}</p>
                </div>
              </aside>
              <div class="mega-col-right">
                <div class="mega-card">
                  <div class="mega-card-text">
                    {{ mega.carrier.cardTagline }}
                  </div>
                  <div class="mega-logo-wrap">
                    <div class="mega-logo-grid" aria-hidden="true">
                      <span v-for="name in MEGA_CARRIER_TILES" :key="name" class="mega-logo-tile">{{ name }}</span>
                    </div>
                  </div>
                </div>
                <p class="mega-footnote">
                  {{ mega.carrier.footnote }}
                </p>
              </div>
            </div>
          </div>
        </div>
        <div class="nav-mega-wrap nav-mega-wrap--resources">
          <a href="#" class="nav-mega-trigger" @click.prevent>{{ navResources }}</a>
          <div class="mega-panel mega-panel--resources" role="region" :aria-label="mega.resource.ariaRegion">
            <div class="mega-resource-inner">
              <aside class="mega-resource-nav" :aria-label="mega.resource.navAria">
                <div v-for="item in mega.resource.navItems" :key="item.key" class="mega-resource-item">
                  <h3 class="mega-resource-title">{{ item.title }}</h3>
                  <p class="mega-resource-desc">{{ item.desc }}</p>
                </div>
              </aside>
              <div class="mega-resource-stage">
                <div class="resource-showcase-card" aria-hidden="true">
                  <div class="rs-pane rs-pane--widget">
                    <div class="rs-widget-grid">
                      <div class="rs-browser">
                        <div class="rs-browser-chrome">
                          <span class="rs-dot" /><span class="rs-dot" /><span class="rs-dot" />
                          <div class="rs-url">yoursite.com</div>
                        </div>
                        <div class="rs-browser-canvas">
                          <div class="rs-widget-pop">
                            <div class="rs-w-brand">
                              <span class="rs-w-logo">GL</span> {{ mega.resource.widgetBrandSuffix }}
                            </div>
                            <label class="rs-w-label" for="rs-demo-input">{{ mega.resource.widgetLabelNo }}</label>
                            <input
                              id="rs-demo-input"
                              class="rs-w-input"
                              type="text"
                              readonly
                              tabindex="-1"
                              :placeholder="mega.resource.widgetPlaceholder"
                            />
                            <button type="button" class="rs-w-track" tabindex="-1">{{ txtTrackDemo }}</button>
                            <span class="rs-w-sample">{{ mega.resource.widgetSample }}</span>
                          </div>
                          <div class="rs-cursor" aria-hidden="true" />
                        </div>
                      </div>
                      <div class="rs-widget-aside">
                        <p class="rs-aside-line">{{ mega.resource.widgetAside }}</p>
                      </div>
                    </div>
                  </div>
                </div>
                <p class="mega-footnote mega-footnote--resource">{{ mega.resource.megaFootnote }}</p>
              </div>
            </div>
          </div>
        </div>
        <a href="#">{{ txtHelp }}</a>
      </nav>
      <div class="nav-actions">
        <LanguageDropdown v-model="uiLang" />

        <template v-if="isLoggedIn">
          <span class="user-name" :title="profile?.username">{{ profile?.nickname || profile?.username }}</span>
          <button type="button" class="link-login" @click="onLogout">{{ txtLogout }}</button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="link-login">{{ txtLogin }}</RouterLink>
          <RouterLink to="/register" class="btn-register">{{ txtRegister }}</RouterLink>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.topbar {
  position: sticky;
  top: 0;
  z-index: 100;
  /* 不用 backdrop-filter：在 Chrome 等浏览器中常与「子元素溢出层叠」冲突，导致下拉被裁成顶栏一块区域 */
  background: var(--17-header-bg);
  border-bottom: 1px solid var(--17-border);
  overflow: visible;
}

.topbar-inner {
  position: relative;
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 14px 16px 14px 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-sizing: border-box;
  overflow: visible;
}

.brand {
  display: flex;
  align-items: center;
  gap: 0;
  flex-shrink: 0;
  color: inherit;
  text-decoration: none;
}

.brand:hover {
  text-decoration: none;
}

.brand:hover .wordmark-orange {
  color: #ffa866;
}

.brand:hover .wordmark-blue {
  color: #8ec5ff;
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
  line-height: 1;
}

.brand-wordmark {
  display: inline-flex;
  align-items: baseline;
  letter-spacing: -0.045em;
  font-size: clamp(1.05rem, 2.2vw, 1.35rem);
}

.wordmark-orange,
.wordmark-blue {
  font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Arial Black', 'Impact', sans-serif;
  font-weight: 900;
  text-transform: uppercase;
  line-height: 1;
}

.wordmark-orange {
  color: var(--17-brand-orange);
  position: relative;
  z-index: 1;
  padding-right: 0.02em;
  clip-path: polygon(0 0, 100% 0, calc(100% - 0.22em) 100%, 0 100%);
}

.wordmark-blue {
  color: var(--17-brand-blue);
  margin-left: -0.2em;
  position: relative;
  z-index: 0;
  clip-path: polygon(0.18em 0, 100% 0, 100% 100%, 0 100%);
}

.brand-sub {
  font-size: 12px;
  color: var(--17-muted);
  font-weight: 500;
  letter-spacing: 0.02em;
}

.nav {
  display: none;
  gap: 22px;
  margin-left: 0;
  font-size: 14px;
  font-weight: 500;
  overflow: visible;
}

.nav a,
.nav :deep(a),
.nav .nav-mega-trigger {
  color: var(--17-ink);
  text-decoration: none;
}

.nav a:hover,
.nav :deep(a:hover) {
  color: var(--17-teal-dark);
  text-decoration: none;
}

.nav-mega-wrap {
  position: relative;
  display: inline-flex;
  align-items: center;
  overflow: visible;
}

.nav-mega-trigger {
  color: var(--17-ink);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  cursor: default;
}

.nav-mega-wrap:hover .nav-mega-trigger {
  color: var(--17-teal-dark);
}

.nav-mega-trigger--link {
  cursor: pointer;
}

.mega-panel {
  position: absolute;
  /* 与触发器左缘对齐，避免「居中 translate」在靠左菜单时把面板甩出视口左侧 */
  left: 0;
  top: calc(100% + 6px);
  transform: none;
  width: min(920px, calc(100vw - 24px));
  padding: 0;
  background: var(--17-mega-surface);
  border: 1px solid var(--17-mega-border);
  border-radius: 12px;
  box-shadow: 0 20px 56px rgba(0, 0, 0, 0.5);
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
  transition:
    opacity 0.18s ease,
    visibility 0.18s ease;
  z-index: 500;
}

.mega-panel--product {
  width: min(980px, calc(100vw - 24px));
}

.mega-panel--resources {
  width: min(980px, calc(100vw - 24px));
}

.mega-panel::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: -12px;
  height: 12px;
}

.nav-mega-wrap:hover .mega-panel,
.mega-panel:hover {
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
}

.mega-panel-inner {
  display: grid;
  grid-template-columns: minmax(200px, 240px) 1fr;
  gap: 28px;
  padding: 28px 28px 26px;
  align-items: start;
}

@media (max-width: 720px) {
  .mega-panel-inner {
    grid-template-columns: 1fr;
  }
}

.mega-col-left {
  display: flex;
  flex-direction: column;
  gap: 28px;
  padding-right: 8px;
  border-right: 1px solid var(--17-mega-border);
}

@media (max-width: 720px) {
  .mega-col-left {
    border-right: none;
    padding-right: 0;
    border-bottom: 1px solid var(--17-mega-border);
    padding-bottom: 20px;
  }
}

.mega-left-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 700;
  color: var(--17-ink);
  letter-spacing: -0.02em;
}

.mega-left-desc {
  margin: 0;
  font-size: 13px;
  line-height: 1.55;
  color: var(--17-muted);
}

.mega-card {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 22px 24px;
  background: var(--17-card-elevated);
  border-radius: 18px;
  border: 1px solid var(--17-mega-border);
}

.mega-card-text {
  flex: 1;
  min-width: 200px;
  max-width: 340px;
  font-size: clamp(1.1rem, 2vw, 1.35rem);
  font-weight: 800;
  line-height: 1.35;
  color: var(--17-ink);
  letter-spacing: -0.02em;
}

.mega-logo-wrap {
  flex: 1;
  min-width: 200px;
  display: flex;
  justify-content: flex-end;
  overflow: hidden;
}

.mega-logo-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
  width: min(100%, 280px);
  transform: rotate(-4deg);
  transform-origin: center right;
}

.mega-logo-tile {
  display: flex;
  align-items: center;
  justify-content: center;
  aspect-ratio: 1;
  max-height: 44px;
  font-size: 10px;
  font-weight: 800;
  color: var(--17-muted);
  background: var(--17-mega-inner);
  border: 1px solid var(--17-mega-border);
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.mega-footnote {
  margin: 16px 0 0;
  font-size: 12px;
  line-height: 1.65;
  color: var(--17-muted);
}

.mega-product-inner {
  display: grid;
  grid-template-columns: minmax(220px, 32%) 1fr;
  gap: 24px;
  padding: 24px 24px 22px;
  align-items: start;
}

@media (max-width: 720px) {
  .mega-product-inner {
    grid-template-columns: 1fr;
  }
}

.mega-product-nav {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding-right: 12px;
  border-right: 1px solid var(--17-mega-border);
}

@media (max-width: 720px) {
  .mega-product-nav {
    border-right: none;
    padding-right: 0;
    border-bottom: 1px solid var(--17-mega-border);
    padding-bottom: 16px;
  }
}

.mega-product-item {
  padding: 18px 0;
  border-bottom: 1px solid var(--17-border-soft);
}

.mega-product-item:first-child {
  padding-top: 0;
}

.mega-product-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.mega-product-title {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 700;
  color: var(--17-ink);
  letter-spacing: -0.02em;
}

.mega-product-desc {
  margin: 0;
  font-size: 12px;
  line-height: 1.55;
  color: var(--17-muted);
}

.mega-product-stage {
  min-width: 0;
}

.product-showcase-card {
  position: relative;
  overflow: hidden;
  border-radius: 18px;
  border: 1px solid var(--17-mega-border);
  background: linear-gradient(135deg, #141d2e 0%, #1a2740 50%, #152238 100%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
  min-height: 200px;
}

.psc-top {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px 0;
}

.psc-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  font-size: 11px;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(145deg, #003d9e, #001a5c);
  border-radius: 8px;
  letter-spacing: -0.04em;
}

.psc-badge-label {
  font-size: 12px;
  font-weight: 700;
  color: var(--17-ink);
}

.psc-body {
  display: grid;
  grid-template-columns: minmax(0, 0.9fr) minmax(0, 1.35fr) minmax(0, 1fr);
  gap: 10px 12px;
  padding: 12px 16px 18px;
  align-items: end;
}

@media (max-width: 640px) {
  .psc-body {
    grid-template-columns: 1fr;
    align-items: start;
  }
}

.psc-figure {
  height: 120px;
  max-width: 100px;
  margin-top: 8px;
  border-radius: 40% 60% 55% 45% / 55% 45% 55% 45%;
  background: linear-gradient(180deg, #cfd9e6 0%, #a8b8cc 100%);
  opacity: 0.85;
  justify-self: start;
}

.psc-phones {
  position: relative;
  height: 148px;
  min-width: 0;
}

.psc-phone {
  position: absolute;
  width: 118px;
  border-radius: 14px;
  background: var(--17-mega-inner);
  border: 1px solid var(--17-mega-border);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.35);
  padding: 8px 10px 10px;
  box-sizing: border-box;
}

.psc-phone-head {
  font-size: 10px;
  font-weight: 700;
  color: var(--17-ink);
  margin-bottom: 6px;
}

.psc-phone-a {
  left: 0;
  bottom: 0;
  z-index: 1;
  transform: rotate(-4deg);
}

.psc-phone-b {
  left: 56px;
  bottom: 6px;
  z-index: 2;
  transform: rotate(3deg);
}

.psc-phone .psc-bar {
  height: 5px;
  border-radius: 3px;
  background: #2a3848;
  margin-bottom: 5px;
}

.psc-phone .psc-bar-short {
  width: 72%;
}

.psc-dots {
  display: flex;
  gap: 4px;
  margin-top: 8px;
}

.psc-dots span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #c5d3e3;
}

.psc-dots span:nth-child(2) {
  background: #1a8cff;
  box-shadow: 0 0 0 2px rgba(26, 140, 255, 0.2);
}

.psc-mini-card {
  height: 36px;
  border-radius: 8px;
  background: linear-gradient(90deg, #243044, #1a2838);
  margin-bottom: 8px;
}

.psc-cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 5px 10px;
  font-size: 9px;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(90deg, #5b3df5, #7c5cff);
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(91, 61, 245, 0.35);
  position: relative;
}

.psc-cta::after {
  content: '';
  position: absolute;
  right: -10px;
  top: 50%;
  width: 0;
  height: 0;
  margin-top: -5px;
  border: 5px solid transparent;
  border-left-color: #6a48f0;
  filter: drop-shadow(0 1px 1px rgba(0, 0, 0, 0.15));
}

.psc-chart-block {
  align-self: center;
  padding-bottom: 6px;
}

.psc-chart-title {
  margin: 0 0 10px;
  font-size: 12px;
  font-weight: 800;
  line-height: 1.35;
  color: var(--17-ink);
  letter-spacing: -0.02em;
}

.psc-bars {
  display: flex;
  align-items: flex-end;
  gap: 5px;
  height: 52px;
  margin-bottom: 6px;
}

.psc-bars span {
  width: 14px;
  border-radius: 4px 4px 2px 2px;
  background: linear-gradient(180deg, #7eb6ff, #3d8bff);
}

.psc-bars .b1 {
  height: 28%;
}

.psc-bars .b2 {
  height: 42%;
}

.psc-bars .b3 {
  height: 68%;
}

.psc-bars .b4 {
  height: 100%;
}

.psc-trend-arrow {
  width: 0;
  height: 0;
  margin-left: 4px;
  border-left: 22px solid transparent;
  border-bottom: 14px solid #1a8cff;
  filter: drop-shadow(0 2px 2px rgba(26, 140, 255, 0.25));
}

.mega-footnote--product {
  margin-top: 14px;
}

.mega-resource-inner {
  display: grid;
  grid-template-columns: minmax(220px, 34%) 1fr;
  gap: 28px;
  padding: 26px 26px 24px;
  align-items: start;
}

@media (max-width: 720px) {
  .mega-resource-inner {
    grid-template-columns: 1fr;
  }
}

.mega-resource-nav {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding-right: 12px;
  border-right: 1px solid var(--17-mega-border);
}

@media (max-width: 720px) {
  .mega-resource-nav {
    border-right: none;
    padding-right: 0;
    border-bottom: 1px solid var(--17-mega-border);
    padding-bottom: 12px;
  }
}

.mega-resource-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--17-border-soft);
}

.mega-resource-item:first-child {
  padding-top: 0;
}

.mega-resource-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.mega-resource-title {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 700;
  color: var(--17-ink);
  letter-spacing: -0.02em;
}

.mega-resource-desc {
  margin: 0;
  font-size: 12px;
  line-height: 1.55;
  color: var(--17-muted);
}

.mega-resource-stage {
  min-width: 0;
}

.resource-showcase-card {
  position: relative;
  overflow: hidden;
  border-radius: 18px;
  border: 1px solid var(--17-mega-border);
  background: linear-gradient(145deg, #141d2e 0%, #1a2740 50%, #152238 100%);
  min-height: 220px;
  padding: 18px 20px 20px;
  box-sizing: border-box;
}

.rs-pane {
  min-height: 182px;
}

.rs-pane--widget {
  padding: 0;
}

.rs-widget-grid {
  display: grid;
  grid-template-columns: 1fr minmax(140px, 34%);
  gap: 16px 20px;
  align-items: center;
}

@media (max-width: 640px) {
  .rs-widget-grid {
    grid-template-columns: 1fr;
  }
}

.rs-browser {
  border-radius: 12px;
  background: var(--17-mega-inner);
  border: 1px solid var(--17-mega-border);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.35);
  overflow: hidden;
}

.rs-browser-chrome {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 10px;
  background: #1e2a3d;
  border-bottom: 1px solid var(--17-mega-border);
}

.rs-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #d1d9e4;
}

.rs-dot:nth-child(1) {
  background: #ff6b6b;
}

.rs-dot:nth-child(2) {
  background: #ffd43b;
}

.rs-dot:nth-child(3) {
  background: #69db7c;
}

.rs-url {
  flex: 1;
  margin-left: 6px;
  padding: 4px 10px;
  font-size: 11px;
  color: var(--17-muted);
  background: var(--17-input-bg);
  border-radius: 6px;
  border: 1px solid var(--17-mega-border);
}

.rs-browser-canvas {
  position: relative;
  min-height: 150px;
  padding: 28px 16px 20px;
  background: linear-gradient(180deg, #121a28 0%, #0e1520 100%);
}

.rs-widget-pop {
  position: relative;
  z-index: 1;
  max-width: 220px;
  margin: 0 auto;
  padding: 12px 12px 14px;
  background: var(--17-card-elevated);
  border-radius: 12px;
  border: 1px solid var(--17-mega-border);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.35);
}

.rs-w-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 700;
  color: var(--17-ink);
  margin-bottom: 8px;
}

.rs-w-logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  font-size: 9px;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(145deg, #003d9e, #001a5c);
  border-radius: 6px;
}

.rs-w-label {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.rs-w-input {
  display: block;
  width: 100%;
  box-sizing: border-box;
  margin-bottom: 8px;
  padding: 7px 9px;
  font-size: 11px;
  border: 1px solid var(--17-mega-border);
  border-radius: 8px;
  background: var(--17-input-bg);
  color: var(--17-ink);
}

.rs-w-track {
  display: block;
  width: 100%;
  padding: 8px 10px;
  font-size: 11px;
  font-weight: 700;
  font-family: inherit;
  color: #fff;
  background: linear-gradient(180deg, #3d8dff, #256fea);
  border: none;
  border-radius: 8px;
  cursor: default;
}

.rs-w-sample {
  display: block;
  margin-top: 8px;
  font-size: 10px;
  color: var(--17-muted);
}

.rs-cursor {
  position: absolute;
  right: 18%;
  bottom: 22%;
  width: 22px;
  height: 22px;
  z-index: 2;
  background: linear-gradient(135deg, #7c5cff, #5b3df5);
  border-radius: 50% 50% 50% 0;
  transform: rotate(-25deg);
  box-shadow: 2px 3px 0 rgba(0, 0, 0, 0.12);
}

.rs-cursor::after {
  content: '';
  position: absolute;
  left: 5px;
  top: 5px;
  width: 8px;
  height: 8px;
  background: #fff;
  border-radius: 50%;
  opacity: 0.9;
}

.rs-widget-aside {
  padding: 4px 0;
}

.rs-aside-line {
  margin: 0;
  font-size: clamp(0.95rem, 1.8vw, 1.1rem);
  line-height: 1.4;
  font-weight: 700;
  color: var(--17-ink);
  letter-spacing: -0.02em;
}

.mega-footnote--resource {
  margin-top: 14px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 22px;
  margin-left: auto;
  flex-shrink: 0;
  overflow: visible;
}

.link-login {
  padding: 0;
  border: none;
  background: none;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  color: var(--17-link);
  text-decoration: none;
  cursor: pointer;
}

.link-login:hover {
  text-decoration: underline;
}

a.link-login.router-link-active {
  text-decoration: none;
}

.btn-register {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 22px;
  font-size: 14px;
  font-weight: 700;
  font-family: inherit;
  color: #fff;
  background: linear-gradient(180deg, #3d8dff, #256fea);
  border-radius: 8px;
  text-decoration: none;
  border: none;
  cursor: pointer;
  line-height: 1.25;
  transition: filter 0.15s;
}

.btn-register:hover {
  filter: brightness(1.08);
  text-decoration: none;
  color: #fff;
}

.user-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  font-weight: 600;
  color: var(--17-muted);
}

@media (min-width: 900px) {
  .nav {
    display: flex;
    margin-left: 0;
    flex-shrink: 0;
  }

  .nav-actions {
    margin-left: auto;
    padding-left: 8px;
  }
}
</style>
