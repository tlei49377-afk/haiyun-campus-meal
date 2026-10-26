<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteSlot, generateSlots, listSlots, listStalls, updateSlot } from '../../api/admin'
import type { PickupSlot, Stall } from '../../api/types'

const stalls = ref<Stall[]>([]); const rows = ref<PickupSlot[]>([]); const stallId = ref<number>(); const slotDate = ref(new Date().toLocaleDateString('sv-SE')); const loading = ref(false)
const generateDialog = ref(false); const editDialog = ref(false); const saving = ref(false)
const generateForm = reactive({ stallId: 1, slotDate: slotDate.value, startTime: '11:00', endTime: '13:00', intervalMinutes: 30, capacity: 50 })
const editForm = reactive({ id: 0, startTime: '', endTime: '', capacity: 0, status: 1, booked: 0 })

async function load() {
  if (!stallId.value) { rows.value = []; return }
  loading.value = true
  try { rows.value = await listSlots({ stallId: stallId.value, slotDate: slotDate.value }) } catch (e) { ElMessage.error((e as Error).message) } finally { loading.value = false }
}
function openGenerate() {
  Object.assign(generateForm, { stallId: stallId.value || stalls.value[0]?.id || 1, slotDate: slotDate.value, startTime: '11:00', endTime: '13:00', intervalMinutes: 30, capacity: 50 })
  generateDialog.value = true
}
async function generate() {
  if (!generateForm.stallId || !generateForm.slotDate) return ElMessage.warning('请选择档口和日期')
  if (!generateForm.startTime || !generateForm.endTime) return ElMessage.warning('请选择开始和结束时间')
  saving.value = true
  try {
    const count = await generateSlots(generateForm)
    if (count > 0) ElMessage.success(`已生成 ${count} 个时段`) 
    else ElMessage.warning('所选时间范围的时段已经存在，没有新增时段')
    generateDialog.value = false; stallId.value = generateForm.stallId; slotDate.value = generateForm.slotDate; await load()
  } catch (e) { ElMessage.error((e as Error).message) } finally { saving.value = false }
}
function openEdit(row: PickupSlot) {
  Object.assign(editForm, { id: row.id, startTime: row.startTime.slice(0, 5), endTime: row.endTime.slice(0, 5), capacity: row.capacity, status: row.status, booked: row.booked })
  editDialog.value = true
}
async function saveEdit() {
  if (!editForm.startTime || !editForm.endTime) return ElMessage.warning('请选择开始和结束时间')
  if (editForm.capacity < editForm.booked) return ElMessage.warning('容量不能小于已预约人数')
  saving.value = true
  try {
    await updateSlot({ id: editForm.id, startTime: editForm.startTime, endTime: editForm.endTime, capacity: editForm.capacity, status: editForm.status })
    ElMessage.success('时段已更新'); editDialog.value = false; await load()
  } catch (e) { ElMessage.error((e as Error).message) } finally { saving.value = false }
}
async function toggle(row: PickupSlot) {
  try { await updateSlot({ id: row.id, startTime: row.startTime.slice(0, 5), endTime: row.endTime.slice(0, 5), capacity: row.capacity, status: row.status ? 0 : 1 }); ElMessage.success(row.status ? '时段已停用' : '时段已启用'); await load() } catch (e) { ElMessage.error((e as Error).message) }
}
async function remove(row: PickupSlot) {
  try {
    await ElMessageBox.confirm(`确认删除 ${row.startTime}-${row.endTime} 这个时段？已有预约的时段不能删除。`, '删除时段', { type: 'warning' })
    await deleteSlot(row.id); ElMessage.success('时段已删除'); await load()
  } catch (e) { if (e !== 'cancel' && e !== 'close') ElMessage.error(e instanceof Error ? e.message : '删除失败') }
}
onMounted(async () => { try { stalls.value = await listStalls(); stallId.value = stalls.value[0]?.id; await load() } catch (e) { ElMessage.error((e as Error).message) } })
</script>
<template><div class="page-card"><div class="page-header"><div><h2 class="page-title">取餐时段</h2><p class="page-subtitle">新档口会自动生成未来三天的午餐、晚餐默认时段，可在这里修改容量、时间或删除。</p></div></div><div class="page-actionbar"><div class="query-area"><el-select v-model="stallId" placeholder="选择档口" style="width:190px" @change="load"><el-option v-for="s in stalls" :key="s.id" :label="s.name" :value="s.id" /></el-select><el-date-picker v-model="slotDate" type="date" value-format="YYYY-MM-DD" :clearable="false" @change="load" /><el-button @click="load">刷新</el-button><span class="muted">共 {{ rows.length }} 个时段</span></div><div class="action-area"><el-button type="primary" @click="openGenerate">批量生成</el-button></div></div><el-table v-loading="loading" :data="rows" stripe><el-table-column prop="startTime" label="开始" width="110" align="center" /><el-table-column prop="endTime" label="结束" width="110" align="center" /><el-table-column label="余量" min-width="220"><template #default="{ row }"><el-progress :percentage="Math.round((row.remaining / row.capacity) * 100)" :stroke-width="10" /><span class="muted">{{ row.remaining }} / {{ row.capacity }}</span></template></el-table-column><el-table-column prop="booked" label="已预约" width="100" align="center" /><el-table-column label="状态" width="90" align="center"><template #default="{ row }"><el-tag :type="row.status ? 'success' : 'info'">{{ row.status ? '可用' : '停用' }}</el-tag></template></el-table-column><el-table-column label="操作" align="center" header-align="center" class-name="action-column" width="250"><template #default="{ row }"><el-button text type="primary" @click="openEdit(row)">编辑</el-button><el-button text :type="row.status ? 'warning' : 'success'" @click="toggle(row)">{{ row.status ? '停用' : '启用' }}</el-button><el-button text type="danger" :disabled="row.booked > 0" @click="remove(row)">删除</el-button></template></el-table-column></el-table>
<el-dialog v-model="generateDialog" title="批量生成时段" width="500px"><el-form label-position="top"><el-form-item label="档口"><el-select v-model="generateForm.stallId" style="width:100%"><el-option v-for="s in stalls" :key="s.id" :label="s.name" :value="s.id" /></el-select></el-form-item><el-form-item label="日期"><el-date-picker v-model="generateForm.slotDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item><el-row :gutter="12"><el-col :span="12"><el-form-item label="开始时间"><el-time-picker v-model="generateForm.startTime" value-format="HH:mm" format="HH:mm" style="width:100%" /></el-form-item></el-col><el-col :span="12"><el-form-item label="结束时间"><el-time-picker v-model="generateForm.endTime" value-format="HH:mm" format="HH:mm" style="width:100%" /></el-form-item></el-col></el-row><el-row :gutter="12"><el-col :span="12"><el-form-item label="间隔分钟"><el-input-number v-model="generateForm.intervalMinutes" :min="5" :step="5" style="width:100%" /></el-form-item></el-col><el-col :span="12"><el-form-item label="每个时段容量"><el-input-number v-model="generateForm.capacity" :min="1" style="width:100%" /></el-form-item></el-col></el-row></el-form><template #footer><el-button @click="generateDialog=false">取消</el-button><el-button type="primary" :loading="saving" @click="generate">生成</el-button></template></el-dialog>
<el-dialog v-model="editDialog" title="编辑取餐时段" width="480px"><el-form label-position="top"><el-row :gutter="12"><el-col :span="12"><el-form-item label="开始时间"><el-time-picker v-model="editForm.startTime" value-format="HH:mm" format="HH:mm" style="width:100%" /></el-form-item></el-col><el-col :span="12"><el-form-item label="结束时间"><el-time-picker v-model="editForm.endTime" value-format="HH:mm" format="HH:mm" style="width:100%" /></el-form-item></el-col></el-row><el-form-item :label="`容量（已预约 ${editForm.booked} 人）`"><el-input-number v-model="editForm.capacity" :min="Math.max(1, editForm.booked)" style="width:100%" /></el-form-item><el-form-item label="状态"><el-radio-group v-model="editForm.status"><el-radio :value="1">可用</el-radio><el-radio :value="0">停用</el-radio></el-radio-group></el-form-item></el-form><template #footer><el-button @click="editDialog=false">取消</el-button><el-button type="primary" :loading="saving" @click="saveEdit">保存</el-button></template></el-dialog></div></template>
