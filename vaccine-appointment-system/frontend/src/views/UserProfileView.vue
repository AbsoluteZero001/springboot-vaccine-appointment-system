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
              <th style="min-width:120px;">支付</th>
              <th style="min-width:110px;">状态</th>
              <th style="min-width:170px;">操作</th>
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
                <div class="action-btns">
                  <button v-if="appt.status === 0" class="btn btn-small" @click="openReschedule(appt)"
                          style="background:linear-gradient(135deg, #f59e0b, #f8961e);">
                    改期
                  </button>
                  <button v-if="appt.status === 0" class="btn btn-danger btn-small" @click="cancelAppointment(appt.id)">
                    取消
                  </button>
                  <span v-else-if="appt.remark" style="font-size:0.78rem;color:#64748b;">{{ appt.remark }}</span>
                </div>
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
              <th style="min-width:80px;">凭证</th>
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
              <td>
                <router-link v-if="rec.status === 1" :to="`/records/${rec.id}/certificate`" class="btn btn-small"
                             style="background:linear-gradient(135deg,#16a34a,#22c55e);text-decoration:none;display:inline-block;">
                  查看凭证
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
        </div>
      </div>

      <!-- Family Members Section -->
      <div class="card">
        <div class="family-section-header">
          <h2>👨‍👩‍👧‍👦 家庭成员</h2>
          <button class="btn btn-small" @click="openFamilyAdd">+ 添加成员</button>
        </div>
        <div v-if="familyLoading" style="text-align:center;padding:20px;">
          <div class="loading"></div>
        </div>
        <div v-else-if="familyMembers.length === 0" class="family-empty">
          <p>暂无家庭成员，添加后可为其预约接种</p>
        </div>
        <div v-else class="family-grid">
          <div v-for="m in familyMembers" :key="m.id" class="family-card">
            <div class="family-card-body">
              <div class="family-avatar">👤</div>
              <div class="family-details">
                <strong>{{ m.name }}</strong>
                <span v-if="m.relationship" class="family-rel">{{ m.relationship }}</span>
                <span v-if="m.realName">实名：{{ m.realName }}</span>
                <span v-if="m.idCard">身份证：{{ maskIdCard(m.idCard) }}</span>
                <span v-if="m.phone">📞 {{ maskPhone(m.phone) }}</span>
                <span v-if="m.remark" class="family-remark">{{ m.remark }}</span>
              </div>
            </div>
            <div class="family-card-actions">
              <button class="btn btn-small btn-edit" @click="openFamilyEdit(m)">编辑</button>
              <button class="btn btn-small btn-delete" @click="deleteFamilyMember(m.id)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Family Modal -->
    <div :class="['modal-overlay', { active: showFamilyModal }]" @click.self="showFamilyModal = false">
      <div class="modal-enhanced" style="max-width:480px;">
        <div class="modal-header">
          <div class="modal-header-left"><span class="modal-header-icon">👤</span>
            <h3>{{ editingFamily ? '编辑' : '添加' }}家庭成员</h3></div>
          <button class="modal-close" @click="showFamilyModal = false">✕</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveFamilyMember">
            <div class="form-group">
              <label>姓名 <span class="label-required">*</span></label>
              <input v-model="familyForm.name" class="form-control" required placeholder="请输入姓名" maxlength="50"/>
            </div>
            <div class="form-group">
              <label>关系</label>
              <select v-model="familyForm.relationship" class="form-control">
                <option value="">请选择</option>
                <option value="配偶">配偶</option>
                <option value="子女">子女</option>
                <option value="父母">父母</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div class="form-group">
              <label>身份证号</label>
              <input v-model="familyForm.idCard" class="form-control" placeholder="选填" maxlength="18"/>
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input v-model="familyForm.phone" class="form-control" placeholder="选填" maxlength="20"/>
            </div>
            <div class="form-group">
              <label>真实姓名</label>
              <input v-model="familyForm.realName" class="form-control" placeholder="选填" maxlength="50"/>
            </div>
            <div class="form-group">
              <label>备注</label>
              <input v-model="familyForm.remark" class="form-control" placeholder="选填" maxlength="500"/>
            </div>
            <button class="btn btn-shimmer" type="submit" style="width:100%;">
              {{ editingFamily ? '保存修改' : '添加' }}
            </button>
          </form>
        </div>
      </div>
    </div>

    <!-- Reschedule Modal -->
    <div :class="['modal-overlay', { active: showReschedule }]" @click.self="showReschedule = false">
      <div class="modal-enhanced" style="max-width:460px;">
        <div class="modal-header">
          <div class="modal-header-left"><span class="modal-header-icon">📅</span>
            <h3>改期预约</h3></div>
          <button class="modal-close" @click="showReschedule = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="modal-vaccine-summary">
            <div class="summary-row"><span class="label">疫苗</span><span
                class="value">{{ rescheduleAppt?.vaccine?.name || '—' }}</span></div>
            <div class="summary-row"><span class="label">原预约时间</span><span
                class="value">{{ rescheduleAppt ? formatDate(rescheduleAppt.appointmentTime) : '—' }}</span></div>
          </div>
          <form @submit.prevent="submitReschedule">
            <div class="form-group">
              <label>新预约时间 <span class="label-required">*</span></label>
              <input v-model="newAppointmentTime" type="datetime-local" class="form-control" required
                     :min="minDateTime"/>
            </div>
            <button class="btn btn-shimmer" type="submit" style="width:100%;">确认改期</button>
          </form>
        </div>
      </div>
    </div>

    <!-- Payment Modal -->
    <div :class="['modal-overlay', { active: showPayModal }]" @click.self="closePayModal">
      <div class="modal-enhanced pay-modal">
        <div class="modal-header">
          <div class="modal-header-left"><span class="modal-header-icon">💳</span>
            <h3>确认支付</h3></div>
          <button v-if="!isPaying && !paySuccess" class="modal-close" @click="closePayModal">✕</button>
        </div>
        <div class="modal-body" v-if="payingAppt">
          <div v-if="!isPaying && !paySuccess">
            <div class="pay-vaccine-info">
              <div class="pay-vaccine-icon">💉</div>
              <div class="pay-vaccine-details">
                <h4>{{ payingAppt.vaccine?.name || '疫苗' }}</h4>
                <p>{{ specText(payingAppt.vaccine) }}</p>
              </div>
            </div>
            <div class="pay-info-grid">
              <div class="pay-info-row">
                <span class="pay-label">预约编号</span>
                <span class="pay-value">#{{ payingAppt.id }}</span>
              </div>
              <div class="pay-info-row">
                <span class="pay-label">预约时间</span>
                <span class="pay-value">{{ formatDate(payingAppt.appointmentTime) }}</span>
              </div>
              <div class="pay-info-row pay-total">
                <span class="pay-label">应付金额</span>
                <span class="pay-value pay-price">{{
                    payingAppt.vaccine?.price != null ? '¥' + Number(payingAppt.vaccine.price).toFixed(2) : '¥0.00'
                  }}</span>
              </div>
            </div>
            <button class="btn btn-shimmer pay-confirm-btn" @click="processPayment">
              💳 确认支付 {{
                payingAppt.vaccine?.price != null ? '¥' + Number(payingAppt.vaccine.price).toFixed(0) : '¥0'
              }}
            </button>
          </div>
          <div v-else-if="isPaying" class="pay-processing">
            <div class="pay-spinner"></div>
            <p class="pay-processing-text">正在处理支付...</p>
            <p class="pay-processing-hint">请稍候，正在连接支付网关</p>
          </div>
          <div v-else-if="paySuccess" class="pay-success">
            <div class="pay-success-icon">✅</div>
            <h3>支付成功！</h3>
            <p>您的预约已确认，请按时前往接种</p>
            <button class="btn btn-shimmer" style="margin-top:16px;width:100%;" @click="closePayModal">完成</button>
          </div>
        </div>
      </div>
    </div>

    <SiteFooter />
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

const appointments = ref([])
const records = ref([])
const pendingCount = ref(0)
const completedCount = ref(0)
const showReschedule = ref(false)
const rescheduleAppt = ref(null)
const newAppointmentTime = ref('')
const minDateTime = computed(() => new Date(Date.now() + 3600000).toISOString().slice(0, 16))

// Payment
const showPayModal = ref(false)
const payingAppt = ref(null)
const isPaying = ref(false)
const paySuccess = ref(false)

// Family members
const familyMembers = ref([])
const familyLoading = ref(false)
const showFamilyModal = ref(false)
const editingFamily = ref(null)
const emptyFamilyForm = () => ({name: '', relationship: '', idCard: '', phone: '', realName: '', remark: ''})
const familyForm = reactive(emptyFamilyForm())

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

function payAppointment(appt) {
  payingAppt.value = appt
  isPaying.value = false
  paySuccess.value = false
  showPayModal.value = true
}

function closePayModal() {
  if (isPaying.value) return
  showPayModal.value = false
  payingAppt.value = null
}

async function processPayment() {
  if (!payingAppt.value) return
  isPaying.value = true
  try {
    await api.post(`/appointments/${payingAppt.value.id}/pay`, {userId: auth.currentUser.id, remark: ''})
    await new Promise(resolve => setTimeout(resolve, 1500))
    isPaying.value = false
    paySuccess.value = true
    await loadAppointments()
  } catch (error) {
    isPaying.value = false
    showAlert(error.response?.data?.error || '支付失败', 'error')
    showPayModal.value = false
  }
}

function openReschedule(appt) {
  rescheduleAppt.value = appt
  newAppointmentTime.value = ''
  showReschedule.value = true
}

async function submitReschedule() {
  if (!newAppointmentTime.value) {
    showAlert('请选择新时间', 'error');
    return
  }
  const dt = new Date(newAppointmentTime.value)
  if (dt.getDay() === 0) {
    showAlert('周日休息', 'error');
    return
  }
  try {
    await api.post(`/appointments/${rescheduleAppt.value.id}/reschedule`, {
      userId: auth.currentUser.id,
      newTime: newAppointmentTime.value + ':00'
    })
    showAlert('改期成功！')
    showReschedule.value = false
    await loadAppointments()
  } catch (error) {
    showAlert(error.response?.data?.error || '改期失败', 'error')
  }
}

// ── Family members ──────────────────────────────────────────
function maskIdCard(c) {
  return c && c.length > 8 ? c.substring(0, 3) + '****' + c.substring(c.length - 4) : c || '—'
}

function maskPhone(p) {
  return p && p.length > 7 ? p.substring(0, 3) + '****' + p.substring(p.length - 4) : p || '—'
}

async function loadFamilyMembers() {
  familyLoading.value = true
  try {
    const res = await api.get(`/family-members/user/${auth.currentUser.id}`)
    familyMembers.value = res.data
  } catch { /* silent */
  } finally {
    familyLoading.value = false
  }
}

function openFamilyAdd() {
  editingFamily.value = null
  Object.assign(familyForm, emptyFamilyForm())
  showFamilyModal.value = true
}

function openFamilyEdit(member) {
  editingFamily.value = member
  familyForm.name = member.name || ''
  familyForm.relationship = member.relationship || ''
  familyForm.idCard = member.idCard || ''
  familyForm.phone = member.phone || ''
  familyForm.realName = member.realName || ''
  familyForm.remark = member.remark || ''
  showFamilyModal.value = true
}

async function saveFamilyMember() {
  if (!familyForm.name.trim()) {
    showAlert('请填写姓名', 'error');
    return
  }
  try {
    const data = {
      userId: auth.currentUser.id,
      name: familyForm.name.trim(),
      relationship: familyForm.relationship,
      idCard: familyForm.idCard || null,
      phone: familyForm.phone || null,
      realName: familyForm.realName || null,
      remark: familyForm.remark || null
    }
    if (editingFamily.value) {
      await api.put(`/family-members/${editingFamily.value.id}`, data)
      showAlert('修改成功')
    } else {
      await api.post('/family-members', data)
      showAlert('添加成功')
    }
    showFamilyModal.value = false
    loadFamilyMembers()
  } catch (e) {
    showAlert(e.response?.data?.error || '操作失败', 'error')
  }
}

async function deleteFamilyMember(id) {
  if (!confirm('确定删除该家庭成员？')) return
  try {
    await api.delete(`/family-members/${id}?userId=${auth.currentUser.id}`)
    showAlert('已删除')
    loadFamilyMembers()
  } catch (e) {
    showAlert(e.response?.data?.error || '删除失败', 'error')
  }
}

onMounted(() => {
  if (!auth.isUser) {
    router.replace('/')
    return
  }
  loadAppointments()
  loadRecords()
  loadFamilyMembers()
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

.action-btns {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

/* Family Section */
.family-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0;
}

.family-section-header h2 {
  margin-bottom: 0 !important;
}

.family-section-header h2::after {
  display: none;
}

.family-empty {
  text-align: center;
  padding: 24px;
  color: var(--gray-light);
  font-size: 0.88rem;
}

.family-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.family-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px 18px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  transition: var(--transition);
}

.family-card:hover {
  border-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(67, 97, 238, 0.08);
}

.family-card-body {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.family-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.family-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 0.82rem;
  min-width: 0;
}

.family-details strong {
  color: var(--dark-color);
  font-size: 0.9rem;
}

.family-rel {
  color: var(--primary-color);
  font-weight: 500;
  font-size: 0.78rem;
}

.family-details span {
  color: var(--gray-light);
  font-size: 0.76rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.family-remark {
  font-style: italic;
}

.family-card-actions {
  display: flex;
  gap: 5px;
  flex-shrink: 0;
}

/* Payment Modal */
.pay-modal {
  max-width: 440px;
}

.pay-vaccine-info {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid #bbf7d0;
}

.pay-vaccine-icon {
  font-size: 36px;
  flex-shrink: 0;
}

.pay-vaccine-details h4 {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark-color);
  margin: 0 0 4px;
}

.pay-vaccine-details p {
  font-size: 0.82rem;
  color: #64748b;
  margin: 0;
}

.pay-info-grid {
  background: #f8fafc;
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 20px;
  border: 1px solid #e2e8f0;
}

.pay-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
}

.pay-info-row:last-child {
  border-bottom: none;
}

.pay-label {
  font-size: 0.85rem;
  color: #64748b;
}

.pay-value {
  font-size: 0.88rem;
  font-weight: 600;
  color: var(--dark-color);
}

.pay-total {
  padding-top: 12px;
  margin-top: 4px;
  border-top: 2px dashed #e2e8f0;
  border-bottom: none;
}

.pay-price {
  font-size: 1.2rem;
  font-weight: 800;
  color: #dc2626;
}

.pay-confirm-btn {
  width: 100%;
  padding: 14px;
  font-size: 1rem;
}

/* Processing */
.pay-processing {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.pay-spinner {
  width: 60px;
  height: 60px;
  border: 5px solid #e2e8f0;
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: paySpin 0.8s linear infinite;
  margin-bottom: 20px;
}

@keyframes paySpin {
  to {
    transform: rotate(360deg);
  }
}

.pay-processing-text {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--dark-color);
  margin: 0 0 6px;
}

.pay-processing-hint {
  font-size: 0.82rem;
  color: #94a3b8;
  margin: 0;
}

/* Success */
.pay-success {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  text-align: center;
}

.pay-success-icon {
  font-size: 56px;
  margin-bottom: 12px;
  animation: paySuccessPop 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes paySuccessPop {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  60% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.pay-success h3 {
  font-size: 1.2rem;
  font-weight: 700;
  color: #16a34a;
  margin: 0 0 8px;
}

.pay-success p {
  font-size: 0.88rem;
  color: #64748b;
  margin: 0;
}
</style>
