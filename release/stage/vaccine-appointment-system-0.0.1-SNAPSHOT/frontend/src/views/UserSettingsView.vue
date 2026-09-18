<template>
  <div>
    <SiteHeader active-nav="/settings"/>

    <div class="container">
      <ModalMessage ref="modalRef"/>

      <!-- Banner -->
      <div class="dashboard-banner-enhanced" style="margin-bottom: 28px;">
        <div class="banner-bg-decoration">
          <div class="banner-circle c1"></div>
          <div class="banner-circle c2"></div>
        </div>
        <div class="banner-content">
          <div class="banner-text">
            <h2>👤 个人资料</h2>
            <p class="banner-subtitle">管理您的头像、昵称、实名认证等个人信息</p>
          </div>
        </div>
      </div>

      <div class="settings-layout">
        <!-- Left: Avatar & Verification -->
        <div class="settings-sidebar">
          <div class="card card-avatar-section">
            <div class="avatar-upload">
              <div
                  class="avatar-preview"
                  :style="avatarPreview ? `background-image:url(${avatarPreview})` : ''"
                  @click="triggerAvatarUpload"
                  title="点击更换头像"
              >
                <span v-if="!avatarPreview" class="avatar-initial">{{
                    (userForm.nickname || auth.currentUser?.username || '?')[0].toUpperCase()
                  }}</span>
                <div class="avatar-overlay">
                  <span>📷</span>
                </div>
              </div>
              <input ref="avatarInputRef" type="file" accept="image/*" style="display:none"
                     @change="handleAvatarChange"/>
              <p class="avatar-hint">点击头像更换</p>
            </div>
          </div>

          <!-- Real-name verification -->
          <div class="card" v-if="auth.currentUser?.isVerified !== 1">
            <h3>🛡️ 实名认证</h3>
            <div class="verify-notice">
              <span class="notice-icon">🔒</span>
              <div>
                <strong>请先完成实名认证</strong>
                <p>根据《疫苗管理法》规定，接种疫苗须实名登记。您提交的信息仅用于正规医疗机构疫苗接种登记，不会对外泄露。</p>
              </div>
            </div>
            <form @submit.prevent="submitVerify">
              <div class="form-group">
                <label>真实姓名 <span class="label-required">*</span></label>
                <input v-model="verifyForm.realName" class="form-control input-enhanced" placeholder="请输入真实姓名"
                       required type="text" maxlength="50"/>
              </div>
              <div class="form-group">
                <label>身份证号码 <span class="label-required">*</span></label>
                <input v-model="verifyForm.idCard" class="form-control input-enhanced"
                       placeholder="请输入18位身份证号码" required type="text" maxlength="18"/>
                <p class="verify-hint">格式：前6位地区码 + 8位出生日期 + 3位顺序码 + 1位校验码</p>
              </div>
              <button class="btn btn-shimmer" type="submit" style="width:100%;">提交认证</button>
            </form>
          </div>
          <div class="card" v-else>
            <h3>🛡️ 实名认证</h3>
            <div style="display:flex;align-items:center;gap:10px;padding:12px;background:#f0fdf4;border-radius:10px;">
              <span style="font-size:28px;">✅</span>
              <div>
                <strong style="color:#16a34a;">已实名认证</strong>
                <p style="font-size:0.82rem;color:#64748b;margin:2px 0 0;">{{ auth.currentUser.realName }} ·
                  {{ maskIdCard(auth.currentUser.idCard || '') }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Right: Profile form -->
        <div class="settings-main">
          <div class="card">
            <h3>编辑资料</h3>
            <form @submit.prevent="saveProfile">
              <!-- Username modification -->
              <div class="form-group">
                <label>用户名 <span class="label-required">*</span></label>
                <div class="input-with-button">
                  <input v-model="usernameForm.newUsername" class="form-control input-enhanced"
                         placeholder="修改用户名" type="text" maxlength="50"
                         :disabled="!canChangeUsername"/>
                  <button type="button" class="btn btn-small"
                          :disabled="!canChangeUsername || !usernameForm.newUsername.trim()"
                          @click="saveUsername">
                    {{ canChangeUsername ? '修改' : '冷却中' }}
                  </button>
                </div>
                <p class="field-hint">
                  ⚠️ 用户名是识别账户的唯一身份，每年仅可修改一次。
                  <span v-if="!canChangeUsername && usernameCooldownDays > 0">
                    还需等待 <strong>{{ usernameCooldownDays }}</strong> 天。
                  </span>
                  <span v-else-if="auth.currentUser?.lastUsernameChangeTime">
                    下次可修改日期：<strong>{{ nextChangeDate }}</strong>
                  </span>
                </p>
              </div>
              <div class="form-group">
                <label>昵称 <span class="label-required">*</span></label>
                <input v-model="userForm.nickname" class="form-control input-enhanced" placeholder="设置您的昵称"
                       required type="text" maxlength="50"/>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label>性别</label>
                  <select v-model.number="userForm.gender" class="form-control input-enhanced">
                    <option :value="0">未知</option>
                    <option :value="1">男</option>
                    <option :value="2">女</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>生日</label>
                  <input v-model="userForm.birthday" class="form-control input-enhanced" type="date"/>
                </div>
              </div>
              <div class="form-group">
                <label>手机号 <span class="label-required">*</span></label>
                <input v-model="userForm.phone" class="form-control input-enhanced" placeholder="请输入手机号" required
                       type="text"/>
              </div>
              <div class="form-group">
                <label>备注</label>
                <textarea v-model="userForm.remark" class="form-control input-enhanced" placeholder="备注信息（选填）"
                          rows="3" maxlength="500"></textarea>
              </div>
              <button class="btn btn-shimmer" type="submit" style="width:100%;">💾 保存修改</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <SiteFooter/>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import ModalMessage from '@/components/ModalMessage.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const auth = useAuthStore()
const modalRef = ref(null)
const avatarInputRef = ref(null)

const avatarPreview = ref('')
const userForm = reactive({
  nickname: '',
  gender: 0,
  birthday: '',
  phone: '',
  remark: ''
})

const verifyForm = reactive({
  realName: '',
  idCard: ''
})

const usernameForm = reactive({
  newUsername: ''
})

const ONE_YEAR_MS = 365 * 24 * 60 * 60 * 1000

const canChangeUsername = computed(() => {
  const last = auth.currentUser?.lastUsernameChangeTime
  if (!last) return true
  const elapsed = Date.now() - new Date(last).getTime()
  return elapsed >= ONE_YEAR_MS
})

const nextChangeDate = computed(() => {
  const last = auth.currentUser?.lastUsernameChangeTime
  if (!last) return ''
  const next = new Date(new Date(last).getTime() + ONE_YEAR_MS)
  return next.toLocaleDateString('zh-CN')
})

const usernameCooldownDays = computed(() => {
  const last = auth.currentUser?.lastUsernameChangeTime
  if (!last) return 0
  const elapsed = Date.now() - new Date(last).getTime()
  if (elapsed >= ONE_YEAR_MS) return 0
  return Math.ceil((ONE_YEAR_MS - elapsed) / (24 * 60 * 60 * 1000))
})

function showAlert(message, type = 'success') {
  modalRef.value?.showModal(message, type)
}

function maskIdCard(idCard) {
  if (!idCard || idCard.length < 8) return idCard
  return idCard.substring(0, 3) + '***********' + idCard.substring(14)
}

// ── Avatar ──────────────────────────────────────────────────

function triggerAvatarUpload() {
  avatarInputRef.value?.click()
}

async function handleAvatarChange(event) {
  const target = event.target
  const file = target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    showAlert('请选择图片文件', 'error')
    return
  }

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await api.post(`/users/${auth.currentUser.id}/avatar`, formData, {
      headers: {'Content-Type': 'multipart/form-data'}
    })
    avatarPreview.value = response.data.avatarUrl
    if (auth.currentUser) {
      auth.currentUser.avatarUrl = response.data.avatarUrl
      localStorage.setItem('user', JSON.stringify(auth.currentUser))
    }
    showAlert('头像上传成功')
  } catch (error) {
    showAlert(error.response?.data?.error || '上传失败', 'error')
  } finally {
    target.value = ''
  }
}

// ── Profile ─────────────────────────────────────────────────

function loadProfile() {
  const u = auth.currentUser
  if (!u) return
  usernameForm.newUsername = u.username || ''
  userForm.nickname = u.nickname || u.username || ''
  userForm.gender = u.gender || 0
  userForm.birthday = u.birthday || ''
  userForm.phone = u.phone || ''
  userForm.remark = u.remark || ''
  avatarPreview.value = u.avatarUrl || ''
}

async function saveProfile() {
  if (!userForm.nickname.trim()) {
    showAlert('昵称不能为空', 'error')
    return
  }
  if (!userForm.phone.trim()) {
    showAlert('手机号不能为空', 'error')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(userForm.phone.trim())) {
    showAlert('手机号格式不正确', 'error')
    return
  }
  try {
    const response = await api.put(`/users/${auth.currentUser.id}/profile`, {
      nickname: userForm.nickname.trim(),
      gender: userForm.gender,
      birthday: userForm.birthday || null,
      phone: userForm.phone.trim(),
      remark: userForm.remark.trim()
    })
    // Update auth store
    if (auth.currentUser) {
      const u = auth.currentUser
      u.nickname = userForm.nickname.trim()
      u.gender = userForm.gender
      u.birthday = userForm.birthday
      u.phone = userForm.phone.trim()
      u.remark = userForm.remark.trim()
      localStorage.setItem('user', JSON.stringify(u))
    }
    showAlert('资料已保存')
  } catch (error) {
    showAlert(error.response?.data?.error || '保存失败', 'error')
  }
}

// ── Username ─────────────────────────────────────────────────

async function saveUsername() {
  const name = usernameForm.newUsername.trim()
  if (!name) {
    showAlert('请输入新用户名', 'error')
    return
  }
  if (name.length < 3) {
    showAlert('用户名至少需要3个字符', 'error')
    return
  }
  try {
    const response = await api.put(`/users/${auth.currentUser.id}/username`, {
      username: name
    })
    if (auth.currentUser) {
      auth.currentUser.username = name
      auth.currentUser.lastUsernameChangeTime = response.data.lastUsernameChangeTime
      localStorage.setItem('user', JSON.stringify(auth.currentUser))
    }
    showAlert('用户名修改成功（每年仅可修改一次）')
    usernameForm.newUsername = ''
  } catch (error) {
    showAlert(error.response?.data?.error || '修改失败', 'error')
  }
}

// ── Real-name verification ───────────────────────────────────

async function submitVerify() {
  if (!verifyForm.realName.trim() || !verifyForm.idCard.trim()) {
    showAlert('请填写完整的实名信息', 'error')
    return
  }
  try {
    const response = await api.post(`/users/${auth.currentUser.id}/verify`, {
      realName: verifyForm.realName.trim(),
      idCard: verifyForm.idCard.trim()
    })
    if (auth.currentUser) {
      auth.currentUser.isVerified = 1
      auth.currentUser.realName = verifyForm.realName.trim()
      auth.currentUser.idCard = verifyForm.idCard.trim()
      if (response.data.gender != null) {
        auth.currentUser.gender = response.data.gender
      }
      localStorage.setItem('user', JSON.stringify(auth.currentUser))
    }
    showAlert('实名认证成功！现在可以预约疫苗了')
    verifyForm.realName = ''
    verifyForm.idCard = ''
  } catch (error) {
    showAlert(error.response?.data?.error || '实名认证失败', 'error')
  }
}

onMounted(() => {
  if (!auth.isUser) {
    router.replace('/')
    return
  }
  loadProfile()
})
</script>

<style scoped>
.settings-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
}

@media (max-width: 768px) {
  .settings-layout {
    grid-template-columns: 1fr;
  }
}

/* Avatar */
.card-avatar-section {
  text-align: center;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-preview {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4361ee, #7209b7);
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
  box-shadow: 0 6px 24px rgba(67, 97, 238, 0.25);
}

.avatar-preview:hover {
  transform: scale(1.05);
}

.avatar-initial {
  font-size: 2.8rem;
  font-weight: 700;
  color: white;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-overlay span {
  font-size: 1.5rem;
}

.avatar-preview:hover .avatar-overlay {
  opacity: 1;
}

.avatar-hint {
  font-size: 0.8rem;
  color: #94a3b8;
  margin: 0;
}

/* Form */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 480px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--dark-color);
}

.label-required {
  color: #e53e3e;
  margin-left: 2px;
}

/* Verify */
.verify-notice {
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
  font-size: 0.82rem;
  color: #92400e;
  line-height: 1.5;
}

.verify-notice strong {
  display: block;
  margin-bottom: 4px;
  color: #78350f;
}

.verify-notice .notice-icon {
  font-size: 22px;
  flex-shrink: 0;
  margin-top: 2px;
}

.verify-hint {
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 4px;
}

.input-with-button {
  display: flex;
  gap: 8px;
}

.input-with-button input {
  flex: 1;
}

.input-with-button input:disabled {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}

.input-with-button .btn-small {
  padding: 8px 16px;
  font-size: 0.85rem;
  white-space: nowrap;
  flex-shrink: 0;
}

.field-hint {
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 6px;
  line-height: 1.5;
}

.field-hint strong {
  color: #e53e3e;
}
</style>
