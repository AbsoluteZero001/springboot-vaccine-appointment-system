import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import type {ApiResponse} from '@/services/api'
import api from '@/services/api'

export interface User {
  id: number
  username: string
  email?: string
  phone?: string
  role?: string
  status?: number
}

export interface Admin {
  id: number
  username: string
  role?: string
}

export interface LoginData {
  error?: string
  frozen?: boolean
  freezeSeconds?: number
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const admin = ref<Admin | null>(null)
  const token = ref<string | null>(null)

  // Initialize from localStorage
  const storedUser = localStorage.getItem('user')
  const storedAdmin = localStorage.getItem('admin')
  const storedToken = localStorage.getItem('accessToken')

  if (storedUser) user.value = JSON.parse(storedUser)
  if (storedAdmin) admin.value = JSON.parse(storedAdmin)
  if (storedToken) token.value = storedToken

  const isLoggedIn = computed(() => user.value !== null || admin.value !== null)
  const isUser = computed(() => user.value !== null)
  const isAdmin = computed(() => admin.value !== null)
  const currentUser = computed(() => user.value)
  const currentAdmin = computed(() => admin.value)
  const authToken = computed(() => token.value)

  function setAuthToken(t: string) {
    token.value = t
    localStorage.setItem('accessToken', t)
  }

  function setUser(u: User) {
    user.value = u
    localStorage.setItem('user', JSON.stringify(u))
  }

  function setAdmin(a: Admin) {
    admin.value = a
    localStorage.setItem('admin', JSON.stringify(a))
  }

  function clearAuth() {
    user.value = null
    admin.value = null
    token.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('admin')
    localStorage.removeItem('accessToken')
  }

  function logout() {
    clearAuth()
    window.location.href = '/'
  }

  async function loginUser(username: string, password: string): Promise<LoginData> {
    try {
      const response = await api.post<ApiResponse<{ accessToken: string; user: User }>>('/users/login', {
        username,
        password
      })
      const data = response.data
      if (data.success !== false && data.accessToken) {
        setAuthToken(data.accessToken)
        setUser(data.user)
        return {}
      }
      return data as unknown as LoginData
    } catch (error: any) {
      if (error.response?.data) {
        return error.response.data as LoginData
      }
      return { error: '网络连接失败，请稍后重试' }
    }
  }

  async function loginAdmin(username: string, password: string): Promise<LoginData> {
    try {
      const response = await api.post<ApiResponse<{ accessToken: string; admin: Admin }>>('/admins/login', {
        username,
        password
      })
      const data = response.data
      if (data.success !== false && data.accessToken) {
        setAuthToken(data.accessToken)
        setAdmin(data.admin)
        return {}
      }
      return data as unknown as LoginData
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
    email: string
    phone?: string
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

  return {
    user,
    admin,
    token,
    isLoggedIn,
    isUser,
    isAdmin,
    currentUser,
    currentAdmin,
    authToken,
    setAuthToken,
    setUser,
    setAdmin,
    clearAuth,
    logout,
    loginUser,
    loginAdmin,
    registerUser
  }
})
