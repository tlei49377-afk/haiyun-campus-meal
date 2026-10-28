<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addCart, getAppDish } from '../../api/app'
import type { Dish } from '../../api/types'

const route = useRoute(); const router = useRouter()
const dish = ref<Dish | null>(null)
const loading = ref(true)
const adding = ref(false)
const fallback = ref(false)
function flavorText(value: unknown) {
  if (typeof value !== 'string') return String(value ?? '')
  try {
    const parsed = JSON.parse(value)
    return Array.isArray(parsed) ? parsed.join(' / ') : String(parsed)
  } catch { return value }
}
async function add() {
  if (!dish.value) return
  adding.value = true
  try { await addCart(dish.value.id); ElMessage.success('已加入购物车') }
  catch (error) { ElMessage.error((error as Error).message) }
  finally { adding.value = false }
}
onMounted(async () => {
  try { dish.value = await getAppDish(Number(route.params.dishId)) }
  catch (error) { ElMessage.error((error as Error).message) }
  finally { loading.value = false }
})
</script>

<template>
  <div class="mobile-page dish-detail-page">
    <el-page-header title="返回" @back="router.back()"><template #content><strong>菜品详情</strong></template></el-page-header>
    <div v-loading="loading" class="detail-wrap">
      <template v-if="dish">
        <div class="dish-visual">
          <el-image v-if="dish.image" :src="dish.image" fit="cover" class="dish-image" @error="fallback = true" />
          <div v-if="!dish.image || fallback" class="dish-placeholder">{{ dish.name.slice(0, 2) }}</div>
        </div>
        <section class="page-card detail-card">
          <div class="title-row"><div><h2>{{ dish.name }}</h2><p>{{ dish.categoryName || '校园餐厅' }}</p></div><b>¥{{ Number(dish.price).toFixed(2) }}</b></div>
          <p class="description">{{ dish.description || '当日现做，请在预约时段前往档口取餐。' }}</p>
          <div v-if="dish.flavors?.length" class="flavor-block"><h3>可选口味</h3><div v-for="flavor in dish.flavors" :key="String(flavor.id || flavor.name)" class="flavor-row"><span>{{ flavor.name }}</span><strong>{{ flavorText(flavor.value) }}</strong></div></div>
          <div class="service-note"><span>取餐方式</span><strong>到店自取</strong><span>库存规则</span><strong>按取餐时段限量预约</strong></div>
        </section>
        <div class="detail-actions"><el-button size="large" @click="router.push('/student/cart')">查看购物车</el-button><el-button type="primary" size="large" :loading="adding" @click="add">加入购物车</el-button></div>
      </template>
      <div v-else-if="!loading" class="page-card empty">菜品不存在或已停售</div>
    </div>
  </div>
</template>

<style scoped>
.detail-wrap{margin-top:14px}.dish-visual{height:clamp(180px,58vw,220px);overflow:hidden;border-radius:7px;border:1px solid #dde5e1;background:#f1ece3}.dish-image{width:100%;height:100%}.dish-placeholder{width:100%;height:100%;display:grid;place-items:center;font-size:42px;font-weight:800;color:#a36a3d;background:#f1e8db}.detail-card{margin-top:12px}.title-row{display:flex;justify-content:space-between;gap:16px;align-items:flex-start}.title-row h2{margin:0;font-size:22px}.title-row p{margin:6px 0 0;color:#82908b;font-size:11px}.title-row b{font-size:22px;color:#a35d31}.description{color:#65746f;font-size:13px;line-height:1.8;margin:16px 0}.flavor-block{border-top:1px solid #edf0ee;padding-top:14px}.flavor-block h3{margin:0 0 9px;font-size:13px}.flavor-row{display:flex;justify-content:space-between;gap:16px;padding:8px 0;border-bottom:1px solid #f0f3f1;font-size:12px}.flavor-row span{color:#7f8d88}.flavor-row strong{text-align:right;color:#3c554e}.service-note{display:grid;grid-template-columns:auto 1fr;gap:8px 14px;margin-top:16px;background:#f7faf8;border:1px solid #e5ece8;padding:12px;font-size:11px}.service-note span{color:#87948f}.detail-actions{position:sticky;bottom:68px;display:flex;justify-content:flex-end;gap:8px;margin-top:12px;padding:10px 0;background:linear-gradient(180deg,transparent,#f5f3ed 30%)}@media(min-width:900px){.mode-desktop .detail-wrap{display:grid;grid-template-columns:360px minmax(0,1fr);gap:18px;align-items:start}.mode-desktop .dish-visual{height:320px}.mode-desktop .detail-card{margin-top:0}.mode-desktop .detail-actions{grid-column:1/-1}}@media(max-width:390px){.dish-detail-page{padding-left:10px;padding-right:10px}.title-row h2{font-size:20px}.detail-actions{gap:6px}.detail-actions .el-button{flex:1;margin-left:0}}
</style>
