<template>
  <div>
    <SiteHeader active-nav="/admin/vaccines" />

    <div class="container">
      <AlertMessage ref="alertRef" />

      <!-- Page Banner -->
      <div class="dashboard-banner-enhanced">
        <div class="banner-bg-decoration">
          <div class="banner-circle c1"></div>
          <div class="banner-circle c2"></div>
          <div class="banner-circle c3"></div>
        </div>
        <div class="banner-content">
          <div class="banner-text">
            <h2>疫苗管理</h2>
            <p class="banner-subtitle">管理系统中的疫苗信息，支持添加、编辑、上下架及删除操作</p>
          </div>
          <div class="banner-stats">
            <div class="banner-stat-card">
              <span style="font-size:24px;">💉</span>
              <div class="banner-stat-info">
                <h3>{{ vaccines.length }}</h3>
                <p>疫苗总数</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Add Vaccine Form -->
      <div class="card">
        <h2>添加新疫苗</h2>
        <form @submit.prevent="addVaccine">
          <div class="form-section-label">基本信息</div>
          <div class="form-group">
            <label>疫苗名称 *</label>
            <input v-model="addForm.name" class="form-control" placeholder="如：重组乙型肝炎疫苗（CHO细胞）10μg" required type="text" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>分类</label>
              <input v-model="addForm.category" class="form-control" placeholder="如：乙肝疫苗" type="text" />
            </div>
            <div class="form-group">
              <label>品牌</label>
              <input v-model="addForm.brand" class="form-control" placeholder="如：Engerix-B" type="text" />
            </div>
          </div>
          <div class="form-row-3">
            <div class="form-group">
              <label>剂量规格</label>
              <input v-model="addForm.dosage" class="form-control" placeholder="如：10μg/0.5ml" type="text" />
            </div>
            <div class="form-group">
              <label>制作工艺</label>
              <input v-model="addForm.technique" class="form-control" placeholder="如：CHO细胞" type="text" />
            </div>
            <div class="form-group">
              <label>需接种剂次</label>
              <input v-model.number="addForm.dosesRequired" class="form-control" min="1" placeholder="如：3" type="number" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>适用人群</label>
              <input v-model="addForm.ageRange" class="form-control" placeholder="如：全年龄段" type="text" />
            </div>
            <div class="form-group">
              <label>预防疾病</label>
              <input v-model="addForm.targetDisease" class="form-control" placeholder="如：预防乙型肝炎" type="text" />
            </div>
          </div>
          <div class="form-group">
            <label>生产厂家</label>
            <input v-model="addForm.manufacturer" class="form-control" placeholder="如：华北制药金坦生物" type="text" />
          </div>
          <div class="form-section-label">详细信息</div>
          <div class="form-group">
            <label>接种时间安排</label>
            <textarea v-model="addForm.scheduleInfo" class="form-control" placeholder="如：周一至周五 8:00-11:30, 14:00-16:30；周末休息" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label>疫苗简介</label>
            <textarea v-model="addForm.description" class="form-control" placeholder="简要描述疫苗的特点和适用信息" rows="3"></textarea>
          </div>
          <div class="form-section-label">库存与图片</div>
          <div class="form-row">
            <div class="form-group">
              <label>库存数量 *</label>
              <input v-model.number="addForm.stockQuantity" class="form-control" min="0" required type="number" />
            </div>
            <div class="form-group" style="display:flex; align-items:center; gap:12px; padding-top:28px;">
              <input v-model="addForm.available" style="width:18px;height:18px;" type="checkbox" />
              <label style="margin:0; cursor:pointer;">上架可用</label>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>疫苗图片</label>
              <div class="image-upload-area" @click="triggerFileInput">
                <input ref="fileInputRef" accept="image/*" style="display:none" type="file"
                       @change="onAddImageSelected"/>
                <img v-if="addImagePreview" :src="addImagePreview" alt="预览" class="image-preview-thumb"/>
                <div v-else class="image-upload-placeholder">
                  <span class="upload-placeholder-icon">🖼️</span>
                  <span>点击选择疫苗图片</span>
                </div>
              </div>
              <button v-if="addImagePreview" class="btn-remove-image" type="button" @click.stop="removeAddImage">
                移除图片
              </button>
            </div>
            <div class="form-group"></div>
          </div>
          <div class="btn-center-wrap">
            <button class="btn" type="submit">添加疫苗</button>
          </div>
        </form>
      </div>

      <!-- Vaccine List -->
      <div class="card">
        <h2>疫苗列表</h2>
        <div class="records-table-wrapper">
        <table>
          <thead>
            <tr>
              <th class="col-nowrap">ID</th>
              <th>疫苗名称</th>
              <th class="col-nowrap">分类</th>
              <th>规格/工艺</th>
              <th class="col-nowrap">库存</th>
              <th class="col-nowrap">状态</th>
              <th class="col-nowrap">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="v in vaccines" :key="v.id">
              <td class="col-nowrap">{{ v.id }}</td>
              <td>
                <strong>{{ v.name }}</strong>
                <br v-if="v.brand" /><span v-if="v.brand" class="table-spec">{{ v.brand }}</span>
              </td>
              <td class="col-nowrap">{{ v.category || '—' }}</td>
              <td class="table-spec">{{ [v.dosage, v.technique].filter(Boolean).join(' / ') || '—' }}</td>
              <td class="col-nowrap">{{ v.stockQuantity }}</td>
              <td class="col-nowrap">
                <span :style="v.available && v.stockQuantity > 0
                  ? 'padding:2px 10px; border-radius:50px; font-size:0.8rem; font-weight:600; background:#f0fdf4; color:#16a34a;'
                  : 'padding:2px 10px; border-radius:50px; font-size:0.8rem; font-weight:600; background:#fef2f2; color:#dc2626;'">
                  {{ v.available && v.stockQuantity > 0 ? '上架' : '下架' }}
                </span>
              </td>
              <td class="col-nowrap">
                <div class="table-btn-group">
                  <button class="btn btn-small btn-edit" @click="openEdit(v.id)">编辑</button>
                  <button :class="['btn btn-small', v.available ? 'btn-toggle-off' : 'btn-toggle-on']"
                          @click="toggleAvailability(v.id, v.available)">
                    {{ v.available ? '下架' : '上架' }}
                  </button>
                  <button class="btn btn-small btn-delete" @click="deleteVaccine(v.id)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        </div>
      </div>
    </div>

    <!-- Edit Modal -->
    <VaccineEditModal
      :vaccine="editingVaccine"
      :visible="editModalVisible"
      @close="editModalVisible = false"
      @saved="handleEditSave"
    />

    <SiteFooter />
  </div>
</template>

<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import VaccineEditModal from '@/components/VaccineEditModal.vue'
import type {Vaccine} from '@/components/VaccineCard.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const auth = useAuthStore()
const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)
const vaccines = ref<Vaccine[]>([])
const fileInputRef = ref<HTMLInputElement | null>(null)
const addImageFile = ref<File | null>(null)
const addImagePreview = ref<string | null>(null)

const addForm = reactive({
  name: '',
  category: '',
  brand: '',
  dosage: '',
  technique: '',
  dosesRequired: null as number | null,
  ageRange: '',
  targetDisease: '',
  manufacturer: '',
  scheduleInfo: '',
  description: '',
  stockQuantity: 100,
  available: true
})

const editModalVisible = ref(false)
const editingVaccine = ref<Vaccine | null>(null)
let editVaccineId: number | null = null

function showAlert(msg: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(msg, type)
}

async function loadVaccines() {
  try {
    const response = await api.get('/vaccines')
    vaccines.value = response.data
  } catch {
    showAlert('加载疫苗列表失败', 'error')
  }
}

async function addVaccine() {
  try {
    const response = await api.post('/vaccines', addForm)
    const newVaccine = response.data
    // Upload image if selected
    if (addImageFile.value && newVaccine.id) {
      const formData = new FormData()
      formData.append('file', addImageFile.value)
      await api.post(`/vaccines/${newVaccine.id}/upload-image`, formData, {
        headers: {'Content-Type': 'multipart/form-data'}
      })
    }
    showAlert('疫苗添加成功', 'success')
    await loadVaccines()
    // Reset form
    addForm.name = ''
    addForm.category = ''
    addForm.brand = ''
    addForm.dosage = ''
    addForm.technique = ''
    addForm.dosesRequired = null
    addForm.ageRange = ''
    addForm.targetDisease = ''
    addForm.manufacturer = ''
    addForm.scheduleInfo = ''
    addForm.description = ''
    addForm.stockQuantity = 100
    addForm.available = true
    removeAddImage()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '添加失败', 'error')
  }
}

function triggerFileInput() {
  fileInputRef.value?.click()
}

function onAddImageSelected(e: Event) {
  const input = e.target as HTMLInputElement
  if (input.files && input.files[0]) {
    const file = input.files[0]
    if (!file.type.startsWith('image/')) {
      showAlert('请选择图片文件', 'error')
      return
    }
    addImageFile.value = file
    const reader = new FileReader()
    reader.onload = (ev) => {
      addImagePreview.value = ev.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

function removeAddImage() {
  addImageFile.value = null
  addImagePreview.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

async function openEdit(vaccineId: number) {
  try {
    const response = await api.get(`/vaccines/${vaccineId}`)
    editVaccineId = vaccineId
    editingVaccine.value = response.data
    editModalVisible.value = true
  } catch {
    showAlert('加载疫苗信息失败', 'error')
  }
}

async function handleEditSave(data: Record<string, any>, imageFile?: File | null) {
  if (!editVaccineId) return
  try {
    await api.put(`/vaccines/${editVaccineId}`, data)
    // Upload new image if selected
    if (imageFile) {
      const formData = new FormData()
      formData.append('file', imageFile)
      await api.post(`/vaccines/${editVaccineId}/upload-image`, formData, {
        headers: {'Content-Type': 'multipart/form-data'}
      })
    }
    showAlert('疫苗更新成功', 'success')
    editModalVisible.value = false
    await loadVaccines()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '更新失败', 'error')
  }
}

async function deleteVaccine(vaccineId: number) {
  if (!confirm('确定要删除此疫苗吗？')) return
  try {
    await api.delete(`/vaccines/${vaccineId}`)
    showAlert('疫苗已删除', 'success')
    await loadVaccines()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '删除失败', 'error')
  }
}

async function toggleAvailability(vaccineId: number, current: boolean) {
  if (!confirm(`确定要${current ? '下架' : '上架'}此疫苗吗？`)) return
  try {
    await api.patch(`/vaccines/${vaccineId}/availability`, { available: !current })
    showAlert('状态已更新', 'success')
    await loadVaccines()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '更新失败', 'error')
  }
}

onMounted(() => {
  if (!auth.isAdmin) {
    router.replace('/')
    return
  }
  loadVaccines()
})
</script>
