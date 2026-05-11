<template>
  <div :class="['medical-illustration-wrap', size]" :style="{ width: width || 'auto', height: height || 'auto' }">
    <!-- 疫苗瓶 -->
    <svg v-if="type === 'vaccine'" viewBox="0 0 120 160" fill="none" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="vg1" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#e0e7ff"/>
          <stop offset="100%" style="stop-color:#c7d2fe"/>
        </linearGradient>
        <linearGradient id="vg2" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#4361ee"/>
          <stop offset="100%" style="stop-color:#7209b7"/>
        </linearGradient>
      </defs>
      <rect x="42" y="30" width="36" height="55" rx="6" fill="url(#vg1)" stroke="#a5b4fc" stroke-width="2"/>
      <rect x="50" y="10" width="20" height="24" rx="4" fill="url(#vg2)" opacity="0.3"/>
      <rect x="54" y="14" width="12" height="16" rx="3" fill="#e0e7ff"/>
      <circle cx="60" cy="50" r="8" fill="url(#vg2)" opacity="0.5"/>
      <line x1="48" y1="70" x2="72" y2="70" stroke="#a5b4fc" stroke-width="1.5" stroke-linecap="round"/>
      <line x1="48" y1="78" x2="72" y2="78" stroke="#a5b4fc" stroke-width="1.5" stroke-linecap="round"/>
      <rect x="48" y="90" width="24" height="4" rx="2" fill="url(#vg2)" opacity="0.6"/>
      <rect x="50" y="98" width="20" height="3" rx="1.5" fill="#c7d2fe"/>
    </svg>

    <!-- 注射器 -->
    <svg v-else-if="type === 'syringe'" viewBox="0 0 200 100" fill="none" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="sg1" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#4cc9f0"/>
          <stop offset="100%" style="stop-color:#4361ee"/>
        </linearGradient>
      </defs>
      <rect x="20" y="42" width="120" height="12" rx="6" fill="url(#sg1)" opacity="0.3"/>
      <rect x="30" y="44" width="100" height="8" rx="4" fill="url(#sg1)" opacity="0.5"/>
      <rect x="140" y="40" width="20" height="16" rx="3" fill="#e0e7ff" stroke="#a5b4fc" stroke-width="1.5"/>
      <line x1="160" y1="48" x2="185" y2="48" stroke="#a5b4fc" stroke-width="2" stroke-linecap="round"/>
      <line x1="185" y1="48" x2="192" y2="48" stroke="#6366f1" stroke-width="3" stroke-linecap="round"/>
      <rect x="40" y="34" width="12" height="28" rx="2" fill="#c7d2fe" opacity="0.5"/>
      <rect x="60" y="36" width="10" height="24" rx="2" fill="#c7d2fe" opacity="0.5"/>
      <circle cx="96" cy="48" r="6" fill="white" stroke="#a5b4fc" stroke-width="1"/>
    </svg>

    <!-- 医院建筑 -->
    <svg v-else-if="type === 'hospital'" viewBox="0 0 200 160" fill="none" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="hg1" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" style="stop-color:#eef2ff"/>
          <stop offset="100%" style="stop-color:#e0e7ff"/>
        </linearGradient>
      </defs>
      <rect x="40" y="30" width="120" height="110" rx="8" fill="url(#hg1)" stroke="#c7d2fe" stroke-width="2"/>
      <rect x="70" y="50" width="60" height="30" rx="4" fill="#ddd6fe"/>
      <rect x="75" y="55" width="22" height="20" rx="2" fill="white" stroke="#c7d2fe" stroke-width="1"/>
      <rect x="103" y="55" width="22" height="20" rx="2" fill="white" stroke="#c7d2fe" stroke-width="1"/>
      <rect x="90" y="90" width="20" height="50" rx="3" fill="#ddd6fe"/>
      <rect x="94" y="94" width="12" height="16" rx="2" fill="white" stroke="#c7d2fe" stroke-width="1"/>
      <rect x="55" y="60" width="10" height="70" rx="2" fill="#ddd6fe" opacity="0.5"/>
      <rect x="135" y="60" width="10" height="70" rx="2" fill="#ddd6fe" opacity="0.5"/>
      <circle cx="60" cy="40" r="8" fill="#f72585" opacity="0.7"/>
      <circle cx="60" cy="40" r="4" fill="white"/>
      <rect x="58" y="44" width="4" height="8" rx="1" fill="white"/>
    </svg>

    <!-- 盾牌（安全/防护） -->
    <svg v-else-if="type === 'shield'" viewBox="0 0 140 160" fill="none" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="shg1" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#4361ee"/>
          <stop offset="100%" style="stop-color:#7209b7"/>
        </linearGradient>
      </defs>
      <path d="M70 15 L130 40 L130 85 C130 120 70 148 70 148 C70 148 10 120 10 85 L10 40 Z" fill="url(#shg1)"
            opacity="0.15" stroke="url(#shg1)" stroke-width="2.5"/>
      <path d="M70 30 L115 48 L115 82 C115 110 70 132 70 132 C70 132 25 110 25 82 L25 48 Z" fill="url(#shg1)"
            opacity="0.25"/>
      <text x="70" y="95" text-anchor="middle" font-size="36" font-weight="bold" fill="white" opacity="0.8">✓</text>
    </svg>

    <!-- 日历（预约） -->
    <svg v-else-if="type === 'calendar'" viewBox="0 0 140 150" fill="none" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="cg1" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#4cc9f0"/>
          <stop offset="100%" style="stop-color:#4361ee"/>
        </linearGradient>
      </defs>
      <rect x="15" y="30" width="110" height="105" rx="10" fill="white" stroke="#c7d2fe" stroke-width="2"/>
      <rect x="15" y="30" width="110" height="30" rx="10" fill="url(#cg1)" opacity="0.15"/>
      <rect x="15" y="50" width="110" height="10" fill="url(#cg1)" opacity="0.15"/>
      <circle cx="45" cy="45" r="3" fill="#f72585" opacity="0.7"/>
      <circle cx="60" cy="45" r="3" fill="#f72585" opacity="0.7"/>
      <circle cx="75" cy="45" r="3" fill="#f72585" opacity="0.7"/>
      <rect x="30" y="68" width="14" height="10" rx="2" fill="#e0e7ff"/>
      <rect x="55" y="68" width="14" height="10" rx="2" fill="#e0e7ff"/>
      <rect x="80" y="68" width="14" height="10" rx="2" fill="#e0e7ff"/>
      <rect x="30" y="85" width="14" height="10" rx="2" fill="#ddd6fe"/>
      <rect x="55" y="85" width="14" height="10" rx="2" fill="#ddd6fe"/>
      <rect x="80" y="85" width="14" height="10" rx="2" fill="#ddd6fe"/>
      <rect x="30" y="102" width="14" height="10" rx="2" fill="#fce7f3"/>
      <rect x="55" y="102" width="14" height="10" rx="2" fill="#fce7f3"/>
      <rect x="80" y="102" width="14" height="10" rx="2" fill="#fce7f3"/>
      <rect x="105" y="68" width="14" height="10" rx="2" fill="#e0e7ff"/>
      <rect x="105" y="85" width="14" height="10" rx="2" fill="#ddd6fe"/>
      <rect x="105" y="102" width="14" height="10" rx="2" fill="#fce7f3"/>
    </svg>

    <!-- 医生 -->
    <svg v-else-if="type === 'doctor'" viewBox="0 0 140 160" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="70" cy="45" r="25" fill="#e0e7ff" stroke="#c7d2fe" stroke-width="2"/>
      <circle cx="62" cy="40" r="3" fill="#4361ee" opacity="0.6"/>
      <circle cx="78" cy="40" r="3" fill="#4361ee" opacity="0.6"/>
      <path d="M62 52 Q70 58 78 52" stroke="#7209b7" stroke-width="2" stroke-linecap="round" opacity="0.5"/>
      <path d="M25 130 Q70 80 115 130" fill="#e0e7ff" stroke="#c7d2fe" stroke-width="2"/>
      <rect x="45" y="80" width="50" height="8" rx="3" fill="#4361ee" opacity="0.15"/>
      <circle cx="70" cy="40" r="28" fill="none" stroke="#4cc9f0" stroke-width="1.5" opacity="0.3"
              stroke-dasharray="6 3"/>
    </svg>

    <!-- 心跳/心电 -->
    <svg v-else-if="type === 'heartbeat'" viewBox="0 0 200 100" fill="none" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="hbg1" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" style="stop-color:#f72585"/>
          <stop offset="100%" style="stop-color:#4cc9f0"/>
        </linearGradient>
      </defs>
      <polyline points="10,50 40,50 55,50 63,20 71,80 79,50 95,50 130,50 145,50 153,20 161,80 169,50 190,50"
                fill="none" stroke="url(#hbg1)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
      <circle cx="40" cy="50" r="3" fill="#f72585"/>
      <circle cx="95" cy="50" r="3" fill="#7209b7"/>
      <circle cx="190" cy="50" r="3" fill="#4cc9f0"/>
    </svg>

    <!-- 药丸/胶囊 -->
    <svg v-else-if="type === 'capsule'" viewBox="0 0 120 80" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="10" y="20" width="45" height="40" rx="20" fill="#fce7f3" stroke="#f72585" stroke-width="1.5"
            opacity="0.6"/>
      <rect x="65" y="20" width="45" height="40" rx="20" fill="#e0e7ff" stroke="#4361ee" stroke-width="1.5"
            opacity="0.6"/>
    </svg>

    <!-- 放大镜（搜索） -->
    <svg v-else-if="type === 'search'" viewBox="0 0 140 140" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="55" cy="55" r="35" fill="none" stroke="#c7d2fe" stroke-width="4"/>
      <circle cx="55" cy="55" r="25" fill="#eef2ff" opacity="0.5"/>
      <line x1="80" y1="80" x2="120" y2="120" stroke="#c7d2fe" stroke-width="6" stroke-linecap="round"/>
    </svg>

    <!-- 默认占位 -->
    <div v-else class="default-placeholder">
      <slot>💉</slot>
    </div>
  </div>
</template>

<script lang="ts" setup>
withDefaults(defineProps<{
  type: 'vaccine' | 'syringe' | 'hospital' | 'shield' | 'calendar' | 'doctor' | 'heartbeat' | 'capsule' | 'search'
  size?: 'sm' | 'md' | 'lg'
  width?: string
  height?: string
}>(), {
  type: 'vaccine',
  size: 'md'
})
</script>

<style scoped>
.medical-illustration-wrap {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.medical-illustration-wrap.sm svg {
  width: 60px;
  height: auto;
}

.medical-illustration-wrap.md svg {
  width: 120px;
  height: auto;
}

.medical-illustration-wrap.lg svg {
  width: 200px;
  height: auto;
}

.medical-illustration-wrap svg {
  max-width: 100%;
  height: auto;
}

.default-placeholder {
  font-size: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
