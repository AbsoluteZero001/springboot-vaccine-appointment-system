<template>
  <div class="vaccine-card-modern stagger-enter" :style="{ animationDelay: `${staggerIndex * 0.06}s` }">
    <div class="vaccine-card-header">
      <div class="badge-group">
        <span v-if="vaccine.category" class="vaccine-badge category">{{ vaccine.category }}</span>
        <span v-if="vaccine.technique" class="vaccine-badge technique">{{ vaccine.technique }}</span>
        <span v-if="vaccine.targetDisease" class="vaccine-badge disease">{{ vaccine.targetDisease }}</span>
      </div>
      <div class="stock-indicator" :class="stockClass" :title="stockText">
        <span class="status-dot" :class="stockDotClass"></span>
      </div>
    </div>
    <div class="vaccine-card-body">
      <div class="vaccine-illustration-area">
        <img v-if="vaccine.imageUrl" :src="vaccine.imageUrl" :alt="vaccine.name" class="vaccine-image"/>
        <MedicalIllustration v-else :type="illustrationType" size="sm"/>
      </div>
      <h3>{{ vaccine.name || '未知疫苗' }}</h3>
      <div class="brand">
        {{ vaccine.brand ? '品牌：' + vaccine.brand : '' }}{{ vaccine.manufacturer ? ' | ' + vaccine.manufacturer : '' }}
      </div>
      <div class="spec-grid">
        <div v-if="vaccine.dosage" class="spec-item">
          <span class="spec-icon">💊</span>
          <span class="label">剂量</span> {{ vaccine.dosage }}
        </div>
        <div v-if="vaccine.technique" class="spec-item">
          <span class="spec-icon">🔬</span>
          <span class="label">工艺</span> {{ vaccine.technique }}
        </div>
        <div v-if="vaccine.dosesRequired" class="spec-item">
          <span class="spec-icon">📋</span>
          <span class="label">剂次</span> {{ vaccine.dosesRequired }}剂
        </div>
        <div v-if="vaccine.ageRange" class="spec-item">
          <span class="spec-icon">👥</span>
          <span class="label">适用</span> {{ vaccine.ageRange }}
        </div>
      </div>
      <div v-if="vaccine.description" class="description">{{ vaccine.description }}</div>
    </div>
    <div class="vaccine-card-footer">
      <div>
        <div class="stock-info">
          <span :class="stockClass">
            <span class="stock-icon">{{ stockIcon }}</span>
            {{ stockText }}
          </span>
          <span class="stock-count">库存 {{ vaccine.stockQuantity }} 剂</span>
        </div>
        <div v-if="vaccine.scheduleInfo" class="schedule-hint">
          🕐 {{ truncate(vaccine.scheduleInfo, 24) }}
        </div>
      </div>
      <button
          :disabled="!canBook"
          class="btn-book btn-shimmer"
          :class="{ 'btn-book-disabled': !canBook }"
          @click="$emit('book', vaccine)"
      >
        <span class="btn-book-text">{{ canBook ? '立即预约' : '已缺货' }}</span>
        <span v-if="canBook" class="btn-book-arrow">→</span>
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import MedicalIllustration from './MedicalIllustration.vue'

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
  imageUrl?: string
  stockQuantity: number
  available: boolean
}

const props = defineProps<{
  vaccine: Vaccine
  staggerIndex?: number
}>()

defineEmits<{ book: [vaccine: Vaccine] }>()

const canBook = computed(() => props.vaccine.stockQuantity > 0 && props.vaccine.available !== false)
const stockClass = computed(() =>
  props.vaccine.stockQuantity > 50 ? 'in-stock' : props.vaccine.stockQuantity > 0 ? 'low-stock' : 'out-of-stock'
)
const stockDotClass = computed(() =>
    props.vaccine.stockQuantity > 50 ? 'online' : props.vaccine.stockQuantity > 0 ? 'warning' : 'offline'
)
const stockText = computed(() =>
  props.vaccine.stockQuantity > 50 ? '库存充足' : props.vaccine.stockQuantity > 0 ? '库存紧张' : '已缺货'
)
const stockIcon = computed(() =>
    props.vaccine.stockQuantity > 50 ? '✅' : props.vaccine.stockQuantity > 0 ? '⚠️' : '❌'
)

const illustrationType = computed(() => {
  const cat = (props.vaccine.category || '').toLowerCase()
  const name = (props.vaccine.name || '').toLowerCase()
  if (cat.includes('新冠') || name.includes('新冠')) return 'syringe'
  if (cat.includes('流感') || name.includes('流感')) return 'capsule'
  if (cat.includes('儿童') || name.includes('儿童')) return 'doctor'
  if (cat.includes('hpv') || name.includes('hpv')) return 'shield'
  return 'vaccine'
})

function truncate(text: string, len: number): string {
  return text.length > len ? text.substring(0, len) + '...' : text
}
</script>

<style scoped>
.vaccine-card-modern {
  background: white;
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  position: relative;
}

.vaccine-card-modern::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color), var(--success-color));
  transform: scaleX(0);
  transition: transform 0.4s ease;
  z-index: 1;
}

.vaccine-card-modern:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 50px rgba(67, 97, 238, 0.12);
  border-color: rgba(67, 97, 238, 0.15);
}

.vaccine-card-modern:hover::before {
  transform: scaleX(1);
}

.vaccine-card-header {
  padding: 16px 20px 8px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
}

.badge-group {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.vaccine-badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 4px 12px;
  border-radius: 50px;
  font-size: 0.73rem;
  font-weight: 600;
  white-space: nowrap;
  letter-spacing: 0.01em;
}

.vaccine-badge.category {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.vaccine-badge.technique {
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}

.vaccine-badge.dosage {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.vaccine-badge.disease {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.stock-indicator {
  flex-shrink: 0;
  padding: 4px;
}

.vaccine-card-body {
  padding: 0 20px 12px;
  flex: 1;
}

.vaccine-illustration-area {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
  padding: 8px 0;
  opacity: 0.5;
  transition: opacity 0.3s ease;
}

.vaccine-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 10px;
  opacity: 1;
}

.vaccine-card-modern:hover .vaccine-illustration-area {
  opacity: 0.8;
}

.vaccine-illustration-area :deep(svg) {
  width: 80px;
  height: auto;
  transition: transform 0.4s ease;
}

.vaccine-card-modern:hover .vaccine-illustration-area :deep(svg) {
  transform: scale(1.08);
}

.vaccine-card-body h3 {
  font-size: 1.08rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 4px;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.vaccine-card-modern:hover .vaccine-card-body h3 {
  color: var(--primary-color);
}

.brand {
  font-size: 0.82rem;
  color: var(--gray-color);
  margin-bottom: 12px;
}

.spec-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 7px 14px;
  margin-bottom: 10px;
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.8rem;
  color: var(--gray-color);
}

.spec-icon {
  font-size: 13px;
  flex-shrink: 0;
}

.spec-item .label {
  color: var(--gray-light);
  flex-shrink: 0;
  font-size: 0.75rem;
}

.description {
  font-size: 0.83rem;
  color: var(--gray-color);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-top: 8px;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}

.vaccine-card-footer {
  padding: 14px 20px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(180deg, #fafbfc, #f8fafc);
}

.stock-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stock-info .in-stock {
  color: #16a34a;
  font-weight: 600;
  font-size: 0.84rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stock-info .low-stock {
  color: #f59e0b;
  font-weight: 600;
  font-size: 0.84rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stock-info .out-of-stock {
  color: #dc2626;
  font-weight: 600;
  font-size: 0.84rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stock-icon {
  font-size: 14px;
}

.stock-count {
  font-size: 0.75rem;
  color: var(--gray-light);
}

.schedule-hint {
  font-size: 0.76rem;
  color: var(--gray-light);
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
}

.btn-book {
  padding: 9px 22px;
  border-radius: 50px;
  border: none;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  color: white;
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: inherit;
  box-shadow: 0 4px 15px rgba(67, 97, 238, 0.25);
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.btn-book:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(67, 97, 238, 0.4);
}

.btn-book:active {
  transform: translateY(0);
}

.btn-book-disabled {
  opacity: 0.45;
  cursor: not-allowed;
  filter: grayscale(0.3);
}

.btn-book-disabled:hover {
  transform: none;
}

.btn-book-arrow {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.btn-book:hover .btn-book-arrow {
  transform: translateX(3px);
}

.btn-book-text {
  position: relative;
  z-index: 1;
}
</style>
