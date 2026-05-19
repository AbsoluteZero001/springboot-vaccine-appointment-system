<template>
  <Teleport to="body">
    <div v-if="visible" class="modal-msg-overlay" @click.self="dismiss">
      <div :class="['modal-msg', type]">
        <div class="modal-msg-icon">{{ type === 'success' ? '✅' : '❌' }}</div>
        <p class="modal-msg-text">{{ message }}</p>
        <button class="modal-msg-close" @click="dismiss">知道了</button>
      </div>
    </div>
  </Teleport>
</template>

<script lang="ts" setup>
import {ref} from 'vue'

const visible = ref(false)
const message = ref('')
const type = ref<'success' | 'error'>('success')
let timer: ReturnType<typeof setTimeout> | null = null

function showModal(msg: string, t: 'success' | 'error' = 'success') {
  if (timer) clearTimeout(timer)
  message.value = msg
  type.value = t
  visible.value = true
  timer = setTimeout(() => {
    visible.value = false
  }, 3000)
}

function dismiss() {
  if (timer) clearTimeout(timer)
  visible.value = false
}

defineExpose({showModal})
</script>

<style scoped>
.modal-msg-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease;
}

.modal-msg {
  background: white;
  border-radius: 16px;
  padding: 32px 36px;
  text-align: center;
  max-width: 380px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  animation: scaleIn 0.25s ease;
}

.modal-msg-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.modal-msg-text {
  font-size: 1.05rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 20px;
  line-height: 1.5;
}

.modal-msg-close {
  padding: 10px 36px;
  border-radius: 50px;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.2s ease;
  font-family: inherit;
}

.success .modal-msg-close {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: white;
}

.success .modal-msg-close:hover {
  background: linear-gradient(135deg, #15803d, #16a34a);
  transform: translateY(-1px);
}

.error .modal-msg-close {
  background: linear-gradient(135deg, #dc2626, #ef4444);
  color: white;
}

.error .modal-msg-close:hover {
  background: linear-gradient(135deg, #b91c1c, #dc2626);
  transform: translateY(-1px);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes scaleIn {
  from {
    transform: scale(0.85);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
