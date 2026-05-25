<template>
  <div>
    <SiteHeader active-nav="/vaccines"/>

    <main class="homepage-main">
      <div class="container">
        <ModalMessage ref="modalRef"/>

        <!-- Loading -->
        <div v-if="loading" class="detail-loading">
          <div class="skeleton-detail">
            <div class="skeleton skeleton-img"></div>
            <div class="skeleton skeleton-title" style="width:60%"></div>
            <div class="skeleton skeleton-sub" style="width:40%"></div>
            <div class="skeleton skeleton-text"></div>
            <div class="skeleton skeleton-text"></div>
          </div>
        </div>

        <!-- Detail Content -->
        <div v-else-if="vaccine" class="detail-layout">
          <!-- Breadcrumb -->
          <div class="breadcrumb">
            <router-link to="/dashboard">疫苗列表</router-link>
            <span class="breadcrumb-sep">/</span>
            <span>{{ vaccine.name }}</span>
          </div>

          <!-- Main Detail -->
          <div class="detail-main-card">
            <div class="detail-image-section">
              <img v-if="vaccine.imageUrl" :src="vaccine.imageUrl" :alt="vaccine.name" class="detail-image"/>
              <MedicalIllustration v-else :type="detailIllustration" size="lg" width="200px"/>
            </div>
            <div class="detail-info-section">
              <div class="detail-header">
                <h1>{{ vaccine.name }}</h1>
                <div class="detail-badges">
                  <span v-if="vaccine.category" class="vaccine-badge category">{{ vaccine.category }}</span>
                  <span v-if="vaccine.targetDisease" class="vaccine-badge disease">{{ vaccine.targetDisease }}</span>
                  <span v-if="vaccine.available === false" class="vaccine-badge unavailable">已下架</span>
                </div>
              </div>

              <div class="detail-price-row">
                <span class="detail-price">
                  {{ vaccine.price != null ? '¥' + Number(vaccine.price).toFixed(0) : '免费' }}
                </span>
                <span class="detail-stock" :class="stockClass">
                  {{ stockText }} (库存 {{ vaccine.stockQuantity }} 剂)
                </span>
              </div>

              <div class="detail-specs">
                <div class="detail-spec" v-if="vaccine.brand">
                  <span class="spec-label">品牌</span>
                  <span class="spec-value">{{ vaccine.brand }}</span>
                </div>
                <div class="detail-spec" v-if="vaccine.manufacturer">
                  <span class="spec-label">生产厂家</span>
                  <span class="spec-value">{{ vaccine.manufacturer }}</span>
                </div>
                <div class="detail-spec" v-if="vaccine.dosage">
                  <span class="spec-label">剂型/规格</span>
                  <span class="spec-value">{{ vaccine.dosage }}</span>
                </div>
                <div class="detail-spec" v-if="vaccine.technique">
                  <span class="spec-label">技术路线</span>
                  <span class="spec-value">{{ vaccine.technique }}</span>
                </div>
                <div class="detail-spec" v-if="vaccine.dosesRequired">
                  <span class="spec-label">接种剂次</span>
                  <span class="spec-value">{{ vaccine.dosesRequired }} 剂</span>
                </div>
                <div class="detail-spec" v-if="vaccine.ageRange">
                  <span class="spec-label">适用年龄</span>
                  <span class="spec-value">{{ vaccine.ageRange }}</span>
                </div>
                <div class="detail-spec full-width" v-if="vaccine.scheduleInfo">
                  <span class="spec-label">接种程序</span>
                  <span class="spec-value">{{ vaccine.scheduleInfo }}</span>
                </div>
              </div>

              <button
                  :disabled="!canBook"
                  class="btn btn-book-detail btn-shimmer"
                  @click="openBooking"
              >
                {{ canBook ? '立即预约' : '暂不可约' }}
              </button>
            </div>
          </div>

          <!-- Description Card -->
          <div v-if="vaccine.description" class="detail-card">
            <h3>疫苗介绍</h3>
            <p class="detail-desc">{{ vaccine.description }}</p>
          </div>

          <!-- Reviews Section -->
          <div class="detail-card">
            <h3>用户评价 ({{ reviews.length }})</h3>
            <div v-if="reviews.length === 0" class="empty-reviews">
              <p>暂无评价</p>
            </div>
            <div v-else class="reviews-list">
              <div v-for="r in reviews" :key="r.id" class="review-item">
                <div class="review-header">
                  <span class="review-user">{{ r.user?.nickname || r.user?.username || '匿名用户' }}</span>
                  <span class="review-stars">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</span>
                  <span class="review-date">{{ formatDate(r.createTime) }}</span>
                </div>
                <p class="review-content" v-if="r.content">{{ r.content }}</p>
              </div>
            </div>
            <!-- Review Form (shown if user has completed vaccination for this vaccine) -->
            <div v-if="canReview" class="review-form">
              <h4>发表评价</h4>
              <div class="star-rating">
                <span v-for="s in 5" :key="s" class="star" :class="{ active: s <= reviewRating }"
                      @click="reviewRating = s">
                  {{ s <= reviewRating ? '★' : '☆' }}
                </span>
              </div>
              <textarea v-model="reviewContent" class="form-control" placeholder="分享您的接种体验..." rows="3"
                        maxlength="500"></textarea>
              <button class="btn btn-small" @click="submitReview" :disabled="submittingReview">提交评价</button>
            </div>
          </div>
        </div>

        <!-- Not Found -->
        <div v-else class="empty-state-enhanced">
          <MedicalIllustration type="search" size="md" width="120px"/>
          <h3>疫苗未找到</h3>
          <p>该疫苗不存在或已被删除</p>
          <router-link to="/dashboard" class="btn btn-small">返回列表</router-link>
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

    <!-- Verify Modal -->
    <div :class="['modal-overlay', { active: showVerifyModal }]" @click.self="showVerifyModal = false">
      <div class="modal-enhanced verify-modal">
        <div class="modal-header">
          <div class="modal-header-left"><span class="modal-header-icon">🛡️</span>
            <h3>实名认证</h3></div>
          <button class="modal-close" @click="showVerifyModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="verify-notice">
            <span class="notice-icon">🔒</span>
            <div>
              <strong>预约疫苗需要先完成实名认证</strong>
              <p>根据《疫苗管理法》规定，接种疫苗须实名登记。</p>
            </div>
          </div>
          <form @submit.prevent="submitVerify">
            <div class="form-group">
              <label>真实姓名 <span class="label-required">*</span></label>
              <input v-model="verifyForm.realName" class="form-control" placeholder="请输入真实姓名" required
                     type="text" maxlength="50"/>
            </div>
            <div class="form-group">
              <label>身份证号码 <span class="label-required">*</span></label>
              <input v-model="verifyForm.idCard" class="form-control" placeholder="请输入18位身份证号码" required
                     type="text" maxlength="18"/>
            </div>
            <button class="btn btn-shimmer" type="submit" style="width:100%;">提交认证</button>
          </form>
        </div>
      </div>
    </div>

    <SiteFooter/>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import ModalMessage from '@/components/ModalMessage.vue'
import MedicalIllustration from '@/components/MedicalIllustration.vue'
import AppointmentModal from '@/components/AppointmentModal.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const modalRef = ref(null)

const vaccine = ref(null)
const loading = ref(true)
const reviews = ref([])
const showModal = ref(false)
const showVerifyModal = ref(false)
const selectedVaccine = ref(null)
const canReview = ref(false)
const submittingReview = ref(false)
const reviewRating = ref(5)
const reviewContent = ref('')

const verifyForm = reactive({realName: '', idCard: ''})

const canBook = computed(() => vaccine.value && vaccine.value.stockQuantity > 0 && vaccine.value.available !== false)
const stockClass = computed(() =>
    vaccine.value?.stockQuantity > 50 ? 'in-stock' : vaccine.value?.stockQuantity > 0 ? 'low-stock' : 'out-of-stock'
)
const stockText = computed(() =>
    vaccine.value?.stockQuantity > 50 ? '库存充足' : vaccine.value?.stockQuantity > 0 ? '库存紧张' : '已缺货'
)
const detailIllustration = computed(() => {
  const name = (vaccine.value?.name || '').toLowerCase()
  if (name.includes('新冠')) return 'syringe'
  if (name.includes('流感')) return 'capsule'
  if (name.includes('hpv')) return 'shield'
  return 'vaccine'
})

function showAlert(msg, type = 'success') {
  modalRef.value?.showModal(msg, type)
}

function formatDate(d) {
  return d ? new Date(d).toLocaleDateString('zh-CN') : ''
}

async function loadVaccine() {
  loading.value = true
  try {
    const id = route.params.id
    const res = await api.get(`/vaccines/${id}`)
    vaccine.value = res.data
    loadReviews()
  } catch {
    vaccine.value = null
  } finally {
    loading.value = false
  }
}

async function loadReviews() {
  try {
    const res = await api.get(`/reviews/vaccine/${route.params.id}`)
    reviews.value = res.data
    // Check if user can review
    const hasCompleted = reviews.value.some(r => r.user?.id === auth.currentUser?.id)
    if (!hasCompleted && auth.currentUser) {
      // Check if user has completed a vaccination for this vaccine
      try {
        const recordsRes = await api.get(`/vaccination-records/user/${auth.currentUser.id}`)
        canReview.value = recordsRes.data.some(r =>
            r.vaccine?.id === vaccine.value?.id && r.status === 1
        )
      } catch {
        canReview.value = false
      }
    }
  } catch { /* reviews API not yet available */
  }
}

async function submitReview() {
  if (!reviewRating.value) return
  submittingReview.value = true
  try {
    await api.post('/reviews', {
      vaccineId: Number(route.params.id),
      userId: auth.currentUser.id,
      rating: reviewRating.value,
      content: reviewContent.value.trim()
    })
    showAlert('评价提交成功！')
    reviewContent.value = ''
    reviewRating.value = 5
    loadReviews()
  } catch (e) {
    showAlert(e.response?.data?.error || '评价失败', 'error')
  } finally {
    submittingReview.value = false
  }
}

function openBooking() {
  if (!auth.currentUser?.isVerified) {
    showVerifyModal.value = true
    return
  }
  selectedVaccine.value = vaccine.value
  showModal.value = true
}

async function submitVerify() {
  if (!verifyForm.realName.trim() || !verifyForm.idCard.trim()) {
    showAlert('请填写完整的实名信息', 'error')
    return
  }
  try {
    const res = await api.post(`/users/${auth.currentUser.id}/verify`, {
      realName: verifyForm.realName.trim(), idCard: verifyForm.idCard.trim()
    })
    if (auth.currentUser) {
      auth.currentUser.isVerified = 1
      auth.currentUser.realName = verifyForm.realName.trim()
      if (res.data.gender != null) auth.currentUser.gender = res.data.gender
      localStorage.setItem('user', JSON.stringify(auth.currentUser))
    }
    showAlert('实名认证成功！')
    showVerifyModal.value = false
  } catch (e) {
    showAlert(e.response?.data?.error || '认证失败', 'error')
  }
}

async function handleBooking(appointmentTime) {
  const date = new Date(appointmentTime)
  if (date.getDay() === 0) {
    showAlert('周日休息，请选择工作日预约', 'error');
    return
  }
  try {
    await api.post('/appointments', {
      userId: auth.currentUser.id, vaccineId: vaccine.value.id, appointmentTime
    })
    showAlert('预约成功！请按时前往接种')
    showModal.value = false
    loadVaccine()
  } catch (e) {
    showAlert(e.response?.data?.error || '预约失败', 'error')
  }
}

onMounted(() => {
  if (!auth.isLoggedIn) {
    router.replace('/');
    return
  }
  loadVaccine()
})
</script>

<style scoped>
.detail-loading {
  padding: 40px 0;
}

.skeleton-detail {
  background: white;
  border-radius: var(--border-radius-lg);
  padding: 30px;
  box-shadow: var(--box-shadow);
}

.skeleton-detail .skeleton {
  height: 20px;
  margin-bottom: 12px;
}

.skeleton-detail .skeleton-img {
  height: 280px;
  margin-bottom: 20px;
  border-radius: 12px;
}

.skeleton-detail .skeleton-sub {
  width: 40%;
  height: 16px;
}

.skeleton-detail .skeleton-text {
  width: 90%;
  height: 14px;
}

.breadcrumb {
  margin-bottom: 20px;
  font-size: 0.88rem;
  color: var(--gray-color);
  display: flex;
  align-items: center;
  gap: 6px;
}

.breadcrumb a {
  color: var(--primary-color);
  text-decoration: none;
}

.breadcrumb a:hover {
  text-decoration: underline;
}

.breadcrumb-sep {
  color: #cbd5e1;
}

.detail-main-card {
  background: white;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--box-shadow);
  display: flex;
  gap: 36px;
  overflow: hidden;
  margin-bottom: 24px;
}

.detail-image-section {
  flex: 0 0 380px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  min-height: 340px;
}

.detail-image {
  max-width: 100%;
  max-height: 320px;
  object-fit: contain;
  border-radius: 12px;
}

.detail-info-section {
  flex: 1;
  padding: 28px 32px 28px 0;
}

.detail-header {
  margin-bottom: 16px;
}

.detail-header h1 {
  font-size: 1.6rem;
  font-weight: 800;
  color: var(--dark-color);
  margin-bottom: 10px;
}

.detail-badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.detail-price-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding: 14px 18px;
  background: #f8fafc;
  border-radius: 12px;
}

.detail-price {
  font-size: 1.8rem;
  font-weight: 800;
  color: #dc2626;
}

.detail-stock {
  font-size: 0.88rem;
  font-weight: 600;
}

.detail-stock.in-stock {
  color: #16a34a;
}

.detail-stock.low-stock {
  color: #f59e0b;
}

.detail-stock.out-of-stock {
  color: #dc2626;
}

.detail-specs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 24px;
  padding-top: 18px;
  border-top: 1px solid #f1f5f9;
}

.detail-spec.full-width {
  grid-column: 1 / -1;
}

.detail-spec {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.spec-label {
  font-size: 0.78rem;
  color: var(--gray-light);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  font-weight: 600;
}

.spec-value {
  font-size: 0.92rem;
  color: var(--dark-color);
  font-weight: 500;
}

.btn-book-detail {
  padding: 14px 48px;
  font-size: 1rem;
  border-radius: 50px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  color: white;
  border: none;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(67, 97, 238, 0.3);
  transition: all 0.3s ease;
  font-family: inherit;
}

.btn-book-detail:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(67, 97, 238, 0.4);
}

.btn-book-detail:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  transform: none;
}

.detail-card {
  background: white;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
  padding: 24px 28px;
  margin-bottom: 20px;
}

.detail-card h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 16px;
}

.detail-desc {
  font-size: 0.92rem;
  color: var(--gray-color);
  line-height: 1.8;
  white-space: pre-line;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.review-user {
  font-weight: 600;
  color: var(--dark-color);
  font-size: 0.9rem;
}

.review-stars {
  color: #f59e0b;
  font-size: 1rem;
}

.review-date {
  font-size: 0.8rem;
  color: var(--gray-light);
  margin-left: auto;
}

.review-content {
  font-size: 0.88rem;
  color: var(--gray-color);
  line-height: 1.6;
}

.review-form {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.review-form h4 {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--dark-color);
  margin-bottom: 12px;
}

.star-rating {
  display: flex;
  gap: 4px;
  margin-bottom: 12px;
}

.star {
  font-size: 1.5rem;
  color: #d1d5db;
  cursor: pointer;
  transition: color 0.2s;
}

.star.active {
  color: #f59e0b;
}

.review-form textarea {
  margin-bottom: 12px;
}

.empty-reviews {
  text-align: center;
  color: var(--gray-light);
  padding: 20px;
}

.vaccine-badge.unavailable {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

@media (max-width: 768px) {
  .detail-main-card {
    flex-direction: column;
  }

  .detail-image-section {
    flex: none;
    min-height: 220px;
  }

  .detail-info-section {
    padding: 20px;
  }

  .detail-specs {
    grid-template-columns: 1fr;
  }
}
</style>
