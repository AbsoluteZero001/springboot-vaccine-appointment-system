<template>
  <div v-if="isLoading" class="global-loader">
    <div class="loader-overlay"></div>
    <div class="loader-content">
      <div class="loading"></div>
      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script setup>
import {isLoading} from '@/services/api'

defineProps({
  message: { type: String, default: '加载中...' }
})
</script>
