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
