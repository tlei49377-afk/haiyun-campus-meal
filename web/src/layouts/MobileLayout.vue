<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { House, ShoppingCart, Tickets, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

type ViewMode = 'desktop' | 'mobile'
const route = useRoute(); const router = useRouter(); const auth = useAuthStore()
const savedMode = localStorage.getItem('studentViewMode') as ViewMode | null
const viewMode = ref<ViewMode>(savedMode || (window.innerWidth >= 900 ? 'desktop' : 'mobile'))
const tabs = [
  { path: '/student/home', label: '食堂', icon: House },
  { path: '/student/cart', label: '购物车', icon: ShoppingCart },
  { path: '/student/orders', label: '订单', icon: Tickets }
]
function setMode(mode: ViewMode) { viewMode.value = mode; localStorage.setItem('studentViewMode', mode) }
function logout() { auth.clear(); router.replace('/login') }
</script>
<template>
  <div class="student-shell" :class="viewMode === 'desktop' ? 'mode-desktop' : 'mode-mobile'">
    <header class="student-header">
      <div class="mobile-brand"><strong>海韵食刻</strong><span>海南热带海洋学院 · 校园餐饮</span></div>
      <div class="header-actions"><div class="view-switch"><button :class="{ active: viewMode === 'desktop' }" @click="setMode('desktop')">电脑版</button><button :class="{ active: viewMode === 'mobile' }" @click="setMode('mobile')">手机版</button></div><el-button text :icon="SwitchButton" @click="logout">退出</el-button></div>
    </header>
    <nav class="student-nav"><router-link v-for="tab in tabs" :key="tab.path" :to="tab.path" :class="{ active: route.path === tab.path }"><el-icon><component :is="tab.icon" /></el-icon><span>{{ tab.label }}</span></router-link></nav>
    <main class="student-content"><router-view /></main>
  </div>
</template>
<style scoped>
.student-shell{min-height:100vh;background:#f5f3ed}.mode-mobile{max-width:520px;margin:0 auto;border-left:1px solid #e3e5df;border-right:1px solid #e3e5df}.student-header{height:58px;padding:0 16px;display:flex;justify-content:space-between;align-items:center;background:#fff;color:#24312e;border-bottom:1px solid #dde5e1}.mobile-brand strong{display:block;font-size:17px;letter-spacing:.05em}.mobile-brand span{display:block;margin-top:2px;font-size:10px;color:#84908c;letter-spacing:.03em}.header-actions{display:flex;align-items:center;gap:8px}.view-switch{display:flex;border:1px solid #d5dfda}.view-switch button{border:0;background:#fff;color:#71807b;padding:4px 8px;font-size:10px;cursor:pointer}.view-switch button+button{border-left:1px solid #d5dfda}.view-switch button.active{background:#eaf3ef;color:#146b5c;font-weight:700}.student-header :deep(.el-button){color:#73817d;font-size:12px}.student-nav{position:fixed;bottom:0;left:50%;transform:translateX(-50%);width:min(520px,100%);height:62px;display:flex;background:#fff;border-top:1px solid #dde5e1;z-index:10}.student-nav a{flex:1;display:grid;place-items:center;gap:1px;color:#7b8884;font-size:11px;padding:7px 0;border-top:2px solid transparent}.student-nav a.active{color:#146b5c;border-top-color:#146b5c;font-weight:700;background:#f7faf8}.student-nav .el-icon{font-size:19px}.student-content{min-height:calc(100vh - 58px)}.mode-desktop{max-width:none;border:0}.mode-desktop .student-header{max-width:1180px;margin:0 auto;padding:0 24px;height:64px}.mode-desktop .student-nav{position:static;transform:none;width:auto;max-width:1180px;height:46px;margin:0 auto;justify-content:flex-start;border-bottom:1px solid #dde5e1;box-shadow:none}.mode-desktop .student-nav a{flex:0 0 auto;min-width:112px;display:flex;gap:7px;border-top:0;border-bottom:2px solid transparent;padding:0 18px}.mode-desktop .student-nav a.active{border-bottom-color:#146b5c;background:#f7faf8}.mode-desktop .student-nav .el-icon{font-size:17px}.mode-desktop .student-content{max-width:1180px;margin:0 auto;min-height:0}@media(max-width:700px){.mode-desktop .student-header{max-width:none}.mode-desktop .student-nav{overflow-x:auto;max-width:none;padding:0 8px}.mode-desktop .student-content{max-width:none}}
</style>
