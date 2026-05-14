<template>
  <div>
    <SiteHeader active-nav="/" />

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-particles">
        <div v-for="i in 15" :key="i" class="hero-particle"></div>
      </div>
      <div class="hero-content">
        <div class="hero-text">
          <div class="hero-badge">
            <span class="hero-badge-dot"></span>
            国家免疫规划信息平台
          </div>
          <h1>守护健康，<span>疫苗先行</span></h1>
          <p>在线预约 · 安全接种 · 为全生命周期健康保驾护航<br/>科学防疫，共筑免疫屏障</p>
          <div class="hero-stats">
            <div class="hero-stat">
              <h3><span class="ticker-value">10</span>万+</h3>
              <p>服务人次</p>
            </div>
            <div class="hero-stat">
              <h3><span class="ticker-value">50</span>+</h3>
              <p>疫苗种类</p>
            </div>
            <div class="hero-stat">
              <h3><span class="ticker-value">99</span>%</h3>
              <p>满意度</p>
            </div>
          </div>
        </div>
        <div class="hero-poster">
          <div class="hero-poster-bg">
            <MedicalIllustration type="shield" size="md" width="140px"/>
          </div>
          <div class="hero-poster-content">
            <div class="hero-poster-icon-wrap">
              <span class="hero-poster-icon-animated">🛡️</span>
            </div>
            <h3>全民疫苗接种</h3>
            <p>保护自己，守护家人</p>
            <span class="poster-badge">立即预约 →</span>
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
              <span class="section-title-icon">📢</span>
              <h2>疫苗防控热点资讯</h2>
              <a class="more-link" href="#">
                更多 <span class="more-arrow">→</span>
              </a>
            </div>

            <NewsCarousel />

            <div class="section-title" style="margin-top: 12px;">
              <span class="section-title-icon">✨</span>
              <h2>平台服务</h2>
            </div>

            <div class="info-grid">
              <div class="info-card-enhanced">
                <div class="info-card-icon-wrap blue">
                  <MedicalIllustration type="calendar" size="sm" width="40px"/>
                </div>
                <h3>在线预约</h3>
                <p>足不出户即可预约疫苗接种，选择就近接种点与合适时间段，方便快捷。</p>
              </div>
              <div class="info-card-enhanced">
                <div class="info-card-icon-wrap green">
                  <MedicalIllustration type="shield" size="sm" width="40px"/>
                </div>
                <h3>安全可靠</h3>
                <p>严格保护个人隐私信息，所有数据加密传输，符合国家信息安全等级保护要求。</p>
              </div>
              <div class="info-card-enhanced">
                <div class="info-card-icon-wrap purple">
                  <MedicalIllustration type="doctor" size="sm" width="40px"/>
                </div>
                <h3>接种记录</h3>
                <p>在线查看个人完整疫苗接种记录，电子凭证随时下载，入学出国必备。</p>
              </div>
              <div class="info-card-enhanced">
                <div class="info-card-icon-wrap pink">
                  <MedicalIllustration type="heartbeat" size="sm" width="60px"/>
                </div>
                <h3>智能提醒</h3>
                <p>接种前自动推送提醒通知，续种时间智能计算，不再错过任何一针疫苗。</p>
              </div>
            </div>
          </div>

          <!-- Right Column - Login Card -->
          <div class="homepage-right">
            <div class="login-card-enhanced">
              <div class="login-card-header-enhanced">
                <div class="login-header-icon">
                  <MedicalIllustration type="hospital" size="sm" width="50px"/>
                </div>
                <h2>欢迎登录</h2>
                <p>登录后即可预约疫苗接种</p>
              </div>

              <div class="login-tabs-enhanced">
                <button :class="['login-tab-enhanced', { active: activeTab === 'user-login' }]"
                        @click="activeTab = 'user-login'">
                  <span>🔑</span> 用户登录
                </button>
                <button :class="['login-tab-enhanced', { active: activeTab === 'user-register' }]"
                        @click="activeTab = 'user-register'">
                  <span>📝</span> 用户注册
                </button>
              </div>

              <!-- User Login -->
              <div :class="['login-tab-content', { active: activeTab === 'user-login' }]">
                <form @submit.prevent="handleLogin">
                  <div class="form-group-enhanced">
                    <label>用户名</label>
                    <div class="input-icon-wrap">
                      <span class="input-icon">👤</span>
                      <input v-model="loginForm.username" class="form-control input-enhanced" placeholder="请输入用户名"
                             required type="text"/>
                    </div>
                  </div>
                  <div class="form-group-enhanced">
                    <label>密码</label>
                    <div class="input-icon-wrap">
                      <span class="input-icon">🔒</span>
                      <input v-model="loginForm.password" class="form-control input-enhanced" placeholder="请输入密码"
                             required type="password"/>
                    </div>
                  </div>
                  <LoginMessage :data="loginMsgData" />
                  <div class="btn-center-wrap">
                    <button class="btn btn-shimmer btn-login-submit" type="submit">登 录</button>
                  </div>
                </form>
              </div>

              <!-- User Register -->
              <div :class="['login-tab-content', { active: activeTab === 'user-register' }]">
                <form @submit.prevent="handleRegister">
                  <div class="form-group-enhanced">
                    <label>用户名 <span class="label-required">*</span></label>
                    <input v-model="regForm.username" class="form-control input-enhanced" placeholder="请设置用户名"
                           required type="text"/>
                  </div>
                  <div class="form-group-enhanced">
                    <label>密码 <span class="label-required">*</span></label>
                    <input v-model="regForm.password" class="form-control input-enhanced"
                           placeholder="请设置密码（至少6位）"
                           required type="password" minlength="6"/>
                  </div>
                  <div class="form-group-enhanced">
                    <label>手机号 <span class="label-required">*</span></label>
                    <input v-model="regForm.phone" class="form-control input-enhanced" placeholder="请输入手机号码"
                           required type="text"/>
                  </div>
                  <div class="form-group-enhanced">
                    <label>性别</label>
                    <div class="gender-radio-group">
                      <label class="gender-radio" :class="{ active: regForm.gender === 0 }">
                        <input type="radio" v-model="regForm.gender" :value="0"/>
                        <span>未知</span>
                      </label>
                      <label class="gender-radio" :class="{ active: regForm.gender === 1 }">
                        <input type="radio" v-model="regForm.gender" :value="1"/>
                        <span>男</span>
                      </label>
                      <label class="gender-radio" :class="{ active: regForm.gender === 2 }">
                        <input type="radio" v-model="regForm.gender" :value="2"/>
                        <span>女</span>
                      </label>
                    </div>
                  </div>
                  <div class="form-group-enhanced">
                    <label>出生日期</label>
                    <input v-model="regForm.birthday" class="form-control input-enhanced" type="date"/>
                  </div>
                  <div class="form-group-enhanced">
                    <label>备注</label>
                    <textarea v-model="regForm.remark" class="form-control input-enhanced"
                              placeholder="过敏史、慢性病等健康信息（选填）"
                              rows="2" style="resize: vertical; min-height: 44px;"></textarea>
                  </div>
                  <LoginMessage :data="regMsgData" />
                  <div class="btn-center-wrap">
                    <button class="btn btn-shimmer btn-login-submit" type="submit">注 册</button>
                  </div>
                </form>
              </div>

              <div class="login-divider-enhanced">
                <span>管理员入口</span>
              </div>
              <div class="login-card-admin-link">
                <router-link to="/admin-login">
                  <span>🔐</span> 管理员登录 →
                </router-link>
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
import MedicalIllustration from '@/components/MedicalIllustration.vue'
import type {LoginData} from '@/stores/auth'
import {useAuthStore} from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)

const activeTab = ref('user-login')

const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({
  username: '',
  password: '',
  phone: '',
  gender: 0 as number,
  birthday: '',
  remark: ''
})

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
  if (!regForm.phone.trim()) {
    regMsgData.value = {error: '手机号不能为空'}
    return
  }
  const result = await auth.registerUser({
    username: regForm.username,
    password: regForm.password,
    phone: regForm.phone,
    gender: regForm.gender,
    birthday: regForm.birthday || undefined,
    remark: regForm.remark || undefined
  })
  if (!result.error) {
    regMsgData.value = null
    showAlert('注册成功！请登录', 'success')
    activeTab.value = 'user-login'
    regForm.username = ''
    regForm.password = ''
    regForm.phone = ''
    regForm.gender = 0
    regForm.birthday = ''
    regForm.remark = ''
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

<style scoped>
/* Enhanced Hero */
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  padding: 6px 18px;
  border-radius: 50px;
  font-size: 0.82rem;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 16px;
  backdrop-filter: blur(5px);
}

.hero-badge-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #4cc9f0;
  animation: pulse-glow 2s infinite;
}

.hero-poster {
  position: relative;
  flex: 0 0 340px;
  height: 300px;
  border-radius: 20px;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.04) 100%);
  border: 1px solid rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-poster-bg {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.2;
}

.hero-poster-content {
  text-align: center;
  padding: 30px;
  position: relative;
  z-index: 1;
}

.hero-poster-icon-wrap {
  margin-bottom: 14px;
}

.hero-poster-icon-animated {
  font-size: 56px;
  display: inline-block;
  animation: float-slow 3s ease-in-out infinite;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

/* Enhanced Section Title */
.section-title-icon {
  font-size: 22px;
}

.more-arrow {
  transition: transform 0.3s ease;
  display: inline-block;
}

.more-link:hover .more-arrow {
  transform: translateX(3px);
}

/* Enhanced Info Cards */
.info-card-enhanced {
  background: white;
  border-radius: var(--border-radius);
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.35s ease;
  border: 1px solid #f1f5f9;
  cursor: default;
  position: relative;
  overflow: hidden;
}

.info-card-enhanced::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
  transform: scaleX(0);
  transition: transform 0.4s ease;
}

.info-card-enhanced:hover {
  transform: translateY(-5px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.1);
  border-color: rgba(67, 97, 238, 0.12);
}

.info-card-enhanced:hover::after {
  transform: scaleX(1);
}

.info-card-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  transition: transform 0.3s ease;
}

.info-card-enhanced:hover .info-card-icon-wrap {
  transform: scale(1.1);
}

.info-card-icon-wrap.blue {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
}

.info-card-icon-wrap.green {
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
}

.info-card-icon-wrap.purple {
  background: linear-gradient(135deg, #faf5ff, #f3e8ff);
}

.info-card-icon-wrap.pink {
  background: linear-gradient(135deg, #fdf2f8, #fce7f3);
}

.info-card-enhanced h3 {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 8px;
}

.info-card-enhanced p {
  font-size: 0.85rem;
  color: var(--gray-color);
  line-height: 1.7;
}

/* Enhanced Login Card */
.login-card-enhanced {
  background: white;
  border-radius: var(--border-radius-lg);
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-card-header-enhanced {
  background: linear-gradient(135deg, #1e1b4b, #3730a3, #4361ee);
  padding: 28px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.login-card-header-enhanced::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 30% 60%, rgba(76, 201, 240, 0.15) 0%, transparent 50%),
  radial-gradient(circle at 70% 30%, rgba(114, 9, 183, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.login-header-icon {
  margin-bottom: 12px;
  display: flex;
  justify-content: center;
  position: relative;
  z-index: 1;
  opacity: 0.35;
}

.login-card-header-enhanced h2 {
  color: white;
  font-size: 1.3rem;
  font-weight: 700;
  margin: 0;
  position: relative;
  z-index: 1;
}

.login-card-header-enhanced p {
  color: rgba(255, 255, 255, 0.65);
  font-size: 0.86rem;
  margin-top: 6px;
  position: relative;
  z-index: 1;
}

.login-tabs-enhanced {
  display: flex;
  border-bottom: 2px solid #e2e8f0;
  background: #fafbfc;
}

.login-tab-enhanced {
  flex: 1;
  padding: 14px 8px;
  text-align: center;
  cursor: pointer;
  font-size: 0.88rem;
  font-weight: 600;
  color: var(--gray-color);
  border-bottom: 3px solid transparent;
  transition: all 0.25s ease;
  background: transparent;
  border-top: none;
  border-left: none;
  border-right: none;
  font-family: inherit;
}

.login-tab-enhanced:hover {
  color: var(--primary-color);
  background: rgba(67, 97, 238, 0.03);
}

.login-tab-enhanced.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
  background: linear-gradient(to bottom, rgba(67, 97, 238, 0.04), transparent);
}

.form-group-enhanced {
  margin-bottom: 18px;
}

.form-group-enhanced label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: var(--dark-color);
  font-size: 0.86rem;
}

.input-icon-wrap {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 15px;
  z-index: 1;
  pointer-events: none;
}

.input-icon-wrap .form-control {
  padding-left: 40px;
}

.login-divider-enhanced {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 0 28px 16px;
  color: var(--gray-light);
  font-size: 0.8rem;
}

.login-divider-enhanced::before,
.login-divider-enhanced::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}

.login-card-admin-link {
  text-align: center;
  padding: 0 28px 22px;
}

.login-card-admin-link a {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--gray-color);
  text-decoration: none;
  font-size: 0.85rem;
  transition: all 0.3s ease;
  padding: 8px 20px;
  border-radius: 50px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.login-card-admin-link a:hover {
  color: var(--primary-color);
  border-color: var(--primary-color);
  background: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(67, 97, 238, 0.1);
}

.label-required {
  color: #e53e3e;
  margin-left: 2px;
}

.gender-radio-group {
  display: flex;
  gap: 12px;
}

.gender-radio {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s ease;
  font-size: 0.85rem;
  color: var(--gray-color);
}

.gender-radio input[type="radio"] {
  display: none;
}

.gender-radio:hover {
  border-color: var(--primary-color);
  background: rgba(67, 97, 238, 0.04);
}

.gender-radio.active {
  border-color: var(--primary-color);
  background: rgba(67, 97, 238, 0.08);
  color: var(--primary-color);
  font-weight: 600;
}

textarea.form-control {
  font-family: inherit;
  padding-top: 10px;
}

@media (max-width: 768px) {
  .hero-poster {
    flex: none;
    width: 100%;
    max-width: 340px;
    height: 240px;
  }
}
</style>
