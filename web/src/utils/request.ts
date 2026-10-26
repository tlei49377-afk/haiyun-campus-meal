import axios from 'axios'

export interface ApiResponse<T> {
  code: number
  msg?: string
  data: T
}

const http = axios.create({ baseURL: '/api', timeout: 12000 })

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  if (token) {
    config.headers[role === 'student' ? 'authentication' : 'token'] = token
  }
  config.headers['X-Trace-Id'] = `web-${crypto.randomUUID().slice(0, 12)}`
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse<unknown>
    if (body && typeof body.code === 'number' && body.code !== 1) {
      return Promise.reject(new Error(body.msg || '请求失败'))
    }
    return response
  },
  (error) => {
    const message = error.response?.data?.msg || error.message || '网络异常'
    if (error.response?.status === 401) {
      localStorage.clear()
      window.location.href = '/login'
    }
    return Promise.reject(new Error(message))
  }
)

export async function get<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  const response = await http.get<ApiResponse<T>>(url, { params })
  return response.data.data
}

export async function post<T>(url: string, data?: unknown): Promise<T> {
  const response = await http.post<ApiResponse<T>>(url, data)
  return response.data.data
}

export async function put<T>(url: string, data?: unknown): Promise<T> {
  const response = await http.put<ApiResponse<T>>(url, data)
  return response.data.data
}

export async function del<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  const response = await http.delete<ApiResponse<T>>(url, { params })
  return response.data.data
}
