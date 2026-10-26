<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin } from '../api/admin'
import { studentLogin, studentRegister } from '../api/app'
import { useAuthStore } from '../stores/auth'
const router = useRouter(); const auth = useAuthStore()
const mode = ref<'student' | 'admin' | 'register'>('student')
const studentNo = ref('20230001'); const password = ref('123456')
const username = ref('admin'); const adminPassword = ref('123456'); const loading = ref(false)
const registerStudentNo = ref(''); const registerName = ref(''); const registerPhone = ref(''); const registerPassword = ref(''); const registerConfirm = ref('')
async function submit() {
  loading.value = true
  try {
    if (mode.value === 'admin') {
      const data = await adminLogin({ username: username.value, password: adminPassword.value })
      auth.setSession({ token: data.token, role: 'admin', name: data.name, permissions: data.permissions })
      router.replace('/admin/dashboard')
      return
    }
    if (mode.value === 'register') {
      if (!registerStudentNo.value.trim() || !registerName.value.trim()) return ElMessage.warning('请填写学号和姓名')
      if (registerPassword.value !== registerConfirm.value) return ElMessage.warning('两次密码不一致')
      const data = await studentRegister({ studentNo: registerStudentNo.value.trim(), name: registerName.value.trim(), phone: registerPhone.value.trim() || undefined, password: registerPassword.value })
      auth.setSession({ token: data.token, role: 'student', name: data.name })
      ElMessage.success('注册成功，已自动登录')
      router.replace('/student/home')
      return
    }
    const data = await studentLogin({ studentNo: studentNo.value, password: password.value })
    auth.setSession({ token: data.token, role: 'student', name: data.name })
    router.replace('/student/home')
  } catch (e) { ElMessage.error((e as Error).message) } finally { loading.value = false }
}
</script>
<template>
  <div class="portal-page">
    <header class="portal-header">
      <div class="institution"><span class="institution-mark">HN</span><div><strong>海南热带海洋学院</strong><small>校园餐饮服务中心</small></div></div>
      <span class="service-label">校内服务 · 预约取餐</span>
    </header>

    <main class="portal-main">
      <section class="service-panel">
        <p class="panel-kicker">校园餐饮服务 · 预约取餐</p>
        <h1>海韵食刻</h1>
        <p class="panel-lead">校园食堂预约取餐平台</p>
        <div class="service-rule"></div>
        <div class="service-info">
          <div><span>供餐时间</span><strong>06:30 — 20:30</strong></div>
          <div><span>预约方式</span><strong>按食堂、档口与取餐时段预约</strong></div>
          <div><span>取餐凭证</span><strong>订单生成 6 位取餐码</strong></div>
        </div>
        <div class="flow-line"><span>选食堂</span><i></i><span>选档口</span><i></i><span>约时段</span><i></i><span>凭码取餐</span></div>
        <p class="panel-note">请按预约时段前往对应档口，减少高峰排队。若无法到店，请在订单页及时取消。</p>
      </section>

      <section class="login-panel">
        <div class="login-panel-head"><span class="login-index">01</span><div><h2>账号登录</h2><p>学生与食堂运营人员使用各自入口</p></div></div>
        <div class="login-tabs"><button :class="{ active: mode === 'student' }" @click="mode = 'student'">学生登录</button><button :class="{ active: mode === 'register' }" @click="mode = 'register'">学生注册</button><button :class="{ active: mode === 'admin' }" @click="mode = 'admin'">运营入口</button></div>
        <el-form label-position="top" class="login-form" @submit.prevent="submit">
          <template v-if="mode === 'student'"><el-form-item label="学号"><el-input v-model="studentNo" size="large" placeholder="请输入学号" /></el-form-item><el-form-item label="密码"><el-input v-model="password" type="password" show-password size="large" placeholder="请输入密码" @keyup.enter="submit" /></el-form-item></template>
          <template v-else-if="mode === 'register'"><el-form-item label="学号"><el-input v-model="registerStudentNo" size="large" placeholder="请输入学号" /></el-form-item><el-form-item label="姓名"><el-input v-model="registerName" size="large" placeholder="请输入姓名" /></el-form-item><el-form-item label="手机号（选填）"><el-input v-model="registerPhone" size="large" placeholder="请输入手机号" /></el-form-item><el-form-item label="密码"><el-input v-model="registerPassword" type="password" show-password size="large" placeholder="至少 6 位" /></el-form-item><el-form-item label="确认密码"><el-input v-model="registerConfirm" type="password" show-password size="large" placeholder="再次输入密码" @keyup.enter="submit" /></el-form-item></template>
          <template v-else><el-form-item label="运营账号"><el-input v-model="username" size="large" placeholder="请输入运营账号" /></el-form-item><el-form-item label="密码"><el-input v-model="adminPassword" type="password" show-password size="large" placeholder="请输入密码" @keyup.enter="submit" /></el-form-item></template>
          <el-button type="primary" size="large" native-type="submit" :loading="loading" class="login-submit">{{ mode === 'register' ? '注册并进入' : '登录系统' }}</el-button>
        </el-form>
        <div v-if="mode !== 'register'" class="demo-line"><span>演示账号</span><p>学生：20230001 / 123456</p><p>运营：admin / 123456</p></div><div v-else class="demo-line register-note"><span>注册说明</span><p>学号必须唯一，密码至少 6 位。</p></div>
        <p class="security-note">账号仅用于校园餐饮服务，请勿转借他人。</p>
      </section>
    </main>

    <footer class="portal-footer"><span>海南热带海洋学院 · 校园餐饮服务中心</span><span>服务电话：校内后勤服务台</span></footer>
  </div>
</template>
<style scoped>
.portal-page{min-height:100vh;background:#f5f3ed;background-image:linear-gradient(#e6e8e1 1px,transparent 1px),linear-gradient(90deg,#e6e8e1 1px,transparent 1px);background-size:44px 44px;color:#24312e;display:flex;flex-direction:column}.portal-header{height:78px;padding:0 clamp(24px,6vw,86px);display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #dce2dd;background:rgba(250,249,245,.94)}.institution{display:flex;align-items:center;gap:12px}.institution-mark{display:grid;place-items:center;width:38px;height:38px;border-radius:4px;background:#146b5c;color:#fff;font-weight:800;letter-spacing:.04em}.institution strong,.institution small{display:block}.institution strong{font-size:15px;letter-spacing:.04em}.institution small{font-size:11px;color:#7a8782;margin-top:3px;letter-spacing:.08em}.service-label{font-size:12px;color:#5d6c67;border:1px solid #ccd8d2;padding:7px 11px;background:#fff}.portal-main{flex:1;width:min(1120px,calc(100% - 48px));margin:0 auto;padding:clamp(44px,8vh,92px) 0;display:grid;grid-template-columns:minmax(0,1.25fr) minmax(340px,.75fr);gap:clamp(36px,7vw,104px);align-items:center}.panel-kicker{margin:0 0 16px;color:#a15c32;font-size:11px;letter-spacing:.22em;font-weight:700}.service-panel h1{font-size:clamp(42px,5vw,70px);font-weight:800;letter-spacing:.06em;line-height:1;margin:0;color:#123f37}.panel-lead{margin:14px 0 0;color:#4d5d58;font-size:19px;letter-spacing:.04em}.service-rule{width:64px;height:3px;background:#b56a35;margin:30px 0 28px}.service-info{display:grid;gap:16px}.service-info div{display:grid;grid-template-columns:86px 1fr;gap:14px;align-items:baseline;border-bottom:1px solid #dfe4df;padding-bottom:13px}.service-info span{font-size:12px;color:#7a8782}.service-info strong{font-size:14px;font-weight:600;color:#2d403b}.flow-line{display:flex;align-items:center;gap:9px;flex-wrap:wrap;margin:26px 0 0;color:#146b5c;font-size:12px;font-weight:700}.flow-line i{width:20px;height:1px;background:#9bb5ad;display:block}.panel-note{max-width:500px;margin:24px 0 0;color:#77847f;font-size:12px;line-height:1.9}.login-panel{background:#fff;border:1px solid #d9e1dc;border-top:4px solid #146b5c;padding:28px 28px 24px;box-shadow:0 18px 45px rgba(31,57,49,.08)}.login-panel-head{display:flex;gap:12px;align-items:flex-start;margin-bottom:22px}.login-index{font-size:12px;color:#b56a35;font-weight:800;padding-top:4px}.login-panel h2{font-size:21px;margin:0;color:#20332e}.login-panel-head p{font-size:12px;color:#7b8884;margin:6px 0 0}.login-tabs{display:flex;flex-wrap:wrap;border-bottom:1px solid #e1e7e3;margin-bottom:22px}.login-tabs button{position:relative;border:0;background:transparent;color:#7a8782;padding:10px 13px 12px 0;margin-right:13px;cursor:pointer;font-weight:600}.login-tabs button.active{color:#146b5c}.login-tabs button.active:after{content:"";position:absolute;left:0;right:13px;bottom:-1px;height:2px;background:#146b5c}.login-submit{width:100%;height:44px;margin-top:6px;letter-spacing:.08em}.demo-line{display:grid;grid-template-columns:70px 1fr;gap:3px 8px;margin-top:22px;padding-top:16px;border-top:1px dashed #dce3df;font-size:11px;color:#73817d}.demo-line span{grid-row:span 2;color:#52635e;font-weight:700}.demo-line p{margin:0}.security-note{margin:16px 0 0;color:#9aa5a1;font-size:11px;line-height:1.6}.portal-footer{height:52px;padding:0 clamp(24px,6vw,86px);display:flex;justify-content:space-between;align-items:center;border-top:1px solid #dce2dd;color:#87938f;font-size:11px;background:rgba(250,249,245,.92)}@media(max-width:800px){.portal-header{height:auto;padding:16px 18px;align-items:flex-start;gap:12px}.service-label{font-size:10px}.portal-main{display:block;width:calc(100% - 28px);padding:34px 0 44px}.service-panel{margin-bottom:30px}.service-panel h1{font-size:46px}.panel-lead{font-size:17px}.service-rule{margin:22px 0}.flow-line{gap:7px}.login-panel{padding:22px 18px}.portal-footer{padding:12px 18px;display:block;line-height:1.8}.portal-footer span{display:block}}
</style>
