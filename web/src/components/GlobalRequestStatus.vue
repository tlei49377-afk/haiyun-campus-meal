<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { dismissError, requestFeedback } from '../utils/feedback'

async function copyTraceId() {
  if (!requestFeedback.traceId) return
  await navigator.clipboard.writeText(requestFeedback.traceId)
  ElMessage.success('traceId 已复制')
}
</script>

<template>
  <div v-if="requestFeedback.pending > 0" class="global-loading-bar" aria-label="正在加载" />
  <transition name="request-banner">
    <aside v-if="requestFeedback.visible" class="request-error-banner" role="alert">
      <div>
        <strong>请求未完成</strong>
        <p>{{ requestFeedback.error }}</p>
        <small>traceId：{{ requestFeedback.traceId || '服务端未返回' }}</small>
      </div>
      <div class="request-error-actions">
        <el-button v-if="requestFeedback.traceId" text size="small" @click="copyTraceId">复制</el-button>
        <el-button text size="small" @click="dismissError">关闭</el-button>
      </div>
    </aside>
  </transition>
</template>

<style scoped>
.global-loading-bar{position:fixed;z-index:5000;top:0;left:0;height:3px;background:#146b5c;box-shadow:0 0 8px rgba(20,107,92,.45);animation:loading-slide 1.2s ease-in-out infinite;width:35%}
@keyframes loading-slide{0%{transform:translateX(-100%)}50%{transform:translateX(100%)}100%{transform:translateX(300%)}}
.request-error-banner{position:fixed;z-index:5001;right:20px;top:20px;max-width:440px;background:#fff;border:1px solid #e6c9ba;border-left:4px solid #b45c32;border-radius:6px;box-shadow:0 12px 32px rgba(44,62,57,.16);padding:14px 14px 12px;display:flex;gap:12px;align-items:flex-start}
.request-error-banner strong{display:block;color:#713a22;font-size:13px}.request-error-banner p{margin:5px 0;color:#4f5f5a;font-size:12px;line-height:1.6}.request-error-banner small{color:#8b9692;font-size:10px;word-break:break-all}.request-error-actions{display:flex;flex:none}.request-banner-enter-active,.request-banner-leave-active{transition:opacity .18s ease,transform .18s ease}.request-banner-enter-from,.request-banner-leave-to{opacity:0;transform:translateY(-8px)}
@media(max-width:640px){.request-error-banner{left:10px;right:10px;top:10px;max-width:none}}
</style>
