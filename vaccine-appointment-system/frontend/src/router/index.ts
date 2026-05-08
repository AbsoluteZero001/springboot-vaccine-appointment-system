import {createRouter, createWebHistory} from 'vue-router'

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

// Navigation guards
router.beforeEach((to, _from, next) => {
  const user = localStorage.getItem('user')
  const admin = localStorage.getItem('admin')

  if (to.meta.requiresUser && !user) {
    next('/')
  } else if (to.meta.requiresAdmin && !admin) {
    next('/')
  } else if (to.name === 'home' && user) {
    next('/dashboard')
  } else if (to.name === 'admin-login' && admin) {
    next('/admin')
  } else {
    next()
  }
})

export default router
