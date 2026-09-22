<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <div>
          <span class="page-title">{{ pageTitle }}</span>
          <el-tag :type="tagType" size="small" effect="plain" style="margin-left:10px;">{{ roleLabel }}</el-tag>
        </div>
        <div>
          <el-select v-model="filterCategory" placeholder="全部分类" style="width:140px;margin-right:10px;" clearable @change="load">
            <el-option v-for="c in allowedCategories" :key="c" :label="categoryLabel(c)" :value="c" />
          </el-select>
          <el-input v-model="filterOp" placeholder="搜索操作描述" style="width:200px;margin-right:10px;" clearable @keyup.enter="load" />
          <el-button type="primary" @click="load">查询</el-button>
        </div>
      </div>
    </template>

    <!-- 角色可见范围说明 -->
    <el-alert :title="scopeDesc" type="info" :closable="false" show-icon style="margin-bottom:12px;" />

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="分类" width="110">
        <template #default="{ row }">
          <el-tag :type="categoryTagType(row.category)" size="small">{{ categoryLabel(row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="操作人" width="130">
        <template #default="{ row }">
          <span v-if="row.username">{{ row.username }}</span>
          <span v-else style="color:#c0c4cc;">(未登录)</span>
        </template>
      </el-table-column>
      <el-table-column prop="operation" label="操作描述" min-width="220" show-overflow-tooltip />
      <el-table-column prop="method" label="请求方法" width="130" />
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column prop="createTime" label="操作时间" width="170" />
      <el-table-column label="详情" width="70" fixed="right">
        <template #default="{ row }">
          <el-button size="small" link @click="showDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top:16px;justify-content:flex-end;display:flex;"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10,20,50]"
      layout="total, sizes, prev, pager, next"
      @current-change="load" @size-change="load" />
  </el-card>

  <!-- 详情弹窗 -->
  <el-dialog v-model="detailVisible" title="日志详情" width="560px">
    <el-descriptions :column="1" border v-if="detailRow">
      <el-descriptions-item label="分类">{{ categoryLabel(detailRow.category) }}</el-descriptions-item>
      <el-descriptions-item label="操作人ID">{{ detailRow.userId }}</el-descriptions-item>
      <el-descriptions-item label="操作描述">{{ detailRow.operation }}</el-descriptions-item>
      <el-descriptions-item label="请求方法">{{ detailRow.method }}</el-descriptions-item>
      <el-descriptions-item label="IP">{{ detailRow.ip }}</el-descriptions-item>
      <el-descriptions-item label="操作时间">{{ detailRow.createTime }}</el-descriptions-item>
      <el-descriptions-item label="请求参数">
        <pre class="params-pre">{{ formatParams(detailRow.params) }}</pre>
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../utils/request'

const user = JSON.parse(localStorage.getItem('snake_admin_user') || '{}')
const role = user.role || ''

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filterOp = ref('')
const filterCategory = ref('')

const detailVisible = ref(false)
const detailRow = ref(null)

// ===== 按角色动态显示 =====
const pageTitle = computed(() => ({
  SYS_ADMIN: '系统操作日志',
  CONTENT_ADMIN: '业务操作日志',
  MEDICAL_ADVISOR: '审核操作日志'
}[role] || '操作日志'))

const roleLabel = computed(() => ({
  SYS_ADMIN: '系统管理员',
  CONTENT_ADMIN: '内容管理员',
  MEDICAL_ADVISOR: '医疗顾问'
}[role] || ''))

const tagType = computed(() => ({
  SYS_ADMIN: 'danger',
  CONTENT_ADMIN: 'primary',
  MEDICAL_ADVISOR: 'warning'
}[role] || ''))

const scopeDesc = computed(() => ({
  SYS_ADMIN: '🔍 当前为系统管理员视角：仅显示账号管理、角色权限变更、系统异常、登录等系统级操作日志。医疗业务日志不可见。',
  CONTENT_ADMIN: '📝 当前为内容管理员视角：仅显示蛇种库、科普文章、医院血清库存等业务操作日志及登录记录。账号/权限变更等系统日志不可见。',
  MEDICAL_ADVISOR: '✅ 当前为医疗顾问视角：仅显示审核通过/驳回、内容下线/编辑等审核相关日志及登录记录。账号管理、蛇种/医院维护日志不可见。'
}[role] || ''))

const allowedCategories = computed(() => ({
  SYS_ADMIN: ['SYSTEM', 'LOGIN'],
  CONTENT_ADMIN: ['CONTENT', 'LOGIN'],
  MEDICAL_ADVISOR: ['AUDIT', 'LOGIN']
}[role] || []))

function categoryLabel(c) {
  return { SYSTEM:'系统管理', CONTENT:'业务内容', AUDIT:'审核操作', LOGIN:'登录登出' }[c] || c
}
function categoryTagType(c) {
  return { SYSTEM:'danger', CONTENT:'primary', AUDIT:'warning', LOGIN:'info' }[c] || 'info'
}

async function load() {
  try {
    const data = await request({
      url: '/admin/log/page', method: 'get',
      params: {
        pageNum: pageNum.value, pageSize: pageSize.value,
        category: filterCategory.value || undefined,
        operation: filterOp.value || undefined
      }
    })
    list.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    console.error(e)
  }
}

function showDetail(row) { detailRow.value = row; detailVisible.value = true }
function formatParams(p) {
  if (!p) return '(无)'
  try { return JSON.stringify(JSON.parse(p), null, 2) } catch { return p }
}

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 17px; font-weight: 700; color: #1e293b; }
.params-pre {
  background: #f5f7fa; padding: 12px; border-radius: 6px;
  font-size: 13px; white-space: pre-wrap; word-break: break-all;
  max-height: 300px; overflow: auto; margin: 0;
}
</style>
