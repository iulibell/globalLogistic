import { nextTick } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ProfileLayout from '@/views/ProfileLayout.vue'
import { AUTH_PROFILE_KEY } from '@/constants/authStorage.js'
import { routeNavBegin, routeNavEnd } from '@/composables/useShellLoading.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/login', name: 'login', component: LoginView, meta: { title: '登录' } },
    { path: '/register', name: 'register', component: RegisterView, meta: { title: '注册' } },
    {
      path: '/profile',
      component: ProfileLayout,
      children: [
        {
          path: '',
          name: 'profile',
          component: () => import('@/views/ProfileInfoView.vue'),
        },
        {
          path: 'register-applications',
          name: 'profile-register-applications',
          component: () => import('@/views/RegisterApplicationsView.vue'),
        },
        {
          path: 'order-review',
          name: 'profile-order-review',
          component: () => import('@/views/OrderReviewView.vue'),
        },
        {
          path: 'manager/orders',
          name: 'profile-manager-order-list',
          component: () => import('@/views/ManagerOrderListView.vue'),
        },
        {
          path: 'manager/order-query',
          name: 'profile-manager-order-query',
          component: () => import('@/views/ManagerOrderQueryView.vue'),
        },
        {
          path: 'manager/manual-assign',
          name: 'profile-manager-manual-assign',
          component: () => import('@/views/ManagerManualAssignView.vue'),
        },
        {
          path: 'manager/lines',
          name: 'profile-manager-line-list',
          component: () => import('@/views/ManagerLineListView.vue'),
        },
        {
          path: 'manager/line-query',
          name: 'profile-manager-line-query',
          component: () => import('@/views/ManagerLineQueryView.vue'),
        },
        {
          path: 'manager/line-detail',
          name: 'profile-manager-line-detail',
          component: () => import('@/views/ManagerLineDetailView.vue'),
        },
        {
          path: 'keeper/inbound-list',
          name: 'profile-keeper-inbound-list',
          component: () => import('@/views/KeeperInboundListView.vue'),
        },
        {
          path: 'keeper/inbound-apply',
          name: 'profile-keeper-inbound-apply',
          component: () => import('@/views/KeeperInboundApplyListView.vue'),
        },
        {
          path: 'super/users',
          name: 'profile-super-users',
          component: () => import('@/views/SuperUserAdminView.vue'),
        },
        {
          path: 'super/by-type',
          name: 'profile-super-by-type',
          component: () => import('@/views/SuperUserAdminView.vue'),
        },
        {
          path: 'super/by-id',
          name: 'profile-super-by-id',
          component: () => import('@/views/SuperUserAdminView.vue'),
        },
      ],
    },
  ],
})

const SUPER_PROFILE_NAMES = new Set(['profile-super-users', 'profile-super-by-type', 'profile-super-by-id'])

const MANAGER_PROFILE_NAMES = new Set([
  'profile-manager-order-list',
  'profile-manager-order-query',
  'profile-manager-manual-assign',
  'profile-manager-line-list',
  'profile-manager-line-query',
  'profile-manager-line-detail',
])
const KEEPER_PROFILE_NAMES = new Set(['profile-keeper-inbound-list', 'profile-keeper-inbound-apply'])

router.beforeEach((to, from, next) => {
  if (SUPER_PROFILE_NAMES.has(to.name)) {
    try {
      const raw = sessionStorage.getItem(AUTH_PROFILE_KEY)
      const p = raw ? JSON.parse(raw) : null
      if (!p || p.role !== 'super') {
        next({ name: 'profile' })
        return
      }
    } catch {
      next({ name: 'profile' })
      return
    }
  }
  if (to.name === 'profile-register-applications' || to.name === 'profile-order-review') {
    try {
      const raw = sessionStorage.getItem(AUTH_PROFILE_KEY)
      const p = raw ? JSON.parse(raw) : null
      if (!p || p.role !== 'reviewer') {
        next({ name: 'profile' })
        return
      }
    } catch {
      next({ name: 'profile' })
      return
    }
  }
  if (MANAGER_PROFILE_NAMES.has(to.name)) {
    try {
      const raw = sessionStorage.getItem(AUTH_PROFILE_KEY)
      const p = raw ? JSON.parse(raw) : null
      if (!p || p.role !== 'manager') {
        next({ name: 'profile' })
        return
      }
    } catch {
      next({ name: 'profile' })
      return
    }
  }
  if (KEEPER_PROFILE_NAMES.has(to.name)) {
    try {
      const raw = sessionStorage.getItem(AUTH_PROFILE_KEY)
      const p = raw ? JSON.parse(raw) : null
      if (!p || p.role !== 'keeper') {
        next({ name: 'profile' })
        return
      }
    } catch {
      next({ name: 'profile' })
      return
    }
  }
  const isRealSwitch =
    from.matched.length > 0 && (to.path !== from.path || to.fullPath !== from.fullPath)
  if (isRealSwitch) {
    routeNavBegin()
  }
  next()
})

router.afterEach((to) => {
  document.title = to.meta?.title
    ? `全球物流查询 — ${to.meta.title} | Global Logistics`
    : '全球物流查询 | Global Logistics'
  nextTick(() => {
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        routeNavEnd()
      })
    })
  })
})

router.onError(() => {
  routeNavEnd()
})

export default router
