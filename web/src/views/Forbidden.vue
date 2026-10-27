<template>
  <div class="forbidden-page">
    <div class="page-card">
      <span class="code">403</span>
      <h2>当前账号没有访问权限</h2>
      <p>菜单与后端接口均按权限码校验。如需访问，请联系管理员调整角色权限。</p>
      <el-button type="primary" @click="router.replace('/admin/dashboard')">返回工作台</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.forbidden-page{min-height:70vh;display:grid;place-items:center}.page-card{max-width:480px;text-align:center;padding:42px}.code{font-size:52px;font-weight:800;color:#146b5c}.page-card h2{margin:8px 0 10px;font-size:20px}.page-card p{color:#7b8884;font-size:13px;line-height:1.8;margin-bottom:22px}
</style>
