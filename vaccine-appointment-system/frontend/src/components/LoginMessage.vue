<template>
  <div :class="msgClass">{{ displayText }}</div>
</template>

<script lang="ts" setup>
import {computed, onUnmounted, ref, watch} from 'vue'

interface LoginData {
  error?: string
  frozen?: boolean
  freezeSeconds?: number
}

const props = defineProps<{ data: LoginData | null }>()

const freezeRemaining = ref(0)
let timer: ReturnType<typeof setInterval> | null = null

const isFrozen = computed(() => props.data?.frozen === true && freezeRemaining.value > 0)
const isError = computed(() => !props.data?.frozen && !!props.data?.error)

const msgClass = computed(() => {
  if (isFrozen.value) return 'login-msg login-msg-freeze'
  if (isError.value) return 'login-msg login-msg-error'
  return 'login-msg'
})

const displayText = computed(() => {
  if (isFrozen.value) return `正在冷却中：${freezeRemaining.value}s`
  if (isError.value) return props.data?.error || ''
  return ''
})

function clearTimer() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

function startCountdown(seconds: number) {
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
      startCountdown(newData.freezeSeconds!)
    } else {
      freezeRemaining.value = 0
    }
  },
  { immediate: true }
)

onUnmounted(() => clearTimer())
</script>
