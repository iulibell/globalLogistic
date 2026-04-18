<script setup>
import { RouterView } from 'vue-router'
import { useShellLoading } from '@/composables/useShellLoading.js'

const { shellLoading } = useShellLoading()
</script>

<template>
  <div class="app-root">
    <RouterView />
    <Transition name="shell-fade">
      <div
        v-if="shellLoading"
        class="shell-overlay"
        role="status"
        aria-live="polite"
        aria-busy="true"
      >
        <div class="shell-spinner" aria-hidden="true" />
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.app-root {
  position: relative;
  min-height: 100vh;
}

.shell-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(6, 10, 16, 0.5);
  backdrop-filter: blur(3px);
}

.shell-spinner {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.12);
  border-top-color: var(--17-teal, #3d8dff);
  animation: shell-spin 0.72s linear infinite;
}

@keyframes shell-spin {
  to {
    transform: rotate(360deg);
  }
}

.shell-fade-enter-active,
.shell-fade-leave-active {
  transition: opacity 0.18s ease;
}

.shell-fade-enter-from,
.shell-fade-leave-to {
  opacity: 0;
}
</style>
