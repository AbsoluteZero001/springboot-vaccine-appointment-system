<template>
  <footer id="footer" class="footer">
    <div class="container">
      <p>疫苗预约系统 &copy; 2026 | 守护健康，疫苗先行</p>
      <p style="font-size: 0.85rem; color: #94a3b8; margin-top: 8px;">
        <template v-if="adminMode">国家免疫规划信息平台 · 安全 · 便捷 · 可靠</template>
        <template v-else>国家免疫规划信息平台 · 安全 · 便捷 · 可靠</template>
      </p>
    </div>
  </footer>
</template>

<script lang="ts" setup>
defineProps<{ adminMode?: boolean }>()
</script>
