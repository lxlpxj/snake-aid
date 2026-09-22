<template>
  <div class="login-container">
    <div class="login-box">
      <h1>🐍 蛇咬伤急救系统</h1>
      <p class="sub">管理后台</p>
      <el-form :model="form" label-width="0" @submit.prevent="submit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="submit">
          登 录
        </el-button>
      </el-form>
      <div class="tips">
        <p>测试账号：</p>
        <p>系统管理员: sysadmin / 123456</p>
        <p>内容管理员: contentadmin / 123456</p>
        <p>医疗顾问: doctor / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)

async function submit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form.value)
    localStorage.setItem('snake_admin_token', data.token)
    localStorage.setItem('snake_admin_user', JSON.stringify(data))
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // 错误信息在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
}
.login-box {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.2);
}
.login-box h1 { text-align: center; margin-bottom: 8px; }
.login-box .sub { text-align: center; color: #999; margin-bottom: 32px; }
.login-btn { width: 100%; }
.tips {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  color: #999;
  font-size: 12px;
  text-align: center;
}
.tips p { margin: 4px 0; }
</style>
