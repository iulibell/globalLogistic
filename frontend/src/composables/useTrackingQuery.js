import { computed, ref } from 'vue'
import { fetchLogisticByTransportOrderId } from '@/api/logistic.js'

export function useTrackingQuery() {
  const trackingInput = ref('')
  const queried = ref(false)
  const activeNumbers = ref([])
  const trackingTimeline = ref([])
  const queryLoading = ref(false)
  const queryError = ref('')
  const notFound = ref(false)
  const currentLogistic = ref(null)

  function parseNumbers() {
    const raw = trackingInput.value || ''
    return raw
      .split(/[\s,，;；\n]+/)
      .map((s) => s.trim())
      .filter(Boolean)
  }

  const canQuery = computed(() => parseNumbers().length > 0)

  async function runQuery() {
    const nums = parseNumbers()
    if (!nums.length) return
    const picked = nums.slice(0, 5)
    activeNumbers.value = picked
    queried.value = true
    queryLoading.value = true
    queryError.value = ''
    notFound.value = false
    trackingTimeline.value = []
    currentLogistic.value = null
    try {
      const transportOrderId = picked[0]
      const res = await fetchLogisticByTransportOrderId(transportOrderId)
      if (res.code !== 200) {
        throw new Error(res.message && String(res.message).trim() ? String(res.message) : 'generic_error')
      }
      if (!Array.isArray(res.data) || !res.data.length) {
        notFound.value = true
        queryError.value = '该单号不存在'
        return
      }
      const list = res.data
      const latest = list[list.length - 1]
      currentLogistic.value = latest
      trackingTimeline.value = list.map((node) => ({
        time: node.updateTime || node.createTime || '-',
        place: node.city || '-',
        detail: String(transportOrderId),
        status: node.status,
      }))
    } catch (e) {
      notFound.value = true
      queryError.value = e instanceof Error ? e.message : '查询失败'
      currentLogistic.value = null
    } finally {
      queryLoading.value = false
    }
  }

  function resetQuery() {
    queried.value = false
    activeNumbers.value = []
    trackingTimeline.value = []
    queryLoading.value = false
    queryError.value = ''
    notFound.value = false
    currentLogistic.value = null
  }

  function clearInput() {
    trackingInput.value = ''
  }

  return {
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
  }
}
