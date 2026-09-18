<template>
  <div :class="msgClass">{{ displayText }}</div>
</template>

<script setup>
import {computed, onUnmounted, ref, watch} from 'vue'

const props = defineProps({
  data: { type: Object, default: null }
})

const freezeRemaining = ref(0)
let timer = null

const isFrozen = computed(() => props.data?.frozen === true && freezeRemaining.value > 0)
const isError = computed(() => !props.data?.frozen && !!props.data?.error)

const msgClass = computed(() => {
  if (isFrozen.value) return 'login-msg login-msg-freeze'
  if (isError.value) return 'login-msg login-msg-error'
  return 'login-msg'
})

const displayText = computed(() => {
  if (isFrozen.value) return `正在冷却中：${freezeRemaining.value}秒`
  if (isError.value) return props.data?.error || ''
  return ''
})

function clearTimer() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

function startCountdown(seconds) {
  clearTimer()
  freezeRemaining.value = seconds
  timer = setInterval(() => {
    freezeRemaining.value--
    if (freezeRemaining.value <= 0) {
      clearTimer()
    }
  }, 1000)
}

watch(
  () => props.data,
  (newData) => {
    clearTimer()
    if (newData?.frozen && (newData.freezeSeconds ?? 0) > 0) {
      startCountdown(newData.freezeSeconds)
    } else {
      freezeRemaining.value = 0
    }
  },
  { immediate: true }
)

onUnmounted(() => clearTimer())
</script>
