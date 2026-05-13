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
      path: '/admin-login',
      name: 'admin-login',
      component: () => import('@/views/AdminLoginView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/UserDashboardView.vue'),
      meta: { requiresUser: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/UserProfileView.vue'),
      meta: { requiresUser: true }
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

  // Block navigation until the session has been verified with the backend.
  // Without this, localStorage data from a previous browser session would
  // be trusted blindly, allowing unauthenticated access to protected views.
  if (!auth.isAuthReady) {
    await new Promise<void>((resolve) => {
      const stop = watch(() => auth.isAuthReady, (ready) => {
        if (ready) {
          stop()
          resolve()
        }
      })
    })
  }

  // Convenience: redirect already-authenticated users away from login pages
  if (to.name === 'home' && auth.isUser) {
    return '/dashboard'
  }
  if (to.name === 'admin-login' && auth.isAdmin) {
    return '/admin'
  }

  if (to.meta.requiresUser && !auth.isUser) {
    return '/'
  }
  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return '/admin-login'
  }
  return true
})

export default router
