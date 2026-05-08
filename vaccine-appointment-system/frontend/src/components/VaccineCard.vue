<template>
  <div class="vaccine-card-modern">
    <div class="vaccine-card-header">
      <div class="badge-group">
        <span v-if="vaccine.category" class="vaccine-badge category">{{ vaccine.category }}</span>
        <span v-if="vaccine.technique" class="vaccine-badge technique">{{ vaccine.technique }}</span>
        <span v-if="vaccine.dosage" class="vaccine-badge dosage">{{ vaccine.dosage }}</span>
        <span v-if="vaccine.targetDisease" class="vaccine-badge disease">{{ vaccine.targetDisease }}</span>
      </div>
    </div>
    <div class="vaccine-card-body">
      <h3>{{ vaccine.name || '未知疫苗' }}</h3>
      <div class="brand">
        {{ vaccine.brand ? '品牌：' + vaccine.brand : '' }}{{ vaccine.manufacturer ? ' | ' + vaccine.manufacturer : '' }}
      </div>
      <div class="spec-grid">
        <div v-if="vaccine.dosage" class="spec-item"><span class="label">剂量</span> {{ vaccine.dosage }}</div>
        <div v-if="vaccine.technique" class="spec-item"><span class="label">工艺</span> {{ vaccine.technique }}</div>
        <div v-if="vaccine.dosesRequired" class="spec-item"><span class="label">剂次</span> {{ vaccine.dosesRequired }}剂</div>
        <div v-if="vaccine.ageRange" class="spec-item"><span class="label">适用</span> {{ vaccine.ageRange }}</div>
      </div>
      <div v-if="vaccine.description" class="description">{{ vaccine.description }}</div>
    </div>
    <div class="vaccine-card-footer">
      <div>
        <div class="stock-info">
          <span :class="stockClass">●</span>
          <span>{{ stockText }}（{{ vaccine.stockQuantity }}剂）</span>
        </div>
        <div v-if="vaccine.scheduleInfo" class="schedule-hint">
          🕐 {{ truncate(vaccine.scheduleInfo, 20) }}
        </div>
      </div>
      <button :disabled="!canBook" class="btn-book" @click="$emit('book', vaccine)">
        {{ canBook ? '立即预约' : '已缺货' }}
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'

export interface Vaccine {
  id: number
  name: string
  category?: string
  brand?: string
  dosage?: string
  technique?: string
  dosesRequired?: number
  ageRange?: string
  targetDisease?: string
  manufacturer?: string
  scheduleInfo?: string
  description?: string
  stockQuantity: number
  available: boolean
}

const props = defineProps<{ vaccine: Vaccine }>()
defineEmits<{ book: [vaccine: Vaccine] }>()

const canBook = computed(() => props.vaccine.stockQuantity > 0)
const stockClass = computed(() =>
  props.vaccine.stockQuantity > 50 ? 'in-stock' : props.vaccine.stockQuantity > 0 ? 'low-stock' : 'out-of-stock'
)
const stockText = computed(() =>
  props.vaccine.stockQuantity > 50 ? '库存充足' : props.vaccine.stockQuantity > 0 ? '库存紧张' : '已缺货'
)

function truncate(text: string, len: number): string {
  return text.length > len ? text.substring(0, len) + '...' : text
}
</script>
