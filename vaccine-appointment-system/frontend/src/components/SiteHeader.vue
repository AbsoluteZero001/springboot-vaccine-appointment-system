<template>
  <header class="site-header" :class="{ 'header-scrolled': isScrolled }">
    <div class="container">
      <router-link class="logo" to="/">
        <div class="logo-icon">
          <span class="logo-icon-inner">💉</span>
        </div>
        <span class="logo-text">疫苗预约系统</span>
      </router-link>
      <nav class="main-nav">
        <template v-if="auth.isAdmin">
          <span class="nav-greeting">
            <span class="nav-avatar" v-if="auth.currentUser?.avatarUrl">
              <img :src="auth.currentUser.avatarUrl" alt="avatar"/>
            </span>
            <span class="nav-avatar-placeholder" v-else>👤</span>
            {{ auth.currentUser?.nickname || auth.currentUser?.username || '管理员' }}
          </span>
          <router-link :class="{ active: isActive('/admin') }" to="/admin">
            <span class="nav-icon">📋</span> 预约审核
          </router-link>
          <router-link :class="{ active: isActive('/admin/vaccines') }" to="/admin/vaccines">
            <span class="nav-icon">💊</span> 疫苗管理
          </router-link>
          <router-link :class="{ active: isActive('/admin/users') }" to="/admin/users">
            <span class="nav-icon">👥</span> 用户管理
          </router-link>
          <a href="#" class="nav-logout" @click.prevent="auth.logout()">
            <span class="nav-icon">🚪</span> 退出登录
          </a>
        </template>
        <template v-else-if="auth.isUser">
          <span class="nav-greeting">
            <span class="nav-avatar" v-if="auth.currentUser?.avatarUrl">
              <img :src="auth.currentUser.avatarUrl" alt="avatar"/>
            </span>
            <span class="nav-avatar-placeholder" v-else>👤</span>
            {{ auth.currentUser?.nickname || auth.currentUser?.username || '用户' }}
          </span>
          <router-link :class="{ active: isActive('/dashboard') }" to="/dashboard">
            <span class="nav-icon">🏥</span> 疫苗列表
          </router-link>
          <router-link :class="{ active: isActive('/profile') }" to="/profile">
            <span class="nav-icon">📅</span> 我的预约
          </router-link>
          <router-link :class="{ active: isActive('/settings') }" to="/settings">
            <span class="nav-icon">👤</span> 个人资料
          </router-link>
          <a href="#" class="nav-logout" @click.prevent="auth.logout()">
            <span class="nav-icon">🚪</span> 退出登录
          </a>
        </template>
        <template v-else>
          <router-link :class="{ active: isActive('/') }" to="/">
            <span class="nav-icon">🏠</span> 首页
          </router-link>
          <a href="#about">
            <span class="nav-icon">📰</span> 疫苗资讯
          </a>
          <a href="#about">
            <span class="nav-icon">📖</span> 预约指南
          </a>
          <a href="#footer">
            <span class="nav-icon">ℹ️</span> 关于我们
          </a>
        </template>
      </nav>
    </div>
  </header>
</template>

<script lang="ts" setup>
import {onMounted, onUnmounted, ref} from 'vue'
import {useRoute} from 'vue-router'
import {useAuthStore} from '@/stores/auth'

const route = useRoute()
const auth = useAuthStore()
const isScrolled = ref(false)

function isActive(path: string): boolean {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function handleScroll() {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => window.addEventListener('scroll', handleScroll, {passive: true}))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped>
.site-header {
  background: linear-gradient(135deg, rgba(67, 97, 238, 0.95), rgba(114, 9, 183, 0.92));
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  color: white;
  padding: 0;
  box-shadow: 0 4px 20px rgba(67, 97, 238, 0.3);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.3s ease;
}

.site-header.header-scrolled {
  background: linear-gradient(135deg, rgba(30, 27, 75, 0.97), rgba(49, 46, 129, 0.95));
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}

.site-header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 70px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: white;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
}

.logo:hover .logo-icon {
  background: rgba(255, 255, 255, 0.25);
  transform: rotate(-5deg) scale(1.05);
}

.logo-icon-inner {
  font-size: 20px;
}

.logo-text {
  font-size: 1.3rem;
  font-weight: 700;
  background: linear-gradient(to right, #fff, #e0e7ff);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 0.02em;
}

.main-nav {
  display: flex;
  gap: 4px;
  align-items: center;
}

.main-nav a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 50px;
  transition: all 0.25s ease;
  font-weight: 500;
  font-size: 14px;
  background: transparent;
  border: 1px solid transparent;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.nav-icon {
  font-size: 15px;
}

.main-nav a:hover {
  background: rgba(255, 255, 255, 0.12);
  color: white;
  border-color: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
}

.main-nav a.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border-color: rgba(255, 255, 255, 0.25);
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-greeting {
  color: rgba(255, 255, 255, 0.85);
  font-weight: 600;
  font-size: 0.88rem;
  padding: 6px 14px;
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.1);
  margin-right: 6px;
  white-space: nowrap;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-flex;
  align-items: center;
  gap: 7px;
}

.nav-logout {
  color: rgba(255, 255, 255, 0.6) !important;
}

.nav-logout:hover {
  color: rgba(255, 255, 255, 0.9) !important;
  background: rgba(247, 37, 133, 0.2) !important;
  border-color: rgba(247, 37, 133, 0.25) !important;
}

.nav-avatar {
  display: inline-flex;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.5);
  flex-shrink: 0;
  vertical-align: middle;
}

.nav-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.nav-avatar-placeholder {
  font-size: 18px;
  flex-shrink: 0;
  line-height: 1;
}

@media (max-width: 768px) {
  .site-header .container {
    flex-direction: column;
    height: auto;
    padding: 12px 15px;
    gap: 10px;
  }

  .main-nav {
    width: 100%;
    justify-content: center;
    flex-wrap: wrap;
  }

  .main-nav a {
    font-size: 13px;
    padding: 6px 12px;
  }

  .logo-text {
    font-size: 1.15rem;
  }
}
</style>
