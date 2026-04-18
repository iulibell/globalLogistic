<script setup>
defineProps({
  events: {
    type: Array,
    required: true,
  },
})
</script>

<template>
  <section class="timeline-card">
    <h3 class="timeline-title">物流轨迹</h3>
    <ol class="timeline">
      <li v-for="(ev, idx) in events" :key="idx" class="timeline-item">
        <div class="dot-wrap">
          <span class="dot" :class="{ active: idx === 0 }" />
          <span v-if="idx !== events.length - 1" class="rail" />
        </div>
        <div class="event">
          <div class="event-top">
            <span class="event-status">{{ ev.status }}</span>
            <time>{{ ev.time }}</time>
          </div>
          <div class="event-place">{{ ev.place }}</div>
          <p class="event-detail">{{ ev.detail }}</p>
        </div>
      </li>
    </ol>
  </section>
</template>

<style scoped>
.timeline-card {
  padding: 18px 20px 22px;
}

.timeline-title {
  margin: 0 0 14px;
  font-size: 15px;
}

.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
}

.timeline-item {
  display: grid;
  grid-template-columns: 22px 1fr;
  gap: 12px;
}

.dot-wrap {
  position: relative;
  width: 22px;
  display: flex;
  justify-content: center;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
  margin-top: 5px;
  background: #3d4d62;
  border: 2px solid var(--17-card);
  box-shadow: 0 0 0 2px #3d4d62;
}

.dot.active {
  background: var(--17-teal);
  box-shadow: 0 0 0 2px rgba(61, 141, 255, 0.35);
}

.rail {
  position: absolute;
  top: 19px;
  bottom: -10px;
  width: 2px;
  background: linear-gradient(180deg, #2a3848 0%, #1a2434 100%);
  border-radius: 999px;
}

.event {
  padding-bottom: 16px;
}

.event-top {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 12px;
  align-items: baseline;
  justify-content: space-between;
}

.event-status {
  font-weight: 700;
  font-size: 14px;
}

.event-top time {
  font-size: 12px;
  color: var(--17-muted);
  font-variant-numeric: tabular-nums;
}

.event-place {
  margin-top: 4px;
  font-size: 13px;
  font-weight: 600;
  color: var(--17-muted);
}

.event-detail {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--17-ink);
}
</style>
