<script setup>
import { computed } from 'vue'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'

const year = new Date().getFullYear()
const { uiLang } = useUiLang()
const { t } = useMultiDictionary(['site_footer_link', 'site_footer'], uiLang)

const privacy = computed(() => t('site_footer_link', 'privacy', '隐私'))
const terms = computed(() => t('site_footer_link', 'terms', '条款'))
const contact = computed(() => t('site_footer_link', 'contact', '联系我们'))
const copyrightDemo = computed(() => t('site_footer', 'copyright_demo', '演示界面'))
</script>

<template>
  <footer class="footer">
    <div class="footer-inner">
      <span>© {{ year }} Global Logistics · {{ copyrightDemo }}</span>
      <span class="footer-links">
        <a href="#">{{ privacy }}</a>
        <a href="#">{{ terms }}</a>
        <a href="#">{{ contact }}</a>
      </span>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  margin-top: auto;
  border-top: 1px solid var(--17-border);
  background: var(--17-card);
}

.footer-inner {
  max-width: 1120px;
  margin: 0 auto;
  padding: 16px 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  align-items: center;
  justify-content: space-between;
  color: var(--17-muted);
  font-size: 12px;
}

.footer-links {
  display: flex;
  gap: 14px;
}

.footer-links a {
  color: var(--17-muted);
  text-decoration: none;
  font-weight: 600;
}

.footer-links a:hover {
  color: var(--17-link-hover);
  text-decoration: none;
}
</style>
