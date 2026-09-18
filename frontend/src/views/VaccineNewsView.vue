<template>
  <div>
    <SiteHeader active-nav="/news"/>
    <main class="homepage-main">
      <div class="container">
        <div class="page-hero">
          <h1>疫苗资讯</h1>
          <p>了解疫苗最新动态与科普知识</p>
        </div>

        <div class="news-grid">
          <div class="news-card" v-for="(item, i) in newsItems" :key="i">
            <div class="news-card-image">{{ item.icon }}</div>
            <div class="news-card-body">
              <span class="news-tag">{{ item.tag }}</span>
              <h3>{{ item.title }}</h3>
              <p>{{ item.summary }}</p>
              <span class="news-date">{{ item.date }}</span>
            </div>
          </div>
        </div>
      </div>
    </main>
    <SiteFooter/>
  </div>
</template>

<script setup>
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'

const newsItems = [
  {
    icon: '🦠',
    tag: '流感防控',
    title: '2026年流感疫苗接种指南发布',
    summary: '中国疾控中心发布最新流感疫苗接种技术指南，建议重点人群在10月底前完成接种，优先推荐四价流感疫苗。',
    date: '2026-05-20'
  },
  {
    icon: '🎗️',
    tag: 'HPV疫苗',
    title: '九价HPV疫苗男性适应症获批',
    summary: '国家药监局正式批准九价HPV疫苗用于9-26岁男性接种，可预防肛门癌、生殖器疣等相关疾病。',
    date: '2026-05-15'
  },
  {
    icon: '👶',
    tag: '儿童免疫',
    title: '国家免疫规划疫苗补种原则更新',
    summary: '卫健委发布新版《国家免疫规划疫苗儿童免疫程序》，明确了各类疫苗的补种年龄上限和补种间隔。',
    date: '2026-05-08'
  },
  {
    icon: '💉',
    tag: '新冠疫苗',
    title: '新版新冠疫苗加强针研究进展',
    summary: '针对JN.1变异株的新一代mRNA疫苗完成III期临床试验，预计下半年启动接种，保护效力超85%。',
    date: '2026-05-01'
  },
  {
    icon: '🧓',
    tag: '老年健康',
    title: '带状疱疹疫苗保护效力研究发布',
    summary: '重组带状疱疹疫苗在50岁以上人群中保护效力达97%，卫健委鼓励老年人积极接种以预防带状疱疹后神经痛。',
    date: '2026-04-25'
  },
  {
    icon: '🌍',
    tag: '国际动态',
    title: 'WHO发布2030全球疫苗行动计划',
    summary: '世界卫生组织提出新目标：到2030年实现全球基础疫苗接种率90%以上，消除疫苗可预防疾病的地区差异。',
    date: '2026-04-18'
  }
]
</script>

<style scoped>
.page-hero {
  background: linear-gradient(135deg, #1e1b4b, #3730a3, #4361ee);
  border-radius: var(--border-radius-lg);
  padding: 40px 36px;
  color: white;
  margin-bottom: 32px;
  text-align: center;
}

.page-hero h1 {
  font-size: 1.8rem;
  font-weight: 800;
  margin: 0 0 8px;
}

.page-hero p {
  font-size: 0.95rem;
  opacity: 0.7;
  margin: 0;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.news-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f1f5f9;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.news-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 36px rgba(67, 97, 238, 0.12);
}

.news-card-image {
  height: 120px;
  background: linear-gradient(135deg, #eef2ff, #e0e7ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
}

.news-card-body {
  padding: 20px;
}

.news-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 50px;
  font-size: 0.72rem;
  font-weight: 600;
  background: #eef2ff;
  color: var(--primary-color);
  margin-bottom: 10px;
}

.news-card-body h3 {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark-color);
  margin: 0 0 8px;
  line-height: 1.5;
}

.news-card-body p {
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.7;
  margin: 0 0 12px;
}

.news-date {
  font-size: 0.78rem;
  color: #94a3b8;
}
</style>
