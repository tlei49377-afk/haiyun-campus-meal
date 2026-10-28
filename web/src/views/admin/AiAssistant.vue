<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotRound, Delete, Plus } from '@element-plus/icons-vue'
import { createChatSession, deleteChatSession, getChatSessionMessages, listChatSessions } from '../../api/admin'
import type { ChatSession } from '../../api/types'
import { beginRequest, endRequest, rememberTraceId, reportError } from '../../utils/feedback'

interface ViewMessage { role: 'user' | 'assistant'; text: string }
const sessions = ref<ChatSession[]>([])
const sessionId = ref('')
const input = ref('')
const messages = ref<ViewMessage[]>([])
const loadingSessions = ref(false)
const loadingMessages = ref(false)
const sending = ref(false)
const box = ref<HTMLElement>()
const activeTitle = computed(() => sessions.value.find(item => item.memoryId === sessionId.value)?.title || '新会话')
const welcome = '你好，我可以查询食堂、档口、菜品和订单，也可以协助处理受控写操作。'

function fillMessages(list: Array<{ role: 'user' | 'ai'; content: string }>) {
  messages.value = list.map(item => ({ role: item.role === 'ai' ? 'assistant' : 'user', text: item.content }))
  if (!messages.value.length) messages.value = [{ role: 'assistant', text: welcome }]
}
async function loadSessions() {
  loadingSessions.value = true
  try {
    sessions.value = await listChatSessions()
    if (!sessionId.value && sessions.value[0]) await selectSession(sessions.value[0])
  } catch (error) { ElMessage.error((error as Error).message) }
  finally { loadingSessions.value = false }
}
async function createSession() {
  try { sessionId.value = await createChatSession(); messages.value = [{ role: 'assistant', text: welcome }] }
  catch (error) { ElMessage.error((error as Error).message) }
}
async function selectSession(session: ChatSession) {
  sessionId.value = session.memoryId
  loadingMessages.value = true
  try { fillMessages(await getChatSessionMessages(session.memoryId)) }
  catch (error) { ElMessage.error((error as Error).message) }
  finally { loadingMessages.value = false }
}
async function removeSession(session: ChatSession) {
  try {
    await ElMessageBox.confirm(`确认删除会话“${session.title}”？`, '删除会话', { type: 'warning' })
    await deleteChatSession(session.memoryId)
    sessions.value = sessions.value.filter(item => item.memoryId !== session.memoryId)
    if (sessionId.value === session.memoryId) {
      sessionId.value = ''
      messages.value = []
      if (sessions.value[0]) await selectSession(sessions.value[0])
      else await createSession()
    }
    ElMessage.success('会话已删除')
  } catch (error) { if (error !== 'cancel' && error !== 'close') ElMessage.error((error as Error).message) }
}
async function send() {
  const text = input.value.trim()
  if (!text || sending.value) return
  if (!sessionId.value) await createSession()
  if (!sessionId.value) return
  const writeIntent = /(新增|添加|修改|编辑|删除|上架|下架|起售|停售|启用|停用|取消|调整|改价)/.test(text)
  if (writeIntent) {
    try { await ElMessageBox.confirm('检测到可能修改业务数据的请求。确认发送并允许 AI 进入写操作确认流程？', '写操作确认', { type: 'warning', confirmButtonText: '确认发送' }) }
    catch { return }
  }
  input.value = ''
  messages.value.push({ role: 'user', text })
  sending.value = true
  beginRequest()
  try {
    const token = localStorage.getItem('token') || ''
    const response = await fetch(`/api/admin/chat?memoryId=${encodeURIComponent(sessionId.value)}&memory=${encodeURIComponent(text)}`, {
      headers: { token, 'X-Trace-Id': `web-${crypto.randomUUID().slice(0, 12)}` }
    })
    rememberTraceId(response.headers.get('x-trace-id'))
    if (response.status === 401) { localStorage.clear(); window.location.href = '/login'; throw new Error('登录状态已失效') }
    if (!response.ok || !response.body) throw new Error('AI 服务暂不可用')
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let answer = ''
    messages.value.push({ role: 'assistant', text: '' })
    const current = messages.value[messages.value.length - 1]
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      answer += decoder.decode(value, { stream: true })
      current.text = answer
      await nextTick()
      box.value?.scrollTo({ top: box.value.scrollHeight })
    }
    await loadSessions()
  } catch (error) {
    const message = (error as Error).message
    reportError(message)
    ElMessage.error(message)
    messages.value.push({ role: 'assistant', text: '这次请求没有完成，请检查 AI 配置或稍后重试。' })
  } finally { endRequest(); sending.value = false }
}
onMounted(async () => { await loadSessions(); if (!sessionId.value) await createSession() })
</script>

<template>
  <div class="ai-page">
    <aside class="session-panel page-card">
      <div class="session-head"><strong>历史会话</strong><el-button type="primary" size="small" :icon="Plus" @click="createSession">新建</el-button></div>
      <div v-loading="loadingSessions" class="session-list">
        <div v-for="session in sessions" :key="session.memoryId" :class="['session-item', { active: session.memoryId === sessionId }]" role="button" tabindex="0" @click="selectSession(session)" @keyup.enter="selectSession(session)">
          <el-icon><ChatDotRound /></el-icon>
          <span><strong>{{ session.title }}</strong><small>{{ new Date(session.lastActiveTime).toLocaleString('zh-CN') }}</small></span>
          <el-button text type="danger" :icon="Delete" aria-label="删除会话" @click.stop="removeSession(session)" />
        </div>
        <div v-if="!sessions.length && !loadingSessions" class="empty">暂无历史会话</div>
      </div>
    </aside>
    <section class="page-card chat-panel">
      <div class="page-header"><div><h2 class="page-title">运营 AI 助手</h2><p class="page-subtitle">当前会话：{{ activeTitle }}。涉及写操作时，系统会再次要求确认。</p></div></div>
      <div ref="box" v-loading="loadingMessages" class="chat-box">
        <div v-for="(message, index) in messages" :key="index" :class="['bubble', message.role]">{{ message.text }}</div>
        <div v-if="sending" class="bubble assistant">正在整理业务数据…</div>
      </div>
      <div class="chat-input"><el-input v-model="input" type="textarea" :rows="2" placeholder="例如：帮我查一下今天待接单的订单" @keyup.ctrl.enter="send" /><el-button type="primary" :loading="sending" @click="send">发送</el-button></div>
      <p class="muted">快捷键：Ctrl + Enter 发送。涉及修改、删除、启停等操作时会先弹出确认。</p>
    </section>
  </div>
</template>

<style scoped>
.ai-page{display:grid;grid-template-columns:260px minmax(0,1fr);gap:14px;min-height:calc(100vh - 108px)}.session-panel{padding:14px;display:flex;flex-direction:column;min-height:0}.session-head{display:flex;align-items:center;justify-content:space-between;margin-bottom:12px}.session-head strong{font-size:14px}.session-list{display:grid;align-content:start;gap:6px;overflow:auto;max-height:calc(100vh - 190px)}.session-item{display:flex;align-items:center;gap:8px;border:1px solid #e2e9e5;background:#fff;padding:9px;text-align:left;cursor:pointer;border-radius:4px}.session-item.active{border-color:#7fb3a8;background:#edf6f2}.session-item span{min-width:0;flex:1}.session-item strong,.session-item small{display:block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.session-item strong{font-size:12px;color:#344842}.session-item small{margin-top:4px;font-size:10px;color:#8b9692}.chat-panel{display:flex;flex-direction:column;min-height:0}.chat-box{flex:1;min-height:360px;max-height:58vh;overflow:auto;padding:10px;background:#f6faf8;border-radius:6px}.bubble{max-width:78%;padding:11px 14px;border-radius:10px;margin:8px 0;line-height:1.65;white-space:pre-wrap}.bubble.assistant{background:#fff;border:1px solid #e2ece7;color:#334b46}.bubble.user{background:#146b5c;color:#fff;margin-left:auto}.chat-input{display:flex;gap:10px;margin-top:14px;align-items:flex-end}.chat-input .el-input{flex:1}@media(max-width:900px){.ai-page{grid-template-columns:1fr}.session-list{max-height:220px;grid-template-columns:1fr 1fr}.chat-box{max-height:none}}
</style>
