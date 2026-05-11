import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor: attach auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor: handle 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      localStorage.removeItem('admin')
      localStorage.removeItem('accessToken')
      window.location.href = '/'
    }
    return Promise.reject(error)
  }
)

export interface ApiResponse<T> {
  success: boolean
  message?: string
  data?: T
  timestamp?: string
  status?: number
  error?: string
  frozen?: boolean
  freezeSeconds?: number
  accessToken?: string
  user?: any
  admin?: any
}

// Loading state
export let isLoading = false
let loadingCount = 0

export function setLoading(loading: boolean) {
  if (loading) {
    loadingCount++
    isLoading = true
  } else {
    loadingCount--
    if (loadingCount <= 0) {
      loadingCount = 0
      isLoading = false
    }
  }
}

// Request loading interceptor
api.interceptors.request.use((config) => {
  if (config.method && config.method !== 'get') {
    setLoading(true)
  }
  return config
})

api.interceptors.response.use(
  (response) => {
    if (response.config.method && response.config.method !== 'get') {
      setTimeout(() => setLoading(false), 300)
    }
    return response
  },
  (error) => {
    setLoading(false)
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      localStorage.removeItem('admin')
      localStorage.removeItem('accessToken')
      window.location.href = '/'
    }
    return Promise.reject(error)
  }
)

export default api
