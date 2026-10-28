<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteDish, getDish, listCategoryPage, listDishPage, saveDish, toggleDishStatus } from '../../api/admin'
import type { Dish } from '../../api/types'

type CategoryRow = { id: number; stallId: number; name: string; type: number; status: number; sort?: number }
type FlavorForm = { id?: number; dishId?: number; name: string; value: string }
const rows = ref<Dish[]>([]); const total = ref(0); const page = ref(1); const pageSize = 20
const keyword = ref(''); const categoryId = ref<number>(); const statusFilter = ref<number>(); const categories = ref<CategoryRow[]>([]); const loading = ref(false); const saving = ref(false); const dialog = ref(false)
const form = reactive({ id: undefined as number | undefined, name: '', categoryId: undefined as number | undefined, price: 0, image: '', description: '', status: 1, flavors: [] as FlavorForm[] })
const token = localStorage.getItem('token') || ''
function beforeUpload(file: File) { if (file.size > 2 * 1024 * 1024) { ElMessage.warning('图片不能超过 2MB'); return false } return true }
function uploadSuccess(response: { code: number; msg?: string; data?: string }) { if (response.code === 1 && response.data) { form.image = response.data; ElMessage.success('图片上传成功') } else ElMessage.error(response.msg || '图片上传失败') }

async function loadDishes() {
  loading.value = true
  try {
    const data = await listDishPage({ page: page.value, pageSize, categoryId: categoryId.value, name: keyword.value || undefined, status: statusFilter.value })
    rows.value = data.records || []; total.value = data.total
  } catch (e) { ElMessage.error((e as Error).message) } finally { loading.value = false }
}
async function loadCategories() {
  const data = await listCategoryPage(1); categories.value = data.records || []
}
function resetForm() {
  Object.assign(form, { id: undefined, name: '', categoryId: categoryId.value, price: 0, image: '', description: '', status: 1, flavors: [] })
}
async function editDish(row?: Dish) {
  if (!row) { resetForm(); dialog.value = true; return }
  try {
    const full = await getDish(row.id)
    const flavors = ((full.flavors || []) as unknown as FlavorForm[]).map(f => ({ id: f.id, dishId: f.dishId, name: f.name || '', value: f.value || '' }))
    Object.assign(form, { ...full, price: Number(full.price), flavors })
    dialog.value = true
  } catch (e) { ElMessage.error((e as Error).message) }
}
function addFlavor() { form.flavors.push({ name: '', value: '' }) }
function removeFlavor(index: number) { form.flavors.splice(index, 1) }
async function submitDish() {
  if (!form.name.trim() || !form.categoryId || Number(form.price) <= 0) return ElMessage.warning('请填写菜品名称、分类和有效价格')
  const flavors = form.flavors.filter(f => f.name.trim() || f.value.trim())
  if (flavors.some(f => !f.name.trim() || !f.value.trim())) return ElMessage.warning('口味名称和可选值需要同时填写')
  saving.value = true
  try {
    await saveDish({ ...form, price: Number(form.price), flavors })
    ElMessage.success(form.id ? '菜品已更新' : '菜品已新增')
    dialog.value = false; await loadDishes()
  } catch (e) { ElMessage.error((e as Error).message) } finally { saving.value = false }
}
async function changeDishStatus(row: Dish) {
  try { await toggleDishStatus(row.id, row.status ? 0 : 1); ElMessage.success(row.status ? '菜品已停售' : '菜品已起售'); await loadDishes() } catch (e) { ElMessage.error((e as Error).message) }
}
async function removeDish(row: Dish) {
  try {
    await ElMessageBox.confirm(`确认删除菜品“${row.name}”？起售中的菜品需要先停售。`, '删除菜品', { type: 'warning' })
    await deleteDish(row.id); ElMessage.success('菜品已删除'); await loadDishes()
  } catch (e) { if (e !== 'cancel' && e !== 'close') ElMessage.error(e instanceof Error ? e.message : '删除失败') }
}
onMounted(async () => { try { await loadCategories() } catch { /* 分类为空时仍可显示菜品 */ } await loadDishes() })
</script>
<template><div class="page-card"><div class="page-header"><div><h2 class="page-title">菜品管理</h2><p class="page-subtitle">维护菜品图片、价格、口味、说明和售卖状态；分类在“分类管理”中维护。</p></div></div><div class="page-actionbar"><div class="query-area"><el-input v-model="keyword" placeholder="搜索菜品名称" clearable style="width: 190px" @keyup.enter="page=1;loadDishes()" /><el-select v-model="categoryId" placeholder="全部分类" clearable style="width: 170px" @change="page=1;loadDishes()"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select><el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 120px" @change="page=1;loadDishes()"><el-option label="起售" :value="1" /><el-option label="停售" :value="0" /></el-select><el-button @click="page=1;loadDishes()">查询</el-button><span class="muted">共 {{ total }} 个菜品</span></div><div class="action-area"><el-button type="primary" @click="editDish()">新增菜品</el-button></div></div><el-table v-loading="loading" :data="rows" stripe><el-table-column label="图片" width="82" align="center"><template #default="{ row }"><el-image v-if="row.image" :src="row.image" :preview-src-list="[row.image]" preview-teleported fit="cover" class="table-image"><template #error><span class="image-empty">无图</span></template></el-image><span v-else class="image-empty">无图</span></template></el-table-column><el-table-column prop="name" label="菜品名称" min-width="140" /><el-table-column prop="categoryName" label="分类" min-width="120" /><el-table-column prop="price" label="价格" width="90"><template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template></el-table-column><el-table-column prop="description" label="说明" min-width="200" /><el-table-column label="口味" width="80" align="center"><template #default="{ row }">{{ row.flavorCount ?? row.flavors?.length ?? 0 }} 项</template></el-table-column><el-table-column label="状态" width="85" align="center"><template #default="{ row }"><el-tag :type="row.status ? 'success' : 'info'">{{ row.status ? '起售' : '停售' }}</el-tag></template></el-table-column><el-table-column label="操作" align="center" header-align="center" class-name="action-column" width="230"><template #default="{ row }"><el-button text type="primary" @click="editDish(row)">编辑</el-button><el-button text :type="row.status ? 'warning' : 'success'" @click="changeDishStatus(row)">{{ row.status ? '停售' : '起售' }}</el-button><el-button text type="danger" @click="removeDish(row)">删除</el-button></template></el-table-column></el-table><el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next, total" @current-change="loadDishes" /><el-dialog v-model="dialog" :title="form.id ? '编辑菜品' : '新增菜品'" width="680px"><el-form label-position="top"><el-row :gutter="14"><el-col :span="12"><el-form-item label="菜品名称"><el-input v-model="form.name" /></el-form-item></el-col><el-col :span="12"><el-form-item label="所属分类"><el-select v-model="form.categoryId" style="width:100%"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item></el-col><el-col :span="12"><el-form-item label="价格"><el-input-number v-model="form.price" :min="0.01" :precision="2" style="width:100%" /></el-form-item></el-col><el-col :span="12"><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :value="1">起售</el-radio><el-radio :value="0">停售</el-radio></el-radio-group></el-form-item></el-col></el-row><el-form-item label="说明"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item><el-form-item label="菜品图片"><div class="image-field"><el-upload action="/api/admin/common/upload" :headers="{ token }" name="file" :show-file-list="false" :before-upload="beforeUpload" :on-success="uploadSuccess"><el-button>上传图片</el-button></el-upload><el-input v-model="form.image" placeholder="上传后自动填入，也可手动填写图片地址" /></div><el-image v-if="form.image" :src="form.image" fit="cover" class="image-preview" /></el-form-item><el-form-item label="菜品口味"><div class="flavor-editor"><div v-for="(flavor, index) in form.flavors" :key="index" class="flavor-row"><el-input v-model="flavor.name" placeholder="口味名称，如辣度" /><el-input v-model="flavor.value" placeholder="可选值，如不辣,微辣,中辣" /><el-button text type="danger" @click="removeFlavor(index)">删除</el-button></div><el-button plain type="primary" size="small" @click="addFlavor">添加口味</el-button><span v-if="!form.flavors.length" class="muted">该菜品暂无口味选项</span></div></el-form-item></el-form><template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" :loading="saving" @click="submitDish">保存</el-button></template></el-dialog></div></template>
<style scoped>.table-image{width:48px;height:48px;border:1px solid #e1e8e4;border-radius:3px}.image-empty{display:inline-grid;place-items:center;width:48px;height:48px;background:#f4f6f4;color:#9aa5a1;font-size:10px}.image-field{display:flex;gap:8px;width:100%}.image-field .el-input{flex:1}.image-preview{width:88px;height:88px;border:1px solid #e1e8e4;margin-top:8px;border-radius:3px}.flavor-editor{display:grid;gap:8px;width:100%}.flavor-row{display:grid;grid-template-columns:160px minmax(0,1fr) 52px;gap:8px;align-items:center}.flavor-row .el-button{margin:0}</style>
