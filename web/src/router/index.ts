import { createRouter, createWebHistory } from 'vue-router'

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
        { path: 'canteens', component: () => import('../views/admin/CanteenList.vue') },
        { path: 'stalls', component: () => import('../views/admin/StallList.vue') },
        { path: 'slots', component: () => import('../views/admin/SlotList.vue') },
        { path: 'categories', component: () => import('../views/admin/CategoryList.vue') },
        { path: 'dishes', component: () => import('../views/admin/DishList.vue') },
        { path: 'combos', component: () => import('../views/admin/ComboList.vue') },
        { path: 'staff', component: () => import('../views/admin/StaffRoles.vue') },
        { path: 'reports', component: () => import('../views/admin/Reports.vue') },
        { path: 'orders', component: () => import('../views/admin/OrderList.vue') },
        { path: 'verify', component: () => import('../views/admin/PickupVerify.vue') },
        { path: 'ai', component: () => import('../views/admin/AiAssistant.vue') }
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
        { path: 'cart', component: () => import('../views/h5/Cart.vue') },
        { path: 'orders', component: () => import('../views/h5/Orders.vue') },
        { path: 'pickup/:id', component: () => import('../views/h5/PickupCode.vue') }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const role = localStorage.getItem('role')
  if (to.meta.guest) return true
  const required = to.meta.role as string | undefined
  if (required && role !== required) return '/login'
  return true
})

export default router
