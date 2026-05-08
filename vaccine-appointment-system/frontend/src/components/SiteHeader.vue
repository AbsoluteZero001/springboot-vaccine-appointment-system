<template>
  <header class="site-header">
    <div class="container">
      <router-link class="logo" to="/">
        <div class="logo-icon">💉</div>
        <span class="logo-text">疫苗预约系统</span>
      </router-link>
      <nav class="main-nav">
        <template v-if="auth.isAdmin">
          <router-link :class="{ active: isActive('/admin') }" to="/admin">预约审核</router-link>
          <router-link :class="{ active: isActive('/admin/vaccines') }" to="/admin/vaccines">疫苗管理</router-link>
          <router-link :class="{ active: isActive('/admin/users') }" to="/admin/users">用户管理</router-link>
          <a href="#" @click.prevent="auth.logout()">退出登录</a>
        </template>
        <template v-else-if="auth.isUser">
          <router-link :class="{ active: isActive('/dashboard') }" to="/dashboard">疫苗列表</router-link>
          <router-link :class="{ active: isActive('/profile') }" to="/profile">我的预约</router-link>
          <a href="#" @click.prevent="auth.logout()">退出登录</a>
        </template>
        <template v-else>
          <router-link :class="{ active: isActive('/') }" to="/">首页</router-link>
          <a href="#about">疫苗资讯</a>
          <a href="#about">预约指南</a>
          <a href="#footer">关于我们</a>
        </template>
      </nav>
    </div>
  </header>
</template>

<script lang="ts" setup>
import {useRoute} from 'vue-router'
import {useAuthStore} from '@/stores/auth'

const route = useRoute()
const auth = useAuthStore()

function isActive(path: string): boolean {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}
</script>
