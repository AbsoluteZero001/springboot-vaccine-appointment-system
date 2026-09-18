<template>
  <div>
    <SiteHeader/>

    <main class="homepage-main">
      <div class="container certificate-page">
        <!-- Loading -->
        <div v-if="loading" style="text-align:center;padding:60px;">
          <div class="loading"></div>
          <p style="margin-top:16px;color:var(--gray-color);">加载接种凭证...</p>
        </div>

        <!-- Not Found -->
        <div v-else-if="!record" class="empty-state-enhanced">
          <MedicalIllustration type="search" size="md" width="120px"/>
          <h3>记录未找到</h3>
          <p>接种记录不存在或无权访问</p>
          <router-link to="/profile" class="btn btn-small">返回</router-link>
        </div>

        <!-- Certificate -->
        <div v-else class="certificate-wrapper">
          <!-- Print Controls -->
          <div class="certificate-actions no-print">
            <button class="btn btn-small" @click="printCertificate">🖨️ 打印凭证</button>
            <router-link to="/profile" class="btn btn-small" style="background:#6b7280;">返回</router-link>
          </div>

          <div class="certificate" ref="certRef">
            <!-- Header -->
            <div class="cert-header">
              <div class="cert-emblem">
                <span class="emblem-icon">💉</span>
              </div>
              <h1>疫苗接种凭证</h1>
              <p class="cert-subtitle">Vaccination Certificate</p>
              <div class="cert-divider">
                <span>◆</span><span>◆</span><span>◆</span>
              </div>
            </div>

            <!-- Certificate Number -->
            <div class="cert-no">
              <span>凭证编号：</span>
              <strong>VAC-{{ String(record.id).padStart(8, '0') }}</strong>
              <span style="margin-left:20px;">签发日期：{{
                  formatDate(record.vaccinationTime || record.createTime)
                }}</span>
            </div>

            <!-- Personal Info -->
            <div class="cert-section">
              <h3>接种者信息</h3>
              <div class="cert-info-grid">
                <div class="cert-info-item">
                  <span class="ci-label">姓名</span>
                  <span class="ci-value">{{
                      record.user?.realName || record.user?.nickname || record.user?.username || '—'
                    }}</span>
                </div>
                <div class="cert-info-item">
                  <span class="ci-label">证件号码</span>
                  <span class="ci-value">{{ maskIdCard(record.user?.idCard) }}</span>
                </div>
                <div class="cert-info-item">
                  <span class="ci-label">性别</span>
                  <span class="ci-value">{{ genderText(record.user?.gender) }}</span>
                </div>
                <div class="cert-info-item">
                  <span class="ci-label">联系电话</span>
                  <span class="ci-value">{{ maskPhone(record.user?.phone) }}</span>
                </div>
              </div>
            </div>

            <!-- Vaccination Info -->
            <div class="cert-section">
              <h3>接种信息</h3>
              <div class="cert-info-grid">
                <div class="cert-info-item">
                  <span class="ci-label">疫苗名称</span>
                  <span class="ci-value highlight">{{ record.vaccine?.name || '—' }}</span>
                </div>
                <div class="cert-info-item">
                  <span class="ci-label">生产企业</span>
                  <span class="ci-value">{{ record.vaccine?.manufacturer || '—' }}</span>
                </div>
                <div class="cert-info-item">
                  <span class="ci-label">疫苗品牌</span>
                  <span class="ci-value">{{ record.vaccine?.brand || '—' }}</span>
                </div>
                <div class="cert-info-item">
                  <span class="ci-label">批号/规格</span>
                  <span class="ci-value">{{ record.vaccine?.dosage || '—' }}</span>
                </div>
                <div class="cert-info-item">
                  <span class="ci-label">接种日期</span>
                  <span class="ci-value highlight">{{ formatDateTime(record.vaccinationTime) }}</span>
                </div>
                <div class="cert-info-item">
                  <span class="ci-label">接种状态</span>
                  <span class="ci-value" :class="record.status === 1 ? 'status-done' : 'status-pending'">
                    {{ record.status === 1 ? '已完成接种' : '待接种' }}
                  </span>
                </div>
                <div class="cert-info-item" v-if="record.doctor">
                  <span class="ci-label">接种医生</span>
                  <span class="ci-value">{{ record.doctor.realName || record.doctor.nickname || '—' }}</span>
                </div>
                <div class="cert-info-item full" v-if="record.vaccine?.scheduleInfo">
                  <span class="ci-label">接种程序</span>
                  <span class="ci-value">{{ record.vaccine.scheduleInfo }}</span>
                </div>
                <div class="cert-info-item full" v-if="record.notes">
                  <span class="ci-label">备注</span>
                  <span class="ci-value">{{ record.notes }}</span>
                </div>
              </div>
            </div>

            <!-- QR placeholder & Verification -->
            <div class="cert-footer">
              <div class="cert-qr-placeholder">
                <div class="qr-box">
                  <span style="font-size:48px;">📱</span>
                  <p style="font-size:0.75rem;color:#94a3b8;">电子验证码</p>
                </div>
              </div>
              <div class="cert-verification">
                <p>此凭证由疫苗预约系统自动生成</p>
                <p>如有疑问，请联系接种机构核实</p>
                <p class="cert-watermark">VACCINE APPOINTMENT SYSTEM</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <SiteFooter/>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import MedicalIllustration from '@/components/MedicalIllustration.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const certRef = ref(null)

const record = ref(null)
const loading = ref(true)

function formatDate(d) {
  return d ? new Date(d).toLocaleDateString('zh-CN', {year: 'numeric', month: 'long', day: 'numeric'}) : ''
}

function formatDateTime(d) {
  return d ? new Date(d).toLocaleString('zh-CN') : ''
}

function maskIdCard(card) {
  if (!card || card.length < 8) return '—'
  return card.substring(0, 3) + '****' + card.substring(card.length - 4)
}

function maskPhone(phone) {
  if (!phone || phone.length < 7) return '—'
  return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
}

function genderText(g) {
  return g === 1 ? '男' : g === 2 ? '女' : '未知'
}

function printCertificate() {
  window.print()
}

async function loadRecord() {
  loading.value = true
  try {
    const id = route.params.id
    const res = await api.get(`/vaccination-records/${id}`)
    record.value = res.data
    // Verify ownership
    if (record.value && record.value.user?.id !== auth.currentUser?.id && !auth.isAdmin) {
      record.value = null
    }
  } catch {
    record.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!auth.isLoggedIn) {
    router.replace('/');
    return
  }
  loadRecord()
})
</script>

<style scoped>
.certificate-page {
  max-width: 900px;
}

.certificate-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: flex-end;
}

.certificate {
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 4px;
  padding: 48px 52px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  font-family: 'SimSun', 'STSong', serif;
}

.cert-header {
  text-align: center;
  margin-bottom: 28px;
}

.cert-emblem {
  margin-bottom: 12px;
}

.emblem-icon {
  font-size: 48px;
}

.cert-header h1 {
  font-size: 1.6rem;
  font-weight: 900;
  color: #1a1a2e;
  letter-spacing: 0.1em;
  margin-bottom: 4px;
}

.cert-subtitle {
  font-size: 0.85rem;
  color: #94a3b8;
  letter-spacing: 0.15em;
  text-transform: uppercase;
}

.cert-divider {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
  color: #cbd5e1;
  font-size: 0.5rem;
}

.cert-no {
  font-size: 0.85rem;
  color: var(--gray-color);
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #e2e8f0;
}

.cert-section {
  margin-bottom: 24px;
}

.cert-section h3 {
  font-size: 0.95rem;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
  padding-bottom: 6px;
  border-bottom: 2px solid #e2e8f0;
  letter-spacing: 0.05em;
}

.cert-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 24px;
}

.cert-info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.cert-info-item.full {
  grid-column: 1 / -1;
}

.ci-label {
  font-size: 0.78rem;
  color: var(--gray-light);
}

.ci-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1a1a2e;
}

.ci-value.highlight {
  color: var(--primary-color);
  font-size: 0.95rem;
}

.ci-value.status-done {
  color: #16a34a;
}

.ci-value.status-pending {
  color: #f59e0b;
}

.cert-footer {
  display: flex;
  gap: 36px;
  align-items: center;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
  margin-top: 20px;
}

.cert-qr-placeholder {
  flex-shrink: 0;
}

.qr-box {
  width: 100px;
  height: 100px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafbfc;
}

.cert-verification {
  font-size: 0.82rem;
  color: var(--gray-light);
  line-height: 1.8;
}

.cert-watermark {
  margin-top: 8px;
  font-size: 0.75rem;
  color: #cbd5e1;
  letter-spacing: 0.15em;
}

@media print {
  .no-print, .site-header, .site-footer, .footer {
    display: none !important;
  }

  body {
    background: white;
  }

  .certificate {
    border: 2px solid #000;
    box-shadow: none;
    padding: 30px;
  }
}

@media (max-width: 768px) {
  .certificate {
    padding: 24px 20px;
  }

  .cert-info-grid {
    grid-template-columns: 1fr;
  }

  .certificate-actions {
    flex-direction: column;
  }
}
</style>
