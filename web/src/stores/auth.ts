import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    role: localStorage.getItem('role') || '',
    name: localStorage.getItem('name') || '',
    permissions: JSON.parse(localStorage.getItem('permissions') || '[]') as string[]
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
    },
    clear() {
      this.token = ''
      this.role = ''
      this.name = ''
      this.permissions = []
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('name')
      localStorage.removeItem('permissions')
    }
  }
})
