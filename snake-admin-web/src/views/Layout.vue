<template>
  <el-container class="layout-container" :data-role="role">
    <!-- 侧边栏 -->
    <el-aside width="240px" class="aside" :style="{ background: theme.sidebar, borderRight: '2px solid ' + theme.accent }">
      <!-- LOGO + 角色标识 -->
      <div class="logo">
        <span class="logo-icon">🐍</span>
        <div class="logo-text">
          <div class="logo-title">毒蛇急救</div>
          <div class="logo-sub" :style="{ color: theme.accent }">{{ roleLabel }}</div>
        </div>
      </div>

      <!-- 模块分组菜单 -->
      <el-menu
        :default-active="activeMenu"
        router
        :class="['role-menu', 'role-' + role]"
        :background-color="theme.sidebar"
        text-color="#b0bec5"
        :active-text-color="theme.accent">

        <!-- ============ 系统管理员 ============ -->
        <template v-if="role === 'SYS_ADMIN'">
          <el-menu-item index="/dashboard"><el-icon><DataAnalysis /></el-icon><span>数据看板</span></el-menu-item>
          <el-menu-item index="/users"><el-icon><UserFilled /></el-icon><span>账号管理</span></el-menu-item>
          <el-menu-item index="/logs"><el-icon><Tickets /></el-icon><span>系统操作日志</span></el-menu-item>
        </template>

        <!-- ============ 内容管理员 ============ -->
        <template v-else-if="role === 'CONTENT_ADMIN'">
          <el-menu-item index="/dashboard"><el-icon><DataAnalysis /></el-icon><span>数据看板</span></el-menu-item>
          <el-menu-item index="/snakes"><el-icon><Cpu /></el-icon><span>蛇种库维护</span></el-menu-item>
          <el-menu-item index="/articles"><el-icon><Reading /></el-icon><span>科普文章维护</span></el-menu-item>
          <el-menu-item index="/guides"><el-icon><Document /></el-icon><span>急救指南维护</span></el-menu-item>
          <el-menu-item index="/hospitals"><el-icon><OfficeBuilding /></el-icon><span>医院与血清</span></el-menu-item>
          <el-menu-item index="/my-audit"><el-icon><Tickets /></el-icon><span>我的待审</span></el-menu-item>
          <el-menu-item index="/logs"><el-icon><Tickets /></el-icon><span>业务操作日志</span></el-menu-item>
        </template>

        <!-- ============ 医疗顾问 ============ -->
        <template v-else-if="role === 'MEDICAL_ADVISOR'">
          <el-menu-item index="/dashboard"><el-icon><DataAnalysis /></el-icon><span>数据看板</span></el-menu-item>
          <el-menu-item index="/audit"><el-icon><CircleCheckFilled /></el-icon><span>审核工作台</span></el-menu-item>
          <el-menu-item index="/snakes"><el-icon><View /></el-icon><span>蛇种库（只读）</span></el-menu-item>
          <el-menu-item index="/articles"><el-icon><Reading /></el-icon><span>科普文章（只读）</span></el-menu-item>
          <el-menu-item index="/guides"><el-icon><Document /></el-icon><span>急救指南（编辑）</span></el-menu-item>
          <el-menu-item index="/logs"><el-icon><Tickets /></el-icon><span>审核操作日志</span></el-menu-item>
        </template>
      </el-menu>

      <!-- 底部角色信息 -->
      <div class="aside-footer" :style="{ borderTopColor: theme.accent + '44' }">
        <div class="role-badge" :style="{ background: theme.accent + '22', color: theme.accent, border: '1px solid ' + theme.accent }">
          {{ roleLabel }}
        </div>
      </div>
    </el-aside>

    <!-- 主区域 -->
    <el-container>
      <el-header class="header" :style="{ borderBottomColor: theme.accent + '33' }">
        <div class="header-left">
          <span class="page-title">{{ currentTitle }}</span>
          <el-tag size="small" :style="{ background: theme.accent + '1a', color: theme.accent, borderColor: theme.accent + '55', fontWeight: 500 }">
            {{ roleLabel }}
          </el-tag>
        </div>
        <div class="header-right">
          <span class="user-info">
            <el-avatar :size="32" :style="{ background: theme.accent + 'cc', color: '#fff' }">
              {{ (user?.nickname || user?.username || '?').slice(0,1).toUpperCase() }}
            </el-avatar>
            <span style="margin-left:8px;">{{ user?.nickname || user?.username }}</span>
          </span>
          <el-button :type="role === 'SYS_ADMIN' ? 'danger' : 'default'" link @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

const user = computed(() => {
  try { return JSON.parse(localStorage.getItem('snake_admin_user') || '{}') } catch { return {} }
})
const role = computed(() => user.value.role || '')

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '管理后台')

const roleLabel = computed(() => ({
  SYS_ADMIN: '系统管理员',
  CONTENT_ADMIN: '内容管理员',
  MEDICAL_ADVISOR: '医疗顾问'
}[role.value] || role.value || '未登录'))

// 主题色差异化：三个角色三种风格
const theme = computed(() => {
  const themes = {
    SYS_ADMIN:      { sidebar: '#1e2a3a', accent: '#3498db', headerBg: '#fff' },
    CONTENT_ADMIN:  { sidebar: '#1a3a2a', accent: '#2ecc71', headerBg: '#fff' },
    MEDICAL_ADVISOR: { sidebar: '#3a1e2e', accent: '#e74c3c', headerBg: '#fff' }
  }
  return themes[role.value] || themes.SYS_ADMIN
})

function logout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
    localStorage.removeItem('snake_admin_token')
    localStorage.removeItem('snake_admin_user')
    ElMessage.success('已退出')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container { height: 100vh; }

/* 侧边栏 */
.aside { display: flex; flex-direction: column; }
.logo {
  height: 70px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 18px;
  border-bottom: 1px solid #ffffff14;
}
.logo-icon { font-size: 26px; }
.logo-text { display: flex; flex-direction: column; line-height: 1.2; }
.logo-title { color: #fff; font-size: 17px; font-weight: 700; letter-spacing: 1px; }
.logo-sub { font-size: 12px; margin-top: 2px; opacity: 0.9; }

.role-menu { flex: 1; border-right: none !important; padding: 8px 0; }
.role-menu .el-menu-item {
  height: 48px; line-height: 48px;
  margin: 2px 10px; border-radius: 6px;
  padding-left: 18px !important;
  transition: all 0.2s;
}
.role-menu .el-menu-item:hover { background: rgba(255,255,255,0.08) !important; }
.role-menu .el-menu-item.is-active { background: rgba(255,255,255,0.12) !important; font-weight: 600; }

.aside-footer {
  padding: 14px;
  border-top: 1px solid #ffffff14;
  display: flex;
  justify-content: center;
}
.role-badge {
  font-size: 12px; padding: 4px 14px; border-radius: 20px; font-weight: 600;
}

/* 主区 */
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 58px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.page-title { font-size: 17px; font-weight: 600; color: #1e293b; }
.header-right { display: flex; align-items: center; gap: 16px; }
.user-info { display: flex; align-items: center; color: #334155; font-size: 14px; }

.main { background: #f1f5f9; padding: 20px; }
</style>
