<template>
  <div>
    <SiteHeader active-nav="/admin/vaccines" />

    <div class="container">
      <AlertMessage ref="alertRef" />

      <!-- Add Vaccine Form -->
      <div class="card">
        <h2>添加新疫苗</h2>
        <form @submit.prevent="addVaccine">
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
          <div class="form-group">
            <label>接种时间安排</label>
            <textarea v-model="addForm.scheduleInfo" class="form-control" placeholder="如：周一至周五 8:00-11:30, 14:00-16:30；周末休息" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label>疫苗简介</label>
            <textarea v-model="addForm.description" class="form-control" placeholder="简要描述疫苗的特点和适用信息" rows="3"></textarea>
          </div>
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
          <button class="btn" type="submit">添加疫苗</button>
        </form>
      </div>

      <!-- Vaccine List -->
      <div class="card">
        <h2>疫苗列表</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>疫苗名称</th>
              <th>分类</th>
              <th>规格/工艺</th>
              <th>库存</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="v in vaccines" :key="v.id">
              <td>{{ v.id }}</td>
              <td>
                <strong>{{ v.name }}</strong>
                <br v-if="v.brand" /><span v-if="v.brand" class="table-spec">{{ v.brand }}</span>
              </td>
              <td>{{ v.category || '—' }}</td>
              <td class="table-spec">{{ [v.dosage, v.technique].filter(Boolean).join(' / ') || '—' }}</td>
              <td>{{ v.stockQuantity }}</td>
              <td>
                <span :style="v.available && v.stockQuantity > 0
                  ? 'padding:2px 10px; border-radius:50px; font-size:0.8rem; font-weight:600; background:#f0fdf4; color:#16a34a;'
                  : 'padding:2px 10px; border-radius:50px; font-size:0.8rem; font-weight:600; background:#fef2f2; color:#dc2626;'">
                  {{ v.available && v.stockQuantity > 0 ? '上架' : '下架' }}
                </span>
              </td>
              <td>
                <button class="btn btn-small" @click="openEdit(v.id)">编辑</button>
                <button :style="{ background: v.available ? 'var(--danger-color)' : 'var(--success-color)' }" class="btn btn-small"
                  @click="toggleAvailability(v.id, v.available)">
                  {{ v.available ? '下架' : '上架' }}
                </button>
                <button class="btn btn-danger btn-small" @click="deleteVaccine(v.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
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
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import VaccineEditModal from '@/components/VaccineEditModal.vue'
import type {Vaccine} from '@/components/VaccineCard.vue'
import api from '@/services/api'

const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)
const vaccines = ref<Vaccine[]>([])

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
    await api.post('/vaccines', addForm)
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
  } catch (error: any) {
    showAlert(error.response?.data?.error || '添加失败', 'error')
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

async function handleEditSave(data: Record<string, any>) {
  if (!editVaccineId) return
  try {
    await api.put(`/vaccines/${editVaccineId}`, data)
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
  loadVaccines()
})
</script>
