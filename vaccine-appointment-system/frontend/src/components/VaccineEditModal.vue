<template>
  <div :class="['edit-modal', { active: visible }]" @click.self="$emit('close')">
    <div class="edit-modal-content">
      <div class="edit-modal-header">
        <h3>编辑疫苗</h3>
        <button class="modal-close" @click="$emit('close')">✕</button>
      </div>
      <div class="edit-modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-section-label">基本信息</div>
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
          <div class="form-section-label">详细信息</div>
          <div class="form-group">
            <label>接种时间安排</label>
            <textarea v-model="form.scheduleInfo" class="form-control" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label>简介</label>
            <textarea v-model="form.description" class="form-control" rows="2"></textarea>
          </div>
          <div class="form-section-label">库存与图片</div>
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
          <div class="form-row">
            <div class="form-group">
              <label>疫苗图片</label>
              <div class="image-upload-area" @click="triggerFileInput">
                <input ref="fileInputRef" accept="image/*" style="display:none" type="file" @change="onImageSelected"/>
                <img v-if="imagePreview" :src="imagePreview" alt="预览" class="image-preview-thumb"/>
                <div v-else class="image-upload-placeholder">
                  <span class="upload-placeholder-icon">🖼️</span>
                  <span>点击更换疫苗图片</span>
                </div>
              </div>
              <button v-if="imagePreview" class="btn-remove-image" type="button" @click.stop="removeImage">移除图片
              </button>
            </div>
            <div class="form-group"></div>
          </div>
          <div class="btn-center-wrap">
            <button class="btn" type="submit">保存修改</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {reactive, ref, watch} from 'vue'
import type {Vaccine} from './VaccineCard.vue'

const props = defineProps<{
  visible: boolean
  vaccine: Vaccine | null
}>()

const emit = defineEmits<{
  close: []
  saved: [data: Record<string, any>, imageFile?: File | null]
}>()

const fileInputRef = ref<HTMLInputElement | null>(null)
const selectedImageFile = ref<File | null>(null)
const imagePreview = ref<string | null>(null)

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
    // Reset image selection when vaccine changes
    selectedImageFile.value = null
    imagePreview.value = null
    if (fileInputRef.value) {
      fileInputRef.value.value = ''
    }
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
      // Show existing image if available
      if (v.imageUrl) {
        imagePreview.value = v.imageUrl
      }
    }
  }
)

function triggerFileInput() {
  fileInputRef.value?.click()
}

function onImageSelected(e: Event) {
  const input = e.target as HTMLInputElement
  if (input.files && input.files[0]) {
    const file = input.files[0]
    if (!file.type.startsWith('image/')) {
      return
    }
    selectedImageFile.value = file
    const reader = new FileReader()
    reader.onload = (ev) => {
      imagePreview.value = ev.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

function removeImage() {
  selectedImageFile.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
  // Restore existing image if available
  imagePreview.value = props.vaccine?.imageUrl || null
}

function handleSubmit() {
  emit('saved', {...form}, selectedImageFile.value)
}
</script>
