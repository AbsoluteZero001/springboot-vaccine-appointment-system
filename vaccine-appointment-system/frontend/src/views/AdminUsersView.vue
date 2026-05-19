<template>
  <div>
    <SiteHeader active-nav="/admin/users" />

    <div class="container">
      <ModalMessage ref="alertRef"/>

      <!-- Page Banner -->
      <div class="dashboard-banner-enhanced">
        <div class="banner-bg-decoration">
          <div class="banner-circle c1"></div>
          <div class="banner-circle c2"></div>
          <div class="banner-circle c3"></div>
        </div>
        <div class="banner-content">
          <div class="banner-text">
            <h2>👥 用户管理</h2>
            <p class="banner-subtitle">用户名片 — 查看用户信息、实名状态、预约记录</p>
          </div>
          <div class="banner-stats">
            <div class="banner-stat-card">
              <span style="font-size:24px;">👤</span>
              <div class="banner-stat-info">
                <h3>{{ users.length }}</h3>
                <p>注册用户</p>
              </div>
            </div>
            <div class="banner-stat-card">
              <span style="font-size:24px;">✅</span>
              <div class="banner-stat-info">
                <h3>{{ verifiedCount }}</h3>
                <p>已实名</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Search -->
      <div style="margin-bottom:20px;">
        <input v-model="searchQuery" class="form-control" placeholder="🔍 搜索用户名、昵称、姓名、手机号或身份证号..."
               type="text" style="max-width:500px;"/>
      </div>

      <!-- Business Card Grid -->
      <div v-if="filteredUsers.length" class="card-grid">
        <div v-for="user in filteredUsers" :key="user.id" class="business-card" @click="selectedUser = user">
          <div class="bc-header">
            <div class="bc-avatar" :style="user.avatarUrl ? `background-image:url(${user.avatarUrl})` : ''">
              <span v-if="!user.avatarUrl"
                    class="bc-avatar-initial">{{
                  (user.realName || user.nickname || user.username)[0].toUpperCase()
                }}</span>
            </div>
            <div class="bc-name-section">
              <div class="bc-nickname">{{ user.realName || user.nickname || user.username }}</div>
              <div class="bc-username">@{{ user.username }}</div>
            </div>
            <div class="bc-verified" :class="user.isVerified ? 'verified' : 'unverified'">
              {{ user.isVerified ? '✓ 已实名' : '未实名' }}
            </div>
          </div>
          <div class="bc-body">
            <div class="bc-info-row" v-if="user.realName">
              <span class="bc-label">姓名</span>
              <span class="bc-value bc-realname">{{ user.realName }}</span>
            </div>
            <div class="bc-info-row" v-if="user.idCard">
              <span class="bc-label">身份证</span>
              <span class="bc-value id-card-mask">{{ maskIdCard(user.idCard) }}</span>
            </div>
            <div class="bc-info-row">
              <span class="bc-label">性别</span>
              <span class="bc-value">{{ displayGender(user) }}</span>
            </div>
            <div class="bc-info-row">
              <span class="bc-label">生日</span>
              <span class="bc-value">{{ displayBirthday(user) }}</span>
            </div>
            <div class="bc-info-row">
              <span class="bc-label">手机</span>
              <span class="bc-value">{{ user.phone || '—' }}</span>
            </div>
            <div class="bc-info-row">
              <span class="bc-label">昵称</span>
              <span class="bc-value bc-nickname-secondary">{{ user.nickname || user.username }}</span>
            </div>
            <div class="bc-info-row" v-if="user.remark">
              <span class="bc-label">备注</span>
              <span class="bc-value bc-remark">{{ user.remark }}</span>
            </div>
          </div>
          <div class="bc-footer">
            <span class="bc-status"
                  :class="user.status === 1 ? 'active' : 'disabled'">{{ user.status === 1 ? '正常' : '已停用' }}</span>
            <div class="bc-actions">
              <button class="btn btn-small" @click.stop="toggleUserStatus(user)">{{
                  user.status === 1 ? '停用' : '启用'
                }}
              </button>
              <button class="btn btn-danger btn-small" @click.stop="deleteUser(user.id)">删除</button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state-enhanced">
        <span style="font-size:48px;">📭</span>
        <h3>暂无匹配用户</h3>
        <p>尝试其他搜索关键词</p>
      </div>

      <!-- User Detail Modal -->
      <div v-if="selectedUser" :class="['modal-overlay', { active: !!selectedUser }]" @click.self="selectedUser = null">
        <div class="modal-enhanced detail-modal">
          <div class="modal-header">
            <div class="modal-header-left">
              <span class="modal-header-icon">👤</span>
              <h3>用户详情</h3>
            </div>
            <button class="modal-close" @click="selectedUser = null">✕</button>
          </div>
          <div class="modal-body">
            <div class="detail-header">
              <div class="detail-avatar"
                   :style="selectedUser.avatarUrl ? `background-image:url(${selectedUser.avatarUrl})` : ''">
                <span v-if="!selectedUser.avatarUrl">{{
                    (selectedUser.realName || selectedUser.nickname || selectedUser.username)[0].toUpperCase()
                  }}</span>
              </div>
              <div>
                <h3>{{ selectedUser.realName || selectedUser.nickname || selectedUser.username }}</h3>
                <p>@{{ selectedUser.username }} · {{ selectedUser.isVerified ? '✅ 已实名' : '⚠️ 未实名' }}</p>
              </div>
            </div>
            <div class="detail-grid">
              <div class="detail-item"><span class="detail-label">用户ID</span><span
                  class="detail-value">{{ selectedUser.id }}</span></div>
              <div class="detail-item"><span class="detail-label">真实姓名</span><span
                  class="detail-value">{{ selectedUser.realName || '—' }}</span></div>
              <div class="detail-item"><span class="detail-label">身份证号</span><span
                  class="detail-value">{{ selectedUser.idCard ? maskIdCard(selectedUser.idCard) : '—' }}</span></div>
              <div class="detail-item"><span class="detail-label">手机号</span><span
                  class="detail-value">{{ selectedUser.phone || '—' }}</span></div>
              <div class="detail-item"><span class="detail-label">性别</span><span
                  class="detail-value">{{ displayGenderDetail(selectedUser) }}</span></div>
              <div class="detail-item"><span class="detail-label">生日</span><span
                  class="detail-value">{{ displayBirthdayDetail(selectedUser) }}</span></div>
              <div class="detail-item"><span class="detail-label">状态</span><span
                  class="detail-value">{{ selectedUser.status === 1 ? '正常' : '已停用' }}</span></div>
              <div class="detail-item"><span class="detail-label">实名状态</span><span
                  class="detail-value">{{ selectedUser.isVerified ? '已认证' : '未认证' }}</span></div>
            </div>
            <div v-if="selectedUser.remark" class="detail-remark">
              <strong>备注：</strong>{{ selectedUser.remark }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <SiteFooter />
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import ModalMessage from '@/components/ModalMessage.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const auth = useAuthStore()

interface User {
  id: number
  username: string
  nickname?: string
  phone: string
  status: number
  gender?: number
  birthday?: string
  remark?: string
  avatarUrl?: string
  realName?: string
  idCard?: string
  isVerified?: number
}

const alertRef = ref<InstanceType<typeof ModalMessage> | null>(null)
const users = ref<User[]>([])
const searchQuery = ref('')
const selectedUser = ref<User | null>(null)

const filteredUsers = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return users.value
  return users.value.filter(u =>
      (u.username || '').toLowerCase().includes(q) ||
      (u.nickname || '').toLowerCase().includes(q) ||
      (u.realName || '').toLowerCase().includes(q) ||
      (u.phone || '').includes(q) ||
      (u.idCard || '').includes(q)
  )
})

const verifiedCount = computed(() => users.value.filter(u => u.isVerified === 1).length)

function showAlert(msg: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showModal(msg, type)
}

function genderLabel(g?: number): string {
  if (g === 1) return '男'
  if (g === 2) return '女'
  return '未知'
}

function calcAge(birthday?: string): string {
  if (!birthday) return '—'
  const birth = new Date(birthday)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const m = today.getMonth() - birth.getMonth()
  if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--
  return age > 0 ? `${age}岁` : '—'
}

// ── ID-card-derived fields (authoritative for verified users) ──

function idCardGenderDigit(idCard?: string): number | null {
  if (!idCard || idCard.length < 17) return null
  const ch = idCard.charAt(16)
  if (!/\d/.test(ch)) return null
  return parseInt(ch, 10)
}

function idCardDerivedGender(idCard?: string): number | null {
  const d = idCardGenderDigit(idCard)
  if (d === null) return null
  return (d % 2 === 1) ? 1 : 2
}

function idCardBirthday(idCard?: string): string | null {
  if (!idCard || idCard.length < 14) return null
  const y = idCard.substring(6, 10)
  const m = idCard.substring(10, 12)
  const d = idCard.substring(12, 14)
  if (!/^\d{8}$/.test(y + m + d)) return null
  return `${y}-${m}-${d}`
}

function displayGender(user: User): string {
  if (user.isVerified === 1 && user.idCard) {
    const g = idCardDerivedGender(user.idCard)
    if (g !== null) return genderLabel(g)
  }
  return genderLabel(user.gender)
}

function displayBirthday(user: User): string {
  if (user.isVerified === 1 && user.idCard) {
    const bday = idCardBirthday(user.idCard)
    if (bday) return `${bday} (${calcAge(bday)})`
  }
  if (user.birthday) return `${user.birthday} (${calcAge(user.birthday)})`
  return '—'
}

function displayGenderDetail(user: User): string {
  if (user.isVerified === 1 && user.idCard) {
    const g = idCardDerivedGender(user.idCard)
    if (g !== null) return genderLabel(g)
  }
  return genderLabel(user.gender)
}

function displayBirthdayDetail(user: User): string {
  if (user.isVerified === 1 && user.idCard) {
    const bday = idCardBirthday(user.idCard)
    if (bday) return bday
  }
  return user.birthday || '—'
}

function maskIdCard(idCard: string): string {
  if (!idCard || idCard.length < 8) return idCard
  return idCard.substring(0, 3) + '***********' + idCard.substring(14)
}

async function loadUsers() {
  try {
    const response = await api.get('/users')
    users.value = response.data
  } catch {
    showAlert('加载用户列表失败', 'error')
  }
}

async function toggleUserStatus(user: User) {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    await api.put(`/users/${user.id}`, {status: newStatus})
    showAlert('用户状态已更新', 'success')
    await loadUsers()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '更新失败', 'error')
  }
}

async function deleteUser(userId: number) {
  if (!confirm('确定要删除此用户吗？其所有预约记录也将被删除。')) return
  try {
    await api.delete(`/users/${userId}`)
    showAlert('用户已删除', 'success')
    selectedUser.value = null
    await loadUsers()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '删除失败', 'error')
  }
}

onMounted(() => {
  if (!auth.isAdmin) {
    router.replace('/')
    return
  }
  loadUsers()
})
</script>

<style scoped>
/* Business Card Grid */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

/* Business Card */
.business-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.business-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 36px rgba(67, 97, 238, 0.12);
  border-color: rgba(67, 97, 238, 0.2);
}

.bc-header {
  background: linear-gradient(135deg, #1e1b4b, #3730a3, #4361ee);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  position: relative;
  overflow: hidden;
}

.bc-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 80% 20%, rgba(76, 201, 240, 0.15) 0%, transparent 60%);
}

.bc-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.3);
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.bc-avatar-initial {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
}

.bc-name-section {
  flex: 1;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.bc-nickname {
  font-size: 1.05rem;
  font-weight: 700;
  color: white;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bc-username {
  font-size: 0.78rem;
  color: rgba(255, 255, 255, 0.6);
}

.bc-verified {
  padding: 3px 12px;
  border-radius: 50px;
  font-size: 0.72rem;
  font-weight: 600;
  position: relative;
  z-index: 1;
  white-space: nowrap;
}

.bc-verified.verified {
  background: rgba(34, 197, 94, 0.2);
  color: #bbf7d0;
  border: 1px solid rgba(34, 197, 94, 0.3);
}

.bc-verified.unverified {
  background: rgba(251, 191, 36, 0.2);
  color: #fde68a;
  border: 1px solid rgba(251, 191, 36, 0.3);
}

.bc-body {
  padding: 16px 20px;
}

.bc-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid #f8fafc;
}

.bc-info-row:last-child {
  border-bottom: none;
}

.bc-label {
  font-size: 0.78rem;
  color: #94a3b8;
  flex-shrink: 0;
}

.bc-value {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--dark-color);
  text-align: right;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.id-card-mask {
  font-family: monospace;
  letter-spacing: 1px;
}

.bc-remark {
  color: #64748b;
  font-size: 0.8rem;
}

.bc-realname {
  font-weight: 700;
  color: #1e1b4b;
  font-size: 0.9rem;
}

.bc-nickname-secondary {
  color: #94a3b8;
  font-size: 0.8rem;
}

.bc-footer {
  padding: 12px 20px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafbfc;
}

.bc-status {
  padding: 3px 12px;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 600;
}

.bc-status.active {
  background: #f0fdf4;
  color: #16a34a;
}

.bc-status.disabled {
  background: #fef2f2;
  color: #dc2626;
}

.bc-actions {
  display: flex;
  gap: 8px;
}

/* Detail Modal */
.detail-modal {
  max-width: 560px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.detail-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: white;
  font-weight: 700;
}

.detail-header h3 {
  font-size: 1.15rem;
  margin: 0 0 4px;
}

.detail-header p {
  font-size: 0.82rem;
  color: var(--gray-light);
  margin: 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.detail-label {
  font-size: 0.75rem;
  color: #94a3b8;
}

.detail-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--dark-color);
}

.detail-remark {
  margin-top: 16px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.6;
}

.empty-state-enhanced {
  text-align: center;
  padding: 60px 20px;
  color: var(--gray-light);
}

.empty-state-enhanced h3 {
  color: var(--gray-color);
  margin: 12px 0 6px;
}
</style>
