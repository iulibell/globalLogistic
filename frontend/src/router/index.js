import { nextTick } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import { routeNavBegin, routeNavEnd } from '@/composables/useShellLoading.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/login', name: 'login', component: LoginView, meta: { title: '登录' } },
    { path: '/register', name: 'register', component: RegisterView, meta: { title: '注册' } },
  ],
})

router.beforeEach((to, from, next) => {
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
