<template>
  <div>
    <SiteHeader active-nav="/guide"/>
    <main class="homepage-main">
      <div class="container">
        <div class="page-hero">
          <h1>预约指南</h1>
          <p>在线预约疫苗接种流程说明</p>
        </div>

        <div class="steps-section">
          <div class="step-card" v-for="(step, i) in steps" :key="i">
            <div class="step-number">{{ i + 1 }}</div>
            <div class="step-content">
              <h3>{{ step.title }}</h3>
              <p>{{ step.desc }}</p>
            </div>
          </div>
        </div>

        <div class="faq-section">
          <h2>常见问题</h2>
          <div class="faq-list">
            <div class="faq-item" v-for="(faq, i) in faqs" :key="i">
              <div class="faq-q" @click="faq.open = !faq.open">
                <span>{{ faq.q }}</span>
                <span class="faq-arrow" :class="{ open: faq.open }">▼</span>
              </div>
              <div class="faq-a" v-show="faq.open">{{ faq.a }}</div>
            </div>
          </div>
        </div>
      </div>
    </main>
    <SiteFooter/>
  </div>
</template>

<script setup>
import {reactive} from 'vue'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'

const steps = [
  {
    title: '注册并实名认证',
    desc: '注册账号后，在疫苗列表页点击预约时会引导完成实名认证。需提供真实姓名和身份证号，信息仅用于接种登记。'
  },
  {
    title: '选择疫苗',
    desc: '在疫苗列表页可按分类筛选或搜索疫苗，点击疫苗卡片查看详细信息，选择合适的疫苗后点击"立即预约"。'
  },
  {
    title: '选择预约时间',
    desc: '在弹出的预约窗口中，先选择是为本人预约还是为家属预约，然后选择日期和时段。工作日可预约上午和下午，周六仅上午。'
  },
  {
    title: '确认支付',
    desc: '预约成功后，在"我的预约"页面点击"去支付"，确认疫苗信息和金额后完成支付。支付成功即锁定预约名额。'
  },
  {title: '按时接种', desc: '按照预约时间携带身份证前往接种点。如有家属预约，家属本人也需到场。迟到30分钟以上将视为爽约。'},
  {title: '获取接种凭证', desc: '接种完成后，管理员会生成接种记录。在"我的预约"→"接种记录"中可查看并下载电子接种凭证。'}
]

const faqs = reactive([
  {q: '周日可以预约接种吗？', a: '周日为休息日，不开放预约。请选择周一至周六的时段，周六仅开放上午时段。', open: false},
  {
    q: '预约后可以改期或取消吗？',
    a: '在预约时间前，可以在"我的预约"页面申请改期或取消预约。取消后疫苗库存会自动释放。',
    open: false
  },
  {
    q: '可以为家人预约接种吗？',
    a: '可以。先在"家庭成员"页面添加家属信息（姓名、身份证号、手机号），然后在预约时选择家属即可。',
    open: false
  },
  {
    q: '忘记按时接种会怎样？',
    a: '预约时间过后未到场的，系统会自动标记为"未到场"，疫苗库存会释放。如需接种请重新预约。',
    open: false
  },
  {
    q: '如何查看接种记录？',
    a: '在"我的预约"页面下方可查看所有接种记录，已接种的疫苗可点击"查看凭证"获取电子接种证书。',
    open: false
  },
  {
    q: '疫苗价格是最终价格吗？',
    a: '系统中显示的价格为参考价格，实际费用以接种点公示为准。部分国家免疫规划疫苗免费接种。',
    open: false
  }
])
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

.steps-section {
  display: flex;
  flex-direction: column;
  gap: 0;
  margin-bottom: 40px;
}

.step-card {
  display: flex;
  gap: 20px;
  padding: 24px;
  background: white;
  border-left: 4px solid var(--primary-color);
  position: relative;
}

.step-card:first-child {
  border-radius: 16px 16px 0 0;
}

.step-card:last-child {
  border-radius: 0 0 16px 16px;
}

.step-card:not(:last-child) {
  border-bottom: 1px solid #f1f5f9;
}

.step-card::after {
  content: '';
  position: absolute;
  left: 20px;
  top: 72px;
  bottom: -1px;
  width: 2px;
  background: #e2e8f0;
}

.step-card:last-child::after {
  display: none;
}

.step-number {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: 800;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.step-content h3 {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--dark-color);
  margin: 0 0 6px;
}

.step-content p {
  font-size: 0.88rem;
  color: #64748b;
  line-height: 1.7;
  margin: 0;
}

.faq-section h2 {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 16px;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.faq-item {
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.faq-q {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  font-weight: 600;
  color: var(--dark-color);
  font-size: 0.92rem;
  user-select: none;
  transition: background 0.2s;
}

.faq-q:hover {
  background: #f8fafc;
}

.faq-arrow {
  font-size: 0.7rem;
  color: #94a3b8;
  transition: transform 0.25s ease;
}

.faq-arrow.open {
  transform: rotate(180deg);
}

.faq-a {
  padding: 0 20px 16px;
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.7;
}
</style>
