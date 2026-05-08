<template>
  <div>
    <SiteHeader active-nav="/" />

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-particles">
        <div v-for="i in 10" :key="i" class="hero-particle"></div>
      </div>
      <div class="hero-content">
        <div class="hero-text">
          <h1>守护健康，<span>疫苗先行</span></h1>
          <p>国家免疫规划信息平台 · 在线预约 · 安全接种<br />为全生命周期健康保驾护航</p>
          <div class="hero-stats">
            <div class="hero-stat">
              <h3>10万+</h3>
              <p>服务人次</p>
            </div>
            <div class="hero-stat">
              <h3>50+</h3>
              <p>疫苗种类</p>
            </div>
            <div class="hero-stat">
              <h3>99%</h3>
              <p>满意度</p>
            </div>
          </div>
        </div>
        <div class="hero-poster">
          <div class="hero-poster-content">
            <span class="hero-poster-icon">🛡️</span>
            <h3>全民疫苗接种</h3>
            <p>保护自己，守护家人</p>
            <span class="poster-badge">立即预约</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <main class="homepage-main">
      <div class="container">
        <div class="homepage-layout">
          <!-- Left Column -->
          <div class="homepage-left">
            <div class="section-title">
              <span class="icon">📢</span>
              <h2>疫苗防控热点资讯</h2>
              <a class="more-link" href="#">更多 →</a>
            </div>

            <NewsCarousel />

            <div class="section-title" style="margin-top: 8px;">
              <span class="icon">✨</span>
              <h2>平台服务</h2>
            </div>

            <div class="info-grid">
              <div class="info-card">
                <div class="info-card-icon blue">📋</div>
                <h3>在线预约</h3>
                <p>足不出户即可预约疫苗接种，选择就近接种点与合适时间段，方便快捷。</p>
              </div>
              <div class="info-card">
                <div class="info-card-icon green">🔒</div>
                <h3>安全可靠</h3>
                <p>严格保护个人隐私信息，所有数据加密传输，符合国家信息安全等级保护要求。</p>
              </div>
              <div class="info-card">
                <div class="info-card-icon purple">📊</div>
                <h3>接种记录</h3>
                <p>在线查看个人完整疫苗接种记录，电子凭证随时下载，入学出国必备。</p>
              </div>
              <div class="info-card">
                <div class="info-card-icon pink">⏰</div>
                <h3>智能提醒</h3>
                <p>接种前自动推送提醒通知，续种时间智能计算，不再错过任何一针疫苗。</p>
              </div>
            </div>
          </div>

          <!-- Right Column - Login Card -->
          <div class="homepage-right">
            <div class="login-card">
              <div class="login-card-header">
                <h2>欢迎登录</h2>
                <p>登录后即可预约疫苗接种</p>
              </div>

              <div class="login-tabs">
                <button :class="['login-tab', { active: activeTab === 'user-login' }]" @click="activeTab = 'user-login'">
                  用户登录
                </button>
                <button :class="['login-tab', { active: activeTab === 'user-register' }]" @click="activeTab = 'user-register'">
                  用户注册
                </button>
              </div>

              <!-- User Login -->
              <div :class="['login-tab-content', { active: activeTab === 'user-login' }]">
                <form @submit.prevent="handleLogin">
                  <div class="form-group">
                    <label>用户名</label>
                    <input v-model="loginForm.username" class="form-control" placeholder="请输入用户名" required type="text" />
                  </div>
                  <div class="form-group">
                    <label>密码</label>
                    <input v-model="loginForm.password" class="form-control" placeholder="请输入密码" required type="password" />
                  </div>
                  <LoginMessage :data="loginMsgData" />
                  <button class="btn" type="submit">登 录</button>
                </form>
              </div>

              <!-- User Register -->
              <div :class="['login-tab-content', { active: activeTab === 'user-register' }]">
                <form @submit.prevent="handleRegister">
                  <div class="form-group">
                    <label>用户名</label>
                    <input v-model="regForm.username" class="form-control" placeholder="请设置用户名" required type="text" />
                  </div>
                  <div class="form-group">
                    <label>密码</label>
                    <input v-model="regForm.password" class="form-control" placeholder="请设置密码" required type="password" />
                  </div>
                  <div class="form-group">
                    <label>邮箱</label>
                    <input v-model="regForm.email" class="form-control" placeholder="请输入邮箱地址" required type="email" />
                  </div>
                  <div class="form-group">
                    <label>手机号（选填）</label>
                    <input v-model="regForm.phone" class="form-control" placeholder="请输入手机号码" type="text" />
                  </div>
                  <LoginMessage :data="regMsgData" />
                  <button class="btn" type="submit">注 册</button>
                </form>
              </div>

              <div class="login-divider">— 管理员入口 —</div>
              <div class="login-card-admin-link">
                <router-link to="/admin-login">🔐 管理员登录 →</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <SiteFooter />

    <!-- Alert container for inline alerts -->
    <AlertMessage ref="alertRef" />
  </div>
</template>

<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import NewsCarousel from '@/components/NewsCarousel.vue'
import LoginMessage from '@/components/LoginMessage.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import type {LoginData} from '@/stores/auth'
import {useAuthStore} from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)

const activeTab = ref('user-login')

const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', password: '', email: '', phone: '' })

const loginMsgData = ref<LoginData | null>(null)
const regMsgData = ref<LoginData | null>(null)

function showAlert(message: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(message, type)
}

async function handleLogin() {
  const result = await auth.loginUser(loginForm.username, loginForm.password)
  if (!result.error && !result.frozen) {
    loginMsgData.value = null
    showAlert('登录成功！')
    setTimeout(() => router.push('/dashboard'), 1000)
  } else {
    loginMsgData.value = result
  }
}

async function handleRegister() {
  const result = await auth.registerUser({
    username: regForm.username,
    password: regForm.password,
    email: regForm.email,
    phone: regForm.phone || undefined
  })
  if (!result.error) {
    regMsgData.value = null
    showAlert('注册成功！请登录', 'success')
    activeTab.value = 'user-login'
    regForm.username = ''
    regForm.password = ''
    regForm.email = ''
    regForm.phone = ''
  } else {
    regMsgData.value = result
  }
}

onMounted(() => {
  if (auth.isUser) {
    router.replace('/dashboard')
  }
})
</script>
