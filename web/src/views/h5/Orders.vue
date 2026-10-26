<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { cancelMyOrder, listMyOrders, payOrder, repeatMyOrder } from '../../api/app'
import type { Order } from '../../api/types'
const router = useRouter(); const rows = ref<Order[]>([]); const loading = ref(true); const payingId = ref<number>()
const statusText: Record<number, string> = { 10: '待支付', 20: '待接单', 30: '制作中', 40: '待取餐', 50: '已取餐', 60: '已取消', 70: '已退款' }
async function load() { loading.value = true; try { rows.value = (await listMyOrders()).records || [] } finally { loading.value = false } }
async function payNow(order: Order) { payingId.value = order.id; try { await payOrder({ orderNumber: order.number, payMethod: 3 }); ElMessage.success('支付成功，档口开始备餐'); await load() } catch (e) { ElMessage.error((e as Error).message) } finally { payingId.value = undefined } }
async function cancel(order: Order) { try { await ElMessageBox.confirm('确认取消这笔订单？已支付订单将按退款处理，并释放取餐时段。', '取消订单', { type: 'warning' }); await cancelMyOrder(order.id); ElMessage.success('订单已取消'); await load() } catch (e) { if (e !== 'cancel') ElMessage.error((e as Error).message) } }
async function repeat(order: Order) { try { await repeatMyOrder(order.id); ElMessage.success('餐品已加入购物车'); router.push('/student/cart') } catch (e) { ElMessage.error((e as Error).message) } }
onMounted(load)
</script>
<template><div class="mobile-page orders-page"><h2 class="page-title">我的订单</h2><p class="page-subtitle">取餐码只在待取餐订单中显示。</p><div v-loading="loading" class="orders-list"><div v-if="!rows.length" class="page-card empty">还没有订单记录。</div><article v-for="order in rows" :key="order.id" class="page-card order-card" @click="router.push(`/student/pickup/${order.id}`)"><div class="order-top"><strong>{{ order.orderDishes || order.number }}</strong><el-tag :type="order.status === 40 ? 'success' : order.status === 30 ? 'primary' : 'info'">{{ statusText[order.status] }}</el-tag></div><div class="order-meta"><span>订单号 {{ order.number }}</span><span>{{ order.orderTime }}</span></div><div class="order-bottom"><b>¥{{ order.amount }}</b><div class="order-actions"><el-button v-if="order.status === 10" type="primary" size="small" :loading="payingId === order.id" @click.stop="payNow(order)">模拟支付</el-button><el-button v-if="[10,20].includes(order.status)" text type="danger" @click.stop="cancel(order)">取消</el-button><el-button text @click.stop="repeat(order)">再来一单</el-button><el-button text type="primary" @click.stop="router.push(`/student/pickup/${order.id}`)">详情</el-button></div></div></article></div></div></template>
<style scoped>.order-card{margin-bottom:12px;cursor:pointer}.order-top,.order-bottom{display:flex;justify-content:space-between;align-items:center}.order-meta{display:flex;flex-direction:column;gap:5px;color:#85938f;font-size:12px;margin:12px 0}.order-bottom b{color:#bd6b35}.order-actions{display:flex;align-items:center;gap:2px}.order-actions :deep(.el-button){padding:0 6px;font-size:11px}</style>
