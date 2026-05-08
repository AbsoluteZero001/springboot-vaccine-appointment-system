<template>
  <div id="app-root">
    <LoadingOverlay />
    <router-view />
  </div>
</template>

<script lang="ts" setup>
import LoadingOverlay from '@/components/LoadingOverlay.vue'
</script>
