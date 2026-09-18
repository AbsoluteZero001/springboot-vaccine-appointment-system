<template>
  <div>
    <SiteHeader active-nav="/admin/statistics"/>

    <main class="homepage-main">
      <div class="container">
        <ModalMessage ref="modalRef"/>

        <div class="dashboard-banner-enhanced">
          <div class="banner-bg-decoration">
            <div class="banner-circle c1"></div>
            <div class="banner-circle c2"></div>
            <div class="banner-circle c3"></div>
          </div>
          <div class="banner-content">
            <div class="banner-text"><h2>📊 数据统计</h2>
              <p class="banner-subtitle">预约系统全局数据概览</p></div>
          </div>
        </div>

        <!-- Overview Cards -->
        <div class="stats-overview-grid">
          <div class="stat-card">
            <div class="stat-card-icon" style="background: linear-gradient(135deg, #667eea, #764ba2);">
              <span>📋</span>
            </div>
            <div class="stat-card-body">
              <h3>{{ stats.totalAppointments || 0 }}</h3>
              <p>总预约数</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-card-icon" style="background: linear-gradient(135deg, #f59e0b, #f8961e);">
              <span>⏳</span>
            </div>
            <div class="stat-card-body">
              <h3>{{ stats.appointedCount || 0 }}</h3>
              <p>待处理</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-card-icon" style="background: linear-gradient(135deg, #16a34a, #22c55e);">
              <span>✅</span>
            </div>
            <div class="stat-card-body">
              <h3>{{ stats.completedCount || 0 }}</h3>
              <p>已完成</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-card-icon" style="background: linear-gradient(135deg, #dc2626, #f72585);">
              <span>❌</span>
            </div>
            <div class="stat-card-body">
              <h3>{{ stats.noShowCount || 0 }}</h3>
              <p>未到场</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-card-icon" style="background: linear-gradient(135deg, #6b7280, #94a3b8);">
              <span>🚫</span>
            </div>
            <div class="stat-card-body">
              <h3>{{ stats.cancelledCount || 0 }}</h3>
              <p>已取消</p>
            </div>
          </div>
        </div>

        <!-- Rate Cards -->
        <div class="rate-grid">
          <div class="rate-card">
            <h3>接种成功率</h3>
            <div class="rate-circle-wrapper">
              <svg viewBox="0 0 120 120" class="rate-circle">
                <circle cx="60" cy="60" r="52" fill="none" stroke="#e2e8f0" stroke-width="10"/>
                <circle cx="60" cy="60" r="52" fill="none" stroke="url(#grad-success)" stroke-width="10"
                        :stroke-dasharray="circumference" :stroke-dashoffset="successRateOffset"
                        stroke-linecap="round" transform="rotate(-90 60 60)" class="rate-arc"/>
                <text x="60" y="55" text-anchor="middle" class="rate-value">
                  {{ (stats.appointmentSuccessRate || 0).toFixed(1) }}%
                </text>
                <text x="60" y="72" text-anchor="middle" class="rate-label">成功率</text>
              </svg>
              <defs>
                <linearGradient id="grad-success" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" style="stop-color:#16a34a;stop-opacity:1"/>
                  <stop offset="100%" style="stop-color:#22c55e;stop-opacity:1"/>
                </linearGradient>
              </defs>
            </div>
          </div>
          <div class="rate-card">
            <h3>接种率</h3>
            <div class="rate-circle-wrapper">
              <svg viewBox="0 0 120 120" class="rate-circle">
                <circle cx="60" cy="60" r="52" fill="none" stroke="#e2e8f0" stroke-width="10"/>
                <circle cx="60" cy="60" r="52" fill="none" stroke="url(#grad-vax)" stroke-width="10"
                        :stroke-dasharray="circumference" :stroke-dashoffset="vaxRateOffset"
                        stroke-linecap="round" transform="rotate(-90 60 60)" class="rate-arc"/>
                <text x="60" y="55" text-anchor="middle" class="rate-value">{{
                    (stats.vaccinationRate || 0).toFixed(1)
                  }}%
                </text>
                <text x="60" y="72" text-anchor="middle" class="rate-label">接种率</text>
              </svg>
              <defs>
                <linearGradient id="grad-vax" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" style="stop-color:#4361ee;stop-opacity:1"/>
                  <stop offset="100%" style="stop-color:#4cc9f0;stop-opacity:1"/>
                </linearGradient>
              </defs>
            </div>
          </div>
        </div>

        <!-- Bar Chart (Status Distribution) -->
        <div class="chart-card">
          <h3>预约状态分布</h3>
          <div class="bar-chart">
            <div class="bar-item" v-for="bar in barData" :key="bar.label">
              <div class="bar-label">
                <span>{{ bar.label }}</span>
                <span class="bar-count">{{ bar.value }}</span>
              </div>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: bar.percent + '%', background: bar.color }">
                  <span class="bar-percent-text">{{ bar.percent }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Raw Stats Table -->
        <div class="card" style="margin-top:20px;">
          <h3 style="font-size:1rem;">统计数据明细</h3>
          <table>
            <thead>
            <tr>
              <th>指标</th>
              <th>数值</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td>总预约数</td>
              <td><strong>{{ stats.totalAppointments || 0 }}</strong></td>
            </tr>
            <tr>
              <td>待处理预约</td>
              <td><strong style="color:#f59e0b;">{{ stats.appointedCount || 0 }}</strong></td>
            </tr>
            <tr>
              <td>已完成预约</td>
              <td><strong style="color:#16a34a;">{{ stats.completedCount || 0 }}</strong></td>
            </tr>
            <tr>
              <td>未到场</td>
              <td><strong style="color:#dc2626;">{{ stats.noShowCount || 0 }}</strong></td>
            </tr>
            <tr>
              <td>已取消</td>
              <td><strong style="color:#6b7280;">{{ stats.cancelledCount || 0 }}</strong></td>
            </tr>
            <tr>
              <td>接种成功率</td>
              <td><strong style="color:#16a34a;">{{ (stats.appointmentSuccessRate || 0).toFixed(1) }}%</strong></td>
            </tr>
            <tr>
              <td>接种率</td>
              <td><strong style="color:#4361ee;">{{ (stats.vaccinationRate || 0).toFixed(1) }}%</strong></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </main>

    <SiteFooter/>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import ModalMessage from '@/components/ModalMessage.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const auth = useAuthStore()
const modalRef = ref(null)
const stats = ref({})
const circumference = 2 * Math.PI * 52 // ~327

const successRateOffset = computed(() => {
  const rate = stats.value.appointmentSuccessRate || 0
  return circumference - (circumference * rate / 100)
})
const vaxRateOffset = computed(() => {
  const rate = stats.value.vaccinationRate || 0
  return circumference - (circumference * rate / 100)
})
const maxBar = computed(() => Math.max(
    stats.value.appointedCount || 0, stats.value.completedCount || 0,
    stats.value.noShowCount || 0, stats.value.cancelledCount || 0, 1
))
const barData = computed(() => {
  const total = stats.value.totalAppointments || 1
  return [
    {
      label: '待处理',
      value: stats.value.appointedCount || 0,
      percent: ((stats.value.appointedCount || 0) / total * 100).toFixed(1),
      color: 'linear-gradient(90deg, #f59e0b, #f8961e)'
    },
    {
      label: '已完成',
      value: stats.value.completedCount || 0,
      percent: ((stats.value.completedCount || 0) / total * 100).toFixed(1),
      color: 'linear-gradient(90deg, #16a34a, #22c55e)'
    },
    {
      label: '未到场',
      value: stats.value.noShowCount || 0,
      percent: ((stats.value.noShowCount || 0) / total * 100).toFixed(1),
      color: 'linear-gradient(90deg, #dc2626, #f72585)'
    },
    {
      label: '已取消',
      value: stats.value.cancelledCount || 0,
      percent: ((stats.value.cancelledCount || 0) / total * 100).toFixed(1),
      color: 'linear-gradient(90deg, #6b7280, #94a3b8)'
    },
  ]
})

function showAlert(msg, type = 'success') {
  modalRef.value?.showModal(msg, type)
}

async function loadStats() {
  try {
    const res = await api.get('/statistics')
    stats.value = res.data
  } catch {
    showAlert('加载统计数据失败', 'error')
  }
}

onMounted(() => {
  if (!auth.isAdmin) {
    router.replace('/');
    return
  }
  loadStats()
})
</script>

<style scoped>
.stats-overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: var(--border-radius);
  padding: 20px;
  box-shadow: var(--box-shadow);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: var(--transition);
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--box-shadow-hover);
}

.stat-card-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

.stat-card-body h3 {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--dark-color);
  line-height: 1.2;
}

.stat-card-body p {
  font-size: 0.82rem;
  color: var(--gray-light);
  margin: 0;
}

.rate-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.rate-card {
  background: white;
  border-radius: var(--border-radius);
  padding: 24px;
  box-shadow: var(--box-shadow);
  text-align: center;
}

.rate-card h3 {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 16px;
}

.rate-circle-wrapper {
  display: flex;
  justify-content: center;
}

.rate-circle {
  width: 160px;
  height: 160px;
}

.rate-arc {
  transition: stroke-dashoffset 1s ease;
}

.rate-value {
  font-size: 1.2rem;
  font-weight: 800;
  fill: var(--dark-color);
}

.rate-label {
  font-size: 0.75rem;
  fill: var(--gray-light);
}

.chart-card {
  background: white;
  border-radius: var(--border-radius);
  padding: 24px;
  box-shadow: var(--box-shadow);
}

.chart-card h3 {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 20px;
}

.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.bar-label {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: var(--dark-color);
  font-weight: 500;
}

.bar-count {
  color: var(--gray-light);
}

.bar-track {
  width: 100%;
  height: 28px;
  background: #f1f5f9;
  border-radius: 14px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 12px;
  transition: width 0.6s ease;
  min-width: 40px;
}

.bar-percent-text {
  font-size: 0.78rem;
  font-weight: 700;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .rate-grid {
    grid-template-columns: 1fr;
  }

  .stats-overview-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>
