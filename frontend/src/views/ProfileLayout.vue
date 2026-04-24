<script setup>
import { computed, onMounted, watch } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import { useAuthSession } from '@/composables/useAuthSession.js'
import { useUiLang } from '@/composables/useUiLang.js'
import { useMultiDictionary } from '@/composables/useMultiDictionary.js'
import { useSortedDictionaryOptions } from '@/composables/useSortedDictionaryOptions.js'
import { pageDictFallback } from '@/utils/pageDictionaryFallback.js'

const FALLBACK_ROLES = [
  { value: 'super', label: '超级管理员' },
  { value: 'manager', label: '管理员' },
  { value: 'keeper', label: '仓管' },
  { value: 'driver', label: '司机' },
  { value: 'reviewer', label: '审核员' },
]

const route = useRoute()
const { isLoggedIn, profile } = useAuthSession()
const { uiLang } = useUiLang()
const { t, maps } = useMultiDictionary(['page_profile'], uiLang)
const { options: roleOptions } = useSortedDictionaryOptions('login_role_option', uiLang, FALLBACK_ROLES)

const title = computed(() => t('page_profile', 'title', '个人中心'))
const sidebarEyebrow = computed(() => t('page_profile', 'sidebar_eyebrow', '用户中心'))
const navProfile = computed(() => t('page_profile', 'nav_profile', '个人信息'))
const navRegisterApplication = computed(() =>
  t('page_profile', 'nav_register_application', pageDictFallback('page_profile', 'nav_register_application', uiLang.value)),
)

const navOrderReview = computed(() =>
  t(
    'page_profile',
    'nav_order_review',
    pageDictFallback('page_profile', 'nav_order_review', uiLang.value) || '商品审核',
  ),
)

const navManagerOrderList = computed(() =>
  t('page_profile', 'nav_manager_order_list', pageDictFallback('page_profile', 'nav_manager_order_list', uiLang.value)),
)
const navManagerOrderQuery = computed(() =>
  t('page_profile', 'nav_manager_order_query', pageDictFallback('page_profile', 'nav_manager_order_query', uiLang.value)),
)
const navManagerManualAssign = computed(() =>
  t(
    'page_profile',
    'nav_manager_manual_assign',
    pageDictFallback('page_profile', 'nav_manager_manual_assign', uiLang.value) || '手动派单',
  ),
)
const navManagerLineList = computed(() =>
  t(
    'page_profile',
    'nav_manager_line_list',
    pageDictFallback('page_profile', 'nav_manager_line_list', uiLang.value) || '路线列表',
  ),
)
const navManagerLineQuery = computed(() =>
  t(
    'page_profile',
    'nav_manager_line_query',
    pageDictFallback('page_profile', 'nav_manager_line_query', uiLang.value) || '查询路线',
  ),
)
const navKeeperInboundList = computed(() =>
  t('page_profile', 'nav_keeper_inbound_list', pageDictFallback('page_profile', 'nav_keeper_inbound_list', uiLang.value)),
)
const navKeeperInboundApply = computed(() =>
  t('page_profile', 'nav_keeper_inbound_apply', pageDictFallback('page_profile', 'nav_keeper_inbound_apply', uiLang.value)),
)
const navKeeperInboundQuery = computed(() =>
  t('page_profile', 'nav_keeper_inbound_query', pageDictFallback('page_profile', 'nav_keeper_inbound_query', uiLang.value)),
)
const navKeeperOutboundList = computed(() =>
  t('page_profile', 'nav_keeper_outbound_list', pageDictFallback('page_profile', 'nav_keeper_outbound_list', uiLang.value) || '出库列表'),
)
const navKeeperOutboundQuery = computed(() =>
  t('page_profile', 'nav_keeper_outbound_query', pageDictFallback('page_profile', 'nav_keeper_outbound_query', uiLang.value) || '出库查询'),
)
const navKeeperStockList = computed(() =>
  t('page_profile', 'nav_keeper_stock_list', pageDictFallback('page_profile', 'nav_keeper_stock_list', uiLang.value) || '库存列表'),
)
const navKeeperStockQuery = computed(() =>
  t('page_profile', 'nav_keeper_stock_query', pageDictFallback('page_profile', 'nav_keeper_stock_query', uiLang.value) || '查询库存'),
)
const navDriverTransportOrders = computed(() =>
  t('page_profile', 'nav_driver_transport_orders', pageDictFallback('page_profile', 'nav_driver_transport_orders', uiLang.value) || '运输派单'),
)
const navDriverCurrentAssignment = computed(() =>
  t(
    'page_profile',
    'nav_driver_current_assignment',
    pageDictFallback('page_profile', 'nav_driver_current_assignment', uiLang.value) || '当前派单',
  ),
)

const navSuperUsers = computed(() =>
  t('page_profile', 'nav_super_users', pageDictFallback('page_profile', 'nav_super_users', uiLang.value)),
)
const navSuperByType = computed(() =>
  t('page_profile', 'nav_super_by_type', pageDictFallback('page_profile', 'nav_super_by_type', uiLang.value)),
)
const navSuperById = computed(() =>
  t('page_profile', 'nav_super_by_id', pageDictFallback('page_profile', 'nav_super_by_id', uiLang.value)),
)
const navSuperDictionary = computed(() =>
  t('page_profile', 'nav_super_dictionary', pageDictFallback('page_profile', 'nav_super_dictionary', uiLang.value) || '字典列表'),
)

const ariaSidebarNav = computed(() =>
  t('page_profile', 'aria_sidebar_nav', pageDictFallback('page_profile', 'aria_sidebar_nav', uiLang.value)),
)

const isReviewer = computed(() => profile.value?.role === 'reviewer')
const isSuper = computed(() => profile.value?.role === 'super')
const isManager = computed(() => profile.value?.role === 'manager')
const isKeeper = computed(() => profile.value?.role === 'keeper')
const isDriver = computed(() => profile.value?.role === 'driver')

const valueEmpty = computed(() =>
  t('page_profile', 'value_empty', pageDictFallback('page_profile', 'value_empty', uiLang.value)),
)

const displayName = computed(() => {
  const p = profile.value
  if (!p) return ''
  const nick = p.nickname != null && String(p.nickname).trim() ? String(p.nickname).trim() : ''
  if (nick) return nick
  return p.username != null && String(p.username).trim() ? String(p.username).trim() : ''
})

const userRoleLabel = computed(() => {
  const r = profile.value?.role
  if (!r) return ''
  return roleOptions.value.find((o) => o.value === r)?.label || String(r)
})

const sidebarWelcome = computed(() => {
  if (!isLoggedIn.value) {
    return title.value
  }
  const name = displayName.value || valueEmpty.value
  const role = userRoleLabel.value
  const rolePart = role ? ` ${role}` : ''
  const tpl = t(
    'page_profile',
    'sidebar_welcome',
    pageDictFallback('page_profile', 'sidebar_welcome', uiLang.value),
  )
  return tpl.replace('{name}', name).replace('{role}', rolePart)
})

function applyProfileDocumentTitle() {
  if (!route.path.startsWith('/profile')) return
  let piece
  if (route.name === 'profile-register-applications') {
    piece = t(
      'page_profile',
      'doc_title_register_apps',
      pageDictFallback('page_profile', 'doc_title_register_apps', uiLang.value),
    )
  } else if (route.name === 'profile-order-review') {
    piece = t(
      'page_profile',
      'doc_title_order_review',
      pageDictFallback('page_profile', 'doc_title_order_review', uiLang.value),
    )
  } else if (route.name === 'profile-manager-order-list') {
    piece = t(
      'page_profile',
      'doc_title_manager_order_list',
      pageDictFallback('page_profile', 'doc_title_manager_order_list', uiLang.value),
    )
  } else if (route.name === 'profile-manager-order-query') {
    piece = t(
      'page_profile',
      'doc_title_manager_order_query',
      pageDictFallback('page_profile', 'doc_title_manager_order_query', uiLang.value),
    )
  } else if (route.name === 'profile-manager-manual-assign') {
    piece = t(
      'page_profile',
      'doc_title_manager_manual_assign',
      pageDictFallback('page_profile', 'doc_title_manager_manual_assign', uiLang.value),
    )
  } else if (route.name === 'profile-manager-line-list') {
    piece = t(
      'page_profile',
      'doc_title_manager_line_list',
      pageDictFallback('page_profile', 'doc_title_manager_line_list', uiLang.value),
    )
  } else if (route.name === 'profile-manager-line-query') {
    piece = t(
      'page_profile',
      'doc_title_manager_line_query',
      pageDictFallback('page_profile', 'doc_title_manager_line_query', uiLang.value),
    )
  } else if (route.name === 'profile-manager-line-detail') {
    piece = t(
      'page_profile',
      'doc_title_manager_line_detail',
      pageDictFallback('page_profile', 'doc_title_manager_line_detail', uiLang.value),
    )
  } else if (route.name === 'profile-keeper-inbound-list') {
    piece = t(
      'page_profile',
      'doc_title_keeper_inbound_list',
      pageDictFallback('page_profile', 'doc_title_keeper_inbound_list', uiLang.value),
    )
  } else if (route.name === 'profile-keeper-inbound-apply') {
    piece = t(
      'page_profile',
      'doc_title_keeper_inbound_apply',
      pageDictFallback('page_profile', 'doc_title_keeper_inbound_apply', uiLang.value),
    )
  } else if (route.name === 'profile-keeper-inbound-apply-query') {
    piece = t(
      'page_profile',
      'doc_title_keeper_inbound_query',
      pageDictFallback('page_profile', 'doc_title_keeper_inbound_query', uiLang.value),
    )
  } else if (route.name === 'profile-keeper-outbound-list') {
    piece = t(
      'page_profile',
      'doc_title_keeper_outbound_list',
      pageDictFallback('page_profile', 'doc_title_keeper_outbound_list', uiLang.value) || '出库列表',
    )
  } else if (route.name === 'profile-keeper-outbound-query') {
    piece = t(
      'page_profile',
      'doc_title_keeper_outbound_query',
      pageDictFallback('page_profile', 'doc_title_keeper_outbound_query', uiLang.value) || '出库查询',
    )
  } else if (route.name === 'profile-keeper-stock-list') {
    piece = t(
      'page_profile',
      'doc_title_keeper_stock_list',
      pageDictFallback('page_profile', 'doc_title_keeper_stock_list', uiLang.value) || '库存列表',
    )
  } else if (route.name === 'profile-keeper-stock-query') {
    piece = t(
      'page_profile',
      'doc_title_keeper_stock_query',
      pageDictFallback('page_profile', 'doc_title_keeper_stock_query', uiLang.value) || '查询库存',
    )
  } else if (route.name === 'profile-driver-transport-orders') {
    piece = t(
      'page_profile',
      'doc_title_driver_transport_orders',
      pageDictFallback('page_profile', 'doc_title_driver_transport_orders', uiLang.value) || '运输派单',
    )
  } else if (route.name === 'profile-driver-current-assignment') {
    piece = t(
      'page_profile',
      'doc_title_driver_current_assignment',
      pageDictFallback('page_profile', 'doc_title_driver_current_assignment', uiLang.value) || '当前派单',
    )
  } else if (route.name === 'profile-super-users') {
    piece = t(
      'page_profile',
      'doc_title_super_users',
      pageDictFallback('page_profile', 'doc_title_super_users', uiLang.value),
    )
  } else if (route.name === 'profile-super-by-type') {
    piece = t(
      'page_profile',
      'doc_title_super_by_type',
      pageDictFallback('page_profile', 'doc_title_super_by_type', uiLang.value),
    )
  } else if (route.name === 'profile-super-by-id') {
    piece = t(
      'page_profile',
      'doc_title_super_by_id',
      pageDictFallback('page_profile', 'doc_title_super_by_id', uiLang.value),
    )
  } else if (route.name === 'profile-super-dictionaries') {
    piece = t(
      'page_profile',
      'doc_title_super_dictionary',
      pageDictFallback('page_profile', 'doc_title_super_dictionary', uiLang.value) || '字典列表',
    )
  } else {
    piece = t('page_profile', 'doc_title', pageDictFallback('page_profile', 'doc_title', uiLang.value))
  }
  document.title = `全球物流查询 — ${piece} | Global Logistics`
}

watch(
  () => [maps.value.page_profile, uiLang.value, route.name],
  () => applyProfileDocumentTitle(),
  { deep: true },
)

onMounted(() => applyProfileDocumentTitle())
</script>

<template>
  <div class="page">
    <SiteHeader />
    <div class="profile-frame">
      <aside class="sidebar" :aria-label="ariaSidebarNav">
        <div class="sidebar-head">
          <p class="sidebar-eyebrow">{{ sidebarEyebrow }}</p>
          <p class="sidebar-title">{{ sidebarWelcome }}</p>
        </div>
        <nav class="sidebar-nav">
          <RouterLink
            :to="{ name: 'profile' }"
            class="sidebar-link"
            active-class=""
            exact-active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navProfile }}</span>
          </RouterLink>
          <RouterLink
            v-if="isSuper"
            :to="{ name: 'profile-super-users' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navSuperUsers }}</span>
          </RouterLink>
          <RouterLink
            v-if="isSuper"
            :to="{ name: 'profile-super-by-type' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navSuperByType }}</span>
          </RouterLink>
          <RouterLink
            v-if="isSuper"
            :to="{ name: 'profile-super-by-id' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navSuperById }}</span>
          </RouterLink>
          <RouterLink
            v-if="isSuper"
            :to="{ name: 'profile-super-dictionaries' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navSuperDictionary }}</span>
          </RouterLink>
          <RouterLink
            v-if="isReviewer"
            :to="{ name: 'profile-order-review' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navOrderReview }}</span>
          </RouterLink>
          <RouterLink
            v-if="isReviewer"
            :to="{ name: 'profile-register-applications' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navRegisterApplication }}</span>
          </RouterLink>
          <RouterLink
            v-if="isManager"
            :to="{ name: 'profile-manager-order-list' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navManagerOrderList }}</span>
          </RouterLink>
          <RouterLink
            v-if="isManager"
            :to="{ name: 'profile-manager-order-query' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navManagerOrderQuery }}</span>
          </RouterLink>
          <RouterLink
            v-if="isManager"
            :to="{ name: 'profile-manager-line-list' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navManagerLineList }}</span>
          </RouterLink>
          <RouterLink
            v-if="isManager"
            :to="{ name: 'profile-manager-line-query' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navManagerLineQuery }}</span>
          </RouterLink>
          <RouterLink
            v-if="isKeeper"
            :to="{ name: 'profile-keeper-inbound-list' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navKeeperInboundList }}</span>
          </RouterLink>
          <RouterLink
            v-if="isKeeper"
            :to="{ name: 'profile-keeper-inbound-apply' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navKeeperInboundApply }}</span>
          </RouterLink>
          <RouterLink
            v-if="isKeeper"
            :to="{ name: 'profile-keeper-inbound-apply-query' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navKeeperInboundQuery }}</span>
          </RouterLink>
          <RouterLink
            v-if="isKeeper"
            :to="{ name: 'profile-keeper-outbound-list' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navKeeperOutboundList }}</span>
          </RouterLink>
          <RouterLink
            v-if="isKeeper"
            :to="{ name: 'profile-keeper-outbound-query' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navKeeperOutboundQuery }}</span>
          </RouterLink>
          <RouterLink
            v-if="isKeeper"
            :to="{ name: 'profile-keeper-stock-list' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navKeeperStockList }}</span>
          </RouterLink>
          <RouterLink
            v-if="isKeeper"
            :to="{ name: 'profile-keeper-stock-query' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navKeeperStockQuery }}</span>
          </RouterLink>
          <RouterLink
            v-if="isDriver"
            :to="{ name: 'profile-driver-transport-orders' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navDriverTransportOrders }}</span>
          </RouterLink>
          <RouterLink
            v-if="isDriver"
            :to="{ name: 'profile-driver-current-assignment' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navDriverCurrentAssignment }}</span>
          </RouterLink>
          <RouterLink
            v-if="isManager"
            :to="{ name: 'profile-manager-manual-assign' }"
            class="sidebar-link"
            active-class="sidebar-link--active"
          >
            <span class="sidebar-link-glow" aria-hidden="true" />
            <span class="sidebar-link-text">{{ navManagerManualAssign }}</span>
          </RouterLink>
        </nav>
      </aside>

      <main class="main">
        <RouterView />
      </main>
    </div>
    <SiteFooter />
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--17-bg);
}

.profile-frame {
  flex: 1;
  display: flex;
  align-items: stretch;
  min-height: 0;
  width: 100%;
  background: linear-gradient(165deg, #03060d 0%, #060b14 42%, #04070e 100%);
}

.sidebar {
  width: 260px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  padding: 28px 0 32px;
  background: linear-gradient(180deg, #02050a 0%, #0a1018 55%, #060a12 100%);
  border-right: 1px solid rgba(61, 141, 255, 0.14);
  box-shadow: inset -1px 0 0 rgba(0, 0, 0, 0.35);
}

.sidebar-head {
  padding: 0 22px 22px;
  border-bottom: 1px solid rgba(36, 48, 68, 0.85);
}

.sidebar-eyebrow {
  margin: 0 0 6px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(126, 184, 255, 0.55);
}

.sidebar-title {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  line-height: 1.35;
  color: #e8eef6;
  letter-spacing: -0.02em;
  word-break: break-word;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 12px 0;
}

.sidebar-link {
  position: relative;
  display: flex;
  align-items: center;
  padding: 12px 14px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #9eb0c8;
  text-decoration: none;
  transition:
    color 0.15s,
    background 0.15s;
}

.sidebar-link:hover {
  color: #d4e5ff;
  background: rgba(61, 141, 255, 0.06);
  text-decoration: none;
}

.sidebar-link--active {
  color: #f0f6ff;
  background: rgba(61, 141, 255, 0.12);
}

.sidebar-link--active .sidebar-link-glow {
  opacity: 1;
}

.sidebar-link-glow {
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: linear-gradient(180deg, #5aa3ff, #256fea);
  opacity: 0;
  transition: opacity 0.15s;
}

.sidebar-link-text {
  position: relative;
  z-index: 1;
}

.main {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 32px clamp(16px, 3vw, 40px) 48px;
  box-sizing: border-box;
}

@media (max-width: 768px) {
  .profile-frame {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    flex-direction: row;
    align-items: center;
    padding: 16px 16px 12px;
    border-right: none;
    border-bottom: 1px solid rgba(61, 141, 255, 0.14);
    box-shadow: none;
  }

  .sidebar-head {
    padding: 0 12px 0 0;
    border-bottom: none;
    flex: 1;
    min-width: 0;
  }

  .sidebar-nav {
    flex-direction: row;
    padding: 0;
    gap: 8px;
    overflow-x: auto;
    flex-shrink: 0;
  }

  .sidebar-link {
    white-space: nowrap;
  }

  .main {
    padding: 20px 16px 32px;
  }
}
</style>
