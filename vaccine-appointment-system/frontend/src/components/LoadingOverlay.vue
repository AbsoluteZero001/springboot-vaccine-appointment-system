<template>
  <div v-if="isLoading" class="global-loader">
    <div class="loader-overlay"></div>
    <div class="loader-content">
      <div class="loading"></div>
      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {isLoading} from '@/services/api'

withDefaults(defineProps<{ message?: string }>(), { message: 'Loading...' })
</script>
