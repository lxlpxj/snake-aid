<template>
  <el-card class="my-audit-card">
    <template #header>
      <div class="audit-header">
        <div>
          <span class="audit-title">📋 我的内容审核状态</span>
          <span class="audit-sub">内容管理员 · 查看我提交的内容当前审核状态（只读）</span>
        </div>
        <el-tag type="info" effect="light" size="large">
          <el-icon><InfoFilled /></el-icon>
          审核操作由医疗顾问完成，您仅可查看结果
        </el-tag>
      </div>
    </template>

    <el-alert type="warning" :closable="false" style="margin-bottom:16px;">
      草稿状态请点击「提交审核」按钮送审，审核通过后内容将正式发布到 APP 端。驳回后可修改重新提交。
    </el-alert>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="科普文章" name="article">
        <el-table :data="articles" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="category" label="分类" width="90" />
          <el-table-column label="审核状态" width="110">
            <template #default="{ row }">
              <el-tag :type="statusType(row.auditStatus)" size="small">{{ statusText(row.auditStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="updateTime" label="更新时间" width="170" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link @click="previewArticle(row)">查看</el-button>
              <el-button size="small" link type="primary" @click="edit('article', row)">编辑</el-button>
              <el-button size="small" link type="warning" v-if="row.auditStatus === 0 || row.auditStatus === 3" @click="submitAudit('article', row)">提交审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="蛇种信息" name="snake">
        <el-table :data="snakes" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="speciesName" label="学名" min-width="160" show-overflow-tooltip />
          <el-table-column label="审核状态" width="110">
            <template #default="{ row }">
              <el-tag :type="statusType(row.auditStatus)" size="small">{{ statusText(row.auditStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link @click="edit('snake', row)">编辑</el-button>
              <el-button size="small" link type="warning" v-if="row.auditStatus === 0 || row.auditStatus === 3" @click="submitAudit('snake', row)">提交审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="急救指南" name="guide">
        <el-table :data="guides" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="指南标题" min-width="220" show-overflow-tooltip />
          <el-table-column label="审核状态" width="110">
            <template #default="{ row }">
              <el-tag :type="statusType(row.auditStatus)" size="small">{{ statusText(row.auditStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link @click="edit('guide', row)">编辑</el-button>
              <el-button size="small" link type="warning" v-if="row.auditStatus === 0 || row.auditStatus === 3" @click="submitAudit('guide', row)">提交审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 预览抽屉 -->
    <el-drawer v-model="previewVisible" :title="previewTitle" size="560px">
      <div class="preview-content" v-html="previewContent"></div>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const activeTab = ref('article')

const articles = ref([])
const snakes = ref([])
const guides = ref([])

const previewVisible = ref(false)
const previewTitle = ref('')
const previewContent = ref('')

function statusText(s) { return { 0:'草稿', 1:'待审核', 2:'已发布', 3:'已驳回' }[s] || '-' }
function statusType(s) { return { 0:'info', 1:'warning', 2:'success', 3:'danger' }[s] || 'info' }

async function loadAll() {
  try { const d = await request({ url: '/admin/article/page', method: 'get', params: { pageNum:1, pageSize:100 } }); articles.value = d.records || [] } catch {}
  try { const d = await request({ url: '/admin/snake/page',    method: 'get', params: { pageNum:1, pageSize:100 } }); snakes.value    = d.records || [] } catch {}
  try { const d = await request({ url: '/admin/guide/page',    method: 'get', params: { pageNum:1, pageSize:100 } }); guides.value    = d.records || [] } catch {}
}

async function submitAudit(type, row) {
  try {
    await request({ url: `/admin/${type}/${row.id}/submit-audit`, method: 'post' })
    ElMessage.success('已提交审核，等待医疗顾问审核')
    loadAll()
  } catch {}
}

function edit(type, row) {
  const routes = { article: '/articles', snake: '/snakes', guide: '/guides' }
  router.push(routes[type])
  ElMessage.info(`请在「${routeLabel(type)}」页面编辑后再次提交审核`)
}
function routeLabel(t) { return { article: '科普文章', snake: '蛇种库', guide: '急救指南' }[t] }

function previewArticle(r) { previewTitle.value = r.title; previewContent.value = r.content || '(无内容)'; previewVisible.value = true }

onMounted(loadAll)
</script>

<style scoped>
.my-audit-card { border-radius: 10px; }
.audit-header { display: flex; justify-content: space-between; align-items: center; }
.audit-title { font-size: 18px; font-weight: 700; color: #1e293b; margin-right: 10px; }
.audit-sub { font-size: 13px; color: #64748b; }

.preview-content { line-height: 1.9; color: #334155; }
</style>
