<template>
  <div>
    <SiteHeader active-nav="/admin/users" />

    <div class="container">
      <AlertMessage ref="alertRef" />
      <div class="card">
        <h2>用户列表</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>手机号</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.phone || '—' }}</td>
              <td>{{ user.status === 1 ? '正常' : '已停用' }}</td>
              <td>
                <button class="btn btn-small" @click="toggleUserStatus(user.id, user.status)">
                  {{ user.status === 1 ? '停用' : '启用' }}
                </button>
                <button class="btn btn-danger btn-small" @click="deleteUser(user.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <SiteFooter />
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import api from '@/services/api'

interface User {
  id: number
  username: string
  email: string
  phone?: string
  status: number
}

const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)
const users = ref<User[]>([])

function showAlert(msg: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(msg, type)
}

async function loadUsers() {
  try {
    const response = await api.get('/users')
    users.value = response.data
  } catch {
    showAlert('加载用户列表失败', 'error')
  }
}

async function toggleUserStatus(userId: number, currentStatus: number) {
  const newStatus = currentStatus === 1 ? 0 : 1
  try {
    await api.put(`/users/${userId}`, { status: newStatus })
    showAlert('用户状态已更新', 'success')
    await loadUsers()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '更新用户状态失败', 'error')
  }
}

async function deleteUser(userId: number) {
  if (!confirm('确定要删除此用户吗？其所有预约记录也将被删除。')) return
  try {
    await api.delete(`/users/${userId}`)
    showAlert('用户已删除', 'success')
    await loadUsers()
  } catch (error: any) {
    showAlert(error.response?.data?.error || '删除用户失败', 'error')
  }
}

onMounted(() => {
  loadUsers()
})
</script>
