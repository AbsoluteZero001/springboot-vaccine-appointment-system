import {createRouter, createWebHistory} from 'vue-router'
import {watch} from 'vue'
import {useAuthStore} from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/UserDashboardView.vue'),
        meta: {requiresAuth: true}
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/UserProfileView.vue'),
        meta: {requiresAuth: true}
    },
    {
        path: '/settings',
        name: 'settings',
        component: () => import('@/views/UserSettingsView.vue'),
        meta: {requiresAuth: true}
    },
      {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/AdminDashboardView.vue'),
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/vaccines',
      name: 'admin-vaccines',
      component: () => import('@/views/AdminVaccineView.vue'),
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('@/views/AdminUsersView.vue'),
      meta: { requiresAdmin: true }
    }
  ]
})

router.beforeEach(async (to, _from) => {
  const auth = useAuthStore()

    // Block navigation until session verified
  if (!auth.isAuthReady) {
    await new Promise((resolve) => {
      const stop = watch(() => auth.isAuthReady, (ready) => {
        if (ready) {
          stop()
          resolve()
        }
      })
    })
  }

    // Redirect authenticated users from home to appropriate dashboard
    if (to.name === 'home' && auth.isLoggedIn) {
        if (auth.isAdmin) return '/admin'
    return '/dashboard'
  }

    // Protect authenticated routes
    if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return '/'
  }
  if (to.meta.requiresAdmin && !auth.isAdmin) {
      return '/'
  }
  return true
})

export default router
