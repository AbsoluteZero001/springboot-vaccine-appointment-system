import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import api from '@/services/api'
import axios from 'axios'

export interface SysUser {
  id: number
  username: string
    nickname: string
    phone: string
    role: string        // ROLE_USER or ROLE_ADMIN
  status?: number
    gender?: number     // 0=未知 1=男 2=女
    birthday?: string
    remark?: string
    avatarUrl?: string
    realName?: string
    idCard?: string
    isVerified?: number // 0=未实名 1=已实名
    lastUsernameChangeTime?: string
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
            data: {
                id: number;
                username: string;
                nickname: string;
                phone: string;
                role: string
                status: number;
                isVerified: number;
                realName: string;
                idCard: string
                avatarUrl: string;
                gender: number;
                birthday: string;
                remark: string;
                lastUsernameChangeTime: string;
                token: string
            }
        }>('/auth/login', {username, password})
        const body = response.data
        if (body.data && body.data.token) {
            setAuthToken(body.data.token)
            setCurrentUser({
                id: body.data.id,
                username: body.data.username,
                nickname: body.data.nickname || body.data.username,
                phone: body.data.phone || '',
                role: body.data.role,
                status: body.data.status,
                isVerified: body.data.isVerified || 0,
                realName: body.data.realName || '',
                idCard: body.data.idCard || '',
                avatarUrl: body.data.avatarUrl || '',
                gender: body.data.gender ?? 0,
                birthday: body.data.birthday || '',
                remark: body.data.remark || '',
                lastUsernameChangeTime: body.data.lastUsernameChangeTime || ''
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

    // Verify the stored token with the backend on app startup and sync user profile.
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
        // Sync full user profile from backend
        if (response.data.authenticated && response.data.id) {
            setCurrentUser({
                id: response.data.id,
                username: response.data.username,
                nickname: response.data.nickname || response.data.username,
                phone: response.data.phone || '',
                role: response.data.role,
                status: response.data.status,
                isVerified: response.data.isVerified || 0,
                realName: response.data.realName || '',
                idCard: response.data.idCard || '',
                avatarUrl: response.data.avatarUrl || '',
                gender: response.data.gender ?? 0,
                birthday: response.data.birthday || '',
                remark: response.data.remark || '',
                lastUsernameChangeTime: response.data.lastUsernameChangeTime || ''
            })
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
