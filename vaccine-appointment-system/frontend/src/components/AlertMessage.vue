<template>
  <div v-if="alerts.length" class="alert-container">
    <div
      v-for="(alert, index) in alerts"
      :key="index"
      :class="['alert', `alert-${alert.type}`]"
    >
      {{ alert.message }}
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'

interface Alert {
  message: string
  type: 'success' | 'error'
}

const alerts = ref<Alert[]>([])

function showAlert(message: string, type: 'success' | 'error' = 'success') {
  const alert: Alert = { message, type }
  alerts.value.push(alert)
  setTimeout(() => {
    const idx = alerts.value.indexOf(alert)
    if (idx > -1) alerts.value.splice(idx, 1)
  }, 5000)
}

defineExpose({ showAlert })
</script>
