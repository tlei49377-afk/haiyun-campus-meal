<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataBoard, OfficeBuilding, Shop, Clock, Tickets, FullScreen, ChatDotRound, SwitchButton, Dish, User, TrendCharts, Collection, Box } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'
import { ElNotification } from 'element-plus'

const route = useRoute(); const router = useRouter(); const auth = useAuthStore()
const active = computed(() => route.path)
const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
const menus = [
  { path: '/admin/dashboard', label: '工作台', icon: DataBoard },
  { path: '/admin/canteens', label: '食堂管理', icon: OfficeBuilding, permission: 'canteen:query' },
  { path: '/admin/stalls', label: '档口管理', icon: Shop, permission: 'stall:query' },
  { path: '/admin/slots', label: '取餐时段', icon: Clock, permission: 'slot:query' },
  { path: '/admin/categories', label: '分类管理', icon: Collection, permission: 'category:query' },
  { path: '/admin/dishes', label: '菜品管理', icon: Dish, permission: 'dish:query' },
  { path: '/admin/combos', label: '套餐管理', icon: Box, permission: 'setmeal:query' },
  { path: '/admin/orders', label: '订单管理', icon: Tickets, permission: 'order:query' },
  { path: '/admin/reports', label: '数据统计', icon: TrendCharts, permission: 'report:query' },
  { path: '/admin/verify', label: '取餐核销', icon: FullScreen, permission: 'order:handle' },
  { path: '/admin/staff', label: '员工与权限', icon: User, permission: 'employee:query' },
  { path: '/admin/ai', label: 'AI 助手', icon: ChatDotRound }
]
const visibleMenus = computed(() => menus.filter(item => auth.hasPermission(item.permission)))
const currentLabel = computed(() => menus.find(item => item.path === active.value)?.label || '运营中心')
let socket: WebSocket | undefined
let reconnectTimer: number | undefined
let closedByUser = false
function connectSocket() {
  if (closedByUser) return
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  const sid = encodeURIComponent(auth.name || 'operator')
  socket = new WebSocket(`${protocol}//${location.host}/ws/${sid}`)
  socket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data) as { type?: number; content?: string }
      ElNotification({ title: data.type === 2 ? '学生催单' : '新订单提醒', message: data.content || '有新的订单消息', type: data.type === 2 ? 'warning' : 'success', duration: 6000 })
    } catch { /* 非 JSON 消息忽略 */ }
  }
  socket.onclose = () => { if (!closedByUser) reconnectTimer = window.setTimeout(connectSocket, 5000) }
}
function logout() { closedByUser = true; socket?.close(); auth.clear(); router.replace('/login') }
onMounted(connectSocket)
onBeforeUnmount(() => { closedByUser = true; socket?.close(); if (reconnectTimer) window.clearTimeout(reconnectTimer) })
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-aside">
      <div class="campus-brand"><span class="campus-mark">海</span><div><strong>海韵食刻</strong><small>海南热带海洋学院</small></div></div>
      <p class="menu-caption">校园餐饮运营</p>
      <el-menu :default-active="active" router class="admin-menu">
        <el-menu-item v-for="item in visibleMenus" :key="item.path" :index="item.path"><el-icon><component :is="item.icon" /></el-icon><span>{{ item.label }}</span></el-menu-item>
      </el-menu>
      <div class="aside-footer"><span>校园餐饮服务中心</span><small>内部运营系统</small></div>
    </aside>
    <section class="admin-main">
      <header class="admin-header"><div class="header-left"><span class="crumb">运营中心</span><i>/</i><strong>{{ currentLabel }}</strong></div><div class="header-right"><span class="header-date">{{ today }}</span><span class="operator">{{ auth.name || '运营人员' }}</span><el-button text :icon="SwitchButton" @click="logout">退出</el-button></div></header>
      <main class="admin-content"><router-view /></main>
    </section>
  </div>
</template>

<style scoped>
.admin-shell{display:flex;min-height:100vh;background:#f5f3ed}.admin-aside{width:218px;background:#123e36;color:#fff;padding:0 11px;display:flex;flex-direction:column}.campus-brand{display:flex;gap:10px;align-items:center;padding:19px 9px 17px;border-bottom:1px solid rgba(255,255,255,.13)}.campus-mark{display:grid;place-items:center;width:35px;height:35px;border-radius:3px;background:#efe4d2;color:#174b40;font-weight:800}.campus-brand strong,.campus-brand small{display:block}.campus-brand strong{font-size:16px;letter-spacing:.06em}.campus-brand small{color:#a9c4bd;font-size:10px;margin-top:3px;letter-spacing:.03em}.menu-caption{margin:18px 10px 7px;color:#8eb2a9;font-size:10px;letter-spacing:.16em}.admin-menu{border:0;background:transparent;flex:1}.admin-menu .el-menu-item{height:46px;color:#c9dcd7;border-radius:2px;margin:2px 0;border-left:3px solid transparent}.admin-menu .el-menu-item:hover{background:rgba(255,255,255,.06);color:#fff}.admin-menu .el-menu-item.is-active{background:#1b5147;color:#fff;border-left-color:#d9a16d;font-weight:700}.admin-menu .el-icon{margin-right:10px}.aside-footer{padding:14px 10px 18px;border-top:1px solid rgba(255,255,255,.12);color:#8eb2a9;font-size:10px;line-height:1.7}.aside-footer span,.aside-footer small{display:block}.aside-footer small{color:#6f978d}.admin-main{flex:1;min-width:0}.admin-header{height:58px;padding:0 22px;display:flex;align-items:center;justify-content:space-between;background:#fff;border-bottom:1px solid #dde5e1}.header-left{display:flex;align-items:center;gap:8px;font-size:12px}.header-left .crumb{color:#8a9692}.header-left i{font-style:normal;color:#c0c9c5}.header-left strong{font-weight:700;color:#334641}.header-right{display:flex;align-items:center;gap:15px;color:#77847f;font-size:11px}.header-date{color:#87938f}.operator{color:#3d554f;font-weight:600}.admin-content{padding:22px}@media(max-width:760px){.admin-aside{width:70px}.campus-brand div,.menu-caption,.aside-footer,.admin-menu span{display:none}.campus-brand{justify-content:center;padding-left:0;padding-right:0}.admin-menu .el-menu-item{justify-content:center;padding:0!important}.admin-menu .el-icon{margin:0}.admin-content{padding:12px}.header-date{display:none}.admin-header{padding:0 12px}.header-right{gap:8px}}
</style>
