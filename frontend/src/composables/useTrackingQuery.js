import { computed, ref } from 'vue'

export function useTrackingQuery() {
  const trackingInput = ref('')
  const queried = ref(false)
  const activeNumbers = ref([])

  function parseNumbers() {
    const raw = trackingInput.value || ''
    return raw
      .split(/[\s,，;；\n]+/)
      .map((s) => s.trim())
      .filter(Boolean)
  }

  const canQuery = computed(() => parseNumbers().length > 0)

  function runQuery() {
    const nums = parseNumbers()
    if (!nums.length) return
    activeNumbers.value = nums.slice(0, 5)
    queried.value = true
  }

  function resetQuery() {
    queried.value = false
    activeNumbers.value = []
  }

  function clearInput() {
    trackingInput.value = ''
  }

  return {
    trackingInput,
    queried,
    activeNumbers,
    canQuery,
    runQuery,
    resetQuery,
    clearInput,
  }
}
