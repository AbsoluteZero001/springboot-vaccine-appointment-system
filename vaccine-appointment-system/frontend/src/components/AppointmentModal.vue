<template>
  <div :class="['modal-overlay', { active: visible }]" @click.self="closeModal">
    <div class="modal-enhanced">
      <div class="modal-header">
        <div class="modal-header-left">
          <span class="modal-header-icon">📋</span>
          <h3>预约接种</h3>
        </div>
        <button class="modal-close" @click="closeModal" title="关闭">✕</button>
      </div>
      <div class="modal-body">
        <div class="modal-vaccine-summary">
          <div class="summary-header">
            <MedicalIllustration type="vaccine" size="sm" width="40px"/>
            <div>
              <div class="summary-vaccine-name">{{ vaccine?.name }}</div>
              <div class="summary-vaccine-brand">{{ vaccine?.brand || '品牌未指定' }}</div>
            </div>
          </div>
          <div class="summary-grid">
            <div class="summary-item">
              <span class="summary-label">剂量规格</span>
              <span class="summary-value">{{ vaccine?.dosage || '—' }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">需接种剂次</span>
              <span class="summary-value">{{ vaccine?.dosesRequired || '—' }} 剂</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">适用人群</span>
              <span class="summary-value">{{ vaccine?.ageRange || '—' }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">接种工艺</span>
              <span class="summary-value">{{ vaccine?.technique || '—' }}</span>
            </div>
          </div>
        </div>

        <div v-if="vaccine?.scheduleInfo" class="schedule-notice">
          <span class="notice-icon">🕐</span>
          <span>{{ vaccine.scheduleInfo }}</span>
        </div>

        <form @submit.prevent="submitBooking">
          <div class="form-group">
            <label>
              <span class="label-icon">📅</span> 选择预约日期
            </label>
            <input v-model="selectedDate" :min="today" class="form-control input-enhanced" type="date"
                   @change="updateTimeSlots"/>
          </div>
          <div class="form-group">
            <label>
              <span class="label-icon">⏰</span> 选择预约时段
            </label>
            <select v-model="selectedTime" class="form-control input-enhanced">
              <option value="">请先选择日期</option>
              <option v-for="slot in timeSlots" :key="slot.value" :disabled="!slot.value" :value="slot.value">
                {{ slot.text }}
              </option>
            </select>
          </div>
          <div v-if="selectedDate && selectedTime" class="booking-confirm-info">
            <span class="confirm-icon">✅</span>
            您选择的接种时间为：<strong>{{ selectedDate }} {{ selectedTime }}</strong>
          </div>
          <button class="btn btn-shimmer" type="submit" :disabled="!selectedDate || !selectedTime">
            <span v-if="!selectedDate || !selectedTime">请完善预约信息</span>
            <span v-else>确认预约</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import MedicalIllustration from './MedicalIllustration.vue'
import type {Vaccine} from './VaccineCard.vue'

const props = defineProps<{
  visible: boolean
  vaccine: Vaccine | null
}>()

const emit = defineEmits<{
  close: []
  booked: [appointmentTime: string]
}>()

const today = new Date().toISOString().split('T')[0]
const selectedDate = ref('')
const selectedTime = ref('')

const timeSlots = ref<{ text: string; value: string }[]>([])

function closeModal() {
  selectedDate.value = ''
  selectedTime.value = ''
  timeSlots.value = []
  emit('close')
}

function updateTimeSlots() {
  const dateVal = selectedDate.value
  if (!dateVal) {
    timeSlots.value = []
    return
  }

  const date = new Date(dateVal)
  const day = date.getDay()

  if (day === 0) {
    timeSlots.value = [{ text: '周日休息，请选择工作日', value: '' }]
  } else if (day === 6) {
    timeSlots.value = [
      { text: '08:00 - 08:30', value: '08:00' },
      { text: '08:30 - 09:00', value: '08:30' },
      { text: '09:00 - 09:30', value: '09:00' },
      { text: '09:30 - 10:00', value: '09:30' },
      { text: '10:00 - 10:30', value: '10:00' },
      { text: '10:30 - 11:00', value: '10:30' },
      { text: '11:00 - 11:30', value: '11:00' }
    ]
  } else {
    timeSlots.value = [
      { text: '08:00 - 08:30', value: '08:00' },
      { text: '08:30 - 09:00', value: '08:30' },
      { text: '09:00 - 09:30', value: '09:00' },
      { text: '09:30 - 10:00', value: '09:30' },
      { text: '10:00 - 10:30', value: '10:00' },
      { text: '10:30 - 11:00', value: '10:30' },
      { text: '11:00 - 11:30', value: '11:00' },
      { text: '14:00 - 14:30', value: '14:00' },
      { text: '14:30 - 15:00', value: '14:30' },
      { text: '15:00 - 15:30', value: '15:00' },
      { text: '15:30 - 16:00', value: '15:30' },
      { text: '16:00 - 16:30', value: '16:00' }
    ]
  }
}

function submitBooking() {
  if (!selectedDate.value || !selectedTime.value) return

  const date = new Date(selectedDate.value)
  if (date.getDay() === 0) return

  const appointmentTime = selectedDate.value + 'T' + selectedTime.value + ':00'
  emit('booked', appointmentTime)
}
</script>

<style scoped>
.modal-enhanced {
  background: white;
  border-radius: 20px;
  width: 540px;
  max-width: 94vw;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.25);
  animation: modalSlideUp 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.modal-header {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  padding: 20px 28px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.modal-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 70% 50%, rgba(76, 201, 240, 0.2) 0%, transparent 60%);
  pointer-events: none;
}

.modal-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-header-icon {
  font-size: 22px;
}

.modal-header h3 {
  font-size: 1.15rem;
  font-weight: 700;
  margin: 0;
  color: white;
  position: relative;
  z-index: 1;
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.15);
  color: white;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
  position: relative;
  z-index: 1;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
}

.modal-body {
  padding: 24px 28px;
}

.modal-vaccine-summary {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 20px;
  border: 1px solid #e2e8f0;
}

.summary-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.summary-vaccine-name {
  font-weight: 700;
  color: var(--dark-color);
  font-size: 1rem;
}

.summary-vaccine-brand {
  font-size: 0.8rem;
  color: var(--gray-light);
  margin-top: 2px;
}

.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.84rem;
}

.summary-label {
  color: var(--gray-light);
}

.summary-value {
  font-weight: 600;
  color: var(--dark-color);
}

.schedule-notice {
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 10px;
  padding: 12px 16px;
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 0.85rem;
  color: #92400e;
  line-height: 1.5;
}

.notice-icon {
  font-size: 18px;
  flex-shrink: 0;
  margin-top: 1px;
}

.form-group label {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--dark-color);
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.label-icon {
  font-size: 16px;
}

.form-group .form-control {
  padding: 12px 16px;
  font-size: 15px;
}

.booking-confirm-info {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 10px;
  padding: 12px 16px;
  font-size: 0.88rem;
  color: #166534;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  animation: slide-up 0.3s ease-out;
}

.confirm-icon {
  font-size: 18px;
}

.modal-body .btn {
  width: 100%;
  padding: 14px;
  font-size: 15px;
  margin-top: 4px;
}

.modal-body .btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.modal-body .btn:disabled:hover {
  transform: none;
}

@media (max-width: 480px) {
  .modal-body {
    padding: 18px 20px;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
