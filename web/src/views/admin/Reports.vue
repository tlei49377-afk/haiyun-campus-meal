<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { ordersReport, top10Report, turnoverReport, userReport } from '../../api/admin'

const end = new Date(); const begin = new Date(Date.now() - 6 * 86400000)
const beginDate = ref(begin.toLocaleDateString('sv-SE')); const endDate = ref(end.toLocaleDateString('sv-SE')); const loading = ref(false)
const turnover = ref({ dateList: '', turnoverList: '' })
const orders = ref({ dateList: '', orderCountList: '', validOrderCountList: '', totalOrderCount: 0, validOrderCount: 0, orderCompletionRate: 0 })
const users = ref({ dateList: '', totalUserList: '', newUserList: '' })
const top = ref({ nameList: '', numberList: '' })
const turnoverEl = ref<HTMLElement>(); const ordersEl = ref<HTMLElement>(); const usersEl = ref<HTMLElement>(); const topEl = ref<HTMLElement>()
const instanceMap = new Map<string, echarts.ECharts>()
const turnoverTotal = computed(() => turnover.value.turnoverList.split(',').filter(Boolean).reduce((n, v) => n + Number(v), 0))
const dailyRows = computed(() => { const dates = turnover.value.dateList.split(',').filter(Boolean); const turns = turnover.value.turnoverList.split(',').filter(Boolean); const counts = orders.value.orderCountList.split(',').filter(Boolean); const valid = orders.value.validOrderCountList.split(',').filter(Boolean); return dates.map((date, i) => ({ date, turnover: Number(turns[i] || 0), orders: Number(counts[i] || 0), valid: Number(valid[i] || 0) })) })
const topRows = computed(() => { const names = top.value.nameList.split(',').filter(Boolean); const nums = top.value.numberList.split(',').filter(Boolean); return names.map((name, i) => ({ rank: i + 1, name, number: Number(nums[i] || 0) })) })

function setChart(key: string, element: HTMLElement | undefined, option: echarts.EChartsOption) { if (!element) return; let chart = instanceMap.get(key); if (!chart) { chart = echarts.init(element); instanceMap.set(key, chart) } chart.setOption(option, true) }
function renderCharts() {
  const dates = turnover.value.dateList.split(',').filter(Boolean)
  const turns = turnover.value.turnoverList.split(',').map(Number)
  const orderCounts = orders.value.orderCountList.split(',').map(Number)
  const validCounts = orders.value.validOrderCountList.split(',').map(Number)
  const totalUsers = users.value.totalUserList.split(',').map(Number)
  const newUsers = users.value.newUserList.split(',').map(Number)
  const topRowsDesc = [...topRows.value].reverse()
  setChart('turnover', turnoverEl.value, { color:['#146b5c'], tooltip:{trigger:'axis'}, grid:{left:46,right:18,top:24,bottom:32}, xAxis:{type:'category',data:dates,axisLine:{lineStyle:{color:'#ccd8d2'}}}, yAxis:{type:'value',splitLine:{lineStyle:{color:'#eef2f0'}}}, series:[{name:'营业额',type:'line',smooth:true,symbolSize:7,data:turns,areaStyle:{color:'rgba(20,107,92,.12)'}}] })
  setChart('orders', ordersEl.value, { color:['#b56a35','#146b5c'], tooltip:{trigger:'axis'}, legend:{top:0,right:8}, grid:{left:42,right:18,top:34,bottom:32}, xAxis:{type:'category',data:dates}, yAxis:{type:'value',splitLine:{lineStyle:{color:'#eef2f0'}}}, series:[{name:'总订单',type:'bar',barMaxWidth:24,data:orderCounts},{name:'有效订单',type:'bar',barMaxWidth:24,data:validCounts}] })
  setChart('users', usersEl.value, { color:['#607b74','#146b5c'], tooltip:{trigger:'axis'}, legend:{top:0,right:8}, grid:{left:42,right:18,top:34,bottom:32}, xAxis:{type:'category',data:users.value.dateList.split(',').filter(Boolean)}, yAxis:{type:'value',splitLine:{lineStyle:{color:'#eef2f0'}}}, series:[{name:'累计用户',type:'line',data:totalUsers,smooth:true},{name:'新增用户',type:'line',data:newUsers,smooth:true}] })
  setChart('top', topEl.value, { color:['#146b5c'], tooltip:{trigger:'axis',axisPointer:{type:'shadow'}}, grid:{left:90,right:28,top:16,bottom:24}, xAxis:{type:'value',splitLine:{lineStyle:{color:'#eef2f0'}}}, yAxis:{type:'category',data:topRowsDesc.map(r=>r.name)}, series:[{type:'bar',barMaxWidth:18,data:topRowsDesc.map(r=>r.number)}] })
}
async function load() { loading.value = true; try { const [t, o, u, p] = await Promise.all([turnoverReport(beginDate.value, endDate.value), ordersReport(beginDate.value, endDate.value), userReport(beginDate.value, endDate.value), top10Report(beginDate.value, endDate.value)]); turnover.value = t; orders.value = o; users.value = u; top.value = p; await nextTick(); renderCharts() } catch (error) { ElMessage.error((error as Error).message) } finally { loading.value = false } }
async function exportReport() { try { const response = await fetch('/api/admin/report/export', { headers: { token: localStorage.getItem('token') || '' } }); if (!response.ok) throw new Error('报表导出失败'); const blob = await response.blob(); const url = URL.createObjectURL(blob); const link = document.createElement('a'); link.href = url; link.download = `海韵食刻运营数据_${beginDate.value}_${endDate.value}.xlsx`; link.click(); URL.revokeObjectURL(url) } catch (error) { ElMessage.error((error as Error).message) } }
function resizeCharts() { instanceMap.forEach(chart => chart.resize()) }
onMounted(async () => { await load(); window.addEventListener('resize', resizeCharts) })
onBeforeUnmount(() => { window.removeEventListener('resize', resizeCharts); instanceMap.forEach(chart => chart.dispose()); instanceMap.clear() })
</script>
<template><div v-loading="loading"><div class="page-card"><div class="page-header"><div><h2 class="page-title">数据统计</h2><p class="page-subtitle">营业额、订单、用户增长和菜品销量综合分析。</p></div></div><div class="page-actionbar"><div class="query-area"><el-date-picker v-model="beginDate" type="date" value-format="YYYY-MM-DD" /><span class="muted">至</span><el-date-picker v-model="endDate" type="date" value-format="YYYY-MM-DD" /><el-button type="primary" @click="load">查询</el-button></div><div class="action-area"><el-button @click="exportReport">导出运营报表</el-button></div></div><div class="metric-row"><div><span>区间营业额</span><strong>¥{{ turnoverTotal.toFixed(2) }}</strong></div><div><span>总订单数</span><strong>{{ orders.totalOrderCount }}</strong></div><div><span>有效订单</span><strong>{{ orders.validOrderCount }}</strong></div><div><span>完成率</span><strong>{{ (orders.orderCompletionRate * 100).toFixed(1) }}%</strong></div></div><div class="chart-grid"><section class="chart-card"><h3>营业额趋势</h3><div ref="turnoverEl" class="chart"></div></section><section class="chart-card"><h3>订单数量统计</h3><div ref="ordersEl" class="chart"></div></section><section class="chart-card"><h3>用户增长</h3><div ref="usersEl" class="chart"></div></section><section class="chart-card"><h3>菜品销量前十</h3><div ref="topEl" class="chart tall"></div></section></div><el-table :data="dailyRows" stripe class="report-table"><el-table-column prop="date" label="日期" /><el-table-column prop="turnover" label="营业额"><template #default="{ row }">¥{{ row.turnover.toFixed(2) }}</template></el-table-column><el-table-column prop="orders" label="订单数" /><el-table-column prop="valid" label="有效订单" /></el-table></div></div></template>
<style scoped>.metric-row{display:grid;grid-template-columns:repeat(4,1fr);border-top:1px solid #e7ece9;border-bottom:1px solid #e7ece9;margin:8px 0 14px}.metric-row div{padding:14px 16px;border-right:1px solid #e7ece9}.metric-row div:last-child{border:0}.metric-row span,.metric-row strong{display:block}.metric-row span{font-size:11px;color:#87938f}.metric-row strong{font-size:23px;color:#146b5c;margin-top:5px}.chart-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:12px;margin-bottom:14px}.chart-card{border:1px solid #e1e8e4;padding:12px 14px;min-width:0;background:#fff}.chart-card h3{margin:0 0 8px;font-size:13px;color:#334842}.chart{height:240px;width:100%}.chart.tall{height:270px}.report-table{margin-top:4px}@media(max-width:850px){.chart-grid{grid-template-columns:1fr}.metric-row{grid-template-columns:1fr 1fr}.metric-row div:nth-child(2){border-right:0}.metric-row div:nth-child(3),.metric-row div:nth-child(4){border-top:1px solid #e7ece9}}
</style>
