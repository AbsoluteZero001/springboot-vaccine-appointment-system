<template>
  <div>
    <SiteHeader active-nav="/family"/>

    <main class="homepage-main">
      <div class="container">
        <ModalMessage ref="modalRef"/>

        <!-- Family Members Section -->
        <div class="card family-section">
          <div class="family-section-header">
            <h3>家庭成员</h3>
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
    </main>

    <!-- Family Member Modal -->
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
              <label>身份证号 <span class="label-required">*</span></label>
              <input v-model="familyForm.idCard" class="form-control" required placeholder="请输入身份证号"
                     maxlength="18"/>
            </div>
            <div class="form-group">
              <label>手机号 <span class="label-required">*</span></label>
              <input v-model="familyForm.phone" class="form-control" required placeholder="请输入手机号"
                     maxlength="20"/>
            </div>
            <div class="form-group">
              <label>备注</label>
              <input v-model="familyForm.remark" class="form-control" placeholder="选填" maxlength="500"/>
            </div>
            <button class="btn btn-shimmer" type="submit" style="width:100%;margin-top:8px;">
              {{ editingFamily ? '保存修改' : '添加' }}
            </button>
          </form>
        </div>
      </div>
    </div>

    <SiteFooter/>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import ModalMessage from '@/components/ModalMessage.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const auth = useAuthStore()
const modalRef = ref(null)

const familyMembers = ref([])
const familyLoading = ref(false)
const showFamilyModal = ref(false)
const editingFamily = ref(null)
const emptyFamilyForm = () => ({name: '', idCard: '', phone: '', remark: ''})
const familyForm = reactive(emptyFamilyForm())

function showAlert(message, type = 'success') {
  modalRef.value?.showModal(message, type)
}

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
  familyForm.idCard = member.idCard || ''
  familyForm.phone = member.phone || ''
  familyForm.remark = member.remark || ''
  showFamilyModal.value = true
}

async function saveFamilyMember() {
  if (!familyForm.name.trim()) {
    showAlert('请填写姓名', 'error');
    return
  }
  if (!familyForm.idCard.trim()) {
    showAlert('请填写身份证号', 'error');
    return
  }
  if (!familyForm.phone.trim()) {
    showAlert('请填写手机号', 'error');
    return
  }
  try {
    const data = {
      userId: auth.currentUser.id,
      name: familyForm.name.trim(),
      idCard: familyForm.idCard.trim(),
      phone: familyForm.phone.trim(),
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
  loadFamilyMembers()
})
</script>

<style scoped>
.family-section {
  margin-top: 24px;
}

.family-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.family-section-header h3 {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--dark-color);
  margin: 0;
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

.btn-edit {
  background: linear-gradient(135deg, #4361ee, #4895ef);
}

.btn-delete {
  background: linear-gradient(135deg, #dc2626, #f72585);
}

.label-required {
  color: #e53e3e;
  margin-left: 2px;
}
</style>
