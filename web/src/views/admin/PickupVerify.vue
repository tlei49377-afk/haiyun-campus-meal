<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { verifyPickup } from '../../api/admin'
const code = ref(''); const result = ref<{ message: string; orderNumber: string; pickupCode: string } | null>(null); const loading = ref(false)
async function verify() { if (!/^\d{6}$/.test(code.value)) return ElMessage.warning('请输入 6 位取餐码'); loading.value = true; try { result.value = await verifyPickup(code.value); ElMessage.success(result.value.message); code.value = '' } catch (e) { ElMessage.error((e as Error).message) } finally { loading.value = false } }
</script>
<template><div class="page-card verify-card"><div class="verify-mark">取</div><h2 class="page-title">取餐核销</h2><p class="page-subtitle">学生出示取餐码，档口人员输入后确认交接。</p><el-input v-model="code" maxlength="6" size="large" placeholder="输入 6 位取餐码" class="code-input" @keyup.enter="verify" /><el-button type="primary" size="large" :loading="loading" class="verify-button" @click="verify">确认核销</el-button><div v-if="result" class="verify-result"><strong>{{ result.message }}</strong><span>订单号：{{ result.orderNumber }}</span><span>取餐码：{{ result.pickupCode }}</span></div></div></template>
<style scoped>.verify-card{max-width:620px;margin:8vh auto;text-align:center}.verify-mark{width:54px;height:54px;border-radius:16px;background:#0f766e;color:#fff;display:grid;place-items:center;margin:0 auto 18px;font-size:24px;font-weight:800}.code-input{max-width:320px;margin:8px auto 16px;display:block}.code-input :deep(input){text-align:center;letter-spacing:.35em;font-size:22px}.verify-button{width:220px}.verify-result{display:grid;gap:8px;margin:24px auto 0;padding:18px;border-radius:12px;background:#eef7f4;color:#39645c}.verify-result strong{font-size:20px}</style>
