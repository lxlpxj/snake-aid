<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>📋 蛇伤病历统计（APP 用户提交）</span>
        <div>
          <el-input v-model="keyword" placeholder="搜索蛇种/医院" style="width:200px;margin-right:10px;" clearable @keyup.enter="load" />
          <el-select v-model="filterSev" placeholder="严重程度" style="width:120px;margin-right:10px;" clearable @change="load">
            <el-option value="mild" label="轻度" />
            <el-option value="moderate" label="中度" />
            <el-option value="severe" label="重度" />
            <el-option value="critical" label="极危" />
          </el-select>
          <el-button :icon="Refresh" @click="load">刷新</el-button>
        </div>
      </div>
    </template>

    <!-- 概览卡片 -->
    <el-row :gutter="16" style="margin-bottom:20px;">
      <el-col :span="6"><el-statistic title="总病历数" :value="overview.total" /></el-col>
      <el-col :span="6"><el-statistic title="使用血清" :value="overview.withSerum" suffix="例" /></el-col>
      <el-col :span="6"><el-statistic title="重度+极危" :value="overview.severePlus" suffix="例" /></el-col>
      <el-col :span="6"><el-statistic title="本月新增" :value="overview.thisMonth" suffix="例" /></el-col>
    </el-row>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="用户ID" width="80">
        <template #default="{ row }">{{ row.userId }}</template>
      </el-table-column>
      <el-table-column prop="snakeName" label="蛇种" min-width="140" show-overflow-tooltip />
      <el-table-column label="毒型" width="110">
        <template #default="{ row }">
          <el-tag v-if="row.venomType" size="small">{{ venomLabel(row.venomType) }}</el-tag>
          <span v-else style="color:#999">—</span>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="咬伤部位" width="110" />
      <el-table-column label="严重程度" width="110">
        <template #default="{ row }">
          <el-tag v-if="row.severity" size="small" :type="sevTag(row.severity)">{{ sevLabel(row.severity) }}</el-tag>
          <span v-else style="color:#999">—</span>
        </template>
      </el-table-column>
      <el-table-column label="分数" width="75">
        <template #default="{ row }">{{ row.severityScore ?? '—' }}</template>
      </el-table-column>
      <el-table-column label="血清" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.serumUsed == 1" size="small" type="success">已用</el-tag>
          <span v-else style="color:#999">未用</span>
        </template>
      </el-table-column>
      <el-table-column prop="hospital" label="住院医院" min-width="140" show-overflow-tooltip />
      <el-table-column prop="bitePlace" label="咬伤地点" width="140" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="详情" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" link @click="showDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:16px;display:flex;justify-content:flex-end;">
      <el-pagination v-model:current-page="page.current" v-model:page-size="page.size"
        :page-sizes="[10,20,50]" :total="page.total" layout="total, sizes, prev, pager, next" @size-change="load" @current-change="load" />
    </div>

    <el-dialog v-model="detailVisible" title="病历详情" width="600px">
      <el-descriptions v-if="currentRow" :column="2" border>
        <el-descriptions-item label="用户">{{ currentRow.userId }}</el-descriptions-item>
        <el-descriptions-item label="蛇种">{{ currentRow.snakeName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentRow.gender || '—' }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ currentRow.age || '—' }}</el-descriptions-item>
        <el-descriptions-item label="咬伤部位">{{ currentRow.location || '—' }}</el-descriptions-item>
        <el-descriptions-item label="咬伤地点">{{ currentRow.bitePlace || '—' }}</el-descriptions-item>
        <el-descriptions-item label="毒型">{{ venomLabel(currentRow.venomType) }}</el-descriptions-item>
        <el-descriptions-item label="严重程度">{{ sevLabel(currentRow.severity) }} ({{ currentRow.severityScore ?? '-' }}分)</el-descriptions-item>
        <el-descriptions-item label="是否用血清">{{ currentRow.serumUsed == 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="血清详情">{{ currentRow.serumDetail || '—' }}</el-descriptions-item>
        <el-descriptions-item label="住院医院" :span="2">{{ currentRow.hospital || '—' }}</el-descriptions-item>
        <el-descriptions-item label="局部症状" :span="2">{{ currentRow.localSymptoms || '—' }}</el-descriptions-item>
        <el-descriptions-item label="全身症状" :span="2">{{ currentRow.systemicSymptoms || '—' }}</el-descriptions-item>
        <el-descriptions-item label="治疗方案" :span="2">{{ currentRow.treatment || '—' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark || '—' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'

const API = '/api/admin/snake-record'
const token = () => localStorage.getItem('snake_admin_token') || ''

const list = ref([])
const keyword = ref('')
const filterSev = ref('')
const page = reactive({ current: 1, size: 10, total: 0 })
const overview = reactive({ total: 0, withSerum: 0, severePlus: 0, thisMonth: 0 })
const detailVisible = ref(false)
const currentRow = ref(null)

const venomLabel = t => ({ neurotoxic: '神经毒', hemotoxic: '血循毒', cytotoxic: '细胞毒', mixed: '混合毒' }[t] || t || '—')
const sevLabel = s => ({ mild: '轻度', moderate: '中度', severe: '重度', critical: '极危' }[s] || s)
const sevTag = s => ({ mild: 'success', moderate: 'warning', severe: 'danger', critical: '' }[s] || '')

async function load() {
  const qs = new URLSearchParams({ pageNum: page.current, pageSize: page.size })
  if (keyword.value) qs.set('keyword', keyword.value)
  if (filterSev.value) qs.set('severity', filterSev.value)
  const res = await fetch(API + '/page?' + qs, { headers: { Authorization: 'Bearer ' + token() } })
  const j = await res.json()
  if (j.code === 200) {
    list.value = j.data.records || []
    page.total = j.data.total || 0
    // 概览
    overview.total = page.total
    overview.withSerum = list.value.filter(r => r.serumUsed == 1).length
    overview.severePlus = list.value.filter(r => r.severity === 'severe' || r.severity === 'critical').length
    const now = new Date()
    overview.thisMonth = list.value.filter(r => {
      const t = r.createTime && new Date(r.createTime)
      return t && t.getMonth() === now.getMonth() && t.getFullYear() === now.getFullYear()
    }).length
  }
}
function showDetail(row) { currentRow.value = row; detailVisible.value = true }
onMounted(load)
</script>
<style scoped>.header-bar { display:flex; justify-content:space-between; align-items:center; font-size:18px; font-weight:600; }</style>
