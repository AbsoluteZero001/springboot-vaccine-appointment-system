<template>
  <div>
    <SiteHeader active-nav="/dashboard" />

    <main class="homepage-main">
      <div class="container">
        <AlertMessage ref="alertRef" />

        <!-- Welcome Banner -->
        <div class="dashboard-banner">
          <div>
            <h2>欢迎回来，{{ auth.currentUser?.username || '用户' }} 👋</h2>
            <p>选择疫苗，在线预约接种时间 · {{ today }}</p>
          </div>
          <div class="banner-stats">
            <div class="banner-stat">
              <h3>{{ allVaccines.length }}</h3>
              <p>可用疫苗</p>
            </div>
            <div class="banner-stat">
              <h3>{{ categories.size }}</h3>
              <p>疫苗种类</p>
            </div>
          </div>
        </div>

        <!-- Filter Bar -->
        <div class="filter-bar">
          <div class="filter-tabs">
            <button
              :class="['filter-tab', { active: activeCategory === 'all' }]"
              @click="filterByCategory('all')"
            >
              全部疫苗
            </button>
            <button
              v-for="cat in categories"
              :key="cat"
              :class="['filter-tab', { active: activeCategory === cat }]"
              @click="filterByCategory(cat)"
            >
              {{ cat }}
            </button>
          </div>
          <div class="search-box">
            <span class="search-icon">🔍</span>
            <input v-model="searchKeyword" placeholder="搜索疫苗名称或品牌..." type="text" @input="filterVaccines" />
          </div>
        </div>

        <!-- Vaccine Grid -->
        <div v-if="filteredVaccines.length" class="vaccine-grid-modern">
          <VaccineCard
            v-for="vaccine in filteredVaccines"
            :key="vaccine.id"
            :vaccine="vaccine"
            @book="openBooking"
          />
        </div>
        <div v-else class="empty-state">
          <span class="empty-icon">🔍</span>
          <h3>未找到匹配的疫苗</h3>
          <p>请尝试其他搜索关键词或筛选条件</p>
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
import type {Vaccine} from '@/components/VaccineCard.vue'
import VaccineCard from '@/components/VaccineCard.vue'
import AppointmentModal from '@/components/AppointmentModal.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const auth = useAuthStore()
const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)

const today = new Date().toLocaleDateString('zh-CN')
const allVaccines = ref<Vaccine[]>([])
const activeCategory = ref('all')
const searchKeyword = ref('')
const showModal = ref(false)
const selectedVaccine = ref<Vaccine | null>(null)

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

function showAlert(message: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(message, type)
}

async function loadVaccines() {
  try {
    const response = await api.get('/vaccines/available')
    allVaccines.value = response.data
  } catch {
    showAlert('加载疫苗数据失败', 'error')
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
