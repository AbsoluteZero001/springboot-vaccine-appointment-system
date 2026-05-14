import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import api from '@/services/api'
import axios from 'axios'

export interface SysUser {
  id: number
  username: string
    phone: string
    role: string        // ROLE_USER or ROLE_ADMIN
  status?: number
    gender?: number     // 0=未知 1=男 2=女
    birthday?: string
    remark?: string
}

export interface LoginData {
  error?: string
  frozen?: boolean
  freezeSeconds?: number
}

export const useAuthStore = defineStore('auth', () => {
    const currentUser = ref<SysUser | null>(null)
  const token = ref<string | null>(null)
  const isAuthReady = ref(false)

    // Initialize from localStorage
  const storedUser = localStorage.getItem('user')
  const storedToken = localStorage.getItem('accessToken')
    if (storedUser) currentUser.value = JSON.parse(storedUser)
  if (storedToken) token.value = storedToken

    const isLoggedIn = computed(() => currentUser.value !== null)
    const isUser = computed(() => currentUser.value?.role === 'ROLE_USER')
    const isAdmin = computed(() => currentUser.value?.role === 'ROLE_ADMIN')
  const authToken = computed(() => token.value)
    const userRole = computed(() => currentUser.value?.role ?? null)

  function setAuthToken(t: string) {
    token.value = t
    localStorage.setItem('accessToken', t)
  }

    function setCurrentUser(u: SysUser) {
        currentUser.value = u
    localStorage.setItem('user', JSON.stringify(u))
  }

  function clearAuth() {
      currentUser.value = null
    token.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('accessToken')
  }

  function logout() {
    clearAuth()
    window.location.href = '/'
  }

    async function login(username: string, password: string): Promise<LoginData> {
    try {
        const response = await api.post<{
            code: number
            message: string
            data: { id: number; username: string; role: string; token: string }
        }>('/auth/login', {username, password})
        const body = response.data
        if (body.data && body.data.token) {
            setAuthToken(body.data.token)
            setCurrentUser({
                id: body.data.id,
                username: body.data.username,
                phone: '',
                role: body.data.role
            })
        return {}
      }
        return body as unknown as LoginData
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data as LoginData
      }
      return { error: '网络连接失败，请稍后重试' }
    }
  }

  async function registerUser(userData: {
    username: string
    password: string
      phone: string
      gender?: number
      birthday?: string
      remark?: string
  }): Promise<{ error?: string }> {
    try {
      await api.post('/users/register', userData)
      return {}
    } catch (error: any) {
      if (error.response?.data) {
        return { error: error.response.data.error || '注册失败' }
      }
      return { error: '网络连接失败，请稍后重试' }
    }
  }

  // Verify the stored token with the backend on app startup.
  async function verifySession() {
    const storedToken = localStorage.getItem('accessToken')
    if (!storedToken) {
      clearAuth()
      isAuthReady.value = true
      return
    }

    try {
        const response = await axios.get('/api/auth/verify', {
        headers: { Authorization: `Bearer ${storedToken}` }
      })
        // Update role from backend response
        if (currentUser.value && response.data.role) {
            currentUser.value.role = response.data.role
            localStorage.setItem('user', JSON.stringify(currentUser.value))
        }
    } catch {
      clearAuth()
    }
    isAuthReady.value = true
  }

  return {
      currentUser,
    token,
    isAuthReady,
    isLoggedIn,
    isUser,
    isAdmin,
      userRole,
    authToken,
    setAuthToken,
      setCurrentUser,
    clearAuth,
    logout,
      login,
    registerUser,
    verifySession
  }
})
