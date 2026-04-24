<script setup>
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useTrackingQuery } from '@/composables/useTrackingQuery.js'
import HomeFeaturesSection from '@/components/HomeFeaturesSection.vue'
import MarketingStats from '@/components/MarketingStats.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import SiteHeader from '@/components/SiteHeader.vue'
import TrackingHero from '@/components/TrackingHero.vue'
import TrackingResultPanel from '@/components/TrackingResultPanel.vue'

const {
  trackingInput,
  queried,
  activeNumbers,
  trackingTimeline,
  queryLoading,
  queryError,
  notFound,
  currentLogistic,
  canQuery,
  runQuery,
  resetQuery,
  clearInput,
} = useTrackingQuery()

const route = useRoute()

let autoQuerying = false
async function tryAutoQueryFromRoute() {
  const fromQuery = route.query.transportOrderId
  const transportOrderId = fromQuery == null ? '' : String(fromQuery).trim()
  if (!transportOrderId || autoQuerying) return
  if (activeNumbers.value[0] === transportOrderId && queried.value) return
  autoQuerying = true
  trackingInput.value = transportOrderId
  try {
    await runQuery()
  } finally {
    autoQuerying = false
  }
}

onMounted(() => {
  tryAutoQueryFromRoute()
})

watch(
  () => route.query.transportOrderId,
  () => {
    tryAutoQueryFromRoute()
  },
)
</script>

<template>
  <div class="page">
    <SiteHeader @home="resetQuery" />
    <TrackingHero
      v-if="!queried"
      v-model="trackingInput"
      :can-query="canQuery"
      @query="runQuery"
      @clear="clearInput"
    />
    <TrackingResultPanel
      v-if="queried"
      v-model:tracking-input="trackingInput"
      :numbers="activeNumbers"
      :timeline="trackingTimeline"
      :loading="queryLoading"
      :not-found="notFound"
      :query-error="queryError"
      :logistic="currentLogistic"
      :can-query="canQuery"
      @query="runQuery"
      @clear="clearInput"
      @reset="resetQuery"
    />
    <MarketingStats v-else />
    <HomeFeaturesSection v-if="!queried" />
    <SiteFooter />
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
