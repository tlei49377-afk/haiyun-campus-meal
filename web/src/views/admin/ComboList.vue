<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createSetmeal, deleteSetmeals, getSetmeal, listCategoryPage, listDishPage, setmealPage, toggleSetmealStatus, updateSetmeal } from '../../api/admin'
import type { Dish, Setmeal } from '../../api/types'

type CategoryRow = { id: number; name: string; type: number; status: number }
type SetmealDishRow = { dishId: number; name: string; price: number; copies: number }
const rows = ref<Setmeal[]>([]); const dishes = ref<Dish[]>([]); const categories = ref<CategoryRow[]>([]); const total = ref(0); const page = ref(1); const pageSize = 20
const name = ref(''); const categoryFilter = ref<number>(); const status = ref<number>(); const dishKeyword = ref(''); const loading = ref(false)
const dialog = ref(false); const editing = ref(false); const saving = ref(false)
const form = reactive({ id: undefined as number | undefined, categoryId: undefined as number | undefined, name: '', price: 0, description: '', image: '', status: 1, dishIds: [] as number[], dishCopies: {} as Record<number, number> })
const token = localStorage.getItem('token') || ''
const filteredDishes = computed(() => { const kw = dishKeyword.value.trim(); return kw ? dishes.value.filter(d => d.name.includes(kw)) : dishes.value })
function beforeUpload(file: File) { if (file.size > 2 * 1024 * 1024) { ElMessage.warning('图片不能超过 2MB'); return false } return true }
function uploadSuccess(response: { code: number; msg?: string; data?: string }) { if (response.code === 1 && response.data) { form.image = response.data; ElMessage.success('图片上传成功') } else ElMessage.error(response.msg || '图片上传失败') }
function changeDishSelection(dishId: number, checked: string | number | boolean) { const selected = Boolean(checked); if (selected) { if (!form.dishIds.includes(dishId)) form.dishIds.push(dishId); form.dishCopies[dishId] = form.dishCopies[dishId] || 1 } else { form.dishIds = form.dishIds.filter(id => id !== dishId); delete form.dishCopies[dishId] } }
async function load() {
  loading.value = true
  try {
    const data = await setmealPage({ page: page.value, pageSize, name: name.value || undefined, categoryId: categoryFilter.value, status: status.value })
    rows.value = (data.records || []) as unknown as Setmeal[]; total.value = data.total
  } catch (e) { ElMessage.error((e as Error).message) } finally { loading.value = false }
}
function openCreate() {
  editing.value = false; dishKeyword.value = ''
  Object.assign(form, { id: undefined, categoryId: categories.value[0]?.id, name: '', price: 0, description: '', image: '', status: 1, dishIds: [], dishCopies: {} })
  dialog.value = true
}
async function openEdit(row: Setmeal) {
  try {
    const detail = await getSetmeal(row.id) as unknown as { id: number; categoryId: number; name: string; price: number; description?: string; image?: string; status: number; setmealDishes?: SetmealDishRow[] }
    const relations = detail.setmealDishes || []
    const copies: Record<number, number> = {}
    relations.forEach(d => { copies[d.dishId] = d.copies || 1 })
    editing.value = true; dishKeyword.value = ''
    Object.assign(form, { id: detail.id, categoryId: detail.categoryId, name: detail.name, price: Number(detail.price), description: detail.description || '', image: detail.image || '', status: detail.status, dishIds: relations.map(d => d.dishId), dishCopies: copies })
    dialog.value = true
  } catch (e) { ElMessage.error((e as Error).message) }
}
async function save() {
  if (!form.name.trim() || !form.categoryId) return ElMessage.warning('请填写套餐名称并选择分类')
  if (!form.dishIds.length) return ElMessage.warning('请至少选择一道菜品')
  if (Number(form.price) <= 0) return ElMessage.warning('套餐价格必须大于 0')
  saving.value = true
  try {
    const selected = form.dishIds.map(id => {
      const dish = dishes.value.find(d => d.id === id)
      if (!dish) return null
      return { dishId: dish.id, name: dish.name, price: dish.price, copies: form.dishCopies[dish.id] || 1 }
    }).filter(Boolean)
    const payload = { id: form.id, categoryId: form.categoryId, name: form.name, price: Number(form.price), description: form.description, image: form.image, status: form.status, setmealDishes: selected }
    if (editing.value) await updateSetmeal(payload); else await createSetmeal(payload)
    ElMessage.success('套餐已保存'); dialog.value = false; await load()
  } catch (e) { ElMessage.error((e as Error).message) } finally { saving.value = false }
}
async function toggle(row: Setmeal) {
  try { await toggleSetmealStatus(row.id, row.status ? 0 : 1); ElMessage.success(row.status ? '套餐已停售' : '套餐已起售'); await load() } catch (e) { ElMessage.error((e as Error).message) }
}
async function remove(row: Setmeal) {
  try {
    await ElMessageBox.confirm(`确认删除套餐“${row.name}”？起售中的套餐需要先停售。`, '删除套餐', { type: 'warning' })
    await deleteSetmeals([row.id]); ElMessage.success('套餐已删除'); await load()
  } catch (e) { if (e !== 'cancel' && e !== 'close') ElMessage.error(e instanceof Error ? e.message : '删除失败') }
}
onMounted(async () => { try { const [c, d] = await Promise.all([listCategoryPage(2), listDishPage({ page: 1, pageSize: 200 })]); categories.value = c.records; dishes.value = d.records || [] } catch { /* 基础数据失败时保留空状态 */ } await load() })
</script>
<template><div class="page-card"><div class="page-header"><div><h2 class="page-title">套餐管理</h2><p class="page-subtitle">组合菜品、设置份数，并维护套餐图片、价格和起售状态。</p></div></div><div class="page-actionbar"><div class="query-area"><el-input v-model="name" placeholder="套餐名称" clearable style="width:180px" @keyup.enter="page=1;load()" /><el-select v-model="categoryFilter" placeholder="全部分类" clearable style="width:180px" @change="page=1;load()"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select><el-select v-model="status" placeholder="全部状态" clearable style="width:120px" @change="page=1;load()"><el-option label="起售" :value="1" /><el-option label="停售" :value="0" /></el-select><el-button @click="page=1;load()">查询</el-button><span class="muted">共 {{ total }} 个套餐</span></div><div class="action-area"><el-button type="primary" @click="openCreate">新增套餐</el-button></div></div><el-table v-loading="loading" :data="rows" stripe><el-table-column label="图片" width="82" align="center"><template #default="{ row }"><el-image v-if="row.image" :src="row.image" :preview-src-list="[row.image]" preview-teleported fit="cover" class="table-image"><template #error><span class="image-empty">无图</span></template></el-image><span v-else class="image-empty">无图</span></template></el-table-column><el-table-column prop="name" label="套餐名称" min-width="160" /><el-table-column prop="categoryName" label="分类" min-width="130" /><el-table-column prop="price" label="价格" width="95"><template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template></el-table-column><el-table-column label="菜品数" width="85" align="center"><template #default="{ row }">{{ row.dishCount ?? 0 }} 道</template></el-table-column><el-table-column prop="description" label="说明" min-width="220" /><el-table-column label="状态" width="85" align="center"><template #default="{ row }"><el-tag :type="row.status ? 'success' : 'info'">{{ row.status ? '起售' : '停售' }}</el-tag></template></el-table-column><el-table-column label="操作" align="center" header-align="center" class-name="action-column" width="230"><template #default="{ row }"><el-button text type="primary" @click="openEdit(row)">编辑</el-button><el-button text :type="row.status ? 'warning' : 'success'" @click="toggle(row)">{{ row.status ? '停售' : '起售' }}</el-button><el-button text type="danger" @click="remove(row)">删除</el-button></template></el-table-column></el-table><el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next, total" @current-change="load" /><el-dialog v-model="dialog" :title="editing ? '编辑套餐' : '新增套餐'" width="760px"><el-form label-position="top"><el-row :gutter="14"><el-col :span="12"><el-form-item label="套餐名称"><el-input v-model="form.name" /></el-form-item></el-col><el-col :span="12"><el-form-item label="套餐价格"><el-input-number v-model="form.price" :min="0.01" :precision="2" style="width:100%" /></el-form-item></el-col><el-col :span="12"><el-form-item label="套餐分类"><el-select v-model="form.categoryId" style="width:100%"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item></el-col><el-col :span="12"><el-form-item label="售卖状态"><el-radio-group v-model="form.status"><el-radio :value="1">起售</el-radio><el-radio :value="0">停售</el-radio></el-radio-group></el-form-item></el-col></el-row><el-form-item label="说明"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item><el-form-item label="套餐图片"><div class="image-field"><el-upload action="/api/admin/common/upload" :headers="{ token }" name="file" :show-file-list="false" :before-upload="beforeUpload" :on-success="uploadSuccess"><el-button>上传图片</el-button></el-upload><el-input v-model="form.image" placeholder="上传后自动填入，也可手动填写图片地址" /></div><el-image v-if="form.image" :src="form.image" fit="cover" class="image-preview" /></el-form-item><el-form-item label="包含菜品与份数"><div class="dish-picker"><el-input v-model="dishKeyword" placeholder="搜索菜品" clearable /><div class="dish-options"><label v-for="dish in filteredDishes" :key="dish.id" class="dish-option"><el-checkbox :model-value="form.dishIds.includes(dish.id)" @change="changeDishSelection(dish.id, $event)">{{ dish.name }} <small>¥{{ Number(dish.price).toFixed(2) }}</small></el-checkbox><el-input-number v-if="form.dishIds.includes(dish.id)" v-model="form.dishCopies[dish.id]" :min="1" :max="10" size="small" controls-position="right" class="copies-input" /></label></div></div></el-form-item></el-form><template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></template></el-dialog></div></template>
<style scoped>.table-image{width:48px;height:48px;border:1px solid #e1e8e4;border-radius:3px}.image-empty{display:inline-grid;place-items:center;width:48px;height:48px;background:#f4f6f4;color:#9aa5a1;font-size:10px}.image-field{display:flex;gap:8px;width:100%}.image-field .el-input{flex:1}.image-preview{width:88px;height:88px;border:1px solid #e1e8e4;margin-top:8px;border-radius:3px}.dish-picker{display:grid;gap:8px;width:100%}.dish-options{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:6px;max-height:300px;overflow:auto;border:1px solid #e5ece9;padding:10px}.dish-option{display:flex;align-items:center;justify-content:space-between;gap:8px;padding:4px 6px;background:#fafcfa}.dish-option .el-checkbox{flex:1}.dish-option small{color:#96a19d;margin-left:4px}.copies-input{width:90px}.dish-option :deep(.el-checkbox__label){white-space:normal;line-height:1.4}@media(max-width:700px){.dish-options{grid-template-columns:1fr}}
</style>
