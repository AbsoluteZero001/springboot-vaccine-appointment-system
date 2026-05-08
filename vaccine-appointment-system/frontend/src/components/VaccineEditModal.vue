<template>
  <div :class="['edit-modal', { active: visible }]" @click.self="$emit('close')">
    <div class="edit-modal-content">
      <div class="edit-modal-header">
        <h3>编辑疫苗</h3>
        <button class="modal-close" @click="$emit('close')">✕</button>
      </div>
      <div class="edit-modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>疫苗名称</label>
            <input v-model="form.name" class="form-control" required type="text" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>分类</label>
              <input v-model="form.category" class="form-control" type="text" />
            </div>
            <div class="form-group">
              <label>品牌</label>
              <input v-model="form.brand" class="form-control" type="text" />
            </div>
          </div>
          <div class="form-row-3">
            <div class="form-group">
              <label>剂量规格</label>
              <input v-model="form.dosage" class="form-control" type="text" />
            </div>
            <div class="form-group">
              <label>制作工艺</label>
              <input v-model="form.technique" class="form-control" type="text" />
            </div>
            <div class="form-group">
              <label>剂次</label>
              <input v-model.number="form.dosesRequired" class="form-control" min="1" type="number" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>适用人群</label>
              <input v-model="form.ageRange" class="form-control" type="text" />
            </div>
            <div class="form-group">
              <label>预防疾病</label>
              <input v-model="form.targetDisease" class="form-control" type="text" />
            </div>
          </div>
          <div class="form-group">
            <label>生产厂家</label>
            <input v-model="form.manufacturer" class="form-control" type="text" />
          </div>
          <div class="form-group">
            <label>接种时间安排</label>
            <textarea v-model="form.scheduleInfo" class="form-control" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label>简介</label>
            <textarea v-model="form.description" class="form-control" rows="2"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>库存</label>
              <input v-model.number="form.stockQuantity" class="form-control" min="0" required type="number" />
            </div>
            <div class="form-group" style="display:flex; align-items:center; gap:12px; padding-top:28px;">
              <input v-model="form.available" style="width:18px;height:18px;" type="checkbox" />
              <label style="margin:0; cursor:pointer;">上架可用</label>
            </div>
          </div>
          <button class="btn" type="submit">保存修改</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {reactive, watch} from 'vue'
import type {Vaccine} from './VaccineCard.vue'

const props = defineProps<{
  visible: boolean
  vaccine: Vaccine | null
}>()

const emit = defineEmits<{
  close: []
  saved: [data: Record<string, any>]
}>()

const form = reactive({
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
  stockQuantity: 0,
  available: true
})

watch(
  () => props.vaccine,
  (v) => {
    if (v) {
      form.name = v.name || ''
      form.category = v.category || ''
      form.brand = v.brand || ''
      form.dosage = v.dosage || ''
      form.technique = v.technique || ''
      form.dosesRequired = v.dosesRequired || null
      form.ageRange = v.ageRange || ''
      form.targetDisease = v.targetDisease || ''
      form.manufacturer = v.manufacturer || ''
      form.scheduleInfo = v.scheduleInfo || ''
      form.description = v.description || ''
      form.stockQuantity = v.stockQuantity || 0
      form.available = v.available
    }
  }
)

function handleSubmit() {
  emit('saved', { ...form })
}
</script>
