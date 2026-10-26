<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get, put } from '../../utils/request'
import { ElMessage } from 'element-plus'
import type { Order } from '../../api/types'
const route = useRoute(); const router = useRouter(); const order = ref<Order | null>(null); const loading = ref(true); const paying = ref(false)
const statusText: Record<number, string> = { 10: '待支付', 20: '待接单', 30: '制作中', 40: '待取餐', 50: '已取餐', 60: '已取消', 70: '已退款' }
async function payNow() { if (!order.value) return; paying.value = true; try { await put('/app/order/payment', { orderNumber: order.value.number, payMethod: 3 }); ElMessage.success('支付成功，档口开始备餐'); order.value = await get<Order>(`/app/order/orderDetail/${route.params.id}`) } catch (e) { ElMessage.error((e as Error).message) } finally { paying.value = false } }
onMounted(async () => { try { order.value = await get<Order>(`/app/order/orderDetail/${route.params.id}`) } finally { loading.value = false } })
</script>
<template><div class="mobile-page"><el-page-header title="返回" @back="router.back()"><template #content><strong>订单详情</strong></template></el-page-header><div v-if="order" v-loading="loading"><div class="page-card code-card"><span class="code-label">取餐码</span><strong class="code">{{ order.pickupCode || '待生成' }}</strong><p>{{ statusText[order.status] }}</p><span class="muted">到档口出示此码，由工作人员核销</span><el-button v-if="order.status === 10" type="primary" :loading="paying" class="pay-button" @click="payNow">模拟支付</el-button></div><div class="page-card detail-card"><div><span>订单号</span><b>{{ order.number }}</b></div><div><span>金额</span><b>¥{{ order.amount }}</b></div><div><span>下单时间</span><b>{{ order.orderTime }}</b></div><div><span>餐品</span><b>{{ order.orderDishes || '以订单明细为准' }}</b></div></div></div><div v-else v-loading="loading" class="page-card empty">正在加载订单…</div></div></template>
<style scoped>.pay-button{margin-top:14px;width:180px}.code-card .muted{color:#cfe4de!important}.code-card{text-align:center;padding:30px 20px;background:linear-gradient(145deg,#0f766e,#174b47);color:#fff}.code-label{font-size:12px;letter-spacing:.2em;opacity:.75}.code{display:block;font-size:44px;letter-spacing:.12em;margin:18px 0 8px;font-family:ui-monospace,monospace}.code-card p{margin:0 0 10px}.detail-card{margin-top:14px;display:grid;gap:14px}.detail-card div{display:flex;justify-content:space-between;gap:16px;border-bottom:1px solid #eef2f0;padding-bottom:10px}.detail-card span{color:#87948f;font-size:13px}.detail-card b{text-align:right;font-size:13px}</style>
