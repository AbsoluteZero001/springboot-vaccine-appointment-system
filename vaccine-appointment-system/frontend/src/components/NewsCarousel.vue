<template>
  <div id="newsCarousel" class="carousel-container" @mouseenter="pauseAutoPlay" @mouseleave="startAutoPlay">
    <div :style="{ transform: `translateX(-${currentSlide * 100}%)` }" class="carousel-track">
      <div v-for="(slide, index) in slides" :key="index" class="carousel-slide">
        <div class="carousel-slide-inner">
          <div :style="{ background: slide.bg }" class="carousel-image">
            <span class="slide-icon">{{ slide.icon }}</span>
            <div class="carousel-image-bg"></div>
          </div>
          <div class="carousel-info">
            <span :class="['carousel-tag', slide.tagClass]">{{ slide.tag }}</span>
            <h3>{{ slide.title }}</h3>
            <p>{{ slide.desc }}</p>
            <span class="carousel-date">{{ slide.date }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="carousel-controls">
      <button class="carousel-btn" @click="prevSlide">‹</button>
      <button class="carousel-btn" @click="nextSlide">›</button>
    </div>

    <div class="carousel-indicators">
      <button
        v-for="(_, index) in slides"
        :key="index"
        :class="['carousel-dot', { active: currentSlide === index }]"
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
    bg: 'linear-gradient(135deg, #667eea, #764ba2)',
    tag: '热点',
    tagClass: 'hot',
    title: '新冠疫苗加强针接种通知',
    desc: '根据国家卫健委最新部署，即日起开展新冠疫苗加强针接种工作。建议已完成基础免疫满6个月的18岁以上人群尽快接种。',
    date: '2026-04-27 · 国家卫健委'
  },
  {
    icon: '🌡️',
    bg: 'linear-gradient(135deg, #f093fb, #f5576c)',
    tag: '提醒',
    tagClass: 'new',
    title: '秋冬季流感疫苗预约已开放',
    desc: '2026年度流感疫苗预约接种已全面开放。老人、儿童及慢性病患者为重点推荐人群，建议在流感季来临前完成接种。',
    date: '2026-04-26 · 疾控中心'
  },
  {
    icon: '🎗️',
    bg: 'linear-gradient(135deg, #4facfe, #00f2fe)',
    tag: '关注',
    tagClass: 'hot',
    title: 'HPV疫苗扩龄至9-45岁',
    desc: '九价HPV疫苗接种年龄已正式扩展至9-45岁女性，预约需求激增。建议适龄人群尽早预约，早接种早受益。',
    date: '2026-04-25 · 药监局'
  },
  {
    icon: '👶',
    bg: 'linear-gradient(135deg, #a18cd1, #fbc2eb)',
    tag: '科普',
    tagClass: 'info',
    title: '儿童疫苗接种时间表须知',
    desc: '按照国家免疫规划程序，儿童需按时完成卡介苗、乙肝疫苗、脊髓灰质炎疫苗等免费疫苗接种。家长可在线预约，方便快捷。',
    date: '2026-04-24 · 中国疾控'
  },
  {
    icon: '💪',
    bg: 'linear-gradient(135deg, #fa709a, #fee140)',
    tag: '知识',
    tagClass: 'info',
    title: '带状疱疹疫苗保护效力超90%',
    desc: '重组带状疱疹疫苗在我国获批上市，适用于50岁及以上成人，保护效力超过90%，建议中老年人积极接种预防。',
    date: '2026-04-23 · 健康报'
  }
]

const totalSlides = slides.length
const currentSlide = ref(0)
let autoPlayInterval: ReturnType<typeof setInterval> | null = null

function updateCarousel() {
  // handled reactively by currentSlide
}

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
