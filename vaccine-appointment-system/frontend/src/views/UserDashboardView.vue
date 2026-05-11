<template>
  <div>
    <SiteHeader active-nav="/dashboard" />

    <main class="homepage-main">
      <div class="container">
        <AlertMessage ref="alertRef" />

        <!-- Welcome Banner -->
        <div class="dashboard-banner-enhanced">
          <div class="banner-bg-decoration">
            <div class="banner-circle c1"></div>
            <div class="banner-circle c2"></div>
            <div class="banner-circle c3"></div>
          </div>
          <div class="banner-content">
            <div class="banner-text">
              <h2>
                <span class="banner-greeting">👋 欢迎回来，</span>
                <span class="banner-username">{{ auth.currentUser?.username || '用户' }}</span>
              </h2>
              <p class="banner-subtitle">
                <span class="banner-date">📅 {{ today }}</span>
                <span class="banner-divider">|</span>
                <span>选择疫苗，在线预约接种时间，守护健康每一天</span>
              </p>
            </div>
            <div class="banner-stats">
              <div class="banner-stat-card">
                <div class="banner-stat-icon vaccine-icon">
                  <MedicalIllustration type="vaccine" size="sm" width="40px"/>
                </div>
                <div class="banner-stat-info">
                  <h3 class="ticker-value">{{ allVaccines.length }}</h3>
                  <p>可用疫苗</p>
                </div>
              </div>
              <div class="banner-stat-card">
                <div class="banner-stat-icon category-icon">
                  <MedicalIllustration type="capsule" size="sm" width="40px"/>
                </div>
                <div class="banner-stat-info">
                  <h3 class="ticker-value">{{ categories.size }}</h3>
                  <p>疫苗种类</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Filter Bar -->
        <div class="filter-bar-enhanced">
          <div class="filter-tabs">
            <button
                :class="['filter-tab-enhanced', { active: activeCategory === 'all' }]"
              @click="filterByCategory('all')"
            >
              <span class="filter-tab-icon">🏥</span>
              全部疫苗
            </button>
            <button
              v-for="cat in categories"
              :key="cat"
              :class="['filter-tab-enhanced', { active: activeCategory === cat }]"
              @click="filterByCategory(cat)"
            >
              <span class="filter-tab-icon">{{ getCategoryIcon(cat) }}</span>
              {{ cat }}
            </button>
          </div>
          <div class="search-box-enhanced">
            <span class="search-icon">🔍</span>
            <input v-model="searchKeyword" placeholder="搜索疫苗名称、品牌或疾病..." type="text"
                   @input="filterVaccines"/>
            <button v-if="searchKeyword" class="search-clear" @click="searchKeyword = ''">✕</button>
          </div>
        </div>

        <!-- Skeleton Loading -->
        <div v-if="isLoading" class="vaccine-grid-modern">
          <div v-for="i in 6" :key="'sk-'+i" class="skeleton-card" :style="{ animationDelay: `${i * 0.08}s` }">
            <div class="skeleton-card-header">
              <div class="skeleton skeleton-badge"></div>
              <div class="skeleton skeleton-badge" style="width:50px"></div>
            </div>
            <div class="skeleton-card-body">
              <div class="skeleton skeleton-title"></div>
              <div class="skeleton skeleton-subtitle"></div>
              <div class="skeleton-specs">
                <div class="skeleton skeleton-spec"></div>
                <div class="skeleton skeleton-spec"></div>
                <div class="skeleton skeleton-spec"></div>
                <div class="skeleton skeleton-spec"></div>
              </div>
              <div class="skeleton skeleton-desc"></div>
            </div>
            <div class="skeleton-card-footer">
              <div class="skeleton skeleton-stock"></div>
              <div class="skeleton skeleton-btn"></div>
            </div>
          </div>
        </div>

        <!-- Vaccine Grid -->
        <div v-else-if="filteredVaccines.length" class="vaccine-grid-modern">
          <VaccineCard
              v-for="(vaccine, index) in filteredVaccines"
            :key="vaccine.id"
            :vaccine="vaccine"
              :stagger-index="index"
            @book="openBooking"
          />
        </div>
        <div v-else class="empty-state-enhanced">
          <MedicalIllustration type="search" size="md" width="120px"/>
          <h3>未找到匹配的疫苗</h3>
          <p>请尝试其他搜索关键词或筛选条件</p>
          <button class="btn btn-small" @click="resetFilters">重置筛选</button>
        </div>
      </div>
    </main>

    <!-- Appointment Modal -->
    <AppointmentModal
      :vaccine="selectedVaccine"
      :visible="showModal"
      @booked="handleBooking"
      @close="showModal = false"
    />

    <SiteFooter />
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import MedicalIllustration from '@/components/MedicalIllustration.vue'
import type {Vaccine} from '@/components/VaccineCard.vue'
import VaccineCard from '@/components/VaccineCard.vue'
import AppointmentModal from '@/components/AppointmentModal.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const auth = useAuthStore()
const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)

const today = new Date().toLocaleDateString('zh-CN', {year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'})
const allVaccines = ref<Vaccine[]>([])
const activeCategory = ref('all')
const searchKeyword = ref('')
const showModal = ref(false)
const selectedVaccine = ref<Vaccine | null>(null)
const isLoading = ref(true)

const categories = computed(() => {
  const cats = new Set(allVaccines.value.map(v => v.category).filter(Boolean))
  return cats as Set<string>
})

const filteredVaccines = computed(() => {
  let result = allVaccines.value
  if (activeCategory.value !== 'all') {
    result = result.filter(v => v.category === activeCategory.value)
  }
  const kw = searchKeyword.value.trim().toLowerCase()
  if (kw) {
    result = result.filter(v =>
      (v.name || '').toLowerCase().includes(kw) ||
      (v.brand || '').toLowerCase().includes(kw) ||
      (v.targetDisease || '').toLowerCase().includes(kw)
    )
  }
  return result
})

function filterByCategory(cat: string) {
  activeCategory.value = cat
}

function filterVaccines() {
  // handled reactively via computed
}

function resetFilters() {
  activeCategory.value = 'all'
  searchKeyword.value = ''
}

function getCategoryIcon(cat: string): string {
  const icons: Record<string, string> = {
    '新冠疫苗': '🦠',
    '流感疫苗': '🌡️',
    'HPV疫苗': '🎗️',
    '儿童疫苗': '👶',
    '成人疫苗': '💪',
    '老年疫苗': '🧓',
  }
  return icons[cat] || '💉'
}

function showAlert(message: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(message, type)
}

async function loadVaccines() {
  isLoading.value = true
  try {
    const response = await api.get('/vaccines/available')
    allVaccines.value = response.data
  } catch {
    showAlert('加载疫苗数据失败', 'error')
  } finally {
    // brief delay to show skeleton animation
    setTimeout(() => {
      isLoading.value = false
    }, 400)
  }
}

function openBooking(vaccine: Vaccine) {
  selectedVaccine.value = vaccine
  showModal.value = true
}

async function handleBooking(appointmentTime: string) {
  if (!selectedVaccine.value) return

  const date = new Date(appointmentTime)
  if (date.getDay() === 0) {
    showAlert('周日休息，请选择工作日预约', 'error')
    return
  }

  try {
    await api.post('/appointments', {
      userId: auth.currentUser!.id,
      vaccineId: selectedVaccine.value.id,
      appointmentTime
    })
    showAlert('预约成功！请按时前往接种', 'success')
    showModal.value = false
    await loadVaccines()
  } catch (error: any) {
    const msg = error.response?.data?.error || error.response?.data?.message || '预约失败，请稍后重试'
    showAlert(msg, 'error')
  }
}

onMounted(() => {
  loadVaccines()
})
</script>

<style scoped>
/* Enhanced Dashboard Banner */
.dashboard-banner-enhanced {
  background: linear-gradient(135deg, #1e1b4b 0%, #312e81 30%, #4338ca 60%, #4361ee 100%);
  border-radius: var(--border-radius-lg);
  padding: 32px 36px;
  margin-bottom: 28px;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(67, 97, 238, 0.25);
}

.banner-bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.banner-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
  background: white;
}

.banner-circle.c1 {
  width: 200px;
  height: 200px;
  top: -60px;
  right: -40px;
}

.banner-circle.c2 {
  width: 120px;
  height: 120px;
  bottom: -30px;
  right: 30%;
}

.banner-circle.c3 {
  width: 80px;
  height: 80px;
  top: 20px;
  left: 40%;
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  position: relative;
  z-index: 1;
  flex-wrap: wrap;
}

.banner-text {
  flex: 1;
  min-width: 0;
}

.banner-greeting {
  font-size: 1rem;
  font-weight: 400;
  opacity: 0.85;
}

.banner-username {
  background: linear-gradient(135deg, #4cc9f0, #f0abfc);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: 800;
}

.banner-text h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: white;
  margin-bottom: 8px;
}

.banner-subtitle {
  color: rgba(255, 255, 255, 0.65);
  font-size: 0.88rem;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.banner-divider {
  opacity: 0.3;
}

.banner-stats {
  display: flex;
  gap: 16px;
  flex-shrink: 0;
}

.banner-stat-card {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 14px;
  padding: 14px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.banner-stat-card:hover {
  background: rgba(255, 255, 255, 0.16);
  transform: translateY(-2px);
}

.banner-stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.banner-stat-icon.vaccine-icon {
  background: rgba(76, 201, 240, 0.2);
}

.banner-stat-icon.category-icon {
  background: rgba(240, 171, 252, 0.2);
}

.banner-stat-info h3 {
  font-size: 1.5rem;
  font-weight: 800;
  color: #4cc9f0;
  margin: 0;
  line-height: 1.2;
}

.banner-stat-info p {
  font-size: 0.78rem;
  color: rgba(255, 255, 255, 0.55);
  margin: 0;
}

/* Enhanced Filter Bar */
.filter-bar-enhanced {
  display: flex;
  gap: 14px;
  margin-bottom: 24px;
  align-items: center;
  flex-wrap: wrap;
  background: white;
  padding: 16px 20px;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
}

.filter-tabs {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  flex: 1;
}

.filter-tab-enhanced {
  padding: 8px 18px;
  border-radius: 50px;
  border: 2px solid #e2e8f0;
  background: white;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--gray-color);
  transition: all 0.25s ease;
  font-family: inherit;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 5px;
}

.filter-tab-icon {
  font-size: 15px;
}

.filter-tab-enhanced:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: #f8faff;
}

.filter-tab-enhanced.active {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 14px rgba(67, 97, 238, 0.3);
  transform: translateY(-1px);
}

.search-box-enhanced {
  position: relative;
  flex-shrink: 0;
}

.search-box-enhanced input {
  padding: 10px 40px 10px 40px;
  border: 2px solid #e2e8f0;
  border-radius: 50px;
  font-size: 0.88rem;
  width: 240px;
  transition: all 0.3s ease;
  background: #f8fafc;
  font-family: inherit;
  color: var(--dark-color);
}

.search-box-enhanced input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.08);
  background: white;
  width: 280px;
}

.search-box-enhanced .search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray-light);
  font-size: 15px;
  pointer-events: none;
}

.search-clear {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: none;
  background: #e2e8f0;
  color: var(--gray-color);
  cursor: pointer;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.search-clear:hover {
  background: #cbd5e1;
  color: var(--dark-color);
}

/* Empty State Enhanced */
.empty-state-enhanced {
  text-align: center;
  padding: 70px 20px;
  color: var(--gray-light);
  background: white;
  border-radius: var(--border-radius-lg);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
}

.empty-state-enhanced h3 {
  font-size: 1.15rem;
  color: var(--gray-color);
  margin: 16px 0 8px;
}

.empty-state-enhanced p {
  margin-bottom: 20px;
  font-size: 0.9rem;
}

/* Responsive */
@media (max-width: 768px) {
  .dashboard-banner-enhanced {
    padding: 24px 20px;
  }

  .banner-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .banner-stats {
    width: 100%;
  }

  .banner-stat-card {
    flex: 1;
  }

  .banner-text h2 {
    font-size: 1.25rem;
  }

  .filter-bar-enhanced {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box-enhanced input,
  .search-box-enhanced input:focus {
    width: 100%;
  }
}
</style>
