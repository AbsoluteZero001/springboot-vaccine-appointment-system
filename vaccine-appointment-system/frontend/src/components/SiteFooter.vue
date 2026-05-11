<template>
  <footer id="footer" class="footer-enhanced">
    <div class="footer-top">
      <div class="container">
        <div class="footer-grid">
          <div class="footer-brand">
            <div class="footer-logo">
              <span class="footer-logo-icon">💉</span>
              <span class="footer-logo-text">疫苗预约系统</span>
            </div>
            <p>国家免疫规划信息平台，致力于为全民提供便捷、安全、可靠的疫苗接种预约服务。</p>
            <div class="footer-badges">
              <span class="footer-badge">🔒 数据加密</span>
              <span class="footer-badge">✅ 官方认证</span>
            </div>
          </div>
          <div class="footer-links">
            <h4>快速链接</h4>
            <router-link to="/">首页</router-link>
            <router-link to="/dashboard">疫苗预约</router-link>
            <router-link to="/profile">我的预约</router-link>
            <a href="#footer">关于我们</a>
          </div>
          <div class="footer-links">
            <h4>疫苗分类</h4>
            <a href="#">新冠疫苗</a>
            <a href="#">流感疫苗</a>
            <a href="#">HPV疫苗</a>
            <a href="#">儿童疫苗</a>
          </div>
          <div class="footer-contact">
            <h4>联系我们</h4>
            <p>📞 服务热线：12320</p>
            <p>📧 邮箱：vaccine@health.cn</p>
            <p>🕐 服务时间：周一至周六 8:00-17:00</p>
          </div>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <div class="container">
        <p>&copy; 2026 疫苗预约系统 | 守护健康，疫苗先行</p>
        <p class="footer-sub">国家免疫规划信息平台 · 安全 · 便捷 · 可靠</p>
      </div>
    </div>
  </footer>
</template>

<script lang="ts" setup>
defineProps<{ adminMode?: boolean }>()
</script>

<style scoped>
.footer-enhanced {
  margin-top: 60px;
}

.footer-top {
  background: linear-gradient(135deg, #1e1b4b, #1e293b);
  padding: 50px 0 36px;
  color: white;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr 1.2fr;
  gap: 32px;
}

.footer-brand p {
  color: rgba(255, 255, 255, 0.55);
  font-size: 0.88rem;
  line-height: 1.7;
  margin-top: 14px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.footer-logo-icon {
  font-size: 28px;
}

.footer-logo-text {
  font-size: 1.2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #4cc9f0, #a78bfa);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.footer-badges {
  display: flex;
  gap: 10px;
  margin-top: 14px;
  flex-wrap: wrap;
}

.footer-badge {
  font-size: 0.78rem;
  padding: 4px 12px;
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
}

.footer-links h4,
.footer-contact h4 {
  font-size: 0.95rem;
  font-weight: 700;
  margin-bottom: 16px;
  color: rgba(255, 255, 255, 0.9);
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.5);
  text-decoration: none;
  font-size: 0.88rem;
  transition: all 0.25s ease;
}

.footer-links a:hover {
  color: #4cc9f0;
  transform: translateX(4px);
}

.footer-contact p {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.86rem;
  line-height: 2;
}

.footer-bottom {
  background: rgba(0, 0, 0, 0.2);
  padding: 20px 0;
  text-align: center;
}

.footer-bottom p {
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.84rem;
  margin: 0;
}

.footer-sub {
  margin-top: 6px !important;
  font-size: 0.8rem !important;
}

@media (max-width: 768px) {
  .footer-grid {
    grid-template-columns: 1fr 1fr;
    gap: 24px;
  }
}

@media (max-width: 480px) {
  .footer-grid {
    grid-template-columns: 1fr;
  }
}
</style>
