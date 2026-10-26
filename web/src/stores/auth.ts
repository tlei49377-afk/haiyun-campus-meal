import { defineStore } from 'pinia'
import { get } from '../utils/request'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    role: localStorage.getItem('role') || '',
    name: localStorage.getItem('name') || '',
    permissions: JSON.parse(localStorage.getItem('permissions') || '[]') as string[],
    permissionsLoadedAt: 0
  }),
  actions: {
    setSession(payload: { token: string; role: 'admin' | 'student'; name?: string; permissions?: string[] }) {
      this.token = payload.token
      this.role = payload.role
      this.name = payload.name || ''
      this.permissions = payload.permissions || []
      localStorage.setItem('token', this.token)
      localStorage.setItem('role', this.role)
      localStorage.setItem('name', this.name)
      localStorage.setItem('permissions', JSON.stringify(this.permissions))
      this.permissionsLoadedAt = Date.now()
    },
    async refreshPermissions() {
      if (this.role !== 'admin' || !this.token) return
      try {
        const permissions = await get<string[]>('/admin/permission/me')
        this.permissions = permissions || []
        this.permissionsLoadedAt = Date.now()
        localStorage.setItem('permissions', JSON.stringify(this.permissions))
      } catch {
        // 请求层已经处理错误；这里保留本地权限，避免页面被网络抖动打断
      }
    },
    hasPermission(permission?: string) {
      return !permission || this.permissions.includes(permission)
    },
    clear() {
      this.token = ''
      this.role = ''
      this.name = ''
      this.permissions = []
      this.permissionsLoadedAt = 0
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('name')
      localStorage.removeItem('permissions')
    }
  }
})
