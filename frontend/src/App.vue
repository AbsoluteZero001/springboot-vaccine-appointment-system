<template>
  <div id="app-root">
    <LoadingOverlay v-if="!auth.isAuthReady" />
    <router-view v-else />
  </div>
</template>

<script setup>
import {onMounted} from 'vue'
import LoadingOverlay from '@/components/LoadingOverlay.vue'
import {useAuthStore} from '@/stores/auth'

const auth = useAuthStore()

onMounted(() => {
  auth.verifySession()
})
</script>
