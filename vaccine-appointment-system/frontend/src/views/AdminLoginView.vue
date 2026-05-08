<template>
  <div>
    <SiteHeader active-nav="/admin-login" />

    <main class="admin-login-page">
      <div class="admin-login-wrapper">
        <div class="admin-login-card">
          <div class="admin-login-card-header">
            <div class="admin-icon">🔐</div>
            <h2>管理员登录</h2>
            <p>疫苗预约系统 · 后台管理</p>
          </div>
          <div class="admin-login-card-body">
            <form @submit.prevent="handleLogin">
              <div class="form-group">
                <label>管理员账号</label>
                <input v-model="username" autofocus class="form-control" placeholder="请输入管理员账号" required type="text" />
              </div>
              <div class="form-group">
                <label>密码</label>
                <input v-model="password" class="form-control" placeholder="请输入密码" required type="password" />
              </div>
              <LoginMessage :data="loginMsgData" />
              <button class="btn" type="submit">管理员登录</button>
            </form>
            <div class="admin-login-footer-links">
              <router-link to="/">← 返回用户首页</router-link>
            </div>
          </div>
        </div>

        <div class="admin-login-back">
          <router-link to="/">← 返回疫苗预约系统首页</router-link>
        </div>
      </div>
    </main>

    <SiteFooter />

    <AlertMessage ref="alertRef" />
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import LoginMessage from '@/components/LoginMessage.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import type {LoginData} from '@/stores/auth'
import {useAuthStore} from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)

const username = ref('')
const password = ref('')
const loginMsgData = ref<LoginData | null>(null)

function showAlert(message: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(message, type)
}

async function handleLogin() {
  const result = await auth.loginAdmin(username.value, password.value)
  if (!result.error && !result.frozen) {
    loginMsgData.value = null
    showAlert('管理员登录成功！正在跳转...', 'success')
    setTimeout(() => router.push('/admin'), 800)
  } else {
    loginMsgData.value = result
  }
}

onMounted(() => {
  if (auth.isAdmin) {
    router.replace('/admin')
  }
})
</script>
