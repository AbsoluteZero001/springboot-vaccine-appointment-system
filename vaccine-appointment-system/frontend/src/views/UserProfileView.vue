<template>
  <div>
    <SiteHeader active-nav="/profile" />

    <div class="container">
      <ModalMessage ref="modalRef"/>

      <div class="dashboard-banner-enhanced" style="margin-bottom: 28px;">
        <div class="banner-bg-decoration">
          <div class="banner-circle c1"></div>
          <div class="banner-circle c2"></div>
        </div>
        <div class="banner-content">
          <div class="banner-text">
            <h2>📋 我的预约</h2>
            <p class="banner-subtitle">管理您的疫苗接种预约和接种记录</p>
          </div>
          <div class="banner-stats">
            <div class="banner-stat-card">
              <div class="banner-stat-info">
                <h3 class="ticker-value">{{ pendingCount }}</h3>
                <p>待处理</p>
              </div>
            </div>
            <div class="banner-stat-card">
              <div class="banner-stat-info">
                <h3 class="ticker-value">{{ completedCount }}</h3>
                <p>已完成</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Appointments -->
      <div class="card">
        <h2>预约记录</h2>
        <div class="records-table-wrapper">
        <table>
          <thead>
            <tr>
              <th>编号</th>
              <th>疫苗名称</th>
              <th>品牌/规格</th>
              <th>价格</th>
              <th>预约时间</th>
              <th style="min-width:100px;">支付</th>
              <th style="min-width:90px;">状态</th>
              <th style="min-width:80px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="appt in appointments" :key="appt.id">
              <td>{{ appt.id }}</td>
              <td>{{ appt.vaccine?.name || '—' }}</td>
              <td style="font-size:0.85rem; color: var(--gray-color);">{{ specText(appt.vaccine) }}</td>
              <td style="font-weight:600; color:#dc2626;">
                {{ appt.vaccine?.price != null ? '¥' + Number(appt.vaccine.price).toFixed(0) : '免费' }}
              </td>
              <td>{{ formatDate(appt.appointmentTime) }}</td>
              <td style="text-align:center;">
                <span v-if="appt.paymentStatus === 1"
                      style="display:inline-block;padding:5px 16px;border-radius:50px;font-size:0.82rem;font-weight:600;background:#f0fdf4;color:#16a34a;">已支付</span>
                <button v-else-if="appt.status === 0" class="btn btn-small btn-pay" @click="payAppointment(appt)"
                        style="padding:5px 16px;">
                  去支付
                </button>
                <span v-else style="color:#94a3b8;font-size:0.82rem;">—</span>
              </td>
              <td style="text-align:center;">
                <span :style="statusStyle(appt.status)">{{ statusLabel(appt.status) }}</span>
              </td>
              <td>
                <button v-if="appt.status === 0" class="btn btn-danger btn-small" @click="cancelAppointment(appt.id)">
                  取消
                </button>
                <span v-else-if="appt.remark" style="font-size:0.78rem;color:#64748b;">{{ appt.remark }}</span>
              </td>
            </tr>
          </tbody>
        </table>
        </div>
      </div>

      <!-- Vaccination Records -->
      <div class="card">
        <h2>💉 接种记录</h2>
        <div class="records-table-wrapper">
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
                  {{ rec.status === 0 ? '已安排' : '已接种' }}
                </span>
              </td>
              <td>{{ rec.notes || '—' }}</td>
            </tr>
          </tbody>
        </table>
        </div>
      </div>
    </div>

    <SiteFooter />
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import ModalMessage from '@/components/ModalMessage.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const auth = useAuthStore()
const modalRef = ref(null)

const appointments = ref([])
const records = ref([])
const pendingCount = ref(0)
const completedCount = ref(0)

function showAlert(message, type = 'success') {
  modalRef.value?.showModal(message, type)
}

function specText(vaccine) {
  if (!vaccine) return '—'
  return [vaccine.brand, vaccine.dosage].filter(Boolean).join(' | ') || '—'
}

const STATUS_LABELS = {0: '已预约', 1: '已完成', 2: '未到场', 3: '已取消'}
const STATUS_BG = {0: '#fff7ed', 1: '#f0fdf4', 2: '#fee2e2', 3: '#f5f5f5'}
const STATUS_COLOR = {0: '#c2410c', 1: '#16a34a', 2: '#dc2626', 3: '#6b7280'}

function statusLabel(status) {
  return STATUS_LABELS[status] || '未知'
}
function statusStyle(status) {
  return `display:inline-block;padding:5px 18px; border-radius:50px; font-size:0.82rem; font-weight:600; background:${STATUS_BG[status] || '#f5f5f5'}; color:${STATUS_COLOR[status] || '#6b7280'};`
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleString('zh-CN')
}

async function loadAppointments() {
  try {
    const response = await api.get(`/appointments/user/${auth.currentUser.id}`)
    const data = response.data
    appointments.value = data
    let pending = 0, completed = 0
    data.forEach((a) => {
      if (a.status === 0) pending++
      if (a.status === 1) completed++
    })
    pendingCount.value = pending
    completedCount.value = completed
  } catch {
    showAlert('加载预约记录失败', 'error')
  }
}

async function loadRecords() {
  try {
    const response = await api.get(`/vaccination-records/user/${auth.currentUser.id}`)
    records.value = response.data
  } catch { /* silent */
  }
}

async function cancelAppointment(appointmentId) {
  if (!confirm('确定要取消此预约吗？')) return
  try {
    await api.post(`/appointments/${appointmentId}/cancel`, {userId: auth.currentUser.id})
    showAlert('预约已取消', 'success')
    await loadAppointments()
  } catch (error) {
    showAlert(error.response?.data?.error || '取消失败', 'error')
  }
}

async function payAppointment(appt) {
  if (!confirm(`确认支付 ¥${appt.vaccine?.price != null ? Number(appt.vaccine.price).toFixed(0) : '0'} ？`)) return
  try {
    await api.post(`/appointments/${appt.id}/pay`, {userId: auth.currentUser.id, remark: ''})
    showAlert('支付成功！', 'success')
    await loadAppointments()
  } catch (error) {
    showAlert(error.response?.data?.error || '支付失败', 'error')
  }
}

onMounted(() => {
  if (!auth.isUser) {
    router.replace('/')
    return
  }
  loadAppointments()
  loadRecords()
})
</script>

<style scoped>
.btn-pay {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: white;
  border: none;
}

.btn-pay:hover {
  background: linear-gradient(135deg, #15803d, #16a34a);
}
</style>
