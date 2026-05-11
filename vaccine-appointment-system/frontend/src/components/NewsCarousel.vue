<template>
  <div id="newsCarousel" class="carousel-container" @mouseenter="pauseAutoPlay" @mouseleave="startAutoPlay">
    <div :style="{ transform: `translateX(-${currentSlide * 100}%)` }" class="carousel-track">
      <div v-for="(slide, index) in slides" :key="index" class="carousel-slide">
        <div class="carousel-slide-inner">
          <div :style="{ background: slide.bg }" class="carousel-image">
            <div class="carousel-image-pattern"></div>
            <span class="slide-icon">{{ slide.icon }}</span>
            <div class="carousel-image-bg"></div>
          </div>
          <div class="carousel-info">
            <span :class="['carousel-tag', slide.tagClass]">{{ slide.tag }}</span>
            <h3>{{ slide.title }}</h3>
            <p>{{ slide.desc }}</p>
            <div class="carousel-date-row">
              <span class="carousel-date">{{ slide.date }}</span>
              <span class="carousel-source">{{ slide.source }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="carousel-controls">
      <button class="carousel-btn" @click="prevSlide" title="上一条">‹</button>
      <button class="carousel-btn" @click="nextSlide" title="下一条">›</button>
    </div>

    <div class="carousel-indicators">
      <button
          v-for="(slide, index) in slides"
        :key="index"
        :class="['carousel-dot', { active: currentSlide === index }]"
          :title="slide.title"
        @click="goToSlide(index)"
      ></button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, onUnmounted, ref} from 'vue'

const slides = [
  {
    icon: '🦠',
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    tag: '热点',
    tagClass: 'hot',
    title: '新冠疫苗加强针接种通知',
    desc: '根据国家卫健委最新部署，即日起开展新冠疫苗加强针接种工作。建议已完成基础免疫满6个月的18岁以上人群尽快接种。',
    date: '2026-04-27',
    source: '国家卫健委'
  },
  {
    icon: '🌡️',
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    tag: '提醒',
    tagClass: 'new',
    title: '秋冬季流感疫苗预约已开放',
    desc: '2026年度流感疫苗预约接种已全面开放。老人、儿童及慢性病患者为重点推荐人群，建议在流感季来临前完成接种。',
    date: '2026-04-26',
    source: '疾控中心'
  },
  {
    icon: '🎗️',
    bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    tag: '关注',
    tagClass: 'hot',
    title: 'HPV疫苗扩龄至9-45岁',
    desc: '九价HPV疫苗接种年龄已正式扩展至9-45岁女性，预约需求激增。建议适龄人群尽早预约，早接种早受益。',
    date: '2026-04-25',
    source: '药监局'
  },
  {
    icon: '👶',
    bg: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
    tag: '科普',
    tagClass: 'info',
    title: '儿童疫苗接种时间表须知',
    desc: '按照国家免疫规划程序，儿童需按时完成卡介苗、乙肝疫苗、脊髓灰质炎疫苗等免费疫苗接种。家长可在线预约，方便快捷。',
    date: '2026-04-24',
    source: '中国疾控'
  },
  {
    icon: '💪',
    bg: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    tag: '知识',
    tagClass: 'info',
    title: '带状疱疹疫苗保护效力超90%',
    desc: '重组带状疱疹疫苗在我国获批上市，适用于50岁及以上成人，保护效力超过90%，建议中老年人积极接种预防。',
    date: '2026-04-23',
    source: '健康报'
  }
]

const totalSlides = slides.length
const currentSlide = ref(0)
let autoPlayInterval: ReturnType<typeof setInterval> | null = null

function nextSlide() {
  currentSlide.value = (currentSlide.value + 1) % totalSlides
}

function prevSlide() {
  currentSlide.value = (currentSlide.value - 1 + totalSlides) % totalSlides
}

function goToSlide(index: number) {
  currentSlide.value = index
  resetAutoPlay()
}

function startAutoPlay() {
  autoPlayInterval = setInterval(nextSlide, 4000)
}

function pauseAutoPlay() {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval)
    autoPlayInterval = null
  }
}

function resetAutoPlay() {
  pauseAutoPlay()
  startAutoPlay()
}

onMounted(() => startAutoPlay())
onUnmounted(() => pauseAutoPlay())
</script>

<style scoped>
.carousel-container {
  position: relative;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  margin-bottom: 30px;
  background: white;
  border: 1px solid #f1f5f9;
}

.carousel-track {
  display: flex;
  transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  width: 100%;
}

.carousel-slide {
  min-width: 100%;
  display: flex;
  position: relative;
}

.carousel-slide-inner {
  display: flex;
  width: 100%;
  min-height: 210px;
}

.carousel-image {
  width: 260px;
  min-height: 210px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 56px;
  position: relative;
}

.carousel-image-pattern {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 30% 40%, rgba(255, 255, 255, 0.2) 0%, transparent 40%),
  radial-gradient(circle at 70% 60%, rgba(255, 255, 255, 0.1) 0%, transparent 30%);
  pointer-events: none;
}

.carousel-image .slide-icon {
  font-size: 64px;
  z-index: 1;
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.15));
  animation: float-slow 4s ease-in-out infinite;
}

.carousel-image-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.06;
  background: radial-gradient(circle, white 0%, transparent 70%);
}

.carousel-info {
  flex: 1;
  padding: 28px 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.carousel-tag {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 50px;
  font-size: 0.76rem;
  font-weight: 600;
  margin-bottom: 10px;
  width: fit-content;
  letter-spacing: 0.02em;
}

.carousel-tag.hot {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.carousel-tag.new {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.carousel-tag.info {
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}

.carousel-info h3 {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 8px;
  line-height: 1.4;
}

.carousel-info p {
  font-size: 0.9rem;
  color: var(--gray-color);
  line-height: 1.7;
  margin-bottom: 12px;
}

.carousel-date-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.carousel-date {
  font-size: 0.8rem;
  color: var(--gray-light);
}

.carousel-source {
  font-size: 0.78rem;
  color: var(--primary-color);
  background: #eff6ff;
  padding: 2px 10px;
  border-radius: 50px;
  font-weight: 500;
}

.carousel-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  padding: 0 10px;
  pointer-events: none;
  z-index: 2;
}

.carousel-btn {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(0, 0, 0, 0.06);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: var(--dark-color);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.25s ease;
  pointer-events: auto;
  backdrop-filter: blur(4px);
}

.carousel-btn:hover {
  background: white;
  transform: scale(1.12);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  color: var(--primary-color);
}

.carousel-indicators {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  background: white;
  border-top: 1px solid #f1f5f9;
}

.carousel-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #d1d5db;
  cursor: pointer;
  transition: all 0.35s ease;
  border: none;
  padding: 0;
}

.carousel-dot.active {
  width: 32px;
  border-radius: 4px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  box-shadow: 0 2px 8px rgba(67, 97, 238, 0.3);
}

.carousel-dot:hover {
  background: var(--primary-light);
  transform: scale(1.2);
}

@media (max-width: 768px) {
  .carousel-slide-inner {
    flex-direction: column;
  }

  .carousel-image {
    width: 100%;
    min-height: 130px;
  }

  .carousel-info {
    padding: 20px;
  }
}
</style>
