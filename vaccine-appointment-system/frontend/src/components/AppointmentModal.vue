<template>
  <div :class="['modal-overlay', { active: visible }]" @click.self="closeModal">
    <div class="modal">
      <div class="modal-header">
        <h3>预约接种</h3>
        <button class="modal-close" @click="closeModal">✕</button>
      </div>
      <div class="modal-body">
        <div class="modal-vaccine-summary">
          <div class="summary-row">
            <span class="label">疫苗名称</span>
            <span class="value">{{ vaccine?.name }}</span>
          </div>
          <div class="summary-row">
            <span class="label">品牌</span>
            <span class="value">{{ vaccine?.brand || '—' }}</span>
          </div>
          <div class="divider"></div>
          <div class="summary-row">
            <span class="label">剂量规格</span>
            <span class="value">{{ vaccine?.dosage || '—' }}</span>
          </div>
          <div class="summary-row">
            <span class="label">需接种剂次</span>
            <span class="value">{{ vaccine?.dosesRequired || '—' }} 剂</span>
          </div>
          <div class="summary-row">
            <span class="label">适用人群</span>
            <span class="value">{{ vaccine?.ageRange || '—' }}</span>
          </div>
        </div>

        <div v-if="vaccine?.scheduleInfo" class="schedule-notice">
          <span class="notice-icon">🕐</span>
          <span>接种时间：{{ vaccine.scheduleInfo }}</span>
        </div>

        <form @submit.prevent="submitBooking">
          <div class="form-group">
            <label>选择预约日期</label>
            <input v-model="selectedDate" :min="today" class="form-control" type="date" @change="updateTimeSlots" />
          </div>
          <div class="form-group">
            <label>选择预约时段</label>
            <select v-model="selectedTime" class="form-control">
              <option value="">请先选择日期</option>
              <option v-for="slot in timeSlots" :key="slot.value" :disabled="!slot.value" :value="slot.value">
                {{ slot.text }}
              </option>
            </select>
          </div>
          <button class="btn" type="submit">确认预约</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
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
