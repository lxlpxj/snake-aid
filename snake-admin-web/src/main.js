import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import App from './App.vue'
import './styles.css'

import LoginPage from './views/LoginPage.vue'
import Layout from './views/Layout.vue'
import Dashboard from './views/Dashboard.vue'
import UserPage from './views/UserPage.vue'
import SnakePage from './views/SnakePage.vue'
import GuidePage from './views/GuidePage.vue'
import ArticlePage from './views/ArticlePage.vue'
import HospitalPage from './views/HospitalPage.vue'
import AuditWorkbench from './views/AuditWorkbench.vue'
import MyAuditList from './views/MyAuditList.vue'
import LogPage from './views/LogPage.vue'

const routes = [
  { path: '/login', component: LoginPage },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      // 数据看板 —— SYS_ADMIN（全局统计）+ CONTENT_ADMIN（业务统计）
      { path: 'dashboard', component: Dashboard, meta: { title: '数据看板', roles: ['SYS_ADMIN', 'CONTENT_ADMIN'] } },
      // 账号管理 —— 仅系统管理员
      { path: 'users', component: UserPage, meta: { title: '账号管理', roles: ['SYS_ADMIN'] } },
      // 操作日志 —— SYS_ADMIN（系统日志）+ CONTENT_ADMIN（业务日志）+ MEDICAL_ADVISOR（审核日志）
      { path: 'logs', component: LogPage, meta: { title: '操作日志', roles: ['SYS_ADMIN', 'CONTENT_ADMIN', 'MEDICAL_ADVISOR'] } },
      // 蛇种库 —— 内容管理员维护 + 医疗顾问只读查看
      { path: 'snakes', component: SnakePage, meta: { title: '蛇种库', roles: ['CONTENT_ADMIN', 'MEDICAL_ADVISOR'] } },
      // 急救指南 —— 内容管理员维护 + 医疗顾问编辑
      { path: 'guides', component: GuidePage, meta: { title: '急救指南', roles: ['CONTENT_ADMIN', 'MEDICAL_ADVISOR'] } },
      // 科普文章 —— 内容管理员维护 + 医疗顾问只读
      { path: 'articles', component: ArticlePage, meta: { title: '科普文章', roles: ['CONTENT_ADMIN', 'MEDICAL_ADVISOR'] } },
      // 医院与血清 —— 仅内容管理员
      { path: 'hospitals', component: HospitalPage, meta: { title: '医院与血清', roles: ['CONTENT_ADMIN'] } },
      // 审核工作台 —— 仅医疗顾问
      { path: 'audit', component: AuditWorkbench, meta: { title: '审核工作台', roles: ['MEDICAL_ADVISOR'] } },
      // 我的待审 —— 仅内容管理员（只读看自己提交的内容状态）
      { path: 'my-audit', component: MyAuditList, meta: { title: '我的待审', roles: ['CONTENT_ADMIN'] } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('snake_admin_token')
  if (to.path === '/login') { next(); return }
  if (!token) { next('/login'); return }
  const user = JSON.parse(localStorage.getItem('snake_admin_user') || '{}')
  const role = user.role || ''
  if (to.meta.roles && !to.meta.roles.includes(role)) {
    // 无权限 → 跳转到该角色的首页
    const roleHome = {
      SYS_ADMIN: '/dashboard',
      CONTENT_ADMIN: '/snakes',
      MEDICAL_ADVISOR: '/audit'
    }[role] || '/dashboard'
    next(roleHome); return
  }
  next()
})

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')
