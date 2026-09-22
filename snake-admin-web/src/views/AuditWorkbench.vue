<template>
  <el-card class="audit-card">
    <template #header>
      <div class="audit-header">
        <div>
          <span class="audit-title">🩺 审核工作台</span>
          <span class="audit-sub">医疗顾问专属 · 审核内容准确性</span>
        </div>
        <el-radio-group v-model="filterStatus" size="default">
          <el-radio-button :label="1">
            待审核
            <el-badge v-if="pendingTotal > 0" :value="pendingTotal" class="pending-badge" />
          </el-radio-button>
          <el-radio-button :label="2">已通过</el-radio-button>
          <el-radio-button :label="3">已驳回</el-radio-button>
          <el-radio-button :label="0">草稿</el-radio-button>
          <el-radio-button :label="-1">全部</el-radio-button>
        </el-radio-group>
      </div>
    </template>

    <!-- 三资源 Tab 切换 -->
    <el-tabs v-model="activeTab" class="audit-tabs">
      <el-tab-pane label="科普文章" name="article">
        <el-table :data="articles" border stripe v-loading="articleLoading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="category" label="分类" width="90" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }"><component :is="statusTag(row.auditStatus)" /></template>
          </el-table-column>
          <el-table-column prop="updateTime" label="更新时间" width="170" />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="previewArticle(row)">查看</el-button>
              <template v-if="row.auditStatus === 1">
                <el-button size="small" type="success" @click="pass('article', row)">通过</el-button>
                <el-button size="small" type="danger" @click="reject('article', row)">驳回</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="蛇种信息" name="snake">
        <el-table :data="snakes" border stripe v-loading="snakeLoading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="speciesName" label="学名" min-width="160" show-overflow-tooltip />
          <el-table-column label="有毒" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.isVenomous === 1" type="danger" size="small">有毒</el-tag>
              <el-tag v-else type="info" size="small">无毒</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }"><component :is="statusTag(row.auditStatus)" /></template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="previewSnake(row)">查看</el-button>
              <template v-if="row.auditStatus === 1">
                <el-button size="small" type="success" @click="pass('snake', row)">通过</el-button>
                <el-button size="small" type="danger" @click="reject('snake', row)">驳回</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="急救指南" name="guide">
        <el-table :data="guides" border stripe v-loading="guideLoading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="指南标题" min-width="220" show-overflow-tooltip />
          <el-table-column label="通用" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.isGeneric" type="warning" size="small">通用</el-tag>
              <el-tag v-else type="info" size="small">专属</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }"><component :is="statusTag(row.auditStatus)" /></template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="previewGuide(row)">查看</el-button>
              <template v-if="row.auditStatus === 1">
                <el-button size="small" type="success" @click="pass('guide', row)">通过</el-button>
                <el-button size="small" type="danger" @click="reject('guide', row)">驳回</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>

  <!-- 驳回弹窗 -->
  <el-dialog v-model="rejectDialog" title="驳回审核" width="480px" append-to-body>
    <el-form label-width="90px">
      <el-form-item label="驳回原因">
        <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请填写驳回理由，内容管理员可据此修改..." />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="rejectDialog = false">取消</el-button>
      <el-button type="danger" @click="confirmReject">确认驳回</el-button>
    </template>
  </el-dialog>

  <!-- 预览抽屉（文章为例，其他可复用） -->
  <el-drawer v-model="previewVisible" :title="previewTitle" size="560px" append-to-body>
    <div class="preview-content" v-html="previewContent"></div>
  </el-drawer>
</template>

<script setup>
import { ref, onMounted, computed, watch, h } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const filterStatus = ref(1) // 默认只看待审
const activeTab = ref('article')

// 数据
const articles = ref([])
const snakes = ref([])
const guides = ref([])
const articleLoading = ref(false)
const snakeLoading = ref(false)
const guideLoading = ref(false)

// 驳回
const rejectDialog = ref(false)
const rejectReason = ref('')
let pendingReject = null

// 预览
const previewVisible = ref(false)
const previewTitle = ref('')
const previewContent = ref('')

// 状态 tag —— 渲染函数，template 里用 :is="statusTag(xxx)"
const statusTag = (status) => {
  const map = {
    0: { text: '草稿', type: 'info' },
    1: { text: '待审核', type: 'warning' },
    2: { text: '已发布', type: 'success' },
    3: { text: '已驳回', type: 'danger' }
  }
  const s = map[status] || { text: '-', type: 'info' }
  return h('el-tag', { type: s.type, size: 'small' }, { default: () => s.text })
}

const pendingTotal = computed(() => {
  return articles.value.filter(a => a.auditStatus === 1).length
       + snakes.value.filter(s => s.auditStatus === 1).length
       + guides.value.filter(g => g.auditStatus === 1).length
})

// 加载三类数据
async function loadAll() {
  const s = filterStatus.value
  const statusParam = s === -1 ? null : s
  const params = { pageNum: 1, pageSize: 100 }
  if (statusParam != null) params.auditStatus = statusParam

  articleLoading.value = true
  try { const d = await request({ url: '/admin/article/page', method: 'get', params }); articles.value = d.records || [] } catch { articles.value = [] }
  articleLoading.value = false

  snakeLoading.value = true
  try { const d = await request({ url: '/admin/snake/page', method: 'get', params }); snakes.value = d.records || [] } catch { snakes.value = [] }
  snakeLoading.value = false

  guideLoading.value = true
  try { const d = await request({ url: '/admin/guide/page', method: 'get', params }); guides.value = d.records || [] } catch { guides.value = [] }
  guideLoading.value = false
}

// 通过 / 驳回
function pass(type, row) {
  ElMessageBox.confirm(`确定审核通过这条${typeLabel(type)}吗？`, '确认', { type: 'success' }).then(async () => {
    try {
      await request({ url: `/admin/${type}/${row.id}/audit`, method: 'post', data: { pass: true, remark: '' } })
      ElMessage.success('已通过')
      loadAll()
    } catch {}
  }).catch(() => {})
}
function reject(type, row) {
  pendingReject = { type, row }
  rejectReason.value = ''
  rejectDialog.value = true
}
async function confirmReject() {
  if (!rejectReason.value.trim()) { ElMessage.warning('请填写驳回原因'); return }
  try {
    await request({ url: `/admin/${pendingReject.type}/${pendingReject.row.id}/audit`, method: 'post',
      data: { pass: false, remark: rejectReason.value } })
    ElMessage.success('已驳回')
    rejectDialog.value = false
    loadAll()
  } catch {}
}

function typeLabel(t) { return { article: '科普文章', snake: '蛇种', guide: '急救指南' }[t] || t }

// 预览
function previewArticle(r) { previewTitle.value = r.title; previewContent.value = r.content || '(无内容)'; previewVisible.value = true }
function previewSnake(r) {
  previewTitle.value = r.name + (r.speciesName ? ' (' + r.speciesName + ')' : '')
  previewContent.value = `
    <p><b>毒性：</b>${r.isVenomous === 1 ? '有毒' : '无毒'}</p>
    <p><b>分布地区：</b>${r.region || '-'}</p>
    <p><b>外观特征：</b>${r.features || '-'}</p>
    <p><b>中毒症状：</b>${r.symptoms || '-'}</p>
    <p><b>急救建议：</b>${r.firstAid || '-'}</p>
  `
  previewVisible.value = true
}
function previewGuide(r) { previewTitle.value = r.title; previewContent.value = r.content || '(无内容)'; previewVisible.value = true }

watch(filterStatus, loadAll)
onMounted(loadAll)
</script>

<style scoped>
.audit-card { border-radius: 10px; }
.audit-header { display: flex; justify-content: space-between; align-items: center; }
.audit-title { font-size: 18px; font-weight: 700; color: #1e293b; margin-right: 10px; }
.audit-sub { font-size: 13px; color: #64748b; }
.pending-badge :deep(.el-badge__content) { background: #e74c3c; }

.audit-tabs :deep(.el-tabs__item.is-active) { color: #e74c3c; font-weight: 600; }
.audit-tabs :deep(.el-tabs__active-bar) { background: #e74c3c; }

.preview-content { line-height: 1.9; color: #334155; }
.preview-content :deep(h1), .preview-content :deep(h2), .preview-content :deep(h3) { margin: 16px 0 8px; }
.preview-content :deep(p) { margin: 6px 0; }
</style>
