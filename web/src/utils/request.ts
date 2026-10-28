import axios, { AxiosError, AxiosHeaders } from 'axios'
import { beginRequest, endRequest, rememberTraceId, reportError } from './feedback'

export interface ApiResponse<T> {
  code: number
  msg?: string
  data: T
}

export class ApiError extends Error {
  traceId?: string

  constructor(message: string, traceId?: string) {
    super(message)
    this.name = 'ApiError'
    this.traceId = traceId
  }
}

const http = axios.create({ baseURL: '/api', timeout: 12000 })

function readTraceId(headers: unknown): string {
  if (!headers) return ''
  if (headers instanceof AxiosHeaders) return String(headers.get('x-trace-id') || '')
  const map = headers as Record<string, unknown>
  return String(map['x-trace-id'] || map['X-Trace-Id'] || '')
}

http.interceptors.request.use((config) => {
  beginRequest()
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  if (token) config.headers[role === 'student' ? 'authentication' : 'token'] = token
  config.headers['X-Trace-Id'] = `web-${crypto.randomUUID().slice(0, 12)}`
  return config
}, (error) => {
  endRequest()
  return Promise.reject(error)
})

http.interceptors.response.use(
  (response) => {
    endRequest()
    const traceId = readTraceId(response.headers)
    rememberTraceId(traceId)
    const body = response.data as ApiResponse<unknown>
    if (body && typeof body.code === 'number' && body.code !== 1) {
      const message = body.msg || '请求失败'
      reportError(message, traceId)
      return Promise.reject(new ApiError(message, traceId))
    }
    return response
  },
  (error: AxiosError) => {
    endRequest()
    const traceId = readTraceId(error.response?.headers)
    const message = (error.response?.data as { msg?: string } | undefined)?.msg
      || error.message
      || '网络异常，请稍后重试'
    if (error.response?.status === 401) {
      localStorage.clear()
      reportError('登录状态已失效，请重新登录', traceId)
      window.location.href = '/login'
    } else {
      reportError(message, traceId)
    }
    return Promise.reject(new ApiError(message, traceId))
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
