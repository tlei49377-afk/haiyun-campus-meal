import { reactive } from 'vue'

export const requestFeedback = reactive({
  pending: 0,
  error: '',
  traceId: '',
  visible: false
})

let hideTimer: number | undefined

export function beginRequest() {
  requestFeedback.pending += 1
}

export function endRequest() {
  requestFeedback.pending = Math.max(0, requestFeedback.pending - 1)
}

export function rememberTraceId(traceId?: string | null) {
  if (!traceId) return
  requestFeedback.traceId = traceId
  localStorage.setItem('lastTraceId', traceId)
}

export function reportError(message: string, traceId?: string | null) {
  rememberTraceId(traceId)
  requestFeedback.error = message
  requestFeedback.visible = true
  if (hideTimer) window.clearTimeout(hideTimer)
  hideTimer = window.setTimeout(() => {
    requestFeedback.visible = false
  }, 7000)
}

export function dismissError() {
  requestFeedback.visible = false
}

export function getLastTraceId() {
  return localStorage.getItem('lastTraceId') || ''
}
