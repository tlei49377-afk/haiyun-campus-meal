import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { pinia } from '../stores'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/student/home' },
    { path: '/login', component: () => import('../views/Login.vue'), meta: { guest: true } },
    {
      path: '/admin',
      component: () => import('../layouts/AdminLayout.vue'),
      meta: { role: 'admin' },
      children: [
        { path: '', redirect: '/admin/dashboard' },
        { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue') },
        { path: 'canteens', component: () => import('../views/admin/CanteenList.vue'), meta: { permission: 'canteen:query' } },
        { path: 'stalls', component: () => import('../views/admin/StallList.vue'), meta: { permission: 'stall:query' } },
        { path: 'slots', component: () => import('../views/admin/SlotList.vue'), meta: { permission: 'slot:query' } },
        { path: 'categories', component: () => import('../views/admin/CategoryList.vue'), meta: { permission: 'category:query' } },
        { path: 'dishes', component: () => import('../views/admin/DishList.vue'), meta: { permission: 'dish:query' } },
        { path: 'combos', component: () => import('../views/admin/ComboList.vue'), meta: { permission: 'setmeal:query' } },
        { path: 'staff', component: () => import('../views/admin/StaffRoles.vue'), meta: { permission: 'employee:query' } },
        { path: 'reports', component: () => import('../views/admin/Reports.vue'), meta: { permission: 'report:query' } },
        { path: 'orders', component: () => import('../views/admin/OrderList.vue'), meta: { permission: 'order:query' } },
        { path: 'verify', component: () => import('../views/admin/PickupVerify.vue'), meta: { permission: 'order:handle' } },
        { path: 'ai', component: () => import('../views/admin/AiAssistant.vue') },
        { path: 'forbidden', component: () => import('../views/Forbidden.vue') }
      ]
    },
    {
      path: '/student',
      component: () => import('../layouts/MobileLayout.vue'),
      meta: { role: 'student' },
      children: [
        { path: '', redirect: '/student/home' },
        { path: 'home', component: () => import('../views/h5/Home.vue') },
        { path: 'menu/:stallId', component: () => import('../views/h5/Menu.vue') },
        { path: 'dish/:dishId', component: () => import('../views/h5/DishDetail.vue') },
        { path: 'cart', component: () => import('../views/h5/Cart.vue') },
        { path: 'orders', component: () => import('../views/h5/Orders.vue') },
        { path: 'pickup/:id', component: () => import('../views/h5/PickupCode.vue') }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/login' }
  ]
})

router.beforeEach(async (to) => {
  const auth = useAuthStore(pinia)
  if (to.meta.guest) {
    if (auth.token && auth.role) return auth.role === 'admin' ? '/admin/dashboard' : '/student/home'
    return true
  }

  if (!auth.token || !auth.role) return '/login'
  const requiredRole = to.meta.role as string | undefined
  if (requiredRole && auth.role !== requiredRole) {
    return auth.role === 'admin' ? '/admin/dashboard' : '/student/home'
  }

  if (auth.role === 'admin') {
    await auth.refreshPermissions()
    const requiredPermission = to.meta.permission as string | undefined
    if (!auth.hasPermission(requiredPermission)) return '/admin/forbidden'
  }
  return true
})

export default router
