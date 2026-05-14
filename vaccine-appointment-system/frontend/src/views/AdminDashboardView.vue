<template>
  <div>
    <SiteHeader active-nav="/admin" />

    <main class="homepage-main">
      <div class="container">
        <AlertMessage ref="alertRef" />

        <!-- Dashboard Banner -->
        <div class="dashboard-banner-enhanced" style="margin-bottom: 28px;">
          <div class="banner-bg-decoration">
            <div class="banner-circle c1"></div>
            <div class="banner-circle c2"></div>
            <div class="banner-circle c3"></div>
          </div>
          <div class="banner-content">
            <div class="banner-text">
              <h2>👋 欢迎，{{ auth.currentUser?.username || '管理员' }}</h2>
              <p class="banner-subtitle">审核预约 · 完成接种 · 生成接种记录 · {{ today }}</p>
            </div>
            <div class="banner-stats">
              <div class="banner-stat-card">
                <div class="banner-stat-info">
                  <h3 class="ticker-value">{{ counts[0] }}</h3>
                  <p>已预约</p>
                </div>
              </div>
              <div class="banner-stat-card">
                <div class="banner-stat-info">
                  <h3 class="ticker-value">{{ counts[2] }}</h3>
                  <p>未到场</p>
                </div>
              </div>
              <div class="banner-stat-card">
                <div class="banner-stat-info">
                  <h3 class="ticker-value">{{ counts[1] }}</h3>
                  <p>已完成</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Tabs -->
        <div class="admin-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.status"
            :class="['admin-tab', { active: activeStatus === tab.status }]"
            @click="switchTab(tab.status)"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- Appointment List -->
        <div id="appointmentList">
          <div v-if="!appointments.length" class="empty-state-admin">
            <span class="empty-icon">📋</span>
            <h3>暂无{{ statusLabel(activeStatus) }}</h3>
            <p>当前没有{{ statusLabel(activeStatus) }}记录</p>
          </div>
          <div v-for="a in appointments" :key="a.id" class="appt-card">
            <div class="appt-info">
              <span class="appt-id">#{{ a.id }}</span>
              <span class="appt-user">{{ a.user?.username || '—' }}</span>
              <span class="appt-vaccine"> · {{ a.vaccine?.name || '—' }}</span>
              <span class="appt-vaccine"> · {{ specText(a.vaccine) }}</span>
              <div class="appt-time">
                🕐 {{ formatDate(a.appointmentTime) }}
                <span :class="['status-badge', statusClass(a.status)]">{{ statusLabel(a.status) }}</span>
              </div>
            </div>
            <div v-if="a.status === 0 || a.status === 2" class="appt-actions">
              <button v-if="a.status === 0" class="btn-sm btn-complete" @click="completeAppt(a.id)">
                💉 完成接种
              </button>
              <button v-if="a.status === 2" class="btn-sm btn-complete" @click="createLateRecord(a.id)">
                💉 补录接种
              </button>
              <button v-if="a.status === 0" class="btn-sm btn-cancel-admin" @click="cancelAppt(a.id)">✕ 取消</button>
            </div>
          </div>
        </div>

        <!-- Vaccination Records -->
        <div class="card" style="margin-top: 28px;">
          <h2>接种记录</h2>
          <div class="records-table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>编号</th>
                  <th>用户</th>
                  <th>疫苗名称</th>
                  <th>品牌/规格</th>
                  <th>接种时间</th>
                  <th>状态</th>
                  <th>备注</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="!records.length">
                  <td colspan="7" style="text-align:center; padding:32px; color:#94a3b8;">
                    📋 暂无接种记录
                  </td>
                </tr>
                <tr v-for="r in records" :key="r.id">
                  <td>{{ r.id }}</td>
                  <td>{{ r.user?.username || '—' }}</td>
                  <td>{{ r.vaccine?.name || '—' }}</td>
                  <td style="font-size:0.85rem;color:var(--gray-color);">{{ specText(r.vaccine) }}</td>
                  <td>{{ formatDate(r.vaccinationTime) }}</td>
                  <td>
                    <span style="padding:2px 10px;border-radius:50px;font-size:0.78rem;font-weight:600;background:#f0fdf4;color:#16a34a;">已接种</span>
                  </td>
                  <td style="font-size:0.85rem;">{{ r.notes || '—' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>

    <SiteFooter />
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const auth = useAuthStore()
const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)
const today = new Date().toLocaleDateString('zh-CN')

const tabs = [
  {status: 0, label: '已预约'},
  {status: 2, label: '未到场'},
  {status: 1, label: '已完成'},
  { status: 3, label: '已取消' }
]

const activeStatus = ref(0)
const counts = ref<Record<number, number>>({ 0: 0, 1: 0, 2: 0, 3: 0 })
const appointments = ref<any[]>([])
const records = ref<any[]>([])

const STATUS: Record<number, { text: string; cls: string }> = {
  0: {text: '已预约', cls: 'status-pending'},
  1: {text: '已完成', cls: 'status-completed'},
  2: {text: '未到场', cls: 'status-cancelled'},
  3: { text: '已取消', cls: 'status-cancelled' }
}

function statusLabel(s: number) { return STATUS[s]?.text || '未知' }
function statusClass(s: number) { return STATUS[s]?.cls || '' }

function specText(vaccine?: { brand?: string; dosage?: string }) {
  if (!vaccine) return '—'
  return [vaccine.brand, vaccine.dosage].filter(Boolean).join(' | ') || '—'
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleString('zh-CN')
}

function showAlert(msg: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(msg, type)
}

async function loadStats() {
  try {
    const response = await api.get('/appointments')
    const data = response.data
    const c: Record<number, number> = { 0: 0, 1: 0, 2: 0, 3: 0 }
    data.forEach((a: any) => { c[a.status] = (c[a.status] || 0) + 1 })
    counts.value = c
  } catch { /* silent */ }
}

async function loadAppointments(status: number) {
  try {
    const response = await api.get(`/appointments/status/${status}`)
    appointments.value = response.data
  } catch {
    showAlert('加载预约数据失败', 'error')
  }
}

async function loadRecords() {
  try {
    const response = await api.get('/vaccination-records/status/1')
    records.value = response.data
  } catch { /* silent */ }
}

function switchTab(status: number) {
  activeStatus.value = status
  loadAppointments(status)
}

async function completeAppt(id: number) {
  if (!confirm('确认完成接种？系统将自动生成接种记录。')) return
  try {
    await api.post(`/appointments/${id}/complete`)
    showAlert('接种完成！已自动生成接种记录', 'success')
    await loadStats()
    await loadAppointments(activeStatus.value)
    await loadRecords()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '完成接种失败', 'error')
  }
}

async function createLateRecord(id: number) {
  const notes = prompt('请输入补录说明（可选）：')
  if (notes === null) return
  try {
    await api.post(`/appointments/${id}/late-record`, {notes: notes || ''})
    showAlert('已补录接种记录（预约状态保持为"未到场"）', 'success')
    await loadStats()
    await loadAppointments(activeStatus.value)
    await loadRecords()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '补录失败', 'error')
  }
}

async function cancelAppt(id: number) {
  if (!confirm('确定要取消此预约吗？')) return
  try {
    await api.post(`/appointments/${id}/cancel/admin`)
    showAlert('预约已取消', 'success')
    await loadStats()
    await loadAppointments(activeStatus.value)
  } catch (error: any) {
    showAlert(error.response?.data?.error || '取消失败', 'error')
  }
}

onMounted(() => {
  if (!auth.isAdmin) {
    router.replace('/')
    return
  }
  loadStats()
  loadAppointments(0)
  loadRecords()
})
</script>
