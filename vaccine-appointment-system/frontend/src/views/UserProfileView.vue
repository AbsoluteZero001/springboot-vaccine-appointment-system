<template>
  <div>
    <SiteHeader active-nav="/profile" />

    <div class="container">
      <AlertMessage ref="alertRef" />

      <div class="dashboard-banner" style="margin-bottom: 28px;">
        <div>
          <h2>我的预约</h2>
          <p>管理您的疫苗接种预约和接种记录</p>
        </div>
        <div class="banner-stats">
          <div class="banner-stat">
            <h3>{{ pendingCount }}</h3>
            <p>待处理</p>
          </div>
          <div class="banner-stat">
            <h3>{{ completedCount }}</h3>
            <p>已完成</p>
          </div>
        </div>
      </div>

      <!-- Appointments -->
      <div class="card">
        <h2>预约记录</h2>
        <table>
          <thead>
            <tr>
              <th>编号</th>
              <th>疫苗名称</th>
              <th>品牌/规格</th>
              <th>预约时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="appt in appointments" :key="appt.id">
              <td>{{ appt.id }}</td>
              <td>{{ appt.vaccine?.name || '—' }}</td>
              <td style="font-size:0.85rem; color: var(--gray-color);">{{ specText(appt.vaccine) }}</td>
              <td>{{ formatDate(appt.appointmentTime) }}</td>
              <td>
                <span :style="statusStyle(appt.status)">{{ statusLabel(appt.status) }}</span>
              </td>
              <td>
                <button
                  v-if="appt.status === 0 || appt.status === 1"
                  class="btn btn-danger btn-small"
                  @click="cancelAppointment(appt.id)"
                >
                  取消预约
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Vaccination Records -->
      <div class="card">
        <h2>接种记录</h2>
        <table>
          <thead>
            <tr>
              <th>编号</th>
              <th>疫苗名称</th>
              <th>品牌/规格</th>
              <th>接种时间</th>
              <th>状态</th>
              <th>备注</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="rec in records" :key="rec.id">
              <td>{{ rec.id }}</td>
              <td>{{ rec.vaccine?.name || '—' }}</td>
              <td style="font-size:0.85rem; color: var(--gray-color);">{{ specText(rec.vaccine) }}</td>
              <td>{{ formatDate(rec.vaccinationTime) }}</td>
              <td>
                <span :style="rec.status === 0 ? 'padding:3px 12px;border-radius:50px;font-size:0.8rem;font-weight:600;background:#fff7ed;color:#c2410c;' : 'padding:3px 12px;border-radius:50px;font-size:0.8rem;font-weight:600;background:#f0fdf4;color:#16a34a;'">
                  {{ rec.status === 0 ? '已预约' : '已接种' }}
                </span>
              </td>
              <td>{{ rec.notes || '—' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <SiteFooter />
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const auth = useAuthStore()
const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)

interface Appointment {
  id: number
  status: number
  appointmentTime: string
  vaccine?: { name?: string; brand?: string; dosage?: string }
}

interface VaccinationRecord {
  id: number
  status: number
  vaccinationTime: string
  notes?: string
  vaccine?: { name?: string; brand?: string; dosage?: string }
}

const appointments = ref<Appointment[]>([])
const records = ref<VaccinationRecord[]>([])
const pendingCount = ref(0)
const completedCount = ref(0)

function showAlert(message: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(message, type)
}

function specText(vaccine?: { brand?: string; dosage?: string }) {
  if (!vaccine) return '—'
  return [vaccine.brand, vaccine.dosage].filter(Boolean).join(' | ') || '—'
}

const STATUS_LABELS: Record<number, string> = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
const STATUS_BG: Record<number, string> = { 0: '#fff7ed', 1: '#f0fdf4', 2: '#eff6ff', 3: '#f5f5f5' }
const STATUS_COLOR: Record<number, string> = { 0: '#c2410c', 1: '#16a34a', 2: '#2563eb', 3: '#6b7280' }

function statusLabel(status: number) {
  return STATUS_LABELS[status] || '未知'
}

function statusStyle(status: number) {
  return `padding:3px 12px; border-radius:50px; font-size:0.8rem; font-weight:600; background:${STATUS_BG[status] || '#f5f5f5'}; color:${STATUS_COLOR[status] || '#6b7280'};`
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleString('zh-CN')
}

async function loadAppointments() {
  try {
    const response = await api.get(`/appointments/user/${auth.currentUser!.id}`)
    const data = response.data
    appointments.value = data
    let pending = 0, completed = 0
    data.forEach((a: Appointment) => {
      if (a.status === 0 || a.status === 1) pending++
      if (a.status === 2) completed++
    })
    pendingCount.value = pending
    completedCount.value = completed
  } catch {
    showAlert('加载预约记录失败', 'error')
  }
}

async function loadRecords() {
  try {
    const response = await api.get(`/vaccination-records/user/${auth.currentUser!.id}`)
    records.value = response.data
  } catch {
    // Ignore if no records
  }
}

async function cancelAppointment(appointmentId: number) {
  if (!confirm('确定要取消此预约吗？')) return
  try {
    await api.post(`/appointments/${appointmentId}/cancel`, {
      userId: auth.currentUser!.id
    })
    showAlert('预约已取消', 'success')
    await loadAppointments()
  } catch (error: any) {
    const msg = error.response?.data?.error || '取消失败'
    showAlert(msg, 'error')
  }
}

onMounted(() => {
  loadAppointments()
  loadRecords()
})
</script>
