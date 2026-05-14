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
              <th class="col-nowrap">ID</th>
              <th>用户名</th>
              <th class="col-nowrap">手机号</th>
              <th class="col-nowrap">性别</th>
              <th class="col-nowrap">生日</th>
              <th class="col-nowrap">年龄</th>
              <th>备注</th>
              <th class="col-nowrap">状态</th>
              <th class="col-nowrap">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td class="col-nowrap">{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td class="col-nowrap">{{ user.phone || '—' }}</td>
              <td class="col-nowrap">{{ genderLabel(user.gender) }}</td>
              <td class="col-nowrap">{{ user.birthday || '—' }}</td>
              <td class="col-nowrap">{{ calcAge(user.birthday) }}</td>
              <td class="table-spec">{{ user.remark || '—' }}</td>
              <td class="col-nowrap">
                <span :class="['status-badge', user.status === 1 ? 'status-completed' : 'status-cancelled']">
                  {{ user.status === 1 ? '正常' : '已停用' }}
                </span>
              </td>
              <td class="col-nowrap">
                <div class="table-btn-group">
                  <button class="btn btn-small" @click="toggleUserStatus(user.id, user.status)">
                    {{ user.status === 1 ? '停用' : '启用' }}
                  </button>
                  <button class="btn btn-danger btn-small" @click="deleteUser(user.id)">删除</button>
                </div>
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
import {useRouter} from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import AlertMessage from '@/components/AlertMessage.vue'
import {useAuthStore} from '@/stores/auth'
import api from '@/services/api'

const router = useRouter()
const auth = useAuthStore()

interface User {
  id: number
  username: string
  phone: string
  status: number
  gender?: number
  birthday?: string
  remark?: string
}

const alertRef = ref<InstanceType<typeof AlertMessage> | null>(null)
const users = ref<User[]>([])

function showAlert(msg: string, type: 'success' | 'error' = 'success') {
  alertRef.value?.showAlert(msg, type)
}

function genderLabel(g?: number): string {
  if (g === 1) return '男'
  if (g === 2) return '女'
  return '未知'
}

function calcAge(birthday?: string): string {
  if (!birthday) return '—'
  const birth = new Date(birthday)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const m = today.getMonth() - birth.getMonth()
  if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
    age--
  }
  return age > 0 ? `${age}岁` : '—'
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
  if (!auth.isAdmin) {
    router.replace('/')
    return
  }
  loadUsers()
})
</script>
